package com.example.senior.adapter;

import java.util.ArrayList;

import com.example.senior.R;
import com.example.senior.bean.CalendarTransfer;
import com.example.senior.calendar.LunarCalendar;
import com.example.senior.calendar.SpecialCalendar;
import com.example.senior.util.DateUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarGridAdapter extends BaseAdapter {
    private static final String TAG = "CalendarGridAdapter";
    private Context mContext; // 声明一个上下文对象
    private boolean isLeapyear = false; // 是否为闰年
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int lastDaysOfMonth = 0; // 上一个月的总天数
    private String[] dayNumber = new String[49]; // 一个gridview中的日期存入此数组中
    private ArrayList<CalendarTransfer> transArray = new ArrayList<CalendarTransfer>();
    private static String weekTitle[] = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private LunarCalendar lc;
    private int currentDay = -1; // 用于标记当天

    public CalendarGridAdapter(Context context, int year, int month, int day) {
        mContext = context;
        lc = new LunarCalendar();
        Log.d(TAG, "currentYear=" + year + ", currentMonth=" + month + ", currentDay=" + day);
        // 得到某年的某月的天数且这月的第一天是星期几
        isLeapyear = SpecialCalendar.isLeapYear(year); // 是否为闰年
        daysOfMonth = SpecialCalendar.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = SpecialCalendar.getWeekdayOfMonth(year, month); // 某月第一天为星期几
        lastDaysOfMonth = SpecialCalendar.getDaysOfMonth(isLeapyear, month - 1);  //上一个月的总天数
        Log.d(TAG, isLeapyear + " ======  " + daysOfMonth + "  ============  " + dayOfWeek + "  =========   " + lastDaysOfMonth);
        getWeekDays(year, month);
    }

    @Override
    public int getCount() {
        return dayNumber.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // 根据布局文件item_calendar.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_calendar, null);
            holder.tv_day = convertView.findViewById(R.id.tv_day);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String day = dayNumber[position].split("\\.")[0];
        String festival = dayNumber[position].split("\\.")[1];
        String itemText = day;
        if (position >= 7) {
            itemText = itemText + "\n" + festival;
        }
        holder.tv_day.setText(itemText);
        holder.tv_day.setTextColor(Color.GRAY);
        if (position < 7) {
            // 设置周一到周日的标题
            holder.tv_day.setTextColor(Color.BLACK);
            holder.tv_day.setBackgroundColor(Color.LTGRAY);
        } else if (currentDay == position) {
            holder.tv_day.setBackgroundColor(Color.GREEN); // 设置当天的背景
        } else {
            holder.tv_day.setBackgroundColor(Color.WHITE); // 设置其他日期的背景
        }
        if (position < daysOfMonth + dayOfWeek + 7 - 1 && position >= dayOfWeek + 7 - 1) {
            // 当前月信息显示
            if (DateUtil.isHoliday(festival)) {
                holder.tv_day.setTextColor(Color.BLUE); // 节日字体标蓝
            } else if ((position + 1) % 7 == 6 || (position + 1) % 7 == 0) {
                holder.tv_day.setTextColor(Color.RED); // 周末字体标红
            } else {
                holder.tv_day.setTextColor(Color.BLACK); // 当月字体设黑
            }
        }
        return convertView;
    }

    // 将一个月中的每一天的值添加入数组dayNumber中
    private void getWeekDays(int year, int month) {
        int nextMonthDay = 1;
        String lunarDay = "";
        Log.d(TAG, "begin getWeekDays");
        for (int i = 0; i < dayNumber.length; i++) {
            CalendarTransfer trans = new CalendarTransfer();
            int weekday = (i - 7) % 7 + 1;
            // 周一
            if (i < 7) {
                dayNumber[i] = weekTitle[i] + "." + " ";
            } else if (i < dayOfWeek + 7 - 1) { // 前一个月
                int temp = lastDaysOfMonth - dayOfWeek + 1 - 7 + 1;
                trans = lc.getSubDate(trans, year, month - 1, temp + i, weekday, false);
                lunarDay = trans.day_name;
                dayNumber[i] = (temp + i) + "." + lunarDay;
            } else if (i < daysOfMonth + dayOfWeek + 7 - 1) { // 本月
                int day = i - dayOfWeek + 1 - 7 + 1;
                trans = lc.getSubDate(trans, year, month, day, weekday, false);
                lunarDay = trans.day_name;
                dayNumber[i] = day + "." + lunarDay;
                // 对于当前月才去标记当前日期
                if (year == DateUtil.getNowYear() && month == DateUtil.getNowMonth() && day == DateUtil.getNowDay()) {
                    currentDay = i;
                }
            } else { // 下一个月
                int next_month = month + 1;
                int next_year = year;
                if (next_month >= 13) {
                    next_month = 1;
                    next_year++;
                }
                trans = lc.getSubDate(trans, next_year, next_month, nextMonthDay, weekday, false);
                lunarDay = trans.day_name;
                dayNumber[i] = nextMonthDay + "." + lunarDay;
                nextMonthDay++;
            }
            transArray.add(trans);
        }
//        //把日期数据打印到日志中
//        String abc = "";
//        for (String aDay : dayNumber) {
//            abc = abc + aDay + ":";
//        }
//        Log.d(TAG, abc);
    }

    public CalendarTransfer getCalendarList(int pos) {
        return transArray.get(pos);
    }

    public final class ViewHolder {
        public TextView tv_day;
    }

}
