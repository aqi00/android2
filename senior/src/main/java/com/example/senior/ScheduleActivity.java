package com.example.senior;

import com.example.senior.adapter.SchedulePagerAdapter;
import com.example.senior.calendar.SpecialCalendar;
import com.example.senior.util.DateUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    // 声明一个碎片选中事件的标识串
    public static String ACTION_FRAGMENT_SELECTED = "com.example.senior.ACTION_FRAGMENT_SELECTED";
    // 声明一个选择星期参数的标识串
    public static String EXTRA_SELECTED_WEEK = "selected_week";
    // 声明一个显示节日事件的标识串
    public static String ACTION_SHOW_FESTIVAL = "com.example.senior.ACTION_SHOW_FESTIVAL";
    // 声明一个节日图片参数的标识串
    public static String EXTRA_FESTIVAL_RES = "festival_res";
    private LinearLayout ll_schedule; // 声明一个日程表区域的线性视图对象
    private ViewPager vp_schedule; // 声明一个翻页视图对象
    private int mSelectedWeek; // 当前选中的星期
    private int mFestivalResid = 0; // 节日图片的资源编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        // 从布局文件中获取名叫pts_schedule的翻页标题栏
        PagerTabStrip pts_schedule = findViewById(R.id.pts_schedule);
        pts_schedule.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        pts_schedule.setTextColor(Color.BLACK);
        ll_schedule = findViewById(R.id.ll_schedule);
        // 从布局文件中获取名叫vp_schedule的翻页视图
        vp_schedule = findViewById(R.id.vp_schedule);
        TextView tv_schedule = findViewById(R.id.tv_schedule);
        tv_schedule.setText(DateUtil.getNowYearCN() + " 日程安排");
        // 获取今天所处的星期在一年当中的序号
        mSelectedWeek = SpecialCalendar.getTodayWeek();
        // 构建一个日程表的翻页适配器
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getSupportFragmentManager());
        // 给vp_schedule设置日程表翻页适配器
        vp_schedule.setAdapter(adapter);
        // 设置vp_schedule默认显示当前周数的日程页
        vp_schedule.setCurrentItem(mSelectedWeek - 1);
        // 给vp_schedule添加页面变化监听器
        vp_schedule.addOnPageChangeListener(new SheduleChangeListener());
        // 延迟50毫秒再执行任务mFirst
        mHandler.postDelayed(mFirst, 50);
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 声明一个首次打开页面需要延迟执行的任务
    private Runnable mFirst = new Runnable() {
        @Override
        public void run() {
            sendBroadcast(mSelectedWeek); // 发送广播，表示当前是在第几个星期
        }
    };

    // 发送当前周数的广播
    private void sendBroadcast(int week) {
        // 创建一个广播事件的意图
        Intent intent = new Intent(ACTION_FRAGMENT_SELECTED);
        intent.putExtra(EXTRA_SELECTED_WEEK, week);
        // 通过本地的广播管理器来发送广播
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 创建一个节日图片的广播接收器
        festivalReceiver = new FestivalControlReceiver();
        // 注册广播接收器，注册之后才能正常接收广播
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(festivalReceiver, new IntentFilter(ACTION_SHOW_FESTIVAL));
    }

    @Override
    public void onStop() {
        super.onStop();
        // 注销广播接收器，注销之后就不再接收广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(festivalReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFestivalResid != 0) { // 在横屏和竖屏之间翻转时，不会重新onCreate，只会onResume
            ll_schedule.setBackgroundResource(mFestivalResid);
        }
    }

    // 声明一个节日图片的广播接收器
    private FestivalControlReceiver festivalReceiver;
    // 定义一个广播接收器，用于处理节日图片事件
    private class FestivalControlReceiver extends BroadcastReceiver {

        // 一旦接收到节日图片的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                // 从广播消息中取出节日图片的资源编号
                mFestivalResid = intent.getIntExtra(EXTRA_FESTIVAL_RES, 1);
                // 把页面背景设置为广播发来的节日图片
                ll_schedule.setBackgroundResource(mFestivalResid);
            }
        }
    }

    // 定义一个页面变化监听器，用于处理翻页视图的翻页事件
    public class SheduleChangeListener implements OnPageChangeListener {

        // 在翻页结束后触发
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected position=" + position + ", mSelectedWeek=" + mSelectedWeek);
            mSelectedWeek = position + 1;
            sendBroadcast(mSelectedWeek);
        }

        // 在翻页过程中触发
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        // 翻页状态改变时触发
        public void onPageScrollStateChanged(int arg0) {}
    }
}
