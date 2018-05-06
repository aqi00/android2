package com.example.device;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.device.util.PermissionUtil;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class WeFindActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "WeFindActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_find);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置工具栏的标题文本
        tl_head.setTitle(getResources().getString(R.string.menu_third));
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
        tl_head.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.tv_scan).setOnClickListener(this);
        findViewById(R.id.tv_shake).setOnClickListener(this);
        findViewById(R.id.tv_smell).setOnClickListener(this);
        findViewById(R.id.tv_listen).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_scan) { // 点击了“扫一扫”
            // 跳到扫描二维码页面
            Intent intent = new Intent(this, FindScanActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tv_shake) { // 点击了“摇一摇”
            // 跳到博饼中大奖页面
            Intent intent = new Intent(this, FindShakeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tv_smell) { // 点击了“咻一咻”
            // WeFindActivity内嵌到WeChatActivity中，造成不会在底部弹出权限选择对话框，所以要通过WeChatActivity弹窗
            // 并且权限选择结果onRequestPermissionsResult要在WeChatActivity里面重写
            if (PermissionUtil.checkPermission(WeChatActivity.act, Manifest.permission.ACCESS_FINE_LOCATION, R.id.tv_smell%4096)) {
                // 若已获得定位权限，就跳到卫星浑天仪页面
                PermissionUtil.goActivity(this, FindSmellActivity.class);
            }
        } else if (v.getId() == R.id.tv_listen) { // 点击了“听一听”
            // 跳到蓝牙播音乐页面
            Intent intent = new Intent(this, FindListenActivity.class);
            startActivity(intent);
        }
    }

}
