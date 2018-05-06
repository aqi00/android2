package com.example.senior;

import com.example.senior.adapter.CalendarPagerAdapter;
import com.example.senior.util.DateUtil;
import com.example.senior.widget.MonthPicker;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/7.
 */
@SuppressLint("SetTextI18n")
public class CalendarActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "CalendarActivity";
    private LinearLayout ll_calendar_main; // 声明一个万年历区域的线性视图对象
    private LinearLayout ll_month_select; // 声明一个月份选择区域的线性视图对象
    private MonthPicker mp_month; // 声明一个月份选择器对象
    private ViewPager vp_calendar; // 声明一个翻页视图对象
    private TextView tv_calendar; // 声明一个选中年份的文本视图对象
    private boolean isShowSelect = false; // 是否显示月份选择器
    private int mSelectedYear = 2000; // 当前选中的年份

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ll_calendar_main = findViewById(R.id.ll_calendar_main);
        ll_month_select = findViewById(R.id.ll_month_select);
        // 从布局文件中获取名叫mp_month的月份选择器
        mp_month = findViewById(R.id.mp_month);
        // 从布局文件中获取名叫pts_calendar的翻页标题栏
        findViewById(R.id.btn_ok).setOnClickListener(this);
        PagerTabStrip pts_calendar = findViewById(R.id.pts_calendar);
        pts_calendar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        pts_calendar.setTextColor(Color.BLACK);
        // 从布局文件中获取名叫vp_calendar的翻页视图
        vp_calendar = findViewById(R.id.vp_calendar);
        tv_calendar = findViewById(R.id.tv_calendar);
        tv_calendar.setOnClickListener(this);
        // 万年历默认显示当前年月的月历
        showCalendar(DateUtil.getNowYear(), DateUtil.getNowMonth());
    }

    // 显示指定年月的万年历
    private void showCalendar(int year, int month) {
        // 如果指定年份不是上次选中的年份，则需重新构建该年份的年历
        if (year != mSelectedYear) {
            tv_calendar.setText(year + "年");
            // 构建一个指定年份的年历翻页适配器
            CalendarPagerAdapter adapter = new CalendarPagerAdapter(getSupportFragmentManager(), year);
            // 给vp_calendar设置年历翻页适配器
            vp_calendar.setAdapter(adapter);
            mSelectedYear = year;
        }
        // 设置vp_calendar默认显示指定月份的月历页
        vp_calendar.setCurrentItem(month - 1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_calendar) { // 点击了年份文本
            // 重新选择万年历的年月
            resetPage();
        } else if (v.getId() == R.id.btn_ok) { // 点击了确定按钮
            // 根据月份选择器上设定的年月，刷新万年历的显示界面
            showCalendar(mp_month.getYear(), mp_month.getMonth() + 1);
            resetPage();
        }
    }

    // 重置页面。在显示万年历主页面和显示月份选择器之间切换
    private void resetPage() {
        isShowSelect = !isShowSelect;
        ll_calendar_main.setVisibility(isShowSelect ? View.GONE : View.VISIBLE);
        ll_month_select.setVisibility(isShowSelect ? View.VISIBLE : View.GONE);
    }
}
