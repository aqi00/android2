package com.example.group.widget;

import java.util.ArrayList;
import java.util.List;

import com.example.group.R;
import com.example.group.util.Utils;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class BannerPager extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "BannerPager";
    private Context mContext; // 声明一个上下文对象
    private ViewPager vp_banner; // 声明一个翻页视图对象
    private RadioGroup rg_indicator; // 声明一个单选组对象
    private List<ImageView> mViewList = new ArrayList<ImageView>(); // 声明一个图像视图队列
    private int mInterval = 2000; // 轮播的时间间隔，单位毫秒

    public BannerPager(Context context) {
        this(context, null);
    }

    public BannerPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    // 开始广告轮播
    public void start() {
        // 延迟若干秒后启动滚动任务
        mHandler.postDelayed(mScroll, mInterval);
    }

    // 停止广告轮播
    public void stop() {
        // 移除滚动任务
        mHandler.removeCallbacks(mScroll);
    }

    // 设置广告轮播的时间间隔
    public void setInterval(int interval) {
        mInterval = interval;
    }

    // 设置广告图片队列
    public void setImage(ArrayList<Integer> imageList) {
        int dip_15 = Utils.dip2px(mContext, 15);
        // 根据图片队列生成图像视图队列
        for (int i = 0; i < imageList.size(); i++) {
            Integer imageID = imageList.get(i);
            ImageView iv = new ImageView(mContext);
            iv.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(imageID);
            iv.setOnClickListener(this);
            mViewList.add(iv);
        }
        // 设置翻页视图的图像翻页适配器
        vp_banner.setAdapter(new ImageAdapater());
        // 给翻页视图添加简单的页面变更监听器，此时只需重写onPageSelected方法
        vp_banner.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 高亮显示该位置的指示按钮
                setButton(position);
            }
        });
        // 根据图片队列生成指示按钮队列
        for (int i = 0; i < imageList.size(); i++) {
            RadioButton radio = new RadioButton(mContext);
            radio.setLayoutParams(new RadioGroup.LayoutParams(dip_15, dip_15));
            radio.setGravity(Gravity.CENTER);
            radio.setButtonDrawable(R.drawable.indicator_selector);
            rg_indicator.addView(radio);
        }
        // 设置翻页视图默认显示第一页
        vp_banner.setCurrentItem(0);
        // 默认高亮显示第一个指示按钮
        setButton(0);
    }

    // 设置选中单选组内部的哪个单选按钮
    private void setButton(int position) {
        ((RadioButton) rg_indicator.getChildAt(position)).setChecked(true);
    }

    // 初始化视图
    private void initView() {
        // 根据布局文件banner_pager.xml生成视图对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.banner_pager, null);
        // 从布局文件中获取名叫vp_banner的翻页视图
        vp_banner = view.findViewById(R.id.vp_banner);
        // 从布局文件中获取名叫rg_indicator的单选组
        rg_indicator = view.findViewById(R.id.rg_indicator);
        addView(view); // 将该布局视图添加到横幅轮播条
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个滚动任务
    private Runnable mScroll = new Runnable() {
        @Override
        public void run() {
            scrollToNext(); // 滚动广告图片
            // 延迟若干秒后继续启动滚动任务
            mHandler.postDelayed(this, mInterval);
        }
    };

    // 滚动到下一张广告图
    public void scrollToNext() {
        // 获得下一张广告图的位置
        int index = vp_banner.getCurrentItem() + 1;
        if (index >= mViewList.size()) {
            index = 0;
        }
        // 设置翻页视图显示指定位置的页面
        vp_banner.setCurrentItem(index);
    }

    // 定义一个图像翻页适配器
    private class ImageAdapater extends PagerAdapter {

        // 获取页面项的个数
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // 从容器中销毁指定位置的页面
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        // 实例化指定位置的页面，并将其添加到容器中
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }
    }

    @Override
    public void onClick(View v) {
        // 获取翻页视图当前页面项的序号
        int position = vp_banner.getCurrentItem();
        // 触发点击监听器的onBannerClick方法
        mListener.onBannerClick(position);
    }

    // 设置广告图的点击监听器
    public void setOnBannerListener(BannerClickListener listener) {
        mListener = listener;
    }

    // 声明一个广告图点击的监听器对象
    private BannerClickListener mListener;

    // 定义一个广告图片的点击监听器接口
    public interface BannerClickListener {
        void onBannerClick(int position);
    }

}