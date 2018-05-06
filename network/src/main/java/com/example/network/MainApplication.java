package com.example.network;

import com.example.network.thread.ClientThread;
import com.example.network.util.DateUtil;

import android.app.Application;
import android.os.Build;
import android.os.Message;
import android.util.Log;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    // 声明一个当前应用的静态实例
    private static MainApplication mApp;
    private String mNickName; // 当前用户的昵称
    private ClientThread mClientThread; // 客户端的聊天任务

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        // 在打开应用时对静态的应用实例赋值
        mApp = this;
        // 创建一个聊天任务
        mClientThread = new ClientThread(mApp);
        Log.d(TAG, "mClientThread start");
        // 根据聊天任务，创建并启动聊天线程
        new Thread(mClientThread).start();
    }

    // 发送聊天消息
    public void sendAction(String action, String otherId, String msgText) {
        // 拼接完整的聊天消息
        String content = String.format("%s,%s,%s,%s,%s%s%s\r\n",
                action, Build.SERIAL, getNickName(), DateUtil.getNowDateTime(""),
                otherId, ClientThread.SPLIT_LINE, msgText);
        Log.d(TAG, "sendAction : " + content);
        // 获得一个默认的消息对象
        Message msg = Message.obtain();
        msg.obj = content; // 消息描述
        if (mClientThread == null || mClientThread.mSendHandler == null) {
            Log.d(TAG, "mClientThread or its mSendHandler is null");
        } else {
            // 通过聊天线程的发送处理器，向后端发送消息
            mClientThread.mSendHandler.sendMessage(msg);
        }
    }

    // 获取设置用户的昵称
    public void setNickName(String nickName) {
        mApp.mNickName = nickName;
    }

    // 获取当前用户的昵称
    public String getNickName() {
        return mApp.mNickName;
    }

}
