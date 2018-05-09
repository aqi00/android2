package com.example.custom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class NotifyCounterActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_title;
    private EditText et_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_counter);
        et_title = findViewById(R.id.et_title);
        et_message = findViewById(R.id.et_message);
        findViewById(R.id.btn_send_counter).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_counter) {
            String title = et_title.getText().toString();
            String message = et_message.getText().toString();
            sendCounterNotify(title, message);
        }
    }

    // 发送计时的通知消息（通知栏右边自动计时）
    private void sendCounterNotify(String title, String message) {
        // 创建一个跳转到活动页面的意图
        Intent cancelIntent = new Intent(this, MainActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent deleteIntent = PendingIntent.getActivity(this,
                R.string.app_name, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建一个通知消息的构造器
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(this, getString(R.string.app_name));
        }
        builder.setDeleteIntent(deleteIntent) // 设置内容的清除意图
                .setAutoCancel(true) // 设置是否允许自动清除
                .setUsesChronometer(true) // 设置是否显示计数器
                .setProgress(100, 60, false) // 设置进度条与当前进度
                .setNumber(99) // 设置通知栏右下方的数字
                .setSmallIcon(R.drawable.ic_app) // 设置状态栏里的小图标
                .setTicker("提示消息来啦") // 设置状态栏里面的提示文本
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app)) // 设置通知栏里面的大图标
                .setContentTitle(title) // 设置通知栏里面的标题文本
                .setContentText(message); // 设置通知栏里面的内容文本
        // 根据消息构造器构建一个通知对象
        Notification notify = builder.build();
        // 从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // 使用通知管理器推送通知，然后在手机的通知栏就会看到该消息
        notifyMgr.notify(R.string.app_name, notify);
    }

}
