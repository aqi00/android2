package com.example.mixture.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

@SuppressLint("WifiManagerPotentialLeak")
public class SwitchUtil {
    private static final String TAG = "SwitchUtil";

    // 获取定位功能的开关状态
    public static boolean getGpsStatus(Context ctx) {
        // 从系统服务中获取定位管理器
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 检查定位功能是否打开，若未打开则跳到系统的定位功能设置页面
    public static void checkGpsIsOpen(Context ctx, String hint) {
        if (!getGpsStatus(ctx)) {
            Toast.makeText(ctx, hint, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            ctx.startActivity(intent);
        }
    }

    // 获取无线网络的开关状态
    public static boolean getWlanStatus(Context ctx) {
        // 从系统服务中获取无线网络管理器
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        return wm.isWifiEnabled();
    }

    // 打开或关闭无线网络
    public static void setWlanStatus(Context ctx, boolean enabled) {
        // 从系统服务中获取无线网络管理器
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(enabled);
    }

}
