package com.example.media.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

@SuppressLint("ClickableViewAccessibility")
public class FloatWindow extends View {
    private final static String TAG = "FloatWindow";
    private Context mContext; // 声明一个上下文对象
    private WindowManager wm; // 声明一个窗口管理器对象
    private static WindowManager.LayoutParams wmParams;
    public View mContentView; // 声明一个内容视图对象
    private float mScreenX, mScreenY; // 触摸点在屏幕上的横纵坐标
    private float mLastX, mLastY; // 上次触摸点的横纵坐标
    private float mDownX, mDownY; // 按下点的横纵坐标
    private boolean isShowing = false; // 是否正在显示

    public FloatWindow(Context context) {
        super(context);
        // 从系统服务中获取窗口管理器，后续将通过该管理器添加悬浮窗
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wmParams == null) {
            wmParams = new WindowManager.LayoutParams();
        }
        mContext = context;
    }

    // 设置悬浮窗的内容布局
    public void setLayout(int layoutId) {
        // 从指定资源编号的布局文件中获取内容视图对象
        mContentView = LayoutInflater.from(mContext).inflate(layoutId, null);
        // 接管悬浮窗的触摸事件，使之即可随手势拖动，又可处理点击动作
        mContentView.setOnTouchListener(new OnTouchListener() {
            // 在发生触摸事件时触发
            public boolean onTouch(View v, MotionEvent event) {
                mScreenX = event.getRawX();
                mScreenY = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 手指按下
                        mDownX = mScreenX;
                        mDownY = mScreenY;
                        break;
                    case MotionEvent.ACTION_MOVE: // 手指移动
                        updateViewPosition(); // 更新视图的位置
                        break;
                    case MotionEvent.ACTION_UP: // 手指松开
                        updateViewPosition(); // 更新视图的位置
                        // 响应悬浮窗的点击事件
                        if (Math.abs(mScreenX - mDownX) < 3
                                && Math.abs(mScreenY - mDownY) < 3) {
                            if (mListener != null) {
                                mListener.onFloatClick(v);
                            }
                        }
                        break;
                }
                mLastX = mScreenX;
                mLastY = mScreenY;
                return true;
            }
        });
    }

    // 更新悬浮窗的视图位置
    private void updateViewPosition() {
        // 此处不能直接转为整型，因为小数部分会被截掉，重复多次后就会造成偏移越来越大
        wmParams.x = Math.round(wmParams.x + mScreenX - mLastX);
        wmParams.y = Math.round(wmParams.y + mScreenY - mLastY);
        // 通过窗口管理器更新内容视图的布局参数
        wm.updateViewLayout(mContentView, wmParams);
    }

    // 显示悬浮窗
    public void show() {
        if (mContentView != null) {
            // 设置为TYPE_SYSTEM_ALERT类型，才能悬浮在其它页面之上
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // 注意TYPE_SYSTEM_ALERT从Android8.0开始被舍弃了
                wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                // 从Android8.0开始悬浮窗要使用TYPE_APPLICATION_OVERLAY
                wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            wmParams.format = PixelFormat.RGBA_8888;
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.alpha = 1.0f; // 1.0为完全不透明，0.0为完全透明
            // 对齐方式为靠左且靠上，因此悬浮窗的初始位置在屏幕的左上角
            wmParams.gravity = Gravity.LEFT | Gravity.TOP;
            wmParams.x = 0;
            wmParams.y = 0;
            // 设置悬浮窗的宽度和高度为自适应
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 添加自定义的窗口布局，然后屏幕上就能看到悬浮窗了
            wm.addView(mContentView, wmParams);
            isShowing = true;
        }
    }

    // 关闭悬浮窗
    public void close() {
        if (mContentView != null) {
            // 移除自定义的窗口布局
            wm.removeView(mContentView);
            isShowing = false;
        }
    }

    // 判断悬浮窗是否打开
    public boolean isShow() {
        return isShowing;
    }

    private FloatClickListener mListener; // 声明一个悬浮窗的点击监听器对象
    // 设置悬浮窗的点击监听器
    public void setOnFloatListener(FloatClickListener listener) {
        mListener = listener;
    }

    // 定义一个悬浮窗的点击监听器接口，用于触发点击行为
    public interface FloatClickListener {
        void onFloatClick(View v);
    }

}
