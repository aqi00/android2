package com.example.event.widget;

import java.util.ArrayList;

import com.example.event.R;
import com.example.event.util.Utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class BannerFlipper extends RelativeLayout {
    private static final String TAG = "BannerFlipper";
    private Context mContext; // 声明一个上下文对象
    private ViewFlipper mFlipper; // 声明一个飞掠视图对象
    private RadioGroup mGroup; // 声明一个单选组对象
    private GestureDetector mGesture; // 声明一个手势检测器对象
    private float mFlipGap = 20f; // 触发飞掠事件的距离阈值

    public BannerFlipper(Context context) {
        this(context, null);
    }

    public BannerFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(); // 初始化视图
    }

    // 设置飞掠视图的图片队列
    public void setImage(ArrayList<Integer> imageList) {
        int dip_15 = Utils.dip2px(mContext, 15);
        // 下面给每个图片都分配一个场景，并加入到飞掠视图
        for (Integer imageID : imageList) {
            ImageView iv_item = new ImageView(mContext);
            iv_item.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            iv_item.setScaleType(ImageView.ScaleType.FIT_XY);
            iv_item.setImageResource(imageID);
            // 往飞掠视图添加一个图像视图
            mFlipper.addView(iv_item);
        }
        // 下面给每个图片都分配一个指示圆点
        for (int i = 0; i < imageList.size(); i++) {
            RadioButton radio = new RadioButton(mContext);
            radio.setLayoutParams(new RadioGroup.LayoutParams(dip_15, dip_15));
            radio.setGravity(Gravity.CENTER);
            radio.setButtonDrawable(R.drawable.indicator_selector);
            // 往单选组添加一个指示圆点
            mGroup.addView(radio);
        }
        // 设置飞掠视图当前展示的场景。这里默认展示最后一张
        mFlipper.setDisplayedChild(imageList.size() - 1);
        // 播放下一个场景。最后一张场景的下一张，其实就是第一张场景
        startFlip();
    }

    // 初始化视图
    private void initView() {
        // 根据布局文件banner_flipper.xml生成视图对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.banner_flipper, null);
        // 从布局文件中获取名叫banner_flipper的飞掠视图
        mFlipper = view.findViewById(R.id.banner_flipper);
        // 从布局文件中获取名叫rg_indicator的单选组
        mGroup = view.findViewById(R.id.rg_indicator);
        addView(view);
        // 创建一个手势检测器
        mGesture = new GestureDetector(mContext, new BannerGestureListener());
        // 延迟200毫秒后启动指示器刷新任务
        mHandler.postDelayed(mRefresh, 200);
    }

    // 在分配触摸事件时触发
    public boolean dispatchTouchEvent(MotionEvent event) {
        mGesture.onTouchEvent(event); // 命令由手势检测器接管当前的手势事件
        return true;
    }

    // 定义一个手势检测监听器
    final class BannerGestureListener implements GestureDetector.OnGestureListener {

        // 在手势按下时触发
        public final boolean onDown(MotionEvent event) {
            return true;
        }

        // 在手势飞快掠过时触发
        public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > mFlipGap) { // 从右向左掠过
                startFlip(); // 播放下一个场景
                return true;
            }
            if (e1.getX() - e2.getX() < -mFlipGap) { // 从左向右掠过
                backFlip(); // 播放上一个场景
                return true;
            }
            return false;
        }

        // 在手势长按时触发
        public final void onLongPress(MotionEvent event) {}

        // 在手势滑动过程中触发
        public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //return false;
            // 如果外层是普通的ScrollView，则此处不允许父容器的拦截动作
            // CustomScrollActivity里面通过自定义ScrollView，来区分水平滑动还是垂直滑动
            // BannerOptimizeActivity使用系统ScrollView，则此处需要下面代码禁止父容器的拦截
            if (Math.abs(distanceY) < Math.abs(distanceX)) { // 水平方向的滚动
                // 告诉上级布局不要拦截触摸事件
                BannerFlipper.this.getParent().requestDisallowInterceptTouchEvent(true);
                return true; // 返回true表示要继续处理
            } else { // 垂直方向的滚动
                return false; // 返回false表示不处理了
            }
        }

        // 在已按下但还未滑动或松开时触发
        public final void onShowPress(MotionEvent event) {}

        // 在轻点弹起时触发，也就是点击时触发
        public boolean onSingleTapUp(MotionEvent event) {
            // 获得正在播放的场景位置
            int position = mFlipper.getDisplayedChild();
            // 触发横幅点击监听器的横幅点击事件
            mListener.onBannerClick(position);
            return false;
        }
    }

    // 播放下一个场景
    public void startFlip() {
        mFlipper.startFlipping(); // 开始轮播
        mFlipper.showNext(); // 显示下一个场景
    }

    // 播放上一个场景
    public void backFlip() {
        mFlipper.startFlipping(); // 开始轮播
        mFlipper.showPrevious(); // 显示上一个场景
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个指示器的刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 获得正在播放的场景位置
            int pos = mFlipper.getDisplayedChild();
            // 根据场景位置，设置当前的高亮指示圆点
            ((RadioButton) mGroup.getChildAt(pos)).setChecked(true);
            // 延迟200毫秒后再次启动指示器刷新任务
            mHandler.postDelayed(this, 200);
        }
    };

    private BannerClickListener mListener; // 声明一个横幅点击的监听器对象
    // 设置横幅点击监听器
    public void setOnBannerListener(BannerClickListener listener) {
        mListener = listener;
    }

    // 定义一个横幅点击的监听器接口
    public interface BannerClickListener {
        void onBannerClick(int position);
    }
}
