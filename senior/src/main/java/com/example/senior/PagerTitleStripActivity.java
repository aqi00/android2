package com.example.senior;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.Toast;

import com.example.senior.adapter.ImagePagerAdapater;
import com.example.senior.bean.GoodsInfo;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class PagerTitleStripActivity extends AppCompatActivity implements OnPageChangeListener {
    private ArrayList<GoodsInfo> goodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_strip);
        initPagerStrip(); // 初始化翻页标题栏
        initViewPager(); // 初始化翻页视图
    }

    // 初始化翻页标题栏
    private void initPagerStrip() {
        // 从布局视图中获取名叫pts_title的翻页标题栏
        PagerTitleStrip pts_title = findViewById(R.id.pts_title);
        // 设置翻页标题栏的文本大小
        pts_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        // 设置翻页标题栏的文本颜色
        pts_title.setTextColor(Color.BLUE);
    }

    // 初始化翻页视图
    private void initViewPager() {
        goodsList = GoodsInfo.getDefaultList();
        // 构建一个商品图片的翻页适配器
        ImagePagerAdapater adapter = new ImagePagerAdapater(this, goodsList);
        // 从布局视图中获取名叫vp_content的翻页视图
        ViewPager vp_content = findViewById(R.id.vp_content);
        // 给vp_content设置图片翻页适配器
        vp_content.setAdapter(adapter);
        // 设置vp_content默认显示第一个页面
        vp_content.setCurrentItem(0);
        // 给vp_content添加页面变化监听器
        vp_content.addOnPageChangeListener(this);
    }

    // 翻页状态改变时触发。arg0取值说明为：0表示静止，1表示正在滑动，2表示滑动完毕
    // 在翻页过程中，状态值变化依次为：正在滑动→滑动完毕→静止
    public void onPageScrollStateChanged(int arg0) {}

    // 在翻页过程中触发。该方法的三个参数取值说明为 ：第一个参数表示当前页面的序号
    // 第二个参数表示当前页面偏移的百分比，取值为0到1；第三个参数表示当前页面的偏移距离
    public void onPageScrolled(int arg0, float arg1, int arg2) {}

    // 在翻页结束后触发。arg0表示当前滑到了哪一个页面
    public void onPageSelected(int arg0) {
        Toast.makeText(this, "您翻到的手机品牌是：" + goodsList.get(arg0).name, Toast.LENGTH_SHORT).show();
    }
}
