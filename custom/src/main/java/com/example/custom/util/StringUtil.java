package com.example.custom.util;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class StringUtil {

    // 保留小数点后面多少位
    public static String formatWithString(double value, int digit) {
        String format = String.format("%%.%df", digit);
        return String.format(format, value);
    }

    // 格式化流量数据/文件大小。输入以字节为单位的长整数，输出带具体单位的字符串
    public static String formatData(long data) {
        String result = "";
        if (data > 1024 * 1024) {
            result = String.format("%sM", formatWithString(data / 1024.0 / 1024.0, 1));
        } else if (data > 1024) {
            result = String.format("%sK", formatWithString(data / 1024.0, 1));
        } else {
            result = String.format("%sB", "" + data);
        }
        return result;
    }

}
