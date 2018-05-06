package com.example.mixture.task;

import com.example.mixture.WifiShareActivity;

import android.os.AsyncTask;
import android.util.Log;

public class GetClientNameTask extends AsyncTask<String, Void, String> {
    private final static String TAG = "GetClientNameTask";

    // 线程正在后台处理
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground ip=" + params[0]);
        // 通过jni方式获取主机名。由于NetBIOS协议需要访问网络，因此必须在分线程中进行。
        String info = WifiShareActivity.nameFromJNI(params[0]);
        Log.d(TAG, "doInBackground info=" + info);
        return info;
    }

    // 线程已经完成处理
    protected void onPostExecute(String info) {
        mListener.onFindName(info);
    }

    private FindNameListener mListener;// 声明一个发现设备名称的监听器对象
    // 设置发现设备名称的监听器
    public void setFindNameListener(FindNameListener listener) {
        mListener = listener;
    }

    // 定义一个发现设备名称的监听器接口
    public interface FindNameListener {
        void onFindName(String info);
    }

}
