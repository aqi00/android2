package com.example.event.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.example.event.util.Utils;

/**
 * Created by ouyangshen on 2018/1/4.
 */
public class PullDownScrollView extends ScrollView {
    private float mOffsetX, mOffsetY; // 横纵方向上的偏移
    private float mLastPosX, mLastPosY; // 上次落点的横纵坐标
    private int mInterval; // 与边缘线的间距阈值

    public PullDownScrollView(Context context) {
        this(context, null);
    }

    public PullDownScrollView(Context context, AttributeSet attr) {
        super(context, attr);
        mInterval = Utils.dip2px(context, 3);
    }

    // 在拦截触摸事件时触发
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean result;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下
                mOffsetX = 0.0F;
                mOffsetY = 0.0F;
                mLastPosX = event.getX();
                mLastPosY = event.getY();
                result = super.onInterceptTouchEvent(event);
                break;
            default: // 其余动作，包括手指移动、手指松开等等
                float thisPosX = event.getX();
                float thisPosY = event.getY();
                mOffsetX += Math.abs(thisPosX - mLastPosX); // x轴偏差
                mOffsetY += Math.abs(thisPosY - mLastPosY); // y轴偏差
                mLastPosX = thisPosX;
                mLastPosY = thisPosY;
                if (mOffsetX < mInterval && mOffsetY < mInterval) {
                    result = false; // false传给表示子控件，此时为点击事件
                } else if (mOffsetX < mOffsetY) {
                    result = true; // true表示不传给子控件，此时为垂直滑动
                } else {
                    result = false; // false表示传给子控件，此时为水平滑动
                }
                break;
        }
        return result;
    }

    // 在滚动变更时触发
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        boolean isScrolledToTop;
        boolean isScrolledToBottom;
        if (getScrollY() == 0) { // 下拉滚动到顶部
            isScrolledToTop = true;
            isScrolledToBottom = false;
        } else if (getScrollY() + getHeight() - getPaddingTop() - getPaddingBottom()
                == getChildAt(0).getHeight()) { // 上拉滚动到底部
            isScrolledToBottom = true;
            isScrolledToTop = false;
        } else { // 未拉到顶部，也未拉到底部
            isScrolledToTop = false;
            isScrolledToBottom = false;
        }
        if (mScrollListener != null) {
            if (isScrolledToTop) { // 已经滚动到顶部
                // 触发下拉到顶部的事件
                mScrollListener.onScrolledToTop();
            } else if (isScrolledToBottom) { // 已经滚动到底部
                // 触发上拉到底部的事件
                mScrollListener.onScrolledToBottom();
            }
        }
    }

    private ScrollListener mScrollListener; // 声明一个滚动监听器对象
    // 设置滚动监听器
    public void setScrollListener(ScrollListener listener) {
        mScrollListener = listener;
    }

    // 定义一个滚动监听器接口，用于捕捉到达顶部和到达底部的事件
    public interface ScrollListener {
        void onScrolledToBottom(); // 已经滚动到底部
        void onScrolledToTop(); // 已经滚动到顶部
    }

}
