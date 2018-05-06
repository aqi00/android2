package com.example.network.task;

import com.example.network.http.HttpUploadUtil;

import android.os.AsyncTask;
import android.util.Log;

// 上传文件的线程
public class UploadHttpTask extends AsyncTask<String, Void, String> {
    private final static String TAG = "UploadHttpTask";

    public UploadHttpTask() {
        super();
    }

    // 线程正在后台处理
    protected String doInBackground(String... params) {
        String uploadUrl = params[0]; // 第一个参数是文件上传的服务地址
        String filePath = params[1]; // 第二个参数是待上传的文件路径
        Log.d(TAG, "uploadUrl=" + uploadUrl + ", filePath=" + filePath);
        // 向服务地址上传指定文件
        String result = HttpUploadUtil.upload(uploadUrl, filePath);
        Log.d(TAG, "upload result=" + result);
        return result; // 返回文件上传的结果
    }

    // 线程已经完成处理
    protected void onPostExecute(String result) {
        // HTTP上传完毕，触发监听器的上传结束事件
        mListener.onUploadFinish(result);
    }

    private OnUploadHttpListener mListener; // 声明一个文件上传的监听器对象
    // 设置文件上传的监听器
    public void setOnUploadHttpListener(OnUploadHttpListener listener) {
        mListener = listener;
    }

    // 定义一个文件上传的监听器接口
    public interface OnUploadHttpListener {
        void onUploadFinish(String result);
    }

}
