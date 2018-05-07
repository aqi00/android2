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
//        resp_data.content = "{\"package_list\":[{\"new_version\":\"8.12.5\",\"download_url\":\"http://www.lenovomm.com/appdown/21661264-2\",\"package_name\":\"com.qiyi.video\"},{\"new_version\":\"8.9.2\",\"download_url\":\"http://www.lenovomm.com/appdown/21589548-2\",\"package_name\":\"com.kugou.android\"},{\"new_version\":\"7.0.5.0\",\"download_url\":\"http://www.lenovomm.com/appdown/21665086-2\",\"package_name\":\"com.mt.mtxx.mtxx\"},{\"new_version\":\"6.6.1\",\"download_url\":\"http://www.lenovomm.com/appdown/21665350-2\",\"package_name\":\"com.tencent.mm\"},{\"new_version\":\"7.4.0\",\"download_url\":\"http://www.lenovomm.com/appdown/21672339-2\",\"package_name\":\"com.taobao.taobao\"},{\"new_version\":\"7.3.2\",\"download_url\":\"http://www.lenovomm.com/appdown/21639509-2\",\"package_name\":\"com.tencent.mobileqq\"}]}";
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
