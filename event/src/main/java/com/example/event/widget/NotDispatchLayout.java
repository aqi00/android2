package com.example.event.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class NotDispatchLayout extends LinearLayout {

    public NotDispatchLayout(Context context) {
        super(context);
    }

    public NotDispatchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 在分发触摸事件时触发
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mListener != null) {
            mListener.onNotDispatch();
        }
        // 一般容器默认返回true，即允许分发给下级
        return false;
    }

    private NotDispatchListener mListener; // 声明一个分发监听器对象
    // 设置分发监听器
    public void setNotDispatchListener(NotDispatchListener listener) {
        mListener = listener;
    }

    // 定义一个分发监听器接口
    public interface NotDispatchListener {
        void onNotDispatch();
    }

}
