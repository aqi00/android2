package com.example.test.util;

import android.content.Context;
import android.widget.Toast;

public class ToastTool {
    public static boolean isShown = false; // false表示上线模式，true表示开发模式

    public static void showShort(Context ctx, String msg) { // 显示短提示
        if (isShown) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showLong(Context ctx, String msg) { // 显示长提示
        if (isShown) {
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static void showQuit(Context ctx) {
        Toast.makeText(ctx, "再按一次返回键退出！", Toast.LENGTH_SHORT).show();
    }
}
