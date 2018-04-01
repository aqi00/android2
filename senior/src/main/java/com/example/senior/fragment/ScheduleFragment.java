package com.example.senior.fragment;

import java.util.ArrayList;

import com.example.senior.R;
import com.example.senior.ScheduleActivity;
import com.example.senior.adapter.CalendarGridAdapter;
import com.example.senior.adapter.ScheduleListAdapter;
import com.example.senior.bean.CalendarTransfer;
import com.example.senior.bean.ScheduleArrange;
import com.example.senior.calendar.Constant;
import com.example.senior.calendar.SpecialCalendar;
import com.example.senior.database.DbHelper;
import com.example.senior.database.ScheduleArrangeHelper;
import com.example.senior.util.DateUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

@SuppressLint(value={"SimpleDateFormat", "DefaultLocale"})
public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private int mSelectedWeek, mNowWeek; // 当前选择的周数，以及今天所处的周数
    private ListView lv_shedule; // 声明一个列表视图对象
    private int mYear, mMonth, mDay; // 当前选择周数的星期一对应的年、月、日
    private int first_pos = 0; // 当前选择周数的星期一在该月月历中的位置
    private String thisDate; // 当前选择周数的星期一的具体日期
    private ArrayList<CalendarTransfer> tranArray = new ArrayList<CalendarTransfer>();
    private ScheduleArrangeHelper mArrangeHelper; // 声明一个日程安排的数据库帮助器

    // 获取该碎片的一个实例
    public static ScheduleFragment newInstance(int week) {
        ScheduleFragment fragment = new ScheduleFragment(); // 创建该碎片的一个实例
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putInt("week", week); // 往包裹存入周数
        fragment.setArguments(bundle); // 把包裹塞给碎片
        return fragment; // 返回碎片实例
    }

    // 创建碎片视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        if (getArguments() != null) { // 如果碎片携带有包裹，则打开包裹获取参数信息
            mSelectedWeek = getArguments().getInt("week", 1);
        }
        // 获取今天所处的周数
        mNowWeek = SpecialCalendar.getTodayWeek();
        initDate(mSelectedWeek - mNowWeek);
        Log.d(TAG, "thisDate=" + thisDate + ",fisrt_pos=" + first_pos);
        Log.d(TAG, "mYear=" + mYear + ",mMonth=" + mMonth + ",mDay=" + mDay);
        // 根据年月日计算当周位于哪个日历网格适配器
        CalendarGridAdapter calV = new CalendarGridAdapter(mContext, mYear, mMonth, mDay);
        for (int i = first_pos; i < first_pos + 7; i++) { // 从月历中取出当周的七天
            CalendarTransfer trans = calV.getCalendarList(i);
            Log.d(TAG, "trans.solar_month=" + trans.solar_month + ",trans.solar_day=" + trans.solar_day
                    + ",trans.lunar_month=" + trans.lunar_month + ",trans.lunar_day=" + trans.lunar_day);
            tranArray.add(trans); // 添加到日历转换队列中
        }
        // 根据布局文件fragment_schedule.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_schedule, container, false);
        // 从布局视图中获取名叫lv_shedule的列表视图
        lv_shedule = mView.findViewById(R.id.lv_shedule);
        return mView; // 返回该碎片的视图对象
    }

    // 初始化当周的星期一对应的年、月、日
    private void initDate(int diff_weeks) {
        String nowDate = DateUtil.getNowDate();
        thisDate = DateUtil.getAddDate(nowDate, diff_weeks * 7);
        int thisDay = Integer.valueOf(thisDate.substring(6, 8));
        int weekIndex = DateUtil.getWeekIndex(thisDate);
        int week_count = (int) Math.ceil((thisDay - weekIndex + 0.5) / 7.0);
        if ((thisDay - weekIndex) % 7 > 0) {
            week_count++; // 需要计算当天所在周是当月的第几周
        }
        if (thisDay - weekIndex < 0) {
            week_count++;
        }
        first_pos = week_count * 7;
        mYear = Integer.parseInt(thisDate.substring(0, 4));
        mMonth = Integer.parseInt(thisDate.substring(4, 6));
        mDay = Integer.parseInt(thisDate.substring(6, 8));
    }

    // 检查当周的七天是否存在特殊节日
    private void checkFestival() {
        int i = 0;
        for (; i < tranArray.size(); i++) {
            CalendarTransfer trans = tranArray.get(i);
            int j = 0;
            for (; j < Constant.festivalArray.length; j++) {
                if (trans.day_name.contains(Constant.festivalArray[j])) {
                    // 找到了特殊节日，则发送该节日图片广播
                    sendFestival(Constant.festivalResArray[j]);
                    break;
                }
            }
            if (j < Constant.festivalArray.length) {
                break;
            }
        }
        // 未找到特殊节日，则发送日常图片的广播
        if (i >= tranArray.size()) {
            sendFestival(R.drawable.normal_day);
        }
    }

    // 把图片编号通过广播发出去
    private void sendFestival(int resid) {
        // 创建一个广播事件的意图
        Intent intent = new Intent(ScheduleActivity.ACTION_SHOW_FESTIVAL);
        intent.putExtra(ScheduleActivity.EXTRA_FESTIVAL_RES, resid);
        // 通过本地的广播管理器来发送广播
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 创建一个周数变更的广播接收器
        scrollControlReceiver = new ScrollControlReceiver();
        // 注册广播接收器，注册之后才能正常接收广播
        LocalBroadcastManager.getInstance(mContext).registerReceiver(scrollControlReceiver,
                new IntentFilter(ScheduleActivity.ACTION_FRAGMENT_SELECTED));
        // 获得数据库帮助器的实例
        mArrangeHelper = new ScheduleArrangeHelper(mContext, DbHelper.db_name, null, 1);
        CalendarTransfer begin_trans = tranArray.get(0);
        String begin_day = String.format("%s%02d%02d", begin_trans.solar_year, begin_trans.solar_month, begin_trans.solar_day);
        CalendarTransfer end_trans = tranArray.get(tranArray.size() - 1);
        String end_day = String.format("%s%02d%02d", end_trans.solar_year, end_trans.solar_month, end_trans.solar_day);
        // 根据开始日期和结束日期，到数据库中查询这几天的日程安排信息
        ArrayList<ScheduleArrange> arrangeList = (ArrayList<ScheduleArrange>) mArrangeHelper.queryInfoByDayRange(begin_day, end_day);
        // 构建一个当周日程的列表适配器
        ScheduleListAdapter listAdapter = new ScheduleListAdapter(mContext, tranArray, arrangeList);
        // 给lv_shedule设置日程列表适配器
        lv_shedule.setAdapter(listAdapter);
        // 给lv_shedule设置列表项点击监听器
        lv_shedule.setOnItemClickListener(listAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 注销广播接收器，注销之后就不再接收广播
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(scrollControlReceiver);
        mArrangeHelper.close(); // 关闭数据库连接
    }

    // 声明一个周数变更的广播接收器
    private ScrollControlReceiver scrollControlReceiver;
    // 定义一个广播接收器，用于处理周数变更事件
    private class ScrollControlReceiver extends BroadcastReceiver {

        // 一旦接收到周数变更的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                // 从广播消息中取出最新的周数
                int selectedWeek = intent.getIntExtra(ScheduleActivity.EXTRA_SELECTED_WEEK, 1);
                Log.d(TAG, "onReceive selectedWeek=" + selectedWeek + ", mSelectedWeek=" + mSelectedWeek);
                // 如果碎片对应的周数正好等于广播的周数，则检查当周是否存在节日
                if (mSelectedWeek == selectedWeek) {
                    checkFestival();
                }
            }
        }
    }

}
