package com.example.performance;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.performance.util.DateUtil;

/**
 * Created by ouyangshen on 2018/1/26.
 */
@SuppressLint(value={"StaticFieldLeak","SetTextI18n"})
@TargetApi(Build.VERSION_CODES.M)
public class AlarmIdleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlarmIdleActivity";
    private Button btn_alarm;
    private static TextView tv_alarm;
    private static PendingIntent pIntent; // 声明一个延迟意图对象
    private static AlarmManager mAlarmManager; // 声明一个闹钟管理器对象
    private static String mDesc;
    private static int mDelay = 3000; // 闹钟延迟的间隔
    private boolean isRunning = false; // 闹钟是否已经设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_idle);
        tv_alarm = findViewById(R.id.tv_alarm);
        btn_alarm = findViewById(R.id.btn_alarm);
        btn_alarm.setOnClickListener(this);
        // 创建一个广播事件的意图
        Intent intent = new Intent(ALARM_EVENT);
        // 创建一个用于广播的延迟意图
        pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 从系统服务中获取闹钟管理器
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mDesc = "";
        TextView tv_start = findViewById(R.id.tv_start);
        tv_start.setText("页面打开时间为：" + DateUtil.getNowTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消已设定的闹钟提醒
        mAlarmManager.cancel(pIntent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_alarm) {
            if (!isRunning) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Android6.0之后增强了休眠模式，手机在休眠期间，
                    // 原本在系统闹钟服务AlarmManager中设定好的定时任务，
                    // 即使定时的时刻到达，也要等到苏醒期间才会得到执行。
                    // 如果一定要在休眠期唤醒闹钟，就得调用setAndAllowWhileIdle代替set方法。
                    // 但即使是setAndAllowWhileIdle方法，App每9分钟唤醒次数也不能超过一次
                    mAlarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis()+mDelay, pIntent);
                } else {
                    // 设定延迟若干时间的一次性定时器
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis()+mDelay, pIntent);
                }
                mDesc = DateUtil.getNowTime() + " 设置闹钟";
                tv_alarm.setText(mDesc);
                btn_alarm.setText("取消闹钟");
            } else {
                // 取消已设定的闹钟提醒
                mAlarmManager.cancel(pIntent);
                btn_alarm.setText("设置闹钟");
            }
            isRunning = !isRunning;
        }
    }

    // 声明一个闹钟广播事件的标识串
    private String ALARM_EVENT = "com.example.performance.idle";

    // 定义一个闹钟广播的接收器
    public static class AlarmReceiver extends BroadcastReceiver {
        // 一旦接收到闹钟时间到达的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.d(TAG, "AlarmReceiver onReceive");
                if (tv_alarm != null) {
                    mDesc = String.format("%s\n%s 闹钟时间到达", mDesc, DateUtil.getNowTime());
                    tv_alarm.setText(mDesc);
                    repeatAlarm(); // 重复闹钟提醒设置
                }
            }
        }
    }

    // 重复闹钟提醒。设置每次时刻到达，都重新设置下一次的定时任务，从而间接实现了持续唤醒的功能
    private static void repeatAlarm() {
        // 取消已设定的闹钟提醒
        mAlarmManager.cancel(pIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 即使系统正在休眠期间，也要按时唤醒这个一次性闹钟
            mAlarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis()+mDelay, pIntent);
        } else {
            // 设定延迟若干时间的一次性定时器
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis()+mDelay, pIntent);
        }
    }

    // 适配Android9.0开始
    @Override
    public void onStart() {
        super.onStart();
        // 从Android9.0开始，系统不再支持静态广播，应用广播只能通过动态注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 创建一个闹钟的广播接收器
            alarmReceiver = new AlarmReceiver();
            // 注册广播接收器，注册之后才能正常接收广播
            registerReceiver(alarmReceiver, new IntentFilter(ALARM_EVENT));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 注销广播接收器，注销之后就不再接收广播
            unregisterReceiver(alarmReceiver);
        }
    }

    // 声明一个闹钟的广播接收器
    private AlarmReceiver alarmReceiver;
    // 适配Android9.0结束

}
