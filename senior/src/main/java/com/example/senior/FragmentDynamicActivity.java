package com.example.senior;

import java.util.ArrayList;

import com.example.senior.adapter.MobilePagerAdapter;
import com.example.senior.bean.GoodsInfo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class FragmentDynamicActivity extends AppCompatActivity {
    private static final String TAG = "FragmentDynamicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_dynamic);
        Log.d(TAG, "onCreate");
        initPagerStrip(); // 初始化翻页标题栏
        initViewPager(); // 初始化翻页视图
    }

    // 初始化翻页标题栏
    private void initPagerStrip() {
        // 从布局视图中获取名叫pts_tab的翻页标题栏
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标题栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        // 设置翻页标题栏的文本颜色
        pts_tab.setTextColor(Color.BLACK);
    }

    // 初始化翻页视图
    private void initViewPager() {
        ArrayList<GoodsInfo> goodsList = GoodsInfo.getDefaultList();
        // 构建一个手机商品的碎片翻页适配器
        MobilePagerAdapter adapter = new MobilePagerAdapter(
                getSupportFragmentManager(), goodsList);
        // 从布局视图中获取名叫vp_content的翻页视图
        ViewPager vp_content = findViewById(R.id.vp_content);
        // 给vp_content设置手机商品的碎片适配器
        vp_content.setAdapter(adapter);
        // 设置vp_content默认显示第一个页面
        vp_content.setCurrentItem(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

}
