package com.example.test.encrypt.tool;

public class ConvertBytesToBase64 {
    // 下面的代码转换byte数组到 Base64
    private static char[] CharCode = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '+', '/'};

    private static String ConvertByte3ToString(byte[] bytes) {
        String temp = new String("");
        temp += CharCode[((bytes[0]) >>> 2) & 0x3f];
        temp += CharCode[((bytes[0] & 0x03) << 4) | ((bytes[1] >>> 4) & 0x0f)];
        temp += CharCode[((bytes[1] & 0x0f) << 2) | ((bytes[2] >>> 6) & 0x03)];
        temp += CharCode[bytes[2] & 0x3f];
        return temp;
    }

    private static String ConvertByte2ToString(byte[] bytes) {
        String temp = new String("");
        temp += CharCode[((bytes[0]) >>> 2) & 0x3f];
        temp += CharCode[((bytes[0] & 0x03) << 4) | ((bytes[1] >>> 4) & 0x0f)];
        temp += CharCode[((bytes[1] & 0x0f) << 2)];
        temp += '=';
        return temp;
    }

    private static String ConvertByteToString(byte[] bytes) {
        String temp = new String("");
        temp += CharCode[((bytes[0]) >>> 2) & 0x3f];
        temp += CharCode[((bytes[0] & 0x03) << 4)];
        temp += "==";
        return temp;
    }

    public static String BytesToBase64String(byte[] buffer) {
        String reslut = new String("");
        int length = buffer.length;
        int index = 0;
        for (index = 0; (index + 3) < length; index += 3) {
            byte[] temp = {buffer[index], buffer[index + 1], buffer[index + 2]};
            reslut += ConvertByte3ToString(temp);
        }
        if (length % 3 == 1) {
            byte[] temp = {buffer[length - 1]};
            reslut += ConvertByteToString(temp);
        } else if (length % 3 == 2) {
            byte[] temp = {buffer[length - 2], buffer[length - 1]};
            reslut += ConvertByte2ToString(temp);
        }
        return reslut;
    }
}
