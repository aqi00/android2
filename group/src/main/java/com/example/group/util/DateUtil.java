package com.example.group.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ouyangshen on 2017/9/24.
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
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

    public static String getAddDate(String str, long day_num) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date old_date;
        try {
            old_date = sdf.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        long time = old_date.getTime();
        long diff_time = day_num * 24 * 60 * 60 * 1000;
//		LogUtil.debug(TAG, "day_num="+day_num+", diff_time="+diff_time);
        time += diff_time;
        Date new_date = new Date(time);
        return sdf.format(new_date);
    }

}
