package com.example.media.service;

import com.example.media.MainApplication;
import com.example.media.R;
import com.example.media.util.FlowUtil;
import com.example.media.widget.FloatWindow;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

@SuppressLint("SetTextI18n")
public class TrafficService extends Service {
    private final static String TAG = "TrafficService";
    private FloatWindow mFloatWindow; // 声明一个悬浮窗对象
    private TextView tv_traffic;
    public static int OPEN = 0; // 打开悬浮窗
    public static int CLOSE = 1; // 关闭悬浮窗
    private long curRx; // 当前接收的流量
    private long curTx; // 当前发送的流量
    private final int delayTime = 2000; // 刷新的间隔时间

    // 创建一个处理器对象
    private Handler mHandler = new Handler();
    // 定义一个流量刷新任务
    private Runnable mRefresh = new Runnable() {
        public void run() {
            if (mFloatWindow != null && mFloatWindow.isShow() &&
                    (TrafficStats.getTotalRxBytes() > curRx || TrafficStats.getTotalTxBytes() > curTx)) {
                // 平均一下接收的流量和发送的流量
                long flow = ((TrafficStats.getTotalRxBytes() - curRx) + (TrafficStats
                        .getTotalTxBytes() - curTx)) / 2;
                String desc = String.format("即时流量: %s/S", FlowUtil.BToShowString(flow, 0));
                tv_traffic.setText(desc);
                // 获取接收流量的总字节数
                curRx = TrafficStats.getTotalRxBytes();
                // 获取发送流量的总字节数
                curTx = TrafficStats.getTotalTxBytes();
            }
            // 延迟若干秒后再次启动流量刷新任务
            mHandler.postDelayed(this, delayTime);
        }
    };

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
            mFloatWindow.setLayout(R.layout.float_traffic);
            // 从布局文件中获取展示即时流量的文本视图
            tv_traffic = mFloatWindow.mContentView.findViewById(R.id.tv_traffic);
        }
        // 获取接收流量的总字节数
        curRx = TrafficStats.getTotalRxBytes();
        // 获取发送流量的总字节数
        curTx = TrafficStats.getTotalTxBytes();
        // 立即启动流量刷新任务
        mHandler.post(mRefresh);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // 从意图中解包获得操作类型
            int type = intent.getIntExtra("type", OPEN);
            if (type == OPEN) { // 打开
                if (mFloatWindow != null && !mFloatWindow.isShow()) {
                    tv_traffic.setText("即时流量: 0B/S");
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
