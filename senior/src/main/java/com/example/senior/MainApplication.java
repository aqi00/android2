package com.example.senior;

import java.util.HashMap;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class MainApplication extends Application {
    private final static String TAG = "MainApplication";
    // 声明一个当前应用的静态实例
    private static MainApplication mApp;
    // 声明一个公共的图标映射对象，可当作全局变量使用
    public HashMap<Long, Bitmap> mIconMap = new HashMap<Long, Bitmap>();

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 在打开应用时对静态的应用实例赋值
        mApp = this;
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }


}
