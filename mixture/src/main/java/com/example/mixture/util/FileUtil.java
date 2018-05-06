package com.example.mixture.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import android.graphics.Bitmap;

public class FileUtil {
    private final static String TAG = "FileUtil";

    // 从文件路径中获取扩展名
    public static String getExtendName(String path) {
        int pos = path.lastIndexOf(".");
        return path.substring(pos + 1).toLowerCase(Locale.getDefault());
    }

    // 从文件路径中获取文件名称
    public static String getFileName(String path) {
        int pos = path.lastIndexOf("/");
        return path.substring(pos + 1);
    }

    // 从文件路径中获取目录名称
    public static String getFileDir(String path) {
        int pos = path.lastIndexOf("/");
        return path.substring(0, pos);
    }

    // 把字节数组保存为文件
    public static void writeFile(String path, byte[] bytes) {
        File file = new File(path);
        try {
            File dir = new File(path.substring(0, path.lastIndexOf("/")));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream output = new FileOutputStream(file);
            output.write(bytes);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 把位图对象保存为指定路径的图片文件
    public static void saveBitmap(String path, Bitmap bitmap) {
        try {
            File dirFile = new File(path.substring(0, path.lastIndexOf("/")));
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            // 根据指定文件路径构建缓存输出流对象
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            // 把位图数据压缩到缓存输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            // 完成缓存输出流的写入动作
            bos.flush();
            // 关闭缓存输出流
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
