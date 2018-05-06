package com.example.device.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TurnSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private Paint mPaint1, mPaint2; // 声明两个画笔对象
    private RectF mRectF; // 矩形边界
    private int mBeginAngle1 = 0, mBeginAngle2 = 180; // 两个扇形的起始角度
    private int mInterval = 70; // 绘制间隔
    private boolean isRunning = false; // 是否正在转动
    private final SurfaceHolder mHolder; // 声明一个表面持有者对象

    public TurnSurfaceView(Context context) {
        this(context, null);
    }

    public TurnSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取表面视图的表面持有者
        mHolder = getHolder();
        // 给表面持有者添加表面变更监听器
        mHolder.addCallback(this);
        // 下面两行设置背景为透明，因为SurfaceView默认背景是黑色
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
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
        // 绘制第一个扇形的线程
        new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    draw(mPaint1, mBeginAngle1);
                    try {
                        Thread.sleep(mInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBeginAngle1 += 3;
                }
            }
        }.start();
        // 绘制第二个扇形的线程，第二个扇形在第一个扇形的对面
        new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    draw(mPaint2, mBeginAngle2);
                    try {
                        Thread.sleep(mInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBeginAngle2 += 3;
                }
            }
        }.start();
    }

    // 停止转动
    public void stop() {
        isRunning = false;
    }

    // 绘制图形
    private void draw(Paint paint, int beginAngle) {
        // 因为两个线程都在绘制，所以这里利用同步机制，防止资源被锁住
        synchronized (mHolder) {
            // 锁定表面持有者的画布对象
            Canvas canvas = mHolder.lockCanvas();
            if (canvas != null) {
                // SurfaceView上次的绘图结果仍然保留，如果不想保留上次的绘图，则需将整个画布清空
                // canvas.drawColor(Color.WHITE);
                // 在画布上绘制扇形。第四个参数为true表示绘制扇形，为false表示绘制圆弧
                canvas.drawArc(mRectF, beginAngle, 10, true, paint);
                // 解锁表面持有者的画布对象
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    // 获取指定颜色的画笔
    private Paint getPaint(int color) {
        Paint paint = new Paint(); // 创建新画笔
        paint.setAntiAlias(true); // 设置画笔为无锯齿
        paint.setColor(color); // 设置画笔的颜色
        paint.setStrokeWidth(10); // 设置画笔的线宽
        paint.setStyle(Style.FILL); // 设置画笔的类型。STROK表示空心，FILL表示实心
        return paint;
    }

    // 在表面视图创建时触发
    public void surfaceCreated(SurfaceHolder holder) {
        mPaint1 = getPaint(Color.RED);
        mPaint2 = getPaint(Color.CYAN);
    }

    // 在表面视图变更时触发
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    // 在表面视图销毁时触发
    public void surfaceDestroyed(SurfaceHolder holder) {}
}
