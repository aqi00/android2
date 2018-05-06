package com.example.device.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

public class TurnTextureView extends TextureView implements SurfaceTextureListener, Runnable {
    private Paint mPaint; // 声明一个画笔对象
    private RectF mRectF; // 矩形边界
    private int mBeginAngle = 0; // 起始角度
    private boolean isRunning = false; // 是否正在转动

    public TurnTextureView(Context context) {
        this(context, null);
    }

    public TurnTextureView(Context context, AttributeSet attrs) {
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

    // 开始转动
    public void start() {
        isRunning = true;
        // 启动绘制线程
        new Thread(this).start();
    }

    public void stop() {
        isRunning = false;
    }

    // 停止转动
    @Override
    public void run() {
        while (isRunning) {
            draw(false);
            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBeginAngle += 3;
        }
    }

    // 绘制图形
    private void draw(boolean isFirst) {
        // 锁定纹理视图的画布对象
        Canvas canvas = lockCanvas();
        if (canvas != null) {
            // TextureView上次的绘图结果仍然保留，如果不想保留上次的绘图，则需将整个画布清空
            // canvas.drawColor(Color.WHITE);
            if (!isFirst) {
                // 在画布上绘制扇形。第四个参数为true表示绘制扇形，为false表示绘制圆弧
                canvas.drawArc(mRectF, mBeginAngle, 30, true, mPaint);
            }
            // 解锁纹理视图的画布对象
            unlockCanvasAndPost(canvas);
        }
    }

    // 在纹理表面可用时触发
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        draw(true);
    }

    // 在纹理表面销毁时触发
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    // 在纹理表面的尺寸发生改变时触发
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    // 在纹理表面更新时触发
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
}
