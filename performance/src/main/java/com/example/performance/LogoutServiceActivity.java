package com.example.performance;

import com.example.performance.util.DateUtil;

import android.annotation.SuppressLint;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/27.
 */
@SuppressLint(value={"StaticFieldLeak","SetTextI18n"})
public class LogoutServiceActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "LogoutServiceActivity";
    private CheckBox ck_logout;
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
        setContentView(R.layout.activity_logout_service);
        ck_logout = findViewById(R.id.ck_logout);
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
        if (ck_logout.isChecked()) {
            // 取消已设定的闹钟提醒
            mAlarmManager.cancel(pIntent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_alarm) {
            if (!isRunning) {
                // 在Android4.4之后，操作系统为了节能省电，会调整alarm唤醒的时间，
                // 所以setRepeating方法不保证每次工作都在指定的时间开始，
                // 此时需要先注销原闹钟，再调用set方法开启新闹钟。
//                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//                        System.currentTimeMillis(), 3000, pIntent);
                // 设定延迟若干时间的一次性定时器
                mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis()+mDelay, pIntent);
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
    private String ALARM_EVENT = "com.example.performance.alarm";

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

    // 重复闹钟提醒设置
    private static void repeatAlarm() {
        // 取消已设定的闹钟提醒
        mAlarmManager.cancel(pIntent);
        // 设定延迟若干时间的一次性定时器
        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+mDelay, pIntent);
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
