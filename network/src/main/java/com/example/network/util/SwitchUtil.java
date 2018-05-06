package com.example.network.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

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

    // 检查是否插了sim卡
    public static boolean getSimcardStatus(Context ctx) {
        // 从系统服务中获取电话管理器
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        String serial = tm.getSimSerialNumber();
        if (TextUtils.isEmpty(serial)) {
            return false;
        } else {
            return true;
        }
    }

    // 获取数据连接的开关状态
    public static boolean getMobileDataStatus(Context ctx) {
        // 如果没插sim卡，后面的getMobileDataEnabled也会返回true
        // 所以这里先判断一下有没有插卡，没插卡就表示无数据连接
        if (!getSimcardStatus(ctx)) {
            return false;
        }
        // 从系统服务中获取连接管理器
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isOpen = false;
        try {
            String methodName = "getMobileDataEnabled"; // 这是隐藏方法，需要通过反射调用
            Method method = cm.getClass().getMethod(methodName);
            isOpen = (Boolean) method.invoke(cm);
            Log.d(TAG, "getMobileDataStatus isOpen="+isOpen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    // 打开或关闭数据连接
    public static void setMobileDataStatus(Context ctx, boolean enabled) {
        // 从系统服务中获取连接管理器
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            String methodName = "setMobileDataEnabled"; // 这是隐藏方法，需要通过反射调用
            Method method = cm.getClass().getMethod(methodName, Boolean.TYPE);
            // method.setAccessible(true);
            method.invoke(cm, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置亮度自动调节的开关
    public static void setAutoBrightStatus(Context ctx, boolean enabled) {
        int screenMode = (enabled) ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, screenMode);
    }

    // 获取亮度自动调节的状态
    public static boolean getAutoBrightStatus(Context ctx) {
        int screenMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        try {
            screenMode = Settings.System.getInt(ctx.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    }

//    // Camera对象需要做成单例模式，因为Camera不能重复打开
//    private static Camera mCamera = null;
//
//    // 获取闪光灯/手电筒的开关状态
//    public static boolean getFlashStatus(Context ctx) {
//        if (mCamera == null) {
//            mCamera = Camera.open();
//        }
//        Parameters parameters = mCamera.getParameters();
//        String flashMode = parameters.getFlashMode();
//        return flashMode.equals(Parameters.FLASH_MODE_TORCH);
//    }
//
//    // 打开或关闭闪光灯/手电筒
//    public static void setFlashStatus(Context ctx, boolean enabled) {
//        if (mCamera == null) {
//            mCamera = Camera.open();
//        }
//        Parameters parameters = mCamera.getParameters();
//        if (enabled) {
//            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
//            mCamera.setParameters(parameters);
//        } else {
//            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
//            mCamera.setParameters(parameters);
//            mCamera.release();
//            mCamera = null;
//        }
//    }

}
