package com.example.device;

import com.example.device.util.SwitchUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("SetTextI18n")
public class LocationSettingActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private CheckBox ck_gps; // 声明一个定位功能的复选框对象
    private CheckBox ck_wlan; // 声明一个WLAN功能的复选框对象
    private CheckBox ck_mobiledata; // 声明一个数据连接功能的复选框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting);
        ck_gps = findViewById(R.id.ck_gps);
        ck_wlan = findViewById(R.id.ck_wlan);
        ck_mobiledata = findViewById(R.id.ck_mobiledata);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ck_gps.setOnCheckedChangeListener(null);
        ck_wlan.setOnCheckedChangeListener(null);
        ck_mobiledata.setOnCheckedChangeListener(null);
        // 获取定位功能的开关状态
        boolean isGpsOpen = SwitchUtil.getGpsStatus(this);
        ck_gps.setChecked(isGpsOpen);
        ck_gps.setText("定位功能" + ((isGpsOpen)?"开启":"关闭"));
        // 获取WLAN功能的开关状态
        boolean isWlanOpen = SwitchUtil.getWlanStatus(this);
        ck_wlan.setChecked(isWlanOpen);
        ck_wlan.setText("WLAN功能" + ((isWlanOpen)?"开启":"关闭"));
        // 获取数据连接功能的开关状态
        boolean isMobileOpen = SwitchUtil.getMobileDataStatus(this);
        ck_mobiledata.setChecked(isMobileOpen);
        ck_mobiledata.setText("数据连接" + ((isMobileOpen)?"开启":"关闭"));
        ck_gps.setOnCheckedChangeListener(this);
        ck_wlan.setOnCheckedChangeListener(this);
        ck_mobiledata.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.ck_gps) {
            // 跳转到系统的定位设置页面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else if (id == R.id.ck_wlan) {
            // 设置WLAN功能的开关状态
            SwitchUtil.setWlanStatus(this, isChecked);
            ck_wlan.setText("WLAN功能" + ((isChecked)?"开启":"关闭"));
        } else if (id == R.id.ck_mobiledata) {
            // 设置数据连接功能的开关状态
            SwitchUtil.setMobileDataStatus(this, isChecked);
            ck_mobiledata.setText("数据连接" + ((isChecked)?"开启":"关闭"));
        }
    }

}
