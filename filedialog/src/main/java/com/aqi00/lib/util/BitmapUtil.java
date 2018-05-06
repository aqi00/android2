package com.aqi00.lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Locale;

public class BitmapUtil {

    public static void saveBitmap(String path, Bitmap bitmap, String format, int quality) {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        if (format.toUpperCase(Locale.getDefault()).equals("PNG")) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        try {
            // 根据指定文件路径构建缓存输出流对象
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            // 把位图数据压缩到缓存输出流中
            bitmap.compress(compressFormat, quality, bos);
            // 完成缓存输出流的写入动作
            bos.flush();
            // 关闭缓存输出流
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 把位图数据保存到指定路径的图片文件
    public static void saveBitmap(String path, ByteBuffer buffer,
                                  int sample_size, String format, int quality) {
        try {
            byte[] buff = new byte[buffer.remaining()];
            buffer.get(buff);
            BitmapFactory.Options ontain = new BitmapFactory.Options();
            ontain.inSampleSize = sample_size;
            Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length, ontain);
            saveBitmap(path, bitmap, format, quality);
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

    // 获得旋转角度之后的位图对象
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        // 创建操作图片用的矩阵对象
        Matrix matrix = new Matrix();
        // 执行图片的旋转动作
        matrix.postRotate(rotateDegree);
        // 创建并返回旋转后的位图对象
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                b.getHeight(), matrix, false);
    }

    // 获得图片的缓存路径
    public static String getCachePath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
    }

    // 按照新的宽高缩放位图对象
    public static Bitmap zoomImage(Bitmap origImage, double newWidth, double newHeight) {
        // 获取原始位图的宽度
        float width = origImage.getWidth();
        // 获取原始位图的高度
        float height = origImage.getHeight();
        // 创建操作图片用的矩阵对象
        Matrix matrix = new Matrix();
        // 计算宽度的缩放率
        float scaleWidth = ((float) newWidth) / width;
        // 计算高度的缩放率
        float scaleHeight = ((float) newHeight) / height;
        // 执行图片的缩放动作
        matrix.postScale(scaleWidth, scaleHeight);
        // 创建并返回缩放后的位图对象
        return Bitmap.createBitmap(origImage, 0, 0, (int) width, (int) height, matrix, true);
    }

}
