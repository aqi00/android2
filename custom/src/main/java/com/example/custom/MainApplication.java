package com.example.custom;

import com.example.custom.database.TrafficDBHelper;

import android.app.Application;
import android.util.Log;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class MainApplication extends Application {
    private final static String TAG = "MainApplication";
    // 声明一个当前应用的静态实例
    private static MainApplication mApp;
    // 声明一个公共的流量数据库帮助器
    public TrafficDBHelper mTrafficHelper;

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 在打开应用时对静态的应用实例赋值
        mApp = this;
        // 获得流量数据库帮助器的实例
        mTrafficHelper = TrafficDBHelper.getInstance(this, 1);
        // 打开流量数据库帮助器的写连接
        mTrafficHelper.openWriteLink();
        Log.d(TAG, "onCreate");
    }

}
