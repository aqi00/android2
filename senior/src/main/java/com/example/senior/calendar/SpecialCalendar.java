package com.example.senior.calendar;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class SpecialCalendar {
    private static final String TAG = "SpecialCalendar";

    // 判断是否为闰年
    public static boolean isLeapYear(int year) {
        if (year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 100 != 0 && year % 4 == 0) {
            return true;
        }
        return false;
    }

    //得到某月有多少天数
    public static int getDaysOfMonth(boolean isLeapyear, int month) {
        int daysOfMonth = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysOfMonth = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysOfMonth = 30;
                break;
            case 2:
                if (isLeapyear) {
                    daysOfMonth = 29;
                } else {
                    daysOfMonth = 28;
                }

        }
        return daysOfMonth;
    }

    //指定某年中的某月的第一天是星期几
    public static int getWeekdayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d(TAG, " ===dayOfWeek===  " + dayOfWeek);
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        Log.d(TAG, " ===dayOfWeek===  " + dayOfWeek);
        return dayOfWeek;
    }

    public static int getTodayWeek() {
        int week = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        week = cal.get(Calendar.WEEK_OF_YEAR);
        return week;
    }

}
