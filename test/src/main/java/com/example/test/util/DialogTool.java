package com.example.test.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;

@SuppressLint("DefaultLocale")
public class DialogTool {
    public static boolean isShown = false; // false表示上线模式，true表示开发模式
    public static int SYSTEM = 0; // 系统异常
    public static int IO = 1; // 输入输出异常
    public static int NETWORK = 2; // 网络异常
    private static String[] mError = {"系统异常，请稍候再试", "读写失败，请清理内存空间后再试",
            "网络连接失败，请检查网络设置是否开启"};

    // 根据错误的类型、名称。代码、描述，弹出相应的提醒对话框
    public static void showError(Context ctx, int type, String title, int code, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if (isShown) {
            String desc = String.format("%s\n异常代码：%d\n异常描述：%s", mError[type], code, msg);
            builder.setMessage(desc);
        } else {
            builder.setMessage(mError[type]);
        }
        builder.setTitle(title).setPositiveButton("确定", null);
        builder.create().show();
    }

    // 处理异常信息
    public static void showError(Context ctx, int type, String title, Exception e) {
        if (isShown) {
            e.printStackTrace(); // 把异常的栈信息打印到日志中
        }
        showError(ctx, type, title, -1, e.getMessage());
    }
}
