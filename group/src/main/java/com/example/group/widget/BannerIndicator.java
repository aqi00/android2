package com.example.group.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.group.R;

public class BannerIndicator extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "BannerIndicator";
    private Context mContext; // 声明一个上下文对象
    private ViewPager vp_banner; // 声明一个翻页视图对象
    private PagerIndicator pi_banner; // 声明一个翻页指示器对象
    private List<ImageView> mViewList = new ArrayList<ImageView>(); // 声明一个图像视图队列

    public BannerIndicator(Context context) {
        this(context, null);
    }

    public BannerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    // 设置广告图片队列
    public void setImage(ArrayList<Integer> imageList) {
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
        // 给翻页视图添加页面变更监听器
        vp_banner.addOnPageChangeListener(new BannerChangeListener());
        // 设置翻页视图默认显示第一页
        vp_banner.setCurrentItem(0);
        // 设置翻页指示器的个数与间隔
        pi_banner.setCount(imageList.size(), 15);
    }

    // 初始化视图
    private void initView() {
        // 根据布局文件banner_indicator.xml生成视图对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.banner_indicator, null);
        // 从布局文件中获取名叫vp_banner的翻页视图
        vp_banner = view.findViewById(R.id.vp_banner);
        // 从布局文件中获取名叫pi_banner的翻页指示器
        pi_banner = view.findViewById(R.id.pi_banner);
        addView(view); // 将该布局视图添加到横幅指示器中
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

    // 定义一个广告轮播监听器
    private class BannerChangeListener implements ViewPager.OnPageChangeListener {

        // 翻页状态改变时触发
        public void onPageScrollStateChanged(int arg0) {}

        // 在翻页过程中触发
        public void onPageScrolled(int seq, float ratio, int offset) {
            // 设置指示器高亮圆点的位置
            pi_banner.setCurrent(seq, ratio);
        }

        // 在翻页结束后触发
        public void onPageSelected(int seq) {
            // 设置指示器高亮圆点的位置
            pi_banner.setCurrent(seq, 0);
        }
    }
}