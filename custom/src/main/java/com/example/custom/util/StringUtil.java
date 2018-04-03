package com.example.custom.util;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class StringUtil {

    public static String formatWithString(double value, int digit) {
        String format = String.format("%%.%df", digit);
        return String.format(format, value);
    }

    public static String formatTraffic(long traffic) {
        String result = "";
        if (traffic > 1024 * 1024) {
            result = String.format("%sM", formatWithString(traffic / 1024.0 / 1024.0, 1));
        } else if (traffic > 1024) {
            result = String.format("%sK", formatWithString(traffic / 1024.0, 1));
        } else {
            result = String.format("%sB", "" + traffic);
        }
        return result;
    }

}
