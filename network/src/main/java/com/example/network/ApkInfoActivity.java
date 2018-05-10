package com.example.network;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.network.adapter.ApkInfoAdapter;
import com.example.network.bean.ApkInfo;
import com.example.network.util.ApkUtil;
import com.example.network.util.StringUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ouyangshen on 2018/1/17.
 */
@SuppressLint("DefaultLocale")
public class ApkInfoActivity extends AppCompatActivity implements
        OnClickListener, OnItemClickListener, FileSelectCallbacks {
    private final static String TAG = "ApkInfoActivity";
    private Context mContext;
    private ListView lv_apk;
    private ArrayList<ApkInfo> mApkList; // APK文件的信息队列
    private ProgressDialog mDialog; // 声明一个进度对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_info);
        mContext = this;
        findViewById(R.id.btn_open).setOnClickListener(this);
        lv_apk = findViewById(R.id.lv_apk);
        // 弹出默认的圆圈进度对话框。因为扫描设备上的APK文件和解析安装包较费时间，所以通过进度对话框提示用户耐心等待。
        mDialog = ProgressDialog.show(mContext, "请稍候", "正在努力加载");
        // 延迟100毫秒后启动APK文件加载任务
        new Handler().postDelayed(mLoadApkList, 100);
    }

    // 定义一个APK文件加载任务
    private Runnable mLoadApkList = new Runnable() {
        @Override
        public void run() {
            showApkList(); // 显示APK文件列表
            mDialog.dismiss(); // 关闭进度对话框
        }
    };

    // 显示APK文件列表
    private void showApkList() {
        // 扫描获得设备上的所有APK文件
        mApkList = ApkUtil.getAllApkFile(mContext);
        // 下面将APK文件队列显示在列表视图上
        ApkInfoAdapter adapter = new ApkInfoAdapter(mContext, mApkList);
        lv_apk.setAdapter(adapter);
        lv_apk.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) {
            // 声明一个APK文件的扩展名数组
            String[] apkExs = new String[]{"apk"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, apkExs, null);
        }
    }

    // 在点击文件列表项时触发
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showApkDetail(mApkList.get(position)); // // 显示APK文件的详细信息
    }

    // 显示APK文件的详细信息
    private void showApkDetail(ApkInfo info) {
        ApkInfo newInfo = info;
        if (TextUtils.isEmpty(info.package_name)) {
            // 获得指定apk文件的安装包信息
            newInfo = ApkUtil.getApkInfo(mContext, info.file_path);
        }
        // 下面通过提醒对话框展示APK文件的详细信息
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("APK文件信息");
        String desc = String.format("文件名称：%s\n包名：%s\n版本号：%d\n版本名称：%s\n文件路径：%s\n文件大小：%s",
                info.file_name, newInfo.package_name, newInfo.version_code, newInfo.version_name,
                newInfo.file_path, StringUtil.formatData(newInfo.file_size));
        builder.setMessage(desc);
        builder.setPositiveButton("确定", null);
        builder.create().show();
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        String path = String.format("%s/%s", absolutePath, fileName);
        Log.d(TAG, "path=" + path);
        ApkInfo info = new ApkInfo();
        info.file_name = fileName;
        info.file_path = path;
        showApkDetail(info); // // 显示APK文件的详细信息
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

}