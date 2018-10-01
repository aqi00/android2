package com.example.mixture;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mixture.util.WifiUtil;

/**
 * Created by ouyangshen on 2018/3/14.
 */
public class WifiApActivity extends AppCompatActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "WifiApActivity";
    private CheckBox ck_wifi_switch;
    private EditText et_wifi_name, et_wifi_password;
    private WifiManager mWifiManager; // 声明一个无线网络管理器对象
    private WifiConfiguration mWifiConfig = new WifiConfiguration(); // 声明一个无线网络配置对象
    private int mDesType = WifiConfiguration.KeyMgmt.NONE; // 加密类型
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_ap);
        ck_wifi_switch = findViewById(R.id.ck_wifi_switch);
        et_wifi_name = findViewById(R.id.et_wifi_name);
        et_wifi_password = findViewById(R.id.et_wifi_password);
        findViewById(R.id.btn_wifi_save).setOnClickListener(this);
        initWifiName();
        et_wifi_password.setText("");
        ck_wifi_switch.setOnCheckedChangeListener(this);
        // 从系统服务中获取无线网络管理器
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        setWifiConfig(); // 创建初始的WIFI配置信息
        initDesSpinner();
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void initWifiName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            et_wifi_name.setText(Build.getSerial());
        } else {
            et_wifi_name.setText(Build.SERIAL);
        }
    }

    // 初始化加密类型下拉框
    private void initDesSpinner() {
        Spinner sp_wifi_des = findViewById(R.id.sp_wifi_des);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, desNameArray);
        typeAdapter.setDropDownViewResource(R.layout.item_select);
        sp_wifi_des.setAdapter(typeAdapter);
        sp_wifi_des.setSelection(mDesType);
        sp_wifi_des.setPrompt("请选择加密类型");
        sp_wifi_des.setOnItemSelectedListener(new DesTypeSelectedListener());
        sp_wifi_des.setSelection(mDesType);
    }

    private String[] desNameArray = {"无", "WPA PSK", "WPA2 PSK"};
    private int[] desTypeArray = {WifiConfiguration.KeyMgmt.NONE, WifiConfiguration.KeyMgmt.WPA_PSK, 4};

    class DesTypeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mDesType = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_wifi_save) {
            if (et_wifi_name.getText().length() < 4) {
                Toast.makeText(this, "WIFI名称长度需不小于四位", Toast.LENGTH_SHORT).show();
                return;
            } else if (mDesType != 0 && et_wifi_password.getText().length() < 8) {
                Toast.makeText(this, "WIFI密码长度需不小于八位", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "已保存本次WIFI设置", Toast.LENGTH_SHORT).show();
            // 只有当前已开启WIFI的，才需要断开并重连。当前未开启WIFI的，保存设置后不自动开WIFI
            int timeout = 0;
            if (ck_wifi_switch.isChecked()) {
                ck_wifi_switch.setChecked(false);
                timeout = 2000;
            }
            setWifiConfig(); // 设定WIFI配置信息
            // 延迟若干时间后启动WIFI热点的开启任务
            mHandler.postDelayed(mReOpenTask, timeout);
        }
    }

    // 通过用户名、密码、加密类型创建WIFI配置信息
    private void setWifiConfig() {
        mWifiConfig.allowedKeyManagement.clear();
        mWifiConfig.SSID = et_wifi_name.getText().toString();
        if (mDesType == WifiConfiguration.KeyMgmt.NONE) { // 无密码
            mWifiConfig.preSharedKey = "";
            mWifiConfig.wepKeys[0] = et_wifi_password.getText().toString();
            mWifiConfig.wepTxKeyIndex = 0;
        } else { // WPA加密或者WPA2加密
            mWifiConfig.allowedKeyManagement.set(desTypeArray[mDesType]);
            mWifiConfig.preSharedKey = et_wifi_password.getText().toString();
            mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            mWifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
    }

    // 定义一个WIFI热点的开启任务
    private Runnable mReOpenTask = new Runnable() {
        @Override
        public void run() {
            if (WifiUtil.getWifiApState(mWifiManager) == WifiUtil.WIFI_AP_STATE_DISABLED) {
                ck_wifi_switch.setChecked(true);
            } else {
                // 延迟2秒后再次启动WIFI热点的开启任务
                mHandler.postDelayed(this, 2000);
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_wifi_switch) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                String result;
                if (isChecked) {
                    setWifiConfig(); // 设定WIFI配置信息
                    // 开启WIFI热点
                    result = WifiUtil.setWifiApEnabled(mWifiManager, mWifiConfig, true);
                } else {
                    // 关闭WIFI热点
                    result = WifiUtil.setWifiApEnabled(mWifiManager, mWifiConfig, false);
                }
                Log.d(TAG, "onCheckedChanged: " + isChecked + ". " + result);
                if (!TextUtils.isEmpty(result)) {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                    ck_wifi_switch.setChecked(!isChecked);
                }
            } else {
                Log.d(TAG, "onCheckedChanged: " + isChecked + ". startLocalOnlyHotspot");
                Toast.makeText(this, "仅供测试用，这里打开的热点不能访问互联网", Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    mWifiManager.startLocalOnlyHotspot(new HotspotListener(), mHandler);
                } else {
                    WifiUtil.stopLocalOnlyHotspot(mWifiManager);
                }
            }
        }
    }

    // 定义一个本地热点的开关监听器
    @TargetApi(Build.VERSION_CODES.O)
    private class HotspotListener extends LocalOnlyHotspotCallback {
        @Override
        public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
            super.onStarted(reservation);
            Log.d(TAG, "Wifi Hotspot is on now");
        }

        @Override
        public void onStopped() {
            super.onStopped();
            Log.d(TAG, "onStopped: ");
        }

        @Override
        public void onFailed(int reason) {
            super.onFailed(reason);
            Log.d(TAG, "onFailed: ");
        }
    }

}
