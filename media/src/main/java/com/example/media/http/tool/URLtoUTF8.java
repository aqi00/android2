package com.example.media.http.tool;

import android.util.Log;

import java.util.Locale;

public class URLtoUTF8 {
    private static final String TAG = "URLtoUTF8";

    // 转换为%E4%BD%A0形式
    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase(Locale.getDefault()));
                }
            }
        }
        return sb.toString();
    }

    // 将%E4%BD%A0转换为汉字
    public static String unescape(String s) {
        StringBuffer sbuf = new StringBuffer();
        int l = s.length();
        int ch = -1;
        int b, sumb = 0;
        for (int i = 0, more = -1; i < l; i++) {
            switch (ch = s.charAt(i)) {
                case '%':
                    ch = s.charAt(++i);
                    int hb = (Character.isDigit((char) ch) ? ch - '0'
                            : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                    ch = s.charAt(++i);
                    int lb = (Character.isDigit((char) ch) ? ch - '0'
                            : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                    b = (hb << 4) | lb;
                    break;
                case '+':
                    b = ' ';
                    break;
                default:
                    b = ch;
            }
            if ((b & 0xc0) == 0x80) {
                sumb = (sumb << 6) | (b & 0x3f);
                if (--more == 0)
                    sbuf.append((char) sumb);
            } else if ((b & 0x80) == 0x00) {
                sbuf.append((char) b);
            } else if ((b & 0xe0) == 0xc0) {
                sumb = b & 0x1f;
                more = 1;
            } else if ((b & 0xf0) == 0xe0) {
                sumb = b & 0x0f;
                more = 2;
            } else if ((b & 0xf8) == 0xf0) {
                sumb = b & 0x07;
                more = 3;
            } else if ((b & 0xfc) == 0xf8) {
                sumb = b & 0x03;
                more = 4;
            } else {
                sumb = b & 0x01;
                more = 5;
            }
        }
        return sbuf.toString();
    }

}
