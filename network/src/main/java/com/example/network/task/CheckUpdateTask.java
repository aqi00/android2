package com.example.network.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.network.http.HttpRequestUtil;
import com.example.network.http.tool.HttpReqData;
import com.example.network.http.tool.HttpRespData;
import com.example.network.thread.ClientThread;

/**
 * Created by ouyangshen on 2018/1/12.
 */
// 检查应用更新的线程
public class CheckUpdateTask extends AsyncTask<String, Void, String> {
    private final static String TAG = "CheckUpdateTask";
    private String mQueryUrl = ClientThread.REQUEST_URL + "/checkUpdate"; // 检查应用更新的服务地址

    public CheckUpdateTask() {
        super();
    }

    // 线程正在后台处理
    protected String doInBackground(String... params) {
        Log.d(TAG, "query url=" + mQueryUrl);
        Log.d(TAG, "query params=" + params[0]);
        // 创建一个HTTP请求对象
        HttpReqData req_data = new HttpReqData(mQueryUrl);
        req_data.params.append(params[0]);
        // 发送HTTP请求信息，并获得HTTP应答对象
        HttpRespData resp_data = HttpRequestUtil.postData(req_data);
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        HttpRespData resp_data = new HttpRespData();
//        resp_data.content = "{\"package_list\":[{\"new_version\":\"13.7.5\",\"download_url\":\"https://imtt.dd.qq.com/sjy.10001/sjy.00004/16891/apk/F1E6E8850F6F80BD071AE8E319C51759.apk\",\"package_name\":\"com.qiyi.video\"},{\"new_version\":\"11.2.6\",\"download_url\":\"https://imtt.dd.qq.com/sjy.10001/sjy.00004/16891/apk/52C4F042C7AC91FE1D97EE8699E0191C.apk\",\"package_name\":\"com.kugou.android\"},{\"new_version\":\"9.6.2.0\",\"download_url\":\"https://imtt.dd.qq.com/sjy.10001/sjy.00004/16891/apk/FAE5E860689E67BAEE7038261614B64E.apk\",\"package_name\":\"com.mt.mtxx.mtxx\"},{\"new_version\":\"8.0.25\",\"download_url\":\"https://imtt.dd.qq.com/sjy.10001/sjy.00004/16891/apk/5F93793C6C5F539487B11418A5D4C902.apk\",\"package_name\":\"com.tencent.mm\"},{\"new_version\":\"10.15.0\",\"download_url\":\"https://imtt.dd.qq.com/sjy.10001/sjy.00004/16891/apk/99CA4F060773B985797B9EFA0DA0A632.apk\",\"package_name\":\"com.taobao.taobao\"},{\"new_version\":\"8.9.3\",\"download_url\":\"https://imtt.dd.qq.com/sjy.10001/sjy.00004/16891/apk/995F74DF2D12C2325158455865E4CC4C.apk\",\"package_name\":\"com.tencent.mobileqq\"}]}";
        Log.d(TAG, "err_msg=" + resp_data.err_msg + ", result=" + resp_data.content);
        return resp_data.content; // 返回HTTP调用的应答内容
    }

    // 线程已经完成处理
    protected void onPostExecute(String resp) {
        // HTTP调用完毕，触发监听器的结束检查事件
        mListener.finishCheckUpdate(resp);
    }

    private OnCheckUpdateListener mListener; // 声明一个结束更新检查的监听器对象
    // 设置结束更新检查的监听器
    public void setOnCheckUpdateListener(OnCheckUpdateListener listener) {
        mListener = listener;
    }

    // 定义一个结束更新检查的监听器接口
    public interface OnCheckUpdateListener {
        void finishCheckUpdate(String resp);
    }

}
