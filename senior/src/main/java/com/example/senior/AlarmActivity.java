package com.example.senior;

import java.util.Calendar;

import com.example.senior.util.DateUtil;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/10/7.
 */
@SuppressLint("StaticFieldLeak")
public class AlarmActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "AlarmActivity";
    private static TextView tv_alarm;
    private int mDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        tv_alarm = findViewById(R.id.tv_alarm);
        findViewById(R.id.btn_alarm).setOnClickListener(this);
        initDelaySpinner();
    }

    // 初始化闹钟延迟的下拉框
    private void initDelaySpinner() {
        ArrayAdapter<String> delayAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, delayDescArray);
        Spinner sp_delay = findViewById(R.id.sp_delay);
        sp_delay.setPrompt("请选择闹钟延迟");
        sp_delay.setAdapter(delayAdapter);
        sp_delay.setOnItemSelectedListener(new DelaySelectedListener());
        sp_delay.setSelection(0);
    }

    private int[] delayArray = {5, 10, 15, 20, 25, 30};
    private String[] delayDescArray = {"5秒", "10秒", "15秒", "20秒", "25秒", "30秒"};
    class DelaySelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mDelay = delayArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_alarm) {
            // 创建一个广播事件的意图
            Intent intent = new Intent(ALARM_EVENT);
            // 创建一个用于广播的延迟意图
            PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // 从系统服务中获取闹钟管理器
            AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // 给当前时间加上若干秒
            calendar.add(Calendar.SECOND, mDelay);
            // 开始设定闹钟，延迟若干秒后，携带延迟意图发送闹钟广播
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
            mDesc = DateUtil.getNowTime() + " 设置闹钟";
            tv_alarm.setText(mDesc);
        }
    }

    // 声明一个闹钟广播事件的标识串
    private String ALARM_EVENT = "com.example.senior.AlarmActivity.AlarmReceiver";
    private static String mDesc = ""; // 闹钟时间到达的描述
    private static boolean isArrived = false; // 闹钟时间是否到达

    // 定义一个闹钟广播的接收器
    public static class AlarmReceiver extends BroadcastReceiver {

        // 一旦接收到闹钟时间到达的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.d(TAG, "AlarmReceiver onReceive");
                if (tv_alarm != null && !isArrived) {
                    isArrived = true;
                    mDesc = String.format("%s\n%s 闹钟时间到达", mDesc, DateUtil.getNowTime());
                    tv_alarm.setText(mDesc);
                }
            }
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
            // 创建一个意图过滤器，只处理指定事件来源的广播
            IntentFilter filter = new IntentFilter(ALARM_EVENT);
            // 注册广播接收器，注册之后才能正常接收广播
            registerReceiver(alarmReceiver, filter);
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
