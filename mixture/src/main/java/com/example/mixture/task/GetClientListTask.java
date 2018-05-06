package com.example.mixture.task;

import java.util.ArrayList;

import com.example.mixture.bean.ClientScanResult;
import com.example.mixture.util.WifiUtil;

import android.os.AsyncTask;

public class GetClientListTask extends AsyncTask<Void, Void, ArrayList<ClientScanResult>> {

    // 线程正在后台处理
    protected ArrayList<ClientScanResult> doInBackground(Void... params) {
        // 因为检查设备的连通性需要访问网络，所以获得客户端队列的操作必须在分线程中完成
        return WifiUtil.getClientList(true);
    }

    // 线程已经完成处理
    protected void onPostExecute(ArrayList<ClientScanResult> clientList) {
        mListener.onGetClient(clientList);
    }

    private GetClientListener mListener; // 声明一个获得客户端的监听器对象
    // 设置获得客户端的监听器
    public void setGetClientListener(GetClientListener listener) {
        mListener = listener;
    }

    // 定义一个获得客户端的监听器接口
    public interface GetClientListener {
        void onGetClient(ArrayList<ClientScanResult> clientList);
    }

}
