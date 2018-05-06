package com.example.event.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptLayout extends LinearLayout {

    public InterceptLayout(Context context) {
        super(context);
    }

    public InterceptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 在拦截触摸事件时触发
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mListener != null) {
            mListener.onIntercept();
        }
        // 一般容器默认返回false，即不拦截。但滚动视图ScrollView会拦截
        return true;
    }

    private InterceptListener mListener; // 声明一个拦截监听器对象
    // 设置拦截监听器
    public void setInterceptListener(InterceptListener listener) {
        mListener = listener;
    }

    // 定义一个拦截监听器接口
    public interface InterceptListener {
        void onIntercept();
    }

}
