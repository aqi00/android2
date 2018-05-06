package com.example.mixture.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.mixture.EbookReaderActivity;
import com.example.mixture.bean.BookInfo;
import com.example.mixture.util.AssetsUtil;

import java.util.ArrayList;

/**
 * Created by ouyangshen on 2018/1/8.
 */
@SuppressLint("DefaultLocale")
public class CopyFileService extends IntentService {
    private static final String TAG = "CopyFileService";

    public CopyFileService() {
        super("com.example.mixture.service.CopyFileService");
    }

    // onStartCommand运行于主线程
    public int onStartCommand(Intent intent, int flags, int startid) {
        return super.onStartCommand(intent, flags, startid);
    }

    // onHandleIntent运行分主线程
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "begin onHandleIntent");
        ArrayList<String> pathArray = intent.getExtras().getStringArrayList("file_list");
        if (pathArray != null) {
            ArrayList<BookInfo> bookArray = new ArrayList<BookInfo>();
            for (String path : pathArray) {
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                // 把资产目录下的电子书复制到SD卡
                AssetsUtil.Assets2Sd(this, fileName, path);
                bookArray.add(new BookInfo(path));
            }
            // 把演示用的电子书信息添加到SQLite数据库
            EbookReaderActivity.helper.insert(bookArray);
            String desc = String.format("已成功复制%d个电子书文件", pathArray.size());
            Log.d(TAG, desc);
        }
        Log.d(TAG, "end onHandleIntent");
    }

}
