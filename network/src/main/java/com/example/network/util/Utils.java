package com.example.network.util;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Utils {

    public static String getDotOne(double src) {
        DecimalFormat df = new java.text.DecimalFormat(".0");
        return df.format(src);
    }

    public static String[] mClassNameArray = {"UNKNOWN", "2G", "3G", "4G"};

//	public static int TYPE_UNKNOWN = 0;
//	public static int TYPE_2G = 1;
//	public static int TYPE_3G = 2;
//	public static int TYPE_4G = 3;
//    
//	public static int TYPE_GSM = 1;
//	public static int TYPE_CDMA = 2;
//	public static int TYPE_LTE = 3;
//	public static int TYPE_WCDMA = 4;

    // 获取网络类型的名称
    public static String getNetworkTypeName(TelephonyManager tm, int mobile_type) {
        String type_name = "";
        try {
            Method method = tm.getClass().getMethod("getNetworkTypeName", Integer.TYPE);
            type_name = (String) method.invoke(tm, mobile_type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type_name;
    }

    // 获取网络分代的类型
    public static int getClassType(TelephonyManager tm, int mobile_type) {
        int class_type = 0;
        try {
            Method method = tm.getClass().getMethod("getNetworkClass", Integer.TYPE);
            class_type = (Integer) method.invoke(tm, mobile_type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return class_type;
    }

    // 获取网络分代的名称
    public static String getClassName(TelephonyManager tm, int mobile_type) {
        int class_type = getClassType(tm, mobile_type);
        return mClassNameArray[class_type];
    }

    // 获得指定文件的大小
    public static String getFileSize(String file_path) {
        File file = new File(file_path);
        long size_length = 0;
        if (file.exists()) {
            size_length = file.length();
        }
        String size = size_length + "B";
        if (size_length > 1024 * 1024) {
            size = getDotOne(size_length / 1024.0 / 1024.0) + "MB";
        } else if (size_length > 1024) {
            size = getDotOne(size_length / 1024.0) + "KB";
        }
        return size;
    }

    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
        // 获取当前手机的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 四舍五入取整
    }

    // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2dip(Context context, float pxValue) {
        // 获取当前手机的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f); // 四舍五入取整
    }

}
