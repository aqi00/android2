package com.example.test.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    // 首先初始化一个MessageDigest对象，该对象通过update方法获取原始数据，
    // 并调用digest方法完成哈希计算，再把字节数组逐位转换为十六进制数，最后拼装加密字符串。
    public static String encrypt(String raw) {
        String md5Str = raw;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(raw.getBytes());
            byte[] encryContext = md.digest();

            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < encryContext.length; offset++) {
                i = encryContext[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            md5Str = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Str.toUpperCase(); // 输出大写的加密串
    }

}
