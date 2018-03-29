package com.example.storage;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/1.
 */
public class FileBasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_basic);
        getEnvironmentInfo();
    }

    // 显示系统目录的环境信息
    private void getEnvironmentInfo() {
        TextView tv_file_basic = findViewById(R.id.tv_file_basic);
        String desc = "系统环境（含SD卡）的信息如下：";
        desc = String.format("%s\n　根目录路径：%s", desc,
                Environment.getRootDirectory().getAbsolutePath());
        desc = String.format("%s\n　数据目录路径：%s", desc,
                Environment.getDataDirectory().getAbsolutePath());
        desc = String.format("%s\n　下载缓存目录路径：%s", desc,
                Environment.getDownloadCacheDirectory().getAbsolutePath());
        desc = String.format("%s\n　外部存储(即SD卡)目录路径：%s", desc,
                Environment.getExternalStorageDirectory().getAbsolutePath());
        desc = String.format("%s\n　外部存储(即SD卡)状态：%s", desc,
                Environment.getExternalStorageState());
        desc = String.format("%s\n　SD卡的相机目录路径：%s", desc,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        // DIRECTORY_DOCUMENTS是Android4.4.2（SDK19）及以上版本才有的常量
        // 如果不做SDK版本判断，那么在低版本Android(例如4.2.2)上运行会报错
        // java.lang.NoSuchFieldError: android.os.Environment.DIRECTORY_DOCUMENTS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            desc = String.format("%s\n　SD卡的文档目录路径：%s", desc,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
        }
        desc = String.format("%s\n　SD卡的下载目录路径：%s", desc,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        desc = String.format("%s\n　SD卡的图片目录路径：%s", desc,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        desc = String.format("%s\n　SD卡的视频目录路径：%s", desc,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
        desc = String.format("%s\n　SD卡的音乐目录路径：%s", desc,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
        tv_file_basic.setText(desc);
    }

}
