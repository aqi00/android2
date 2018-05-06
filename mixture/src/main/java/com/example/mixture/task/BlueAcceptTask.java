package com.example.mixture.task;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mixture.util.BluetoothConnector;
import com.example.mixture.util.BluetoothUtil;

// 蓝牙服务端开启侦听任务，一旦有客户端连接进来，就返回该客户端的蓝牙Socket
public class BlueAcceptTask extends AsyncTask<Void, Void, BluetoothSocket> {
    private final static String TAG = "BlueAcceptTask";
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static BluetoothServerSocket mServerSocket; // 声明一个蓝牙服务端套接字对象

    public BlueAcceptTask(boolean secure) {
        Log.d(TAG, "BlueAcceptTask");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // 以下提供了三种侦听方法，使得在不同情况下都能获得服务端的Socket对象
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
            if (secure) { // 安全连接
                mServerSocket = adapter.listenUsingRfcommWithServiceRecord(
                        NAME_SECURE, BluetoothConnector.uuid);
            } else { // 不安全连接
                mServerSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(
                        NAME_INSECURE, BluetoothConnector.uuid);
            }
        } catch (Exception e) { // 遇到异常则尝试第三种侦听方式
            e.printStackTrace();
            mServerSocket = BluetoothUtil.listenServer(adapter);
        }
    }

    // 线程正在后台处理
    protected BluetoothSocket doInBackground(Void... params) {
        Log.d(TAG, "doInBackground");
        BluetoothSocket socket = null;
        while (true) {
            try {
                // 如果accept方法有返回，则表示某部设备过来打招呼了
                socket = mServerSocket.accept();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if (socket != null) { // socket非空，表示名花有主了，赶紧带去见公婆
                break;
            }
        }
        return socket; // 返回侦听到的客户端Socket实例
    }

    // 线程已经完成处理
    protected void onPostExecute(BluetoothSocket socket) {
        // 侦听结束，通知监听器是哪个客户端Socket连了进来
        mListener.onBlueAccept(socket);
    }

    private BlueAcceptListener mListener; // 声明一个蓝牙侦听的监听器对象
    // 提供给外部设置蓝牙侦听监听器
    public void setBlueAcceptListener(BlueAcceptListener listener) {
        mListener = listener;
    }

    // 定义一个蓝牙侦听的监听器接口，用于在倾听响应之后回调onBlueAccept方法
    public interface BlueAcceptListener {
        void onBlueAccept(BluetoothSocket socket);
    }

}
