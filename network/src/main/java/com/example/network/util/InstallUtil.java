package com.example.network.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.example.network.BuildConfig;
import com.example.network.service.AutoInstallService;

public class InstallUtil {
    public static final String TAG = "InstallUtil";

    // 安装指定路径的APK文件
    public static boolean install(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }
        // 创建一个浏览动作的意图
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 兼容Android7.0，把访问文件的Uri方式改为FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 通过FileProvider获得安装包文件的Uri访问方式
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            // 设置Uri的数据类型为APK文件
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            // 给意图添加授权读取Uri的标志
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            // 根据指定文件创建一个Uri对象
            Uri uri = Uri.fromFile(file);
            // 设置Uri的数据类型为APK文件
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            // 给意图添加开辟新任务的标志
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        // 启动系统自带的应用安装程序
        context.startActivity(intent);
        return true;
    }

    // 卸载指定包名的App
    public static boolean uninstall(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        // 根据指定包名创建一个Uri对象
        Uri uri = Uri.parse("package:"+packageName);
        // 创建一个删除动作的意图
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        // 给意图添加开辟新任务的标志
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 启动系统自带的应用卸载程序
        context.startActivity(intent);
        return true;
    }



    public static boolean isAccessibilitySettingsOn(Context context) {
        // 修改AutoInstallService为你自定义的智能安装服务
        String service = context.getPackageName() + "/" + AutoInstallService.class.getCanonicalName();
        Log.d(TAG, "service=" + service);
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        String settingValue = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        Log.d(TAG, "settingValue=" + settingValue);
        if (!TextUtils.isEmpty(settingValue)) {
            splitter.setString(settingValue);
            while (splitter.hasNext()) {
                String accessibilityService = splitter.next();
                Log.d(TAG, "accessibilityService=" + accessibilityService);
                if (accessibilityService.equalsIgnoreCase(service)) {
                    Log.d(TAG, "accessibility is switched on!");
                    return true;
                }
            }
        }
        return false;
    }

}
