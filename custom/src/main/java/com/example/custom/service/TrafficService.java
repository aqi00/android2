package com.example.custom.service;

import java.util.ArrayList;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.custom.MainActivity;
import com.example.custom.MainApplication;
import com.example.custom.R;
import com.example.custom.bean.AppInfo;
import com.example.custom.util.AppUtil;
import com.example.custom.util.DateUtil;
import com.example.custom.util.SharedUtil;
import com.example.custom.util.StringUtil;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class TrafficService extends Service {
    private static final String TAG = "TrafficService";
    private MainApplication app; // 声明一个应用对象
    private int limit_day; // 日限额
    private int mNowDay; // 今日日期
    private Notification mNotify; // 声明一个通知对象

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        // 获取当前应用的唯一实例
        app = MainApplication.getInstance();
        // 从共享参数中获取日限额数值
        limit_day = SharedUtil.getIntance(this).readInt("limit_day", 50);
        // 立即启动流量刷新任务
        mHandler.post(mRefresh);
        return START_STICKY;
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个流量刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 更新流量数据库
            refreshData();
            // 刷新流量通知栏
            refreshNotify();
            // 延迟10秒后再次启动流量刷新任务
            mHandler.postDelayed(this, 10000);
        }
    };

    private void refreshData() {
        mNowDay = Integer.parseInt(DateUtil.getNowDateTime("yyyyMMdd"));
        // 获取最新的应用信息队列
        ArrayList<AppInfo> appinfoList = AppUtil.getAppInfo(this, 1);
        for (int i = 0; i < appinfoList.size(); i++) {
            AppInfo item = appinfoList.get(i);
            // 获取该应用最新的流量接收数据
            item.traffic = TrafficStats.getUidRxBytes(item.uid);
            item.month = mNowDay / 100;
            item.day = mNowDay;
            appinfoList.set(i, item);
        }
        // 往流量数据库插入最新的应用流量记录
        app.mTrafficHelper.insert(appinfoList);
    }

    private void refreshNotify() {
        String lastDate = DateUtil.getAddDate("" + mNowDay, -1);
        // 查询数据库获得截止到昨日的应用流量
        ArrayList<AppInfo> lastArray = app.mTrafficHelper.query("day=" + lastDate);
        // 查询数据库获得截止到今日的应用流量
        ArrayList<AppInfo> thisArray = app.mTrafficHelper.query("day=" + mNowDay);
        long traffic_day = 0;
        // 截止到今日的应用流量减去截止到昨日的应用流量，二者之差便是今日的流量数据
        for (int i = 0; i < thisArray.size(); i++) {
            AppInfo item = thisArray.get(i);
            for (int j = 0; j < lastArray.size(); j++) {
                if (item.uid == lastArray.get(j).uid) {
                    item.traffic -= lastArray.get(j).traffic;
                    break;
                }
            }
            traffic_day += item.traffic;
        }
        String desc = "今日已用流量" + StringUtil.formatData(traffic_day);

        int progress;
        int layoutId = R.layout.notify_traffic_green;
        float trafficM = traffic_day / 1024.0f / 1024.0f;
        if (trafficM > limit_day * 2) { // 超出两倍限额，则展示红色进度条
            progress = (int) ((trafficM > limit_day * 3) ? 100 : (trafficM - limit_day * 2) * 100 / limit_day);
            layoutId = R.layout.notify_traffic_red;
        } else if (trafficM > limit_day) { // 超出一倍限额，则展示橙色进度条
            progress = (int) ((trafficM > limit_day * 2) ? 100 : (trafficM - limit_day) * 100 / limit_day);
            layoutId = R.layout.notify_traffic_yellow;
        } else { // 未超出限额，则展示绿色进度条
            progress = (int) (trafficM * 100 / limit_day);
        }
        Log.d(TAG, "progress=" + progress);
        // 显示流量通知
        showFlowNotify(layoutId, desc, progress);
    }

    private void showFlowNotify(int layoutId, String desc, int progress) {
        // 根据布局文件layoutId生成远程视图对象
        RemoteViews notify_traffic = new RemoteViews(this.getPackageName(), layoutId);
        // 设置远程视图内部的流量文字描述
        notify_traffic.setTextViewText(R.id.tv_flow, desc);
        // 设置远程视图内部的进度条属性
        notify_traffic.setProgressBar(R.id.pb_flow, 100, progress, false);
        // 创建一个跳转到活动页面的意图
        Intent intent = new Intent(this, MainActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent clickIntent = PendingIntent.getActivity(this,
                R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建一个通知消息的构造器
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(this, getString(R.string.app_name));
        }
        builder.setContentIntent(clickIntent) // 设置内容的点击意图
                .setContent(notify_traffic) // 设置内容视图
                .setTicker("手机安全助手运行中") // 设置状态栏里面的提示文本
                .setSmallIcon(R.drawable.ic_app); // 设置状态栏里的小图标
        // 根据消息构造器构建一个通知对象
        mNotify = builder.build();
        // 把服务推送到前台的通知栏
        startForeground(9, mNotify);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNotify != null) {
            // 停止前台展示，也就是清除通知栏的流量消息
            stopForeground(true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
