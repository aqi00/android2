package com.example.device.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class MediaUtil {
    private final static String TAG = "MediaUtil";

    // 获得音视频文件的缓存路径
    public static String getRecordFilePath(Context context, String dir_name, String extend_name) {
        String path = "";
        File recordDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + dir_name + "/");
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        try {
            File recordFile = File.createTempFile(DateUtil.getNowDateTime(), extend_name, recordDir);
            path = recordFile.getAbsolutePath();
            Log.d(TAG, "dir_name=" + dir_name + ", extend_name=" + extend_name + ", path=" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
