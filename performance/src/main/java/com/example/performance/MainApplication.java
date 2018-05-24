package com.example.performance;

import com.example.performance.util.DateUtil;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by ouyangshen on 2017/12/27.
 */
public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    // 声明一个当前应用的静态实例
    private static MainApplication mApp;
    private String mChange = "";

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    // 获取屏幕事件的文字描述
    public String getChangeDesc() {
        return mApp.mChange;
    }

    // 设置屏幕事件的文字描述
    public void setChangeDesc(String change) {
        mApp.mChange = mApp.mChange + change;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 在打开应用时对静态的应用实例赋值
        mApp = this;
        // 创建一个锁屏事件的广播接收器
        LockScreenReceiver lockReceiver = new LockScreenReceiver();
        // 创建一个意图过滤器
        IntentFilter filter = new IntentFilter();
        // 给意图过滤器添加亮屏事件
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 给意图过滤器添加熄屏事件
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 给意图过滤器添加用户解锁事件
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 注册广播接收器，注册之后才能正常接收广播
        registerReceiver(lockReceiver, filter);
    }

    // 定义一个锁屏事件的广播接收器
    private class LockScreenReceiver extends BroadcastReceiver {
        // 一旦接收到锁屏状态发生变化的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String change = "";
                change = String.format("%s\n%s : 收到广播：%s", change,
                        DateUtil.getNowTime(), intent.getAction());
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    // 接收到亮屏广播
                    change = String.format("%s\n这是屏幕点亮事件，可在此开启日常操作", change);
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    // 接收到熄屏广播
                    change = String.format("%s\n这是屏幕关闭事件，可在此暂停耗电操作", change);
                } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    // 接收到解锁广播
                    change = String.format("%s\n这是用户解锁事件", change);
                }
                Log.d(TAG, change);
                // 更新屏幕变化事件的文字描述
                MainApplication.getInstance().setChangeDesc(change);
            }
        }
    }

}
