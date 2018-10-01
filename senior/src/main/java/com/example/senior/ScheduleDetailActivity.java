package com.example.senior;

import java.util.Calendar;
import java.util.List;

import com.example.senior.bean.ScheduleArrange;
import com.example.senior.database.DbHelper;
import com.example.senior.database.ScheduleArrangeHelper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/10/7.
 */
@SuppressLint(value={"SetTextI18n", "DefaultLocale"})
public class ScheduleDetailActivity extends AppCompatActivity implements
        OnClickListener, OnTimeSetListener {
    private static final String TAG = "ScheduleDetailActivity";
    private Button btn_back, btn_edit, btn_save;
    private TextView schedule_date, schedule_time;
    private Spinner schedule_alarm;
    private EditText schedule_title, schedule_content;
    private String month, day, week, holiday, solar_date, lunar_date, detail_date;
    private ScheduleArrange mArrange; // 声明一个日程安排结构
    private ScheduleArrangeHelper mScheduleHelper; // 声明一个日程安排的数据库帮助器

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);
        btn_back = findViewById(R.id.btn_back);
        btn_edit = findViewById(R.id.btn_edit);
        btn_save = findViewById(R.id.btn_save);
        btn_back.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        schedule_date = findViewById(R.id.schedule_date);
        schedule_time = findViewById(R.id.schedule_time);
        schedule_title = findViewById(R.id.schedule_title);
        schedule_content = findViewById(R.id.schedule_content);
        schedule_time.setText("00:00");
        schedule_time.setOnClickListener(this);
        getBundleInfo();
        initAlarmSpinner();
    }

    // 获取前一个页面传来的包裹，并从中得到指定的参数信息
    private void getBundleInfo() {
        Bundle req = getIntent().getExtras();
        day = req.getString("day");
        solar_date = req.getString("solar_date");
        lunar_date = req.getString("lunar_date");
        month = day.substring(0, 6);
        week = req.getString("week");
        holiday = req.getString("holiday");
        detail_date = String.format("%s %s\n%s", solar_date, lunar_date, week);
        if (!TextUtils.isEmpty(holiday)) {
            detail_date = String.format("%s，今天是 %s", detail_date, holiday);
        }
        schedule_date.setText(detail_date);
        Log.d(TAG, "month=" + month + ",day=" + day + ",solar_date=" + solar_date + ",lunar_date="
                + lunar_date + ",week=" + week + ",holiday=" + holiday);
    }

    // 初始化提醒间隔的下拉框
    private void initAlarmSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_select, alarmArray);
        schedule_alarm = findViewById(R.id.schedule_alarm);
        schedule_alarm.setPrompt("请选择提醒间隔");
        schedule_alarm.setAdapter(adapter);
        schedule_alarm.setSelection(0);
        schedule_alarm.setOnItemSelectedListener(new AlarmSelectedListener());
    }

    private int alarmType = 0;
    private String[] alarmArray = {"不提醒", "提前5分钟", "提前10分钟",
            "提前15分钟", "提前半小时", "提前1小时", "当前时间后10秒"};
    private int[] advanceArray = {0, 5, 10, 15, 30, 60, 10};

    class AlarmSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            alarmType = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        mArrange = new ScheduleArrange();
        // 获得数据库帮助器的实例
        mScheduleHelper = new ScheduleArrangeHelper(this, DbHelper.db_name, null, 1);
        // 查询数据库获得当天的日程安排信息
        List<ScheduleArrange> mArrangeList = (List<ScheduleArrange>) mScheduleHelper.queryInfoByDay(day);
        if (mArrangeList.size() >= 1) { // 已有日程安排，则显示该日程信息
            enableEdit(false); // 关闭编辑模式
            mArrange = mArrangeList.get(0);
            schedule_time.setText(mArrange.hour + ":" + mArrange.minute);
            schedule_alarm.setSelection(mArrange.alarm_type);
            schedule_title.setText(mArrange.title);
            schedule_content.setText(mArrange.content);
        } else { // 没有日程安排，则提示用户输入新日程
            enableEdit(true); // 开启编辑模式
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭数据库连接
        mScheduleHelper.close();
    }

    // 在是否开启编辑模式之间切换
    private void enableEdit(boolean enabled) {
        schedule_time.setEnabled(enabled);
        schedule_alarm.setEnabled(enabled);
        schedule_title.setEnabled(enabled);
        schedule_content.setEnabled(enabled);
        if (enabled) {
            schedule_time.setBackgroundResource(R.drawable.editext_selector);
            schedule_title.setBackgroundResource(R.drawable.editext_selector);
            schedule_content.setBackgroundResource(R.drawable.editext_selector);
        } else {
            schedule_time.setBackgroundDrawable(null);
            schedule_title.setBackgroundDrawable(null);
            schedule_content.setBackgroundDrawable(null);
        }
        btn_edit.setVisibility(enabled ? View.GONE : View.VISIBLE);
        btn_save.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.schedule_time) { // 点击了日程时间的文本视图
            Calendar calendar = Calendar.getInstance();
            // 弹出时间选择对话框，供用户选择日程事务发生的时间
            TimePickerDialog dialog = new TimePickerDialog(this, this,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            dialog.show();
        } else if (v.getId() == R.id.btn_back) { // 点击了返回按钮
            finish();
        } else if (v.getId() == R.id.btn_edit) { // 点击了编辑按钮
            enableEdit(true); // 开启编辑模式
        } else if (v.getId() == R.id.btn_save) { // 点击了保存按钮
            if (TextUtils.isEmpty(schedule_title.getText())) {
                Toast.makeText(this, "请输入日程标题", Toast.LENGTH_SHORT).show();
                return;
            }
            enableEdit(false); // 关闭编辑模式
            saveArrange();
        }
    }

    // 保存编辑好的日程安排数据
    private void saveArrange() {
        String[] time_split = schedule_time.getText().toString().split(":");
        mArrange.hour = time_split[0];
        mArrange.minute = time_split[1];
        mArrange.alarm_type = alarmType;
        mArrange.title = schedule_title.getText().toString();
        mArrange.content = schedule_content.getText().toString();
        if (mArrange.xuhao <= 0) { // 不存在日程记录
            mArrange.month = month;
            mArrange.day = day;
            mScheduleHelper.add(mArrange); // 添加新的日程记录
        } else { // 已存在日程记录
            mScheduleHelper.update(mArrange); // 更新旧的日程记录
        }
        Toast.makeText(this, "保存日程成功", Toast.LENGTH_SHORT).show();
        // 设置提醒闹钟
        if (alarmType > 0) { // 如果需要闹钟定时提醒
            // 创建一个广播事件的意图
            Intent intent = new Intent(ALARM_EVENT);
            // 创建一个用于广播的延迟意图
            PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // 从系统服务中获取闹钟管理器
            AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            if (alarmType == 6) { // 以当前时间为基准
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, advanceArray[alarmType]);
            } else { // 指定一个明确的时间点
                int day_int = Integer.parseInt(day);
                calendar.set(day_int / 10000, day_int % 10000 / 100 - 1, day_int % 100,
                        Integer.parseInt(mArrange.hour), Integer.parseInt(mArrange.minute), 0);
                calendar.add(Calendar.SECOND, -advanceArray[alarmType] * 60);
            }
            // 开始设定闹钟，延迟若干秒后，携带延迟意图发送闹钟广播
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }

    // 一旦点击时间对话框上的确定按钮，就会触发监听器的onTimeSet方法
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // 获取时间对话框设定的小时和分钟
        String time = String.format("%02d:%02d", hourOfDay, minute);
        schedule_time.setText(time);
    }

    // 声明一个闹钟广播事件的标识串
    private String ALARM_EVENT = "com.example.senior.ScheduleDetailActivity.AlarmReceiver";
    // 定义一个闹钟广播的接收器
    public static class AlarmReceiver extends BroadcastReceiver {

        // 一旦接收到闹钟时间到达的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.d(TAG, "AlarmReceiver onReceive");
                // 从系统服务中获取震动管理器
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(3000); // 默认震动3秒
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
