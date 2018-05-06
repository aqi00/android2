package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.media.service.TrafficService;
import com.example.media.service.StockService;
import com.example.media.util.AuthorityUtil;

public class FloatWindowActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_window);
        findViewById(R.id.btn_traffic_open).setOnClickListener(this);
        findViewById(R.id.btn_traffic_close).setOnClickListener(this);
        findViewById(R.id.btn_stock_open).setOnClickListener(this);
        findViewById(R.id.btn_stock_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // AppOpsManager.OP_SYSTEM_ALERT_WINDOW是隐藏变量（值为24），不能直接引用
        if (!AuthorityUtil.checkOp(this, 24)) { // 未开启悬浮窗权限
            Toast.makeText(this, "请先给该应用开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            // 跳到悬浮窗权限的设置页面
            AuthorityUtil.requestAlertWindowPermission(this);
            return;
        }
        if (v.getId() == R.id.btn_traffic_open) { // 打开流量悬浮窗
            // 下面携带打开类型启动流量服务
            Intent intent = new Intent(this, TrafficService.class);
            intent.putExtra("type", TrafficService.OPEN);
            startService(intent);
        } else if (v.getId() == R.id.btn_traffic_close) { // 关闭流量悬浮窗
            // 下面携带关闭类型启动流量服务
            Intent intent = new Intent(this, TrafficService.class);
            intent.putExtra("type", TrafficService.CLOSE);
            startService(intent);
        } else if (v.getId() == R.id.btn_stock_open) { // 打开股指悬浮窗
            // 下面携带打开类型启动股指服务
            Intent intent = new Intent(this, StockService.class);
            intent.putExtra("type", StockService.OPEN);
            startService(intent);
        } else if (v.getId() == R.id.btn_stock_close) { // 关闭股指悬浮窗
            // 下面携带关闭类型启动股指服务
            Intent intent = new Intent(this, StockService.class);
            intent.putExtra("type", StockService.CLOSE);
            startService(intent);
        }
    }

    // 从悬浮窗权限的设置页面返回时触发
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthorityUtil.REQUEST_CODE) {
        }
    }

}
