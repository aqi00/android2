package com.example.senior.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.senior.bean.GoodsInfo;
import com.example.senior.fragment.BroadcastFragment;

public class BroadcastPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<GoodsInfo> mGoodsList = new ArrayList<GoodsInfo>(); // 声明一个商品队列

    // 碎片页适配器的构造函数，传入碎片管理器与商品队列
    public BroadcastPagerAdapter(FragmentManager fm, ArrayList<GoodsInfo> goodsList) {
        super(fm);
        mGoodsList = goodsList;
    }

    // 获取碎片Fragment的个数
    public int getCount() {
        return mGoodsList.size();
    }

    // 获取指定位置的碎片Fragment
    public Fragment getItem(int position) {
        return BroadcastFragment.newInstance(position,
                mGoodsList.get(position).pic, mGoodsList.get(position).desc);
    }

    // 获得指定碎片页的标题文本
    public CharSequence getPageTitle(int position) {
        return mGoodsList.get(position).name;
    }

}

