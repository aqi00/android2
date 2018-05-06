package com.example.media.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.media.bean.LrcContent;
import com.example.media.util.MediaUtil;

import android.util.Log;

public class LyricsLoader {
    private static final String TAG = "LyricsLoader";
    private static ArrayList<LrcContent> mLrcList = new ArrayList<LrcContent>(); // 歌词队列
    private static LyricsLoader mLoader; // 声明一个歌词加载器对象

    // 利用单例模式获取歌词加载器的唯一实例
    public static LyricsLoader getInstance(String path) {
        if (mLoader == null) {
            mLoader = new LyricsLoader();
        }
        readLRC(path);
        return mLoader;
    }

    // 从歌词文件中读取歌词内容
    private static void readLRC(String path) {
        mLrcList.clear();
        int offset = 0;
        String extendName = "." + MediaUtil.getExtendName(path);
        // 音频文件的同目录下应当存在扩展名为lrc的同名歌词文件
        File f = new File(path.replace(extendName, ".lrc"));
        try {
            // 创建一个文件输入流对象
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String s;
            while ((s = br.readLine()) != null) {
                // 处理每行开头标记用的方括号
                s = s.replace("[", "");
                s = s.replace("]", "@");
                String[] splitData = s.split("@");
                // 获取歌词的整体偏移时间
                if (splitData.length > 0 && splitData[0].contains("offset")) {
                    String[] splitOffset = splitData[0].split(":");
                    if (splitOffset.length > 1) {
                        offset = Integer.parseInt(splitOffset[1]);
                        Log.d(TAG, "offset=" + offset);
                    }
                }
                if (s.indexOf("0") != 0) { // 不是歌词行，则直接跳过
                    continue;
                }
                // 下面分析每行的歌词信息，并添加至歌词队列
                for (int i = 0; i < splitData.length - 1; i++) {
                    LrcContent lrcContent = new LrcContent();
                    int lrcTime = time2Str(splitData[i]) + offset;
                    lrcContent.setLrcTime(lrcTime);
                    lrcContent.setLrcStr(splitData[splitData.length - 1]);
                    mLrcList.add(lrcContent);
                }
            }
            // 将歌词队列按照开始时间重新升序排列
            Collections.sort(mLrcList, new Comparator<LrcContent>() {
                @Override
                public int compare(LrcContent o1, LrcContent o2) {
                    return (o1.getLrcTime() > o2.getLrcTime()) ? 1 : -1;
                }
            });
            // 把解析后的歌词队列打印到日志中
            for (LrcContent item : mLrcList) {
                Log.d(TAG,  "time=" + item.getLrcTime() + ",str=" + item.getLrcStr());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将字符串时间转换为整型数时间，单位毫秒
    private static int time2Str(String timeStr) {
        timeStr = timeStr.replace(":", ".");
        timeStr = timeStr.replace(".", "@");
        // 将时间分隔成字符串数组
        String timeData[] = timeStr.split("@");
        // 分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);
        // 计算上一行与下一行的时间转换为毫秒数
        return (minute * 60 + second) * 1000 + millisecond * 10;
    }

    // 获取歌词队列
    public ArrayList<LrcContent> getLrcList() {
        return mLrcList;
    }
}
