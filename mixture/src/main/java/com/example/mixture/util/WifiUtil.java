package com.example.mixture.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

import com.example.mixture.bean.ClientScanResult;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtil {
    private static final String TAG = "WifiUtil";
    public static final int WIFI_AP_STATE_DISABLING = 0; // 正在断开
    public static final int WIFI_AP_STATE_DISABLED = 1; // 已断开
    public static final int WIFI_AP_STATE_ENABLING = 2; // 正在连接
    public static final int WIFI_AP_STATE_ENABLED = 3; // 已连接
    public static final int WIFI_AP_STATE_FAILED = 4; // 失败
    public static String[] stateArray = {"正在断开", "已断开", "正在连接", "已连接", "失败"};

    // 开关wifi热点。返回的字符串为空则表示成功，非空则表示失败（字符串保存失败信息）
    public static String setWifiApEnabled(WifiManager wifiMgr,
                                          WifiConfiguration config, boolean enabled) {
        String desc = "";
        if (config.SSID == null || config.SSID.length() <= 0) {
            desc = "热点名称为空";
            return desc;
        }
        try {
            if (enabled) {
                // wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
                wifiMgr.setWifiEnabled(false);
            }
            // 通过反射调用设置热点
            Method method = wifiMgr.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, Boolean.TYPE);
            // 返回热点打开状态
            if (!((Boolean) method.invoke(wifiMgr, config, enabled))) {
                desc = "热点操作失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
            desc = "热点操作异常：" + e.getMessage();
        }
        return desc;
    }

    // 获得wifi热点的开关状态
    public static int getWifiApState(WifiManager wifiMgr) {
        try {
            Method method = wifiMgr.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiMgr);
            if (i > 9) {
                i -= 10;
            }
            Log.d(TAG, "wifi state:  " + i);
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return WIFI_AP_STATE_FAILED;
        }
    }

    // 获取wifi热点的配置信息
    public static WifiConfiguration getWifiApConfiguration(WifiManager wifiMgr) {
        WifiConfiguration config = new WifiConfiguration();
        try {
            Method method = wifiMgr.getClass().getMethod("getWifiApConfiguration");
            config = (WifiConfiguration) method.invoke(wifiMgr);
            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取设备的序列号
    public static String getSerialNumber() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    // 获得已连接设备的信息队列
    public static ArrayList<ClientScanResult> getClientList(boolean only_alive) {
        return getClientList(only_alive, 1000);
    }

    // 获得已连接设备的信息队列
    public static ArrayList<ClientScanResult> getClientList(boolean only_alive, int time_out) {
        BufferedReader br = null;
        ArrayList<ClientScanResult> result = null;

        try {
            result = new ArrayList<ClientScanResult>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, "设备连接信息：" + line);
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    String device = splitted[5];

                    if (mac.matches("..:..:..:..:..:..")) {
                        Log.d(TAG, "ip=" + ip + ", mac=" + mac);
                        if (mac.equals("00:00:00:00:00:00")) {
                            continue;
                        }
                        // 根据IP地址获得网络地址信息
                        InetAddress address = InetAddress.getByName(ip);
                        boolean isReachable = address.isReachable(time_out);
                        Log.d(TAG, "ip=" + ip + ", mac=" + mac + ", device="
                                + device + ", isReachable=" + isReachable);
                        if (!only_alive || isReachable) {
                            result.add(new ClientScanResult(ip, mac, device,
                                    isReachable, ip));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void stopLocalOnlyHotspot(WifiManager wifiMgr) {
        try {
            Method method = wifiMgr.getClass().getMethod("cancelLocalOnlyHotspotRequest");
            method.invoke(wifiMgr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
