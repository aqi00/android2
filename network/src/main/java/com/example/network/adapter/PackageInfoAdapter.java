package com.example.network.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.network.R;
import com.example.network.bean.ApkInfo;
import com.example.network.bean.PackageInfo;
import com.example.network.util.ApkUtil;
import com.example.network.util.InstallUtil;
import com.example.network.util.Utils;
import com.example.network.widget.TextProgressBar;

// 展示应用包信息列表
@SuppressLint("SetTextI18n")
public class PackageInfoAdapter extends BaseAdapter {
    private final static String TAG = "PackageInfoAdapter";
    private Context mContext; // 声明一个上下文对象
    private ArrayList<PackageInfo> mPackageInfoList; // 应用信息队列
    private DownloadManager mDownloadManager; // 声明一个下载管理器对象
    private ArrayList<ApkInfo> mDownloadedApkList; // 已下载的APK文件队列
    private HashMap<Integer, ViewHolder> mViewMap = new HashMap<Integer, ViewHolder>(); // 视图持有者的映射
    private HashMap<Integer, Long> mTaskMap = new HashMap<Integer, Long>(); // 文件下载任务的映射

    public PackageInfoAdapter(Context context, ArrayList<PackageInfo> packageinfoList) {
        mContext = context;
        mPackageInfoList = packageinfoList;
        // 从系统服务中获取下载管理器
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        // 获取设备中所有已下载的APK文件
        mDownloadedApkList = ApkUtil.getAllApkFile(mContext);
    }

    @Override
    public int getCount() {
        return mPackageInfoList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mPackageInfoList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_package, null);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.iv_icon = convertView.findViewById(R.id.iv_icon);
            holder.tv_old_version = convertView.findViewById(R.id.tv_old_version);
            holder.tv_new_version = convertView.findViewById(R.id.tv_new_version);
            holder.btn_operation = convertView.findViewById(R.id.btn_operation);
            // 从布局文件中获取名叫tpb_progress的文本进度条对象
            holder.tpb_progress = convertView.findViewById(R.id.tpb_progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PackageInfo item = mPackageInfoList.get(position);
        holder.tv_name.setText(item.app_name);
        holder.iv_icon.setImageResource(item.package_icon);
        if (!TextUtils.isEmpty(item.new_version)) {
            holder.tv_new_version.setText("最新版本：" + item.new_version);
        } else {
            holder.tv_new_version.setText("");
        }
        // 暂不支持应用升级。替代做法是：先卸载旧版本，再安装新版本
        if (!TextUtils.isEmpty(item.old_version)) {
            holder.tv_old_version.setText("当前版本：" + item.old_version);
            holder.btn_operation.setText("卸载");
            holder.btn_operation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 卸载指定包名的应用
                    InstallUtil.uninstall(mContext, item.package_name);
                }
            });
        } else {
            // 检查本地是否已有该版本下载完的安装包
            final String apkPath = getLocalPath(item.package_name, item.new_version);
            if (TextUtils.isEmpty(apkPath)) { // 本地未找到最新安装包，则需联网下载
                holder.tv_old_version.setText("未安装");
                holder.btn_operation.setText("下载");
                if (TextUtils.isEmpty(item.download_url)) { // 没有下载地址
                    holder.btn_operation.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "未获取到下载地址，请检查网络", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else { // 有下载地址
                    holder.btn_operation.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 从指定地址下载该应用的安装包
                            download(position, item.package_name, item.download_url);
                        }
                    });
                }
            } else { // 本地已有最新安装包，则设置应用状态为已下载
                // 设置应用状态为已下载
                setDownloaded(holder, apkPath);
            }
        }
        mViewMap.put(position, holder);
        // 下载过程中暂停又恢复页面（如熄屏后又亮屏），此时继续展示下载进度
        Log.d(TAG, "position=" + position + ", containsKey=" + mTaskMap.containsKey(position));
        if (mTaskMap.containsKey(position)) {
            // 延迟100毫秒后启动下载进度的刷新任务
            mHandler.postDelayed(new DownloadTask(position), 100);
        }
        return convertView;
    }

    // 开始下载APK文件
    private void download(int position, String package_name, String url) {
        Log.d(TAG, "download position=" + position + ", package_name=" + package_name + ", url=" + url);
        ViewHolder holder = mViewMap.get(position);
        holder.btn_operation.setVisibility(View.GONE); // 隐藏操作按钮
        holder.tpb_progress.setVisibility(View.VISIBLE); // 显示下载进度
        holder.tpb_progress.setProgress(0); // 初始化下载进度
        holder.tpb_progress.setProgressText("已下载0%"); // 初始化下载进度文本
        // 创建一个下载请求对象，指定从哪个网络地址下载文件
        Request down = new Request(Uri.parse(url));
        // 设置允许下载的网络类型
        down.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        // 设置不在通知栏显示
        down.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
        // 设置不在系统下载页面显示
        down.setVisibleInDownloadsUi(false);
        // 设置下载文件在本地的保存路径
        down.setDestinationInExternalFilesDir(
                mContext, Environment.DIRECTORY_DOWNLOADS, package_name + ".apk");
        // 把下载请求对象加入到下载管理器的下载队列中
        long downloadId = mDownloadManager.enqueue(down);
        Log.d(TAG, "download position=" + position + ", downloadId=" + downloadId);
        mTaskMap.put(position, downloadId);
        // 延迟100毫秒后启动下载进度的刷新任务
        mHandler.postDelayed(new DownloadTask(position), 100);
    }

    // 下载完成，准备进行安装
    private void setDownloaded(ViewHolder holder, final String apkPath) {
        holder.tpb_progress.setVisibility(View.GONE); // 隐藏下载进度
        holder.btn_operation.setVisibility(View.VISIBLE); // 显示操作按钮
        int dip_5 = Utils.dip2px(mContext, 5);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.btn_operation.getLayoutParams();
        params.setMargins(dip_5, dip_5, dip_5, dip_5);
        holder.btn_operation.setLayoutParams(params);
        holder.tv_old_version.setText("已下载");
        holder.btn_operation.setText("安装");
        holder.btn_operation.setBackgroundResource(R.color.green);
        holder.btn_operation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对指定路径的APK文件执行安装操作
                InstallUtil.install(mContext, apkPath);
            }
        });
    }

    // 获取该App指定版本号的安装包路径，返回非空则表示有找到
    private String getLocalPath(String packageName, String versionName) {
        String local_path = "";
        for (ApkInfo info : mDownloadedApkList) {
            if (packageName.equals(info.package_name) && versionName.equals(info.version_name)) {
                local_path = info.file_path;
            }
        }
        return local_path;
    }

    private Handler mHandler = new Handler();
    // 下载过程中实时刷新下载进度的任务
    private class DownloadTask implements Runnable {
        private int mPos;
        private ViewHolder holder;
        private long downloadId;
        private String apkPath;

        public DownloadTask(int position) {
            mPos = position;
            holder = mViewMap.get(mPos);
            downloadId = mTaskMap.get(mPos);
        }

        @Override
        public void run() {
            Log.d(TAG, "DownloadTask run downloadId=" + downloadId);
            boolean isFinished = false;
            // 创建一个下载查询对象，按照下载编号进行过滤
            Query down_query = new Query();
            // 设置下载查询对象的编号过滤器
            down_query.setFilterById(downloadId);
            // 向下载管理器发起查询操作，并返回查询结果集的游标
            Cursor cursor = mDownloadManager.query(down_query);
            while (cursor.moveToNext()) {
                int uriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                int totalSizeIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int nowSizeIdx = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                // 根据总大小和已下载大小，计算当前的下载进度
                int progress = (int) (100 * cursor.getLong(nowSizeIdx) / cursor.getLong(totalSizeIdx));
                if (cursor.getString(uriIdx) == null) {
                    break;
                }
                holder.tpb_progress.setProgress(progress); // 刷新下载进度
                holder.tpb_progress.setProgressText("已下载" + progress + "%"); // 刷新下载进度的文本描述
                apkPath = cursor.getString(uriIdx);
                Log.d(TAG, "refresh position=" + mPos + ", progress=" + progress + ", apkPath=" + apkPath);
                if (progress >= 100) { // 下载完毕
                    isFinished = true;
                }
            }
            cursor.close(); // 关闭数据库游标
            if (!isFinished) { // 未完成，则继续刷新
                // 延迟100毫秒后再次启动下载进度的刷新任务
                mHandler.postDelayed(this, 100);
            } else { // 已完成，则准备安装
                mTaskMap.remove(mPos);
                // 设置应用状态为已下载
                setDownloaded(holder, apkPath);
            }
        }
    }

    public final class ViewHolder {
        public TextView tv_name;
        public ImageView iv_icon;
        public TextView tv_old_version;
        public TextView tv_new_version;
        public Button btn_operation;
        public TextProgressBar tpb_progress; // 声明一个文本进度条对象
    }

}
