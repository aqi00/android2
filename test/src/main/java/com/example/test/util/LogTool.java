package com.example.test.util;

import android.util.Log;

public class LogTool {
    public static boolean isShown = false;  // false表示上线模式，true表示开发模式

    public static void v(String tag, String msg) {
        if (isShown) {
            Log.v(tag, msg); // 打印冗余日志
        }
    }

    public static void d(String tag, String msg) {
        if (isShown) {
            Log.d(tag, msg); // 打印调试日志
        }
    }

    public static void i(String tag, String msg) {
        if (isShown) {
            Log.i(tag, msg); // 打印一般日志
        }
    }

    public static void w(String tag, String msg) {
        if (isShown) {
            Log.w(tag, msg); // 打印警告日志
        }
    }

    public static void e(String tag, String msg) {
        if (isShown) {
            Log.e(tag, msg); // 打印错误日志
        }
    }

    public static void wtf(String tag, String msg) {
        if (isShown) {
            Log.wtf(tag, msg);
        }
    }
}
