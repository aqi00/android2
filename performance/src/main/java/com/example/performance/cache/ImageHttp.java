package com.example.performance.cache;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageHttp {

    // 设置http连接的头部信息
    private static void setConnHeader(HttpURLConnection conn) throws ProtocolException {
        // 设置请求方式，常见的有GET和POST两种
        conn.setRequestMethod("GET");
        // 设置连接超时时间
        conn.setConnectTimeout(5000);
        // 设置读写超时时间
        conn.setReadTimeout(10000);
        // 设置数据格式
        conn.setRequestProperty("Accept", "*/*");
        // IE使用
//		conn.setRequestProperty("Accept-Language", "zh-CN");
//		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C)");
        // firefox使用
        // 设置文本语言
        conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        // 设置用户代理
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
        // 设置编码格式
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
    }

    // get图片数据
    public static Bitmap getImage(String uri) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(uri);
            // 创建指定网络地址的HTTP连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setConnHeader(conn);
            conn.connect(); // 开始连接
            // 从HTTP连接获取输入流
            InputStream is = conn.getInputStream();
            // 对输入流中的数据进行解码，得到位图对象
            bitmap = BitmapFactory.decodeStream(is);
            conn.disconnect(); // 断开连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
