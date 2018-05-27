package com.example.network.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class ClientThread implements Runnable {
    private static final String TAG = "ClientThread";
    // 以下为Socket服务器的IP和端口，根据实际情况修改
    private static final String SOCKET_IP = "192.168.1.6";
    private static final int SOCKET_PORT = 52000;
    // 以下是HTTP接口调用的服务地址，根据实际情况修改IP和端口
    public static final String REQUEST_URL = "http://192.168.1.6:8080/NetServer";
    private Context mContext; // 声明一个上下文对象
    public Handler mSendHandler; // 声明一个发送处理器对象
    private BufferedReader mReader = null; // 声明一个缓存读取器对象
    private OutputStream mWriter = null; // 声明一个输出流对象
    // 以下定义了各个常量标识串
    public static String ACTION_RECV_MSG = "com.example.network.RECV_MSG";
    public static String ACTION_GET_LIST = "com.example.network.GET_LIST";
    public static String CONTENT = "CONTENT";
    public static String SPLIT_LINE = "|"; // 行分隔符
    public static String SPLIT_ITEM = ","; // 列分隔符
    public static String LOGIN = "LOGIN"; // 登录
    public static String LOGOUT = "LOGOUT"; // 注销
    public static String SENDMSG = "SENDMSG"; // 发送消息
    public static String RECVMSG = "RECVMSG"; // 接收消息
    public static String GETLIST = "GETLIST"; // 获取在线好友列表
    public static String SENDPHOTO = "SENDPHOTO"; // 发送图片
    public static String RECVPHOTO = "RECVPHOTO"; // 接收图片
    public static String SENDSOUND = "SENDSOUND"; // 发送音频
    public static String RECVSOUND = "RECVSOUND"; // 接收音频

    public ClientThread(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        // 创建一个套接字对象
        Socket socket = new Socket();
        try {
            Log.d(TAG, "connect");
            // 命令套接字连接指定地址的指定端口
            socket.connect(new InetSocketAddress(SOCKET_IP, SOCKET_PORT), 3000);
            // 根据套接字的输入流，构建缓存读取器
            mReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d(TAG, "getOutputStream");
            // 获得套接字的输出流
            mWriter = socket.getOutputStream();
            Log.d(TAG, "RecvThread");
            // 启动一条子线程来读取服务器相应的数据
            new RecvThread().start();
            // 为当前线程初始化消息队列
            Looper.prepare();
            // 创建一个发送处理器对象，让App向后台服务器发送消息
            // 如果是在Application中启动线程，则mRecvHandler要在线程启动后才能初始化
            // 并且要在Looper.prepare之后执行初始化动作
            mSendHandler = new Handler() {
                // 接收到主线程的用户输入数据
                public void handleMessage(Message msg) {
                    try {
                        // 往输出流对象中写入数据
                        mWriter.write(msg.obj.toString().getBytes("utf8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            // 让线程的消息队列开始运行，之后就可以接收消息了
            Looper.loop();
        } catch (Exception e) {
            e.printStackTrace();
            notify(99, e.getMessage());
        }
    }

    // 定义消息接收子线程，让App从后台服务器接收消息
    private class RecvThread extends Thread {
        @Override
        public void run() {
            String content;
            try {
                // 读取到来自服务器的数据
                while ((content = mReader.readLine()) != null) {
                    // 发送正常的消息通知
                    ClientThread.this.notify(0, content);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ClientThread.this.notify(97, e.getMessage());
            }
        }
    }

    private void notify(int type, String message) {
        if (type == 99) { // 连接异常
            // 以下发送连接异常的广播
            String content = String.format("%s%s%s%s", "ERROR", SPLIT_ITEM, SPLIT_LINE, message);
            Intent intent1 = new Intent(ACTION_RECV_MSG);
            intent1.putExtra(CONTENT, content);
            mContext.sendBroadcast(intent1);
            Intent intent2 = new Intent(ACTION_GET_LIST);
            intent2.putExtra(CONTENT, content);
            mContext.sendBroadcast(intent2);
        } else { // 正常消息
            int pos = message.indexOf(SPLIT_LINE);
            String head = message.substring(0, pos - 1);
            String[] splitArray = head.split(SPLIT_ITEM);
            String action = "";
            if (splitArray[0].equals(RECVMSG) // 接收到聊天消息（含文本消息、图片消息、音频消息）
                    || splitArray[0].equals(RECVPHOTO)
                    || splitArray[0].equals(RECVSOUND)) {
                action = ACTION_RECV_MSG;
            } else if (splitArray[0].equals(GETLIST)) { // 获得在线好友列表
                action = ACTION_GET_LIST;
            }
            Log.d(TAG, "action=" + action + ", message=" + message);
            // 以下发送消息内容的广播
            Intent intent = new Intent(action);
            intent.putExtra(CONTENT, message);
            mContext.sendBroadcast(intent);
        }
    }

}
