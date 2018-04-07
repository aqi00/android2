package com.example.custom;

import com.example.custom.service.NormalService;
import com.example.custom.util.DateUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/14.
 */
@SuppressLint("StaticFieldLeak")
public class ServiceNormalActivity extends AppCompatActivity implements OnClickListener {
    private static TextView tv_normal;
    private Intent mIntent; // 声明一个意图对象
    private static String mDesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_normal);
        tv_normal = findViewById(R.id.tv_normal);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        // 创建一个通往普通服务的意图
        mIntent = new Intent(this, NormalService.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) { // 点击了启动服务按钮
            startService(mIntent); // 启动指定意图的服务
        } else if (v.getId() == R.id.btn_stop) { // 点击了停止服务按钮
            stopService(mIntent); // 停止指定意图的服务
        }
    }

    public static void showText(String desc) {
        if (tv_normal != null) {
            mDesc = String.format("%s%s %s\n", mDesc, DateUtil.getNowDateTime("HH:mm:ss"), desc);
            tv_normal.setText(mDesc);
        }
    }

}
