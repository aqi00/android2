package com.example.test.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String encrypt(String raw) {
        String md5Str = raw;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // 创建一个MD5算法对象
            md.update(raw.getBytes()); // 给算法对象加载待加密的原始数据
            byte[] encryContext = md.digest(); // 调用digest方法完成哈希计算
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
                buf.append(Integer.toHexString(i)); // 把字节数组逐位转换为十六进制数
            }
            md5Str = buf.toString(); // 拼装加密字符串
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Str.toUpperCase(); // 输出大写的加密串
    }

}
