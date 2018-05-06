package com.example.media.http.tool;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class StreamTool {
    private static final String TAG = "StreamTool";

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public static String getUnzipStream(InputStream is, String content_encoding, String charset) {
        String resp_content = "";
        GZIPInputStream gzin = null;
        if (content_encoding != null && !content_encoding.equals("")) {
            if (content_encoding.contains("gzip")) {
                try {
                    Log.d(TAG, "content_encoding=" + content_encoding);
                    gzin = new GZIPInputStream(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (gzin == null) {
                resp_content = new String(readInputStream(is), charset);
            } else {
                resp_content = new String(readInputStream(gzin), charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp_content;
    }

}
