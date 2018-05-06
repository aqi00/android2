package com.aqi00.lib.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class DirUtil {

    public static boolean isStorageAdmit(Context context) {
        String tempPath = String.format("%s/%s/", Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                context.getPackageName());
        if (!createDir(tempPath)) {
            return false;
        } else {
            File dir = new File(tempPath);
            return dir.delete();
        }
    }

    public static boolean createDir(String path) {
        boolean result;
        File dir = new File(path);
        if (!dir.exists()) {
            result = dir.mkdirs();
        } else {
            result = true;
        }
        return result;
    }

    public static File createFile(String path, String file_name) {
        createDir(path);
        File file = new File(path, file_name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
