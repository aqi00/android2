package com.example.custom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RemoteViews;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class NotifyCustomActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_song;
    private String PAUSE_EVENT = ""; // “暂停/继续”事件的标识串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_custom);
        et_song = findViewById(R.id.et_song);
        findViewById(R.id.btn_send_custom).setOnClickListener(this);
        // 从资源文件中获取“暂停/继续”事件的标识串
        PAUSE_EVENT = getResources().getString(R.string.pause_event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_custom) {
            // 获取自定义消息的通知对象
            Notification notify = getNotify(this, PAUSE_EVENT,
                    et_song.getText().toString(), true, 50, SystemClock.elapsedRealtime());
            // 从系统服务中获取通知管理器
            NotificationManager notifyMgr = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            // 使用通知管理器推送通知，然后在手机的通知栏就会看到该消息
            notifyMgr.notify(R.string.app_name, notify);
        }
    }

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

}
