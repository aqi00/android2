package com.example.media.task;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureTask implements GestureDetector.OnGestureListener {
    private float mFlipGap = 20f; // 滑动距离的阈值

    public GestureTask() {}

    // 在手势按下时触发
    public final boolean onDown(MotionEvent event) {
        return true;
    }

    // 在手势飞快掠过时触发
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > mFlipGap) { // 从右向左掠过
            if (mListener != null) {
                mListener.gotoNext(); // 播放下一个场景
            }
        }
        if (e1.getX() - e2.getX() < -mFlipGap) { // 从左向右掠过
            if (mListener != null) {
                mListener.gotoPre(); // 播放上一个场景
            }
        }
        return true;
    }

    // 在手势长按时触发
    public final void onLongPress(MotionEvent event) {}

    // 在手势滑动过程中触发
    public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    // 在已按下但还未滑动或松开时触发
    public final void onShowPress(MotionEvent event) {}

    // 在轻点弹起时触发，也就是点击时触发
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    private GestureCallback mListener; // 声明一个手势事件监听器对象
    // 设置手势事件监听器
    public void setGestureCallback(GestureCallback listener) {
        mListener = listener;
    }

    // 定义一个手势事件的监听器接口
    public interface GestureCallback {
        void gotoNext();
        void gotoPre();
    }

}
