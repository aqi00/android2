package com.example.custom.service;

import com.example.custom.MainActivity;
import com.example.custom.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class MusicService extends Service {
    private static final String TAG = "MusicService";
    // 创建一个粘合剂对象
    private final IBinder mBinder = new LocalBinder();
    private String mSong; // 歌曲名称
    private String PAUSE_EVENT = ""; // “暂停/继续”事件的标识串
    private boolean isPlaying = true; // 是否正在播放
    private long mBaseTime; // 基准时间
    private long mPauseTime = 0; // 暂停时间
    private int mProcess = 0; // 播放进度
    // 定义一个当前服务的粘合剂，用于将该服务黏到活动页面的进程中
    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // 返回该服务的粘合剂对象
        return mBinder;
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个音乐播放任务
    private Runnable mPlay = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) { // 正在播放，则刷新播放进度
                if (mProcess < 100) {
                    mProcess += 2;
                } else {
                    mProcess = 0;
                }
                mHandler.postDelayed(this, 1000);
            }
            // 获取自定义消息的通知对象
            Notification notify = getNotify(MusicService.this, PAUSE_EVENT, mSong, isPlaying, mProcess, mBaseTime);
            // 把服务推送到前台的通知栏
            startForeground(2, notify);
        }
    };

    private Notification getNotify(Context ctx, String event, String song, boolean isPlaying, int progress, long time) {
        // 创建一个广播事件的意图
        Intent intent1 = new Intent(event);
        // 创建一个用于广播的延迟意图
        PendingIntent broadIntent = PendingIntent.getBroadcast(
                ctx, R.string.app_name, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        // 根据布局文件notify_music.xml生成远程视图对象
        RemoteViews notify_music = new RemoteViews(ctx.getPackageName(), R.layout.notify_music);
        if (isPlaying) { // 正在播放
            notify_music.setTextViewText(R.id.btn_play, "暂停"); // 设置按钮文字
            notify_music.setTextViewText(R.id.tv_play, song + "正在播放"); // 设置文本文字
            notify_music.setChronometer(R.id.chr_play, time, "%s", true); // 设置计数器
        } else { // 不在播放
            notify_music.setTextViewText(R.id.btn_play, "继续"); // 设置按钮文字
            notify_music.setTextViewText(R.id.tv_play, song + "暂停播放"); // 设置文本文字
            notify_music.setChronometer(R.id.chr_play, time, "%s", false); // 设置计数器
        }
        // 设置远程视图内部的进度条属性
        notify_music.setProgressBar(R.id.pb_play, 100, progress, false);
        // 整个通知已经有点击意图了，那要如何给单个控件添加点击事件？
        // 办法是设置控件点击的广播意图，一旦点击该控件，就发出对应事件的广播。
        notify_music.setOnClickPendingIntent(R.id.btn_play, broadIntent);
        // 创建一个跳转到活动页面的意图
        Intent intent2 = new Intent(ctx, MainActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent clickIntent = PendingIntent.getActivity(ctx,
                R.string.app_name, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建一个通知消息的构造器
        Notification.Builder builder = new Notification.Builder(ctx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(ctx, getString(R.string.app_name));
        }
        builder.setContentIntent(clickIntent) // 设置内容的点击意图
                .setContent(notify_music) // 设置内容视图
                .setTicker(song) // 设置状态栏里面的提示文本
                .setSmallIcon(R.drawable.tt_s); // 设置状态栏里的小图标
        // 根据消息构造器构建一个通知对象
        Notification notify = builder.build();
        return notify;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        // 获取从设备重启后经历的时间值
        mBaseTime = SystemClock.elapsedRealtime();
        // 从意图中获取是否正在播放的字段
        isPlaying = intent.getBooleanExtra("is_play", true);
        // 从意图中获取歌曲名称字段
        mSong = intent.getStringExtra("song");
        Log.d(TAG, "isPlaying=" + isPlaying + ", mSong=" + mSong);
        // 延迟200毫秒后启动音乐播放任务
        mHandler.postDelayed(mPlay, 200);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        // 从资源文件中获取“暂停/继续”事件的标识串
        PAUSE_EVENT = getResources().getString(R.string.pause_event);
        // 创建一个暂停/恢复播放的广播接收器
        pauseReceiver = new PauseReceiver();
        // 创建一个意图过滤器，只处理指定事件来源的广播
        IntentFilter filter = new IntentFilter(PAUSE_EVENT);
        // 注册广播接收器，注册之后才能正常接收广播
        registerReceiver(pauseReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 注销广播接收器，注销之后就不再接收广播
        unregisterReceiver(pauseReceiver);
        super.onDestroy();
    }

    // 声明一个暂停/恢复播放的广播接收器
    private PauseReceiver pauseReceiver;
    // 定义一个广播接收器，用于处理音乐的暂停/恢复播放事件
    public class PauseReceiver extends BroadcastReceiver {

        // 一旦接收到暂停/恢复播放的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                isPlaying = !isPlaying;
                if (isPlaying) { // 正在播放
                    // 延迟200毫秒后启动播放任务
                    mHandler.postDelayed(mPlay, 200);
                    if (mPauseTime > 0) {
                        long gap = SystemClock.elapsedRealtime() - mPauseTime;
                        mBaseTime += gap;
                    }
                } else { // 不在播放
                    mPauseTime = SystemClock.elapsedRealtime();
                }
            }
        }
    }

}
