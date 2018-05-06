package com.example.animation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Scroller;
import android.widget.TextView;

// 平滑滚动的文本视图
public class ScrollTextView extends TextView {
    private Scroller mScroller; // 声明一个滚动器对象

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 创建一个新的滚动器
        mScroller = new Scroller(context);
    }

    // 平滑滚动到指定的绝对坐标
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy); // 滚动相对偏移
    }

    // 从当前位置平滑滚动到相对位移
    public void smoothScrollBy(int dx, int dy) {
        // 设置滚动偏移量，注意正数是往左滚往上滚，负数才是往右滚往下滚
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), -dx, -dy);
        // 调用invalidate方法才能保证computeScroll函数会被调用
        invalidate(); // 立即刷新视图
    }

    // 在调用invalidate方法之后触发
    @Override
    public void computeScroll() {
        // 判断滚动器是否已经滚动完成
        if (mScroller.computeScrollOffset()) {
            // 滚动到指定位置。调用View的scrollTo方法才能完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate(); // 刷新视图
        }
        super.computeScroll();
    }
}
