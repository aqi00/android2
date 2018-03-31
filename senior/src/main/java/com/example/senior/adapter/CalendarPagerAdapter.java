package com.example.senior.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.senior.calendar.Constant;
import com.example.senior.fragment.CalendarFragment;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter {
    private int mYear; // 声明当前日历所处的年份

    // 碎片页适配器的构造函数，传入碎片管理器与年份
    public CalendarPagerAdapter(FragmentManager fm, int year) {
        super(fm);
        mYear = year;
    }

    // 获取碎片Fragment的个数，一年有12个月
    public int getCount() {
        return 12;
    }

    // 获取指定月份的碎片Fragment
    public Fragment getItem(int position) {
        return CalendarFragment.newInstance(mYear, position + 1);
    }

    // 获得指定月份的标题文本
    public CharSequence getPageTitle(int position) {
        return new String(Constant.xuhaoArray[position + 1] + "月");
    }

}
