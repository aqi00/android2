package com.example.senior.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String getNowDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    public static String getNowYearCN() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年");
        return sdf.format(new Date());
    }

    public static int getNowYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static int getNowMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getNowDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
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

    public static int getWeekIndex(String s_date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d_date;
        try {
            d_date = format.parse(s_date);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d_date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index == 0) {
            week_index = 7;
        }
        return week_index;
    }

    public static boolean isHoliday(String text) {
        boolean result = true;
        if ((text.length() == 2 && (text.indexOf("月") > 0
                || text.contains("初") || text.contains("十")
                || text.contains("廿") || text.contains("卅")))
                || (text.length() == 3 && text.indexOf("月") > 0)) {
            result = false;
        }
        return result;
    }

}
