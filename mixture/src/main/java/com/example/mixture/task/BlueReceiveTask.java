package com.example.mixture.task;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

// 服务端开启的数据接收线程
public class BlueReceiveTask extends Thread {
    private static final String TAG = "BlueReceiveTask";
    private BluetoothSocket mSocket; // 声明一个蓝牙套接字对象
    private Handler mHandler; // 声明一个处理器对象

    public BlueReceiveTask(BluetoothSocket socket, Handler handler) {
        mSocket = socket;
        mHandler = handler;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                // 从蓝牙Socket获得输入流，并从中读取输入数据
                bytes = mSocket.getInputStream().read(buffer);
                Log.d(TAG, "BlueReceiveTask read");
                // 将读到的数据通过处理器送回给UI主线程处理
                mHandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
