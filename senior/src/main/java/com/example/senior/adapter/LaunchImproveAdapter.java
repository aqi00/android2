package com.example.senior.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.senior.fragment.LaunchFragment;

public class LaunchImproveAdapter extends FragmentStatePagerAdapter {
    private int[] mImageArray; // 声明一个图片数组

    // 碎片页适配器的构造函数，传入碎片管理器与图片数组
    public LaunchImproveAdapter(FragmentManager fm, int[] imageArray) {
        super(fm);
        mImageArray = imageArray;
    }

    // 获取碎片Fragment的个数
    public int getCount() {
        return mImageArray.length;
    }

    // 获取指定位置的碎片Fragment
    public Fragment getItem(int position) {
        return LaunchFragment.newInstance(position, mImageArray[position]);
    }
}
