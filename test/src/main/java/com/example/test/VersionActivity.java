package com.example.test;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/28.
 */
@SuppressLint("DefaultLocale")
public class VersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        ImageView iv_icon = findViewById(R.id.iv_icon);
        TextView tv_desc = findViewById(R.id.tv_desc);
        iv_icon.setImageResource(R.mipmap.ic_launcher);
        try {
            // 先获取当前应用的包名，再根据包名获取详细的应用信息
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            String desc = String.format("App名称为：%s\nApp版本号为：%d\nApp版本名称为：%s",
                    getResources().getString(R.string.app_name), pi.versionCode, pi.versionName);
            tv_desc.setText(desc);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
