package com.example.senior;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.example.senior.adapter.BroadcastPagerAdapter;
import com.example.senior.bean.GoodsInfo;
import com.example.senior.fragment.BroadcastFragment;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class BroadTempActivity extends AppCompatActivity {
    private static final String TAG = "BroadTempActivity";
    private LinearLayout ll_brd_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_temp);
        ll_brd_temp = findViewById(R.id.ll_brd_temp);
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
        BroadcastPagerAdapter adapter = new BroadcastPagerAdapter(getSupportFragmentManager(), goodsList);
        // 从布局视图中获取名叫vp_content的翻页视图
        ViewPager vp_content = findViewById(R.id.vp_content);
        // 给vp_content设置手机商品的碎片适配器
        vp_content.setAdapter(adapter);
        // 设置vp_content默认显示第一个页面
        vp_content.setCurrentItem(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 创建一个背景色变更的广播接收器
        bgChangeReceiver = new BgChangeReceiver();
        // 创建一个意图过滤器，只处理指定事件来源的广播
        IntentFilter filter = new IntentFilter(BroadcastFragment.EVENT);
        // 注册广播接收器，注册之后才能正常接收广播
        LocalBroadcastManager.getInstance(this).registerReceiver(bgChangeReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 注销广播接收器，注销之后就不再接收广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bgChangeReceiver);
    }

    // 声明一个背景色变更的广播接收器
    private BgChangeReceiver bgChangeReceiver;
    // 定义一个广播接收器，用于处理背景色变更事件
    private class BgChangeReceiver extends BroadcastReceiver {

        // 一旦接收到背景色变更的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                // 从广播消息中取出最新的颜色
                int color = intent.getIntExtra("color", Color.WHITE);
                // 把页面背景设置为广播发来的颜色
                ll_brd_temp.setBackgroundColor(color);
            }
        }
    }

}
