package com.example.senior.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Locale;

public class FileUtil {

    // 把字符串保存到指定路径的文本文件
    public static void saveText(String path, String txt) {
        try {
            // 根据指定文件路径构建文件输出流对象
            FileOutputStream fos = new FileOutputStream(path);
            // 把字符串写入文件输出流
            fos.write(txt.getBytes());
            // 关闭文件输出流
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从指定路径的文本文件中读取内容字符串
    public static String openText(String path) {
        String readStr = "";
        try {
            // 根据指定文件路径构建文件输入流对象
            FileInputStream fis = new FileInputStream(path);
            byte[] b = new byte[fis.available()];
            // 从文件输入流读取字节数组
            fis.read(b);
            // 把字节数组转换为字符串
            readStr = new String(b);
            // 关闭文件输入流
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回文本文件中的文本字符串
        return readStr;
    }

    // 把位图数据保存到指定路径的图片文件
    public static void saveImage(String path, Bitmap bitmap) {
        try {
            // 根据指定文件路径构建缓存输出流对象
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
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
    public static Bitmap openImage(String path) {
        Bitmap bitmap = null;
        try {
            // 根据指定文件路径构建缓存输入流对象
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            // 从缓存输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close(); // 关闭缓存输入流
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回图片文件中的位图数据
        return bitmap;
    }

    public static ArrayList<File> getFileList(String path, String[] extendArray) {
        ArrayList<File> displayedContent = new ArrayList<File>();
        File[] files = null;
        File directory = new File(path);
        if (extendArray != null && extendArray.length > 0) {
            FilenameFilter fileFilter = getTypeFilter(extendArray);
            files = directory.listFiles(fileFilter);
        } else {
            files = directory.listFiles();
        }

        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory() && !f.isHidden()) {
                    displayedContent.add(f);
                }
            }
        }
        return displayedContent;
    }

    public static FilenameFilter getTypeFilter(String[] extendArray) {
        final ArrayList<String> fileExtensions = new ArrayList<String>();
        for (int i = 0; i < extendArray.length; i++) {
            fileExtensions.add(extendArray[i]);
        }
        FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File directory, String fileName) {
                boolean matched = false;
                File f = new File(String.format("%s/%s",
                        directory.getAbsolutePath(), fileName));
                matched = f.isDirectory();
                if (!matched) {
                    for (String s : fileExtensions) {
                        s = String.format(".{0,}\\%s$", s);
                        s = s.toUpperCase(Locale.getDefault());
                        fileName = fileName.toUpperCase(Locale.getDefault());
                        matched = fileName.matches(s);
                        if (matched) {
                            break;
                        }
                    }
                }
                return matched;
            }
        };
        return fileNameFilter;
    }

}
