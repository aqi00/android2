package com.example.device.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class TurnView extends View {
    private Paint mPaint; // 声明一个画笔对象
    private RectF mRectF; // 矩形边界
    private int mBeginAngle = 0; // 起始角度
    private boolean isRunning = false; // 是否正在转动
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    public TurnView(Context context) {
        this(context, null);
    }

    public TurnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(); // 创建新画笔
        mPaint.setAntiAlias(true); // 设置画笔为无锯齿
        mPaint.setColor(Color.RED); // 设置画笔的颜色
        mPaint.setStrokeWidth(10); // 设置画笔的线宽
        mPaint.setStyle(Style.FILL); // 设置画笔的类型。STROK表示空心，FILL表示实心
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 计算转动圆圈的直径
        int diameter = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        // 根据圆圈直径创建转动区域的矩形边界
        mRectF = new RectF(getPaddingLeft(), getPaddingTop(),
                getPaddingLeft() + diameter, getPaddingTop() + diameter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 在画布上绘制扇形。第四个参数为true表示绘制扇形，为false表示绘制圆弧
        canvas.drawArc(mRectF, mBeginAngle, 30, true, mPaint);
    }

    // 开始转动
    public void start() {
        isRunning = true;
        // 立即启动绘制任务
        mHandler.post(drawRunnable);
    }

    // 停止转动
    public void stop() {
        isRunning = false;
    }

    // 定义一个绘制任务，通过持续绘制实现转动效果
    private Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) { // 正在转动
                // 延迟70毫秒后再次启动绘制任务
                mHandler.postDelayed(this, 70);
                mBeginAngle += 3;
                // 立即刷新视图，也就是调用视图的onDraw和dispatchDraw方法
                invalidate();
            } else { // 不在转动
                // 移除绘制任务
                mHandler.removeCallbacks(this);
            }
        }
    };
}
