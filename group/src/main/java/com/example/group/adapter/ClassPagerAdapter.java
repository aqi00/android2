package com.example.group.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.group.fragment.AppliancesFragment;
import com.example.group.fragment.ClothesFragment;

public class ClassPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> mTitleArray; // 声明一个标题文字队列

    // 碎片页适配器的构造函数，传入碎片管理器与标题队列
    public ClassPagerAdapter(FragmentManager fm, ArrayList<String> titleArray) {
        super(fm);
        mTitleArray = titleArray;
    }

    // 获取指定位置的碎片Fragment
    public Fragment getItem(int position) {
        if (position == 0) { // 第一页展示服装
            return new ClothesFragment();
        } else if (position == 1) { // 第二页展示电器
            return new AppliancesFragment();
        }
        return new ClothesFragment();
    }

    // 获取碎片Fragment的个数
    public int getCount() {
        return mTitleArray.size();
    }

    // 获得指定碎片页的标题文本
    public CharSequence getPageTitle(int position) {
        return mTitleArray.get(position);
    }
}
