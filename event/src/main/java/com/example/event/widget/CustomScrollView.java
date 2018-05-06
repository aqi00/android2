package com.example.event.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.example.event.util.Utils;

public class CustomScrollView extends ScrollView {
    private float mOffsetX, mOffsetY; // 横纵方向上的偏移
    private float mLastPosX, mLastPosY; // 上次落点的横纵坐标
    private int mInterval; // 与边缘线的间距阈值

    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attr) {
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
}
