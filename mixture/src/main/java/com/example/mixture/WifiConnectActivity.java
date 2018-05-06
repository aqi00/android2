package com.example.mixture;

import java.util.ArrayList;
import java.util.List;

import com.example.mixture.adapter.WifiListAdapter;
import com.example.mixture.bean.WifiConnect;
import com.example.mixture.util.SwitchUtil;
import com.example.mixture.widget.InputDialogFragment.InputCallbacks;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/11.
 */
public class WifiConnectActivity extends AppCompatActivity implements
        OnCheckedChangeListener, InputCallbacks {
    private static final String TAG = "WifiConnectActivity";
    private WifiManager mWifiManager; // 声明一个无线网络管理器对象
    private ListView lv_wifi; // 声明一个用于展示周围无线网络的列表视图对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect);
        CheckBox ck_wlan = findViewById(R.id.ck_wlan);
        lv_wifi = findViewById(R.id.lv_wifi);
        if (SwitchUtil.getWlanStatus(this)) {
            ck_wlan.setChecked(true);
        }
        ck_wlan.setOnCheckedChangeListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SwitchUtil.checkGpsIsOpen(this, "Android6.0以上版本需要打开定位功能才能查看WIFI");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 移除无线网络扫描任务
        mHandler.removeCallbacks(mRefresh);
        // 从系统服务中获取无线网络管理器
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 延迟50毫秒后启动无线网络扫描任务
        mHandler.postDelayed(mRefresh, 50);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_wlan) {
            SwitchUtil.setWlanStatus(this, isChecked);
        }
    }

    // 定义一个无线网络扫描任务。间隔3秒扫描周围的WIFI
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            scanWifiList(); // 扫描无线网络
            // 延迟3秒后再次启动无线网络扫描任务
            mHandler.postDelayed(this, 3000);
        }
    };

    // 扫描无线网络
    private void scanWifiList() {
        ArrayList<WifiConnect> wifiList = new ArrayList<WifiConnect>();
        // 通过无线网络管理器获得无线网络信息
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int state = mWifiManager.getWifiState();
        String SSID;
        if (state == WifiManager.WIFI_STATE_ENABLED || state == WifiManager.WIFI_STATE_ENABLING) { // 已经开启WLAN功能
            SSID = wifiInfo.getSSID();
        } else { // 尚未开启WLAN功能
            // 清空WIFI列表
            WifiListAdapter wifiAdapter = new WifiListAdapter(this, mWifiManager, wifiList);
            lv_wifi.setAdapter(wifiAdapter);
            return;
        }
        // 开始扫描周围的WIFI网络
        mWifiManager.startScan();
        // 获取扫描到的WIFI列表信息
        ArrayList<ScanResult> newResultList = getResultList();
        // 通过无线网络管理器获得已配置的网络信息
        List<WifiConfiguration> configList = mWifiManager.getConfiguredNetworks();
        for (int i = 0; i < newResultList.size(); i++) {
            ScanResult item = newResultList.get(i);
            WifiConnect wifi = new WifiConnect();
            wifi.SSID = item.SSID;
            // 根据信号大小计算该WIFI的信号强弱等级
            wifi.level = WifiManager.calculateSignalLevel(item.level, 4);
            if (SSID.contains(wifi.SSID)) {
                wifi.status = true;
            }
            // 根据加密名称找到对应的加密类型
            if (item.capabilities.toUpperCase().contains("WPA2")) {
                wifi.type = 4;
            } else if (item.capabilities.toUpperCase().contains("WPA")) {
                wifi.type = WifiConfiguration.KeyMgmt.WPA_PSK;
            } else {
                wifi.type = WifiConfiguration.KeyMgmt.NONE;
            }
            // 根据WIFI名称找到对应的网络编号
            for (int j = 0; j < configList.size(); j++) {
                if (configList.get(j).SSID.contains(wifi.SSID)) {
                    wifi.networkId = configList.get(j).networkId;
                    break;
                }
            }
            wifiList.add(wifi);
        }
        // 根据扫描得到的WIFI队列，刷新WIFI列表
        WifiListAdapter wifiAdapter = new WifiListAdapter(this, mWifiManager, wifiList);
        lv_wifi.setAdapter(wifiAdapter);
    }

    // 获取扫描到的WIFI列表信息
    private ArrayList<ScanResult> getResultList() {
        // 通过无线网络管理器获得WIFI扫描结果
        List<ScanResult> resultList = mWifiManager.getScanResults();
        ArrayList<ScanResult> newResultList = new ArrayList<ScanResult>();
        for (int i = 0; i < resultList.size(); i++) {
            ScanResult item = resultList.get(i);
            int j;
            for (j = 0; j < newResultList.size(); j++) {
                // 过滤重复的WIFI
                ScanResult newItem = newResultList.get(j);
                if (item.SSID.equals(newItem.SSID)) {
                    if (item.level > newItem.level) {
                        newResultList.set(j, item);
                    }
                    break;
                }
            }
            if (j >= newResultList.size()) {
                newResultList.add(item);
            }
        }
        return newResultList;
    }

    // 用户在对话框中输入密码，然后App自动去连接该WIFI
    // 在输入对话框上面点击确定按钮后触发
    public void onInput(String ssid, String password, int type) {
        // 通过用户名、密码、加密类型创建WIFI配置信息
        WifiConfiguration config = createWifiInfo(ssid, password, type);
        // 往无线网络管理器添加新的WIFI配置，并返回该WIFI的网络编号
        int netId = mWifiManager.addNetwork(config);
        if (netId == -1) {
            Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show();
        } else {
            // 启用指定网络编号的WIFI
            mWifiManager.enableNetwork(netId, true);
        }
    }

    // 通过用户名、密码、加密类型创建WIFI配置信息
    private WifiConfiguration createWifiInfo(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        if (type == WifiConfiguration.KeyMgmt.NONE) { // 无密码
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else { // WPA加密或者WPA2加密
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(type);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

}
