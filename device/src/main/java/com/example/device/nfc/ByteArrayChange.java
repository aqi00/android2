package com.example.device.nfc;

public class ByteArrayChange {

    //转换法1   格式为0xabcd1234  字母小写
    /* public static  String ByteArrayToHexString(byte[] src) {
	      StringBuilder stringBuilder = new StringBuilder("0x");
	      if (src == null || src.length <= 0) {
	          return null;
	      }
	      char[] buffer = new char[2];
	      for (int i = 0; i < src.length; i++) {
	          buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
	          buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
	          System.out.println(buffer);
	          stringBuilder.append(buffer);
	      }
	      return stringBuilder.toString();
	  }*/

    // 转换法2   格式为  ABCD1234 字母大写
    // 将Byte数组转换为16进制字符串
    public static String ByteArrayToHexString(byte[] bytesId) {
        int i, j, in;
        String[] hex = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        };
        String output = "";

        for (j = 0; j < bytesId.length; ++j) {
            in = bytesId[j] & 0xff;
            i = (in >> 4) & 0x0f;
            output += hex[i];
            i = in & 0x0f;
            output += hex[i];
        }
        return output;
    }
}
