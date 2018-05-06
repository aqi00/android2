package com.example.network.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ouyangshen on 2017/9/24.
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
    public static String getNowDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public static String getNowDateTime(String formatStr) {
        String format = formatStr;
        if (TextUtils.isEmpty(format)) {
            format = "yyyyMMddHHmmss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String getNowTimeDetail() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    public static String formatTime(String src) {
        String dest = src;
        SimpleDateFormat old_sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat new_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = old_sdf.parse(src);
            dest = new_sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dest;
    }

}
