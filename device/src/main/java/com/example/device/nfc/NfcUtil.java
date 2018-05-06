package com.example.device.nfc;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public final class NfcUtil {
    private final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static int toInt(byte[] b, int s, int n) {
        int ret = 0;
        final int e = s + n;
        for (int i = s; i < e; ++i) {
            ret <<= 8;
            ret |= b[i] & 0xFF;
        }
        return ret;
    }

    public static String toHexString(byte[] d, int s, int n) {
        final char[] ret = new char[n * 2];
        final int e = s + n;
        int x = 0;
        for (int i = s; i < e; ++i) {
            final byte v = d[i];
            ret[x++] = HEX[0x0F & (v >> 4)];
            ret[x++] = HEX[0x0F & v];
        }
        return new String(ret);
    }

    public static String toAmountString(float value) {
        return String.format("%.2f", value);
    }

}
