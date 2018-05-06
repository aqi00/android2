package com.example.network.util;

import java.util.ArrayList;

import com.example.network.bean.ApkInfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class ApkUtil {
    private static final String TAG = "ApkUtil";

    // 获取指定应用已经安装的版本号
    public static String getInstallVersion(Context context, String packageName) {
        String version = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            if (info != null) {
                version = info.versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    // 获取设备上面所有已经存在着的APK文件
    public static ArrayList<ApkInfo> getAllApkFile(Context context) {
        ArrayList<ApkInfo> appAray = new ArrayList<ApkInfo>();
        // 查找本地所有的apk文件，其中mime_type指定了APK的文件类型
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                null, "mime_type=\"application/vnd.android.package-archive\"", null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取文件名
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                // 获取文件完整路径
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                // 获取文件大小
                int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                PackageManager pm = context.getPackageManager();
                // 获取apk文件的包信息
                PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
                if (pi != null) {
                    Log.d(TAG, "packageName="+pi.packageName+", versionName="+pi.versionName);
                    String pkg_name = pi.packageName; // 包名
                    String vs_name = pi.versionName; // 版本名称
                    int vs_code = pi.versionCode; // 版本号
                    // 将该记录添加到apk文件信息列表
                    appAray.add(new ApkInfo(title, path, size, pkg_name, vs_name, vs_code));
                }
            }
            cursor.close(); // 关闭数据库游标
        }
        return appAray;
    }

    // 获取指定文件的安装包信息
    public static ApkInfo getApkInfo(Context context, String path) {
        ApkInfo info = new ApkInfo();
        PackageManager pm = context.getPackageManager();
        // 从指定路径的APK文件中解析应用的包信息，包括包名、版本号等等
        PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (pi != null) {
            Log.d(TAG, "packageName="+pi.packageName+", versionName="+pi.versionName);
            info.file_path = path;
            info.package_name = pi.packageName; // 包名
            info.version_name = pi.versionName; // 版本名称
            info.version_code = pi.versionCode; // 版本号
        }
        return info;
    }

}
