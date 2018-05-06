package com.example.performance.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtil {

    // 把位图对象保存为指定路径的图片文件
    public static void saveBitmap(String path, Bitmap bitmap) {
        try {
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

    // 从指定路径的图片文件中读取位图数据
    public static Bitmap openBitmap(String path) {
        Bitmap bitmap = null;
        try {
            // 根据指定文件路径构建缓存输入流对象
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            // 从缓存输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(bis);
            // 关闭缓存输入流
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回图片文件中的位图数据
        return bitmap;
    }

}
