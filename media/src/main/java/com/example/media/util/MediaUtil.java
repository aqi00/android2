package com.example.media.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Locale;

@SuppressLint("DefaultLocale")
public class MediaUtil {
    private final static String TAG = "MediaUtil";

    // 从文件的完整路径中截出扩展名
    public static String getExtendName(String path) {
        int pos = path.lastIndexOf(".");
        return path.substring(pos + 1).toLowerCase(Locale.getDefault());
    }

    // 格式化播放时长
    public static String formatDuration(int milliseconds) {
        int seconds = milliseconds / 1000;
        int hour = seconds / 3600;
        int minute = seconds / 60;
        int second = seconds % 60;
        String str;
        if (hour > 0) {
            str = String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            str = String.format("%02d:%02d", minute, second);
        }
        return str;
    }

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
