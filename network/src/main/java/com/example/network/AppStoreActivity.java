package com.example.network;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.GridView;
import android.widget.Toast;

import com.example.network.adapter.PackageInfoAdapter;
import com.example.network.bean.PackageInfo;
import com.example.network.bean.PackageResp;
import com.example.network.task.CheckUpdateTask;
import com.example.network.task.CheckUpdateTask.OnCheckUpdateListener;
import com.example.network.util.InstallUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppStoreActivity extends AppCompatActivity implements OnCheckUpdateListener {
    private final static String TAG = "AppStoreActivity";
    private Context mContext;
    private GridView gv_package;
    private PackageInfoAdapter mAdapter;
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private ArrayList<PackageInfo> mPackageList = new ArrayList<PackageInfo>(); // 已安装应用的包信息队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_store);
        mContext = this;
        gv_package = findViewById(R.id.gv_package);
        // 有兴趣的读者可以研究一下这里的智能安装服务
        // 检测智能安装功能是否已经开启，如果尚未开启，则弹窗提示用户开启
//        if (!InstallUtil.isAccessibilitySettingsOn(mContext)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            builder.setTitle("智能安装功能检测");
//            builder.setMessage("您尚未开启应用超市的智能安装功能，建议您先开启智能安装服务");
//            builder.setNegativeButton("暂不开启", null);
//            builder.setPositiveButton("立即开启", new OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                    startActivity(intent);
//                }
//            });
//            builder.create().show();
//        }
        // 延迟100毫秒后启动已安装应用的版本检查任务
        new Handler().postDelayed(mCheck, 100);
    }

    // 定义一个已安装应用的版本检查任务
    private Runnable mCheck = new Runnable() {
        @Override
        public void run() {
            // 弹出默认的圆圈进度对话框
            mDialog = ProgressDialog.show(mContext, "请稍候", "正在努力加载");
            startCheckUpdate(); // 开始进行应用版本检查
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            // 恢复页面时刷新下载进度
            mAdapter.notifyDataSetChanged();
        }
    }

    // 开始进行应用版本检查。下面请求服务器得到这些应用的最新版本以及下载地址
    private void startCheckUpdate() {
        mPackageList = PackageInfo.getDefaultList(mContext);
        try {
            // 创建一个JSON数组对象
            JSONArray array = new JSONArray();
            for (PackageInfo info : mPackageList) {
                // 创建一个JSON对象
                JSONObject item = new JSONObject();
                // 添加一个名叫package_name的字符串参数
                item.put("package_name", info.package_name);
                // 把item这个JSON对象加入到JSON数组
                array.put(item);
            }
            // 创建一个JSON对象
            JSONObject obj = new JSONObject();
            // 添加一个名叫package_list的数组参数
            obj.put("package_list", array);
            // 创建检查更新线程
            CheckUpdateTask task = new CheckUpdateTask();
            // 设置检查更新监听器
            task.setOnCheckUpdateListener(this);
            // 把检查更新线程加入到处理队列。请求参数就是前面构造的json串
            task.execute(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 在检查更新结束时触发。更新应用列表里的最新版本号
    public void finishCheckUpdate(String resp) {
        // 把json串转换为PackageResp类型的数据对象packageResp
        PackageResp packageResp = new Gson().fromJson(resp, PackageResp.class);
        if (packageResp!=null && packageResp.package_list!=null) {
            for (PackageInfo updateInfo : packageResp.package_list) {
                if (TextUtils.isEmpty(updateInfo.download_url) || TextUtils.isEmpty(updateInfo.new_version)) {
                    // 下载地址为空或者新版本号为空，则忽略这条记录
                    continue;
                }
                // 下载地址和新版本号都是非空，则更新包信息队列中的相关字段
                for (int j=0; j<mPackageList.size(); j++) {
                    PackageInfo item = mPackageList.get(j);
                    if (updateInfo.package_name.equals(item.package_name)) {
                        item.download_url = updateInfo.download_url;
                        item.new_version = updateInfo.new_version;
                        mPackageList.set(j, item);
                    }
                }
            }
        } else {
            Toast.makeText(this, "返回数据格式不对，请检查服务是否正常开启",
                    Toast.LENGTH_LONG).show();
        }
        // 下面利用最新的包信息队列，刷新应用列表的展示
        mAdapter = new PackageInfoAdapter(mContext, mPackageList);
        gv_package.setAdapter(mAdapter);
        if (mDialog != null && mDialog.isShowing()) { // 对话框仍在显示
            mDialog.dismiss(); // 关闭对话框
        }
    }
}
