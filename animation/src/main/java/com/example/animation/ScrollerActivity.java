package com.example.animation;

import com.example.animation.widget.ScrollTextView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by ouyangshen on 2017/11/27.
 */
@SuppressLint("ClickableViewAccessibility")
public class ScrollerActivity extends AppCompatActivity implements OnTouchListener, OnGestureListener {
    private ScrollTextView stv_rough; // 声明一个滚动文本视图对象
    private GestureDetector mGesture; // 声明一个手势检测器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller);
        // 从布局文件中获取名叫stv_rough的滚动文本视图
        stv_rough = findViewById(R.id.stv_rough);
        // 给滚动文本视图设置触摸监听器
        stv_rough.setOnTouchListener(this);
        // 创建一个手势检测器
        mGesture = new GestureDetector(this, this);
    }

    // 在发生触摸事件时触发
    public boolean onTouch(View v, MotionEvent event) {
        return mGesture.onTouchEvent(event);
    }

    // 在手势按下时触发
    public boolean onDown(MotionEvent e) {
        return true;
    }

    // 在已按下但还未滑动或松开时触发
    public void onShowPress(MotionEvent e) {}

    // 在轻点弹起时触发，也就是点击时触发
    public boolean onSingleTapUp(MotionEvent e) {
        stv_rough.setText("您轻轻点击了一下");
        return true;
    }

    // 在手势滑动过程中触发
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    // 在手势长按时触发
    public void onLongPress(MotionEvent e) {}

    // 在手势飞快掠过时触发
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        stv_rough.setText("您拖动我啦");
        float offsetX = e2.getRawX() - e1.getRawX();
        float offsetY = e2.getRawY() - e1.getRawY();
        // 命令滚动文本视图平滑滚动若干位移
        stv_rough.smoothScrollBy((int) offsetX, (int) offsetY);
        return true;
    }

}
