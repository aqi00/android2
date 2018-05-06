package com.example.media.service;

import com.example.media.MainApplication;
import com.example.media.R;
import com.example.media.http.tool.HttpReqData;
import com.example.media.http.tool.HttpRespData;
import com.example.media.http.HttpRequestUtil;
import com.example.media.widget.FloatWindow;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class StockService extends Service {
    private final static String TAG = "StockService";
    private FloatWindow mFloatWindow; // 声明一个悬浮窗对象
    private TextView tv_sh_stock, tv_sz_stock;
    public static int OPEN = 0; // 打开悬浮窗
    public static int CLOSE = 1; // 关闭悬浮窗
    private static int SHANGHAI = 0; // 上证综指
    private static int SHENZHEN = 1; // 深圳成指
    private final int delayTime = 5000; // 刷新的间隔时间

    // 创建一个处理器对象
    private Handler mHandler = new Handler() {
        // 在收到消息时触发
        public void handleMessage(Message msg) {
            // 上证指数,3019.9873,-5.6932,-0.19,1348069,14969598
            String desc = (String) msg.obj;
            String[] array = desc.split(",");
            String stock = array[1]; // 当前指数
            float distance = Float.parseFloat(array[2]); // 与上一交易日的指数差额
            String range = array[3]; // 涨跌百分比
            String text = String.format("%s  %s%%", stock, range);
            int type = msg.what;
            if (type == SHANGHAI) { // 上证综指
                tv_sh_stock.setText(text);
                if (distance > 0) { // 股指上涨，标红
                    tv_sh_stock.setTextColor(Color.RED);
                } else { // 股指下跌，标绿
                    tv_sh_stock.setTextColor(Color.GREEN);
                }
            } else if (type == SHENZHEN) { // 深圳成指
                tv_sz_stock.setText(text);
                if (distance > 0) { // 股指上涨，标红
                    tv_sz_stock.setTextColor(Color.RED);
                } else { // 股指下跌，标绿
                    tv_sz_stock.setTextColor(Color.GREEN);
                }
            }
        }
    };

    // 定义一个股指刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (mFloatWindow != null && mFloatWindow.isShow()) {
                new StockThread(SHANGHAI).start(); // 启动上证综指获取线程
                new StockThread(SHENZHEN).start(); // 启动深圳成指获取线程
            }
            // 延迟若干秒后再次启动股指刷新任务
            mHandler.postDelayed(this, delayTime);
        }
    };

    // 定义一个股市指数的获取线程
    private class StockThread extends Thread {
        private int mType;

        public StockThread(int type) {
            mType = type;
        }

        @Override
        public void run() {
            // 创建一个HTTP请求对象
            HttpReqData req_data = new HttpReqData();
            if (mType == SHANGHAI) { // 上证综指
                req_data.url = "http://hq.sinajs.cn/list=s_sh000001";
            } else if (mType == SHENZHEN) { // 深圳成指
                req_data.url = "http://hq.sinajs.cn/list=s_sz399001";
            }
            // 发送HTTP请求信息，并获得HTTP应答对象
            HttpRespData resp_data = HttpRequestUtil.getData(req_data);
            // 返回串形如 var hq_str_s_sh000001="上证指数,3019.9873,-5.6932,-0.19,1348069,14969598";
            String desc = resp_data.content;
            Message msg = Message.obtain(); // 获得一个默认的消息对象
            msg.what = mType; // 消息类型
            msg.obj = desc.substring(desc.indexOf("\"") + 1, desc.lastIndexOf("\"")); // 消息描述
            mHandler.sendMessage(msg); // 向处理器发送消息
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mFloatWindow == null) {
            // 创建一个新的悬浮窗
            mFloatWindow = new FloatWindow(MainApplication.getInstance());
            // 设置悬浮窗的布局内容
            mFloatWindow.setLayout(R.layout.float_stock);
            // 从布局文件中获取展示上证综指的文本视图
            tv_sh_stock = mFloatWindow.mContentView.findViewById(R.id.tv_sh_stock);
            // 从布局文件中获取展示深圳成指的文本视图
            tv_sz_stock = mFloatWindow.mContentView.findViewById(R.id.tv_sz_stock);
        }
        // 立即启动股指刷新任务
        mHandler.post(mRefresh);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // 从意图中解包获得操作类型
            int type = intent.getIntExtra("type", OPEN);
            if (type == OPEN) { // 打开
                if (mFloatWindow != null && !mFloatWindow.isShow()) {
                    tv_sh_stock.setText("正在努力加载股指信息");
                    mFloatWindow.show(); // 显示悬浮窗
                }
            } else if (type == CLOSE) { // 关闭
                if (mFloatWindow != null && mFloatWindow.isShow()) {
                    mFloatWindow.close(); // 关闭悬浮窗
                }
                stopSelf(); // 停止自身服务
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 移除股指刷新任务
        mHandler.removeCallbacks(mRefresh);
    }

}
