package com.example.media.util;

import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

public class AuthorityUtil {

    // 检查是否拥有悬浮窗权限
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        boolean result;
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Method method = manager.getClass().getDeclaredMethod("checkOp",
                    int.class, int.class, String.class);
            int property = (Integer) method.invoke(manager, op,
                    Binder.getCallingUid(), context.getPackageName());
            result = AppOpsManager.MODE_ALLOWED == property;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public static final int REQUEST_CODE = 9;
    // 跳转到悬浮窗权限的设置页面
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestAlertWindowPermission(Activity act) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + act.getPackageName()));
        act.startActivityForResult(intent, REQUEST_CODE);
    }

}
