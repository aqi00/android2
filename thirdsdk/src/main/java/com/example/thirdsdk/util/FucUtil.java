package com.example.thirdsdk.util;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import android.content.Context;

/**
 * 功能性函数扩展类
 */
public class FucUtil {
    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取语记是否包含离线听写资源，如未包含跳转至资源下载页面
     * 1.PLUS_LOCAL_ALL: 本地所有资源
     * 2.PLUS_LOCAL_ASR: 本地识别资源
     * 3.PLUS_LOCAL_TTS: 本地合成资源
     */
    public static String checkLocalResource() {
        String resource = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_ASR);
        try {
            JSONObject result = new JSONObject(resource);
            int ret = result.getInt(SpeechUtility.TAG_RESOURCE_RET);
            switch (ret) {
                case ErrorCode.SUCCESS:
                    JSONArray asrArray = result.getJSONObject("result").optJSONArray("asr");
                    if (asrArray != null) {
                        int i = 0;
                        // 查询否包含离线听写资源
                        for (; i < asrArray.length(); i++) {
                            if ("iat".equals(asrArray.getJSONObject(i).get(SpeechConstant.DOMAIN))) {
                                //asrArray中包含语言、方言字段，后续会增加支持方言的本地听写。
                                //如："accent": "mandarin","language": "zh_cn"
                                break;
                            }
                        }
                        if (i >= asrArray.length()) {

                            SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                            return "没有听写资源，跳转至资源下载页面";
                        }
                    } else {
                        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                        return "没有听写资源，跳转至资源下载页面";
                    }
                    break;
                case ErrorCode.ERROR_VERSION_LOWER:
                    return "语记版本过低，请更新后使用本地功能";
                case ErrorCode.ERROR_INVALID_RESULT:
                    SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
                    return "获取结果出错，跳转至资源下载页面";
                case ErrorCode.ERROR_SYSTEM_PREINSTALL:
                    //语记为厂商预置版本。
                default:
                    break;
            }
        } catch (Exception e) {
            SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
            return "获取结果出错，跳转至资源下载页面";
        }
        return "";
    }

    /**
     * 读取asset目录下音频文件。
     *
     * @return 二进制文件数据
     */
    public static byte[] readAudioFile(Context context, String filename) {
        try {
            InputStream ins = context.getAssets().open(filename);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return data;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
