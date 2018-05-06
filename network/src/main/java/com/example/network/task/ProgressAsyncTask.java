package com.example.network.task;

import android.os.AsyncTask;

// 模拟异步处理的线程
public class ProgressAsyncTask extends AsyncTask<String, Integer, String> {
    private String mBook; // 书籍名称

    public ProgressAsyncTask(String title) {
        super();
        mBook = title;
    }

    // 线程正在后台处理
    protected String doInBackground(String... params) {
        int ratio = 0;
        for (; ratio <= 100; ratio += 5) {
            try {
                Thread.sleep(200); // 睡眠200毫秒模拟网络通信处理
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 通报处理进展。调用该方法会触发onProgressUpdate函数
            publishProgress(ratio);
            // if (ratio >= 50) {
            // cancel(false);
            // }
        }
        return params[0]; // 返回参数是书籍的名称
    }

    // 准备启动线程
    protected void onPreExecute() {
        // 触发监听器的开始事件
        mListener.onBegin(mBook);
    }

    // 线程在通报处理进展
    protected void onProgressUpdate(Integer... values) {
        // 触发监听器的进度更新事件
        mListener.onUpdate(mBook, values[0], 0);
    }

    // 线程已经完成处理
    protected void onPostExecute(String result) {
        // 触发监听器的结束事件
        mListener.onFinish(result);
    }

    // 线程已经取消
    protected void onCancelled(String result) {
        // 触发监听器的取消事件
        mListener.onCancel(result);
    }

    private OnProgressListener mListener; // 声明一个进度更新的监听器对象
    // 设置进度更新的监听器
    public void setOnProgressListener(OnProgressListener listener) {
        mListener = listener;
    }

    // 定义一个进度更新的监听器接口
    public interface OnProgressListener {
        // 在线程处理结束时触发
        void onFinish(String result);
        // 在线程处理取消时触发
        void onCancel(String result);
        // 在线程处理过程中更新进度时触发
        void onUpdate(String request, int progress, int sub_progress);
        // 在线程处理开始时触发
        void onBegin(String request);
    }

}
