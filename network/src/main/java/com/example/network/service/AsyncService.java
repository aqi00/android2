package com.example.network.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class AsyncService extends IntentService {
    private static final String TAG = "AsyncService";

    public AsyncService() {
        super("com.example.network.service.AsyncService");
    }

    // onStartCommand运行于主线程
    public int onStartCommand(Intent intent, int flags, int startid) {
        Log.i(TAG, "onStartCommand");
        // 试试在onStartCommand里面沉睡，页面按钮是不是无法点击了？
//		try {
//			Thread.sleep(30*1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
        return super.onStartCommand(intent, flags, startid);
    }

    // onHandleIntent运行分主线程
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "begin onHandleIntent");
        // 在onHandleIntent这里执行耗时任务，不会影响页面的处理
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "end onHandleIntent");
    }

}
