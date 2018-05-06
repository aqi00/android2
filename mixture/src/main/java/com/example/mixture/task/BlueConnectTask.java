package com.example.mixture.task;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mixture.util.BluetoothConnector;

// 输入对方设备的蓝牙设备对象BluetoothDevice，输出该设备的蓝牙套接字对象BluetoothSocket
public class BlueConnectTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
    private final static String TAG = "BlueConnectTask";
    private String mAddress; // 对方蓝牙设备的MAC地址

    public BlueConnectTask(String address) {
        mAddress = address;
    }

    // 线程正在后台处理
    protected BluetoothSocket doInBackground(BluetoothDevice... params) {
        // 创建一个对方设备的蓝牙连接器，params[0]为对方的蓝牙设备对象BluetoothDevice
        BluetoothConnector connector = new BluetoothConnector(params[0], true,
                BluetoothAdapter.getDefaultAdapter(), null);
        Log.d(TAG, "doInBackground");
        BluetoothSocket socket = null;
        // 蓝牙连接需要完整的权限,有些机型弹窗提示"***想进行通信",这就不行,日志会报错:
        // read failed, socket might closed or timeout, read ret: -1
        try {
            // 开始连接，并返回对方设备的蓝牙套接字对象BluetoothSocket
            socket = connector.connect().getUnderlyingSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket; // 返回对方设备的蓝牙套接字实例
    }

    // 线程已经完成处理
    protected void onPostExecute(BluetoothSocket socket) {
        // 连接完成，通知监听器该地址已具备蓝牙套接字
        mListener.onBlueConnect(mAddress, socket);
    }

    private BlueConnectListener mListener; // 声明一个蓝牙连接的监听器对象
    // 提供给外部设置蓝牙连接监听器
    public void setBlueConnectListener(BlueConnectListener listener) {
        mListener = listener;
    }

    // 定义一个蓝牙连接的监听器接口，用于在成功连接之后调用onBlueConnect方法
    public interface BlueConnectListener {
        void onBlueConnect(String address, BluetoothSocket socket);
    }

}
