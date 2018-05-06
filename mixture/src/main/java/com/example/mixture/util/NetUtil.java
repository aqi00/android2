package com.example.mixture.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetUtil {
    private final static String TAG = "NetUtil";

    public static String[] mClassArray = {"UNKNOWN", "2G", "3G", "4G"};

    public static int TYPE_UNKNOWN = 0;
    public static int TYPE_2G = 1;
    public static int TYPE_3G = 2;
    public static int TYPE_4G = 3;

    public static int TYPE_GSM = 1;
    public static int TYPE_CDMA = 2;
    public static int TYPE_LTE = 3;
    public static int TYPE_WCDMA = 4;

    public static String getNetworkTypeName(TelephonyManager tm, int net_type) {
        String type_name = "";
        try {
            Method method = tm.getClass().getMethod("getNetworkTypeName", Integer.TYPE);
            type_name = (String) method.invoke(tm, net_type);
        } catch (Exception e) {
            Log.d(TAG, "getNetworkTypeName error: " + e.getMessage());
        }
        return type_name;
    }

    public static int getClassType(TelephonyManager tm, int net_type) {
        int class_type = 0;
        try {
            Method method = tm.getClass().getMethod("getNetworkClass", Integer.TYPE);
            class_type = (Integer) method.invoke(tm, net_type);
        } catch (Exception e) {
            Log.d(TAG, "getNetworkClass error: " + e.getMessage());
        }
        return class_type;
    }

    public static String getClassName(TelephonyManager tm, int net_type) {
        int class_type = getClassType(tm, net_type);
        return mClassArray[class_type];
    }

}
