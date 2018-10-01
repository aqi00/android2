package com.example.network;

import com.example.network.bean.PackageInfo;
import com.example.network.util.DateUtil;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint("SetTextI18n")
public class DownloadApkActivity extends AppCompatActivity {
    private static final String TAG = "DownloadApkActivity";
    private static Spinner sp_apk_url;
    private static TextView tv_apk_result;
    private boolean isFirstSelect = true; // 是否首次选择
    private DownloadManager mDownloadManager; // 声明一个下载管理器对象
    private static long mDownloadId = 0; // 下载编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_apk);
        tv_apk_result = findViewById(R.id.tv_apk_result);
        // 从系统服务中获取下载管理器
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        initApkSpinner();
    }

    // 初始化下载安装包的下拉框
    private void initApkSpinner() {
        ArrayAdapter<String> apkUrlAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, PackageInfo.mNameArray);
        sp_apk_url = findViewById(R.id.sp_apk_url);
        sp_apk_url.setPrompt("请选择要下载的安装包");
        sp_apk_url.setAdapter(apkUrlAdapter);
        sp_apk_url.setOnItemSelectedListener(new ApkUrlSelectedListener());
        sp_apk_url.setSelection(0);
    }

    class ApkUrlSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isFirstSelect) { // 刚打开页面时不需要执行下载动作
                isFirstSelect = false;
                return;
            }
            sp_apk_url.setEnabled(false);
            tv_apk_result.setText("正在下载" + PackageInfo.mNameArray[arg2]
                    + "的安装包，请到通知栏查看下载进度");
            // 根据安装包的下载地址构建一个Uri对象
            Uri uri = Uri.parse(PackageInfo.mUrlArray[arg2]);
            // 创建一个下载请求对象，指定从哪个网络地址下载文件
            Request down = new Request(uri);
            // 设置下载任务的标题
            down.setTitle(PackageInfo.mNameArray[arg2] + "下载信息");
            // 设置下载任务的描述
            down.setDescription(PackageInfo.mNameArray[arg2] + "安装包正在下载");
            // 设置允许下载的网络类型
            down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                    | Request.NETWORK_WIFI);
            // 设置通知栏在下载进行时与完成后都可见
            down.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 设置要在系统下载页面显示
            down.setVisibleInDownloadsUi(true);
            // 设置下载文件在本地的保存路径
            down.setDestinationInExternalFilesDir(
                    DownloadApkActivity.this, Environment.DIRECTORY_DOWNLOADS, arg2 + ".apk");
            // 把下载请求对象加入到下载管理器的下载队列中
            mDownloadId = mDownloadManager.enqueue(down);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 定义一个下载完成的广播接收器。用于接收下载完成事件
    public static class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    && tv_apk_result != null) { // 下载完毕
                // 从意图中解包获得下载编号
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.d(TAG, " download complete! id : " + downId + ", mDownloadId=" + mDownloadId);
                tv_apk_result.setVisibility(View.VISIBLE);
                // 拼接下载任务的完成描述
                tv_apk_result.setText(DateUtil.getNowDateTime() + " 编号"
                        + downId + "的下载任务已完成");
                sp_apk_url.setEnabled(true);
            }
        }
    }

    // 定义一个通知栏点击的广播接收器。用于接收下载通知栏的点击事件，在下载过程中有效，下载完成后失效
    public static class NotificationClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, " NotificationClickReceiver onReceive");
            if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)
                    && tv_apk_result != null) { // 点击了通知栏
                // 从意图中解包获得被点击通知的下载编号
                long[] downIds = intent.getLongArrayExtra(
                        DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                for (long downId : downIds) {
                    Log.d(TAG, " notify click! id : " + downId + ", mDownloadId=" + mDownloadId);
                    if (downId == mDownloadId) { // 找到当前的下载任务
                        tv_apk_result.setText(DateUtil.getNowDateTime() + " 编号"
                                + downId + "的下载进度条被点击了一下");
                    }
                }
            }
        }
    }

    // 适配Android9.0开始
    @Override
    public void onStart() {
        super.onStart();
        // 从Android9.0开始，系统不再支持静态广播，应用广播只能通过动态注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 创建一个下载完成的广播接收器
            completeReceiver = new DownloadCompleteReceiver();
            // 注册广播接收器，注册之后才能正常接收广播
            registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            // 创建一个通知栏点击的广播接收器
            clickReceiver = new NotificationClickReceiver();
            registerReceiver(clickReceiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 注销广播接收器，注销之后就不再接收广播
            unregisterReceiver(completeReceiver);
            unregisterReceiver(clickReceiver);
        }
    }

    // 声明一个下载完成的广播接收器
    private DownloadCompleteReceiver completeReceiver;
    // 声明一个下载完成的广播接收器
    private NotificationClickReceiver clickReceiver;
    // 适配Android9.0结束

}
