package com.example.event.widget;

import java.util.ArrayList;

import com.example.event.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("ClickableViewAccessibility")
public class SignatureView extends View {
    private static final String TAG = "SignatureView";
    private Paint mPaint; // 声明一个画笔对象
    private Canvas mCanvas; // 声明一个画布对象
    private Bitmap mBitmap; // 声明一个位图对象
    private Path mPath; // 声明一个路径对象
    private int mPaintColor = Color.BLACK; // 画笔颜色
    private int mStrokeWidth = 3; // 画笔线宽
    private PathPosition mPos = new PathPosition(); // 路径位置
    private ArrayList<PathPosition> mPathArray = new ArrayList<PathPosition>(); // 路径位置队列
    private float mLastX, mLastY; // 上次触摸点的横纵坐标

    public SignatureView(Context context) {
        super(context);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            // 根据SignatureView的属性定义，从布局文件中获取属性数组描述
            TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.SignatureView);
            // 根据属性描述定义，获取布局文件中的画笔颜色
            mPaintColor = attrArray.getColor(R.styleable.SignatureView_paint_color, Color.BLACK);
            // 根据属性描述定义，获取布局文件中的画笔线宽
            mStrokeWidth = attrArray.getInt(R.styleable.SignatureView_stroke_width, 3);
            // 回收属性数组描述
            attrArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initView(getMeasuredWidth(), getMeasuredHeight());
    }

    // 初始化视图
    private void initView(int width, int height) {
        mPaint = new Paint(); // 创建新画笔
        mPaint.setAntiAlias(true); //设置画笔为无锯齿
        mPaint.setStrokeWidth(mStrokeWidth); // 设置画笔的线宽
        mPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型。STROK表示空心，FILL表示实心
        mPaint.setColor(mPaintColor); // 设置画笔的颜色
        mPath = new Path(); // 创建新路径
        // 开启当前视图的绘图缓存
        setDrawingCacheEnabled(true);
        // 创建一个空白位图
        mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        // 根据空白位图创建画布
        mCanvas = new Canvas(mBitmap);
        clear();
    }

    // 清空画布
    public void clear() {
        if (mCanvas != null) {
            // 清空路径位置队列
            mPathArray.clear();
            // 给画布设置透明背景
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate(); // 立刻刷新视图
        }
    }

    // 撤销上一次绘制
    public void revoke() {
        if (mPathArray.size() > 0) {
            // 移除路径位置队列中的最后一个路径
            mPathArray.remove(mPathArray.size() - 1);
            // 给画布设置透明背景
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            for (int i = 0; i < mPathArray.size(); i++) {
                Path posPath = new Path();
                // 移动到下一个坐标点
                posPath.moveTo(mPathArray.get(i).firstX, mPathArray.get(i).firstY);
                // 连接上一个坐标点和下一个坐标点
                posPath.quadTo(mPathArray.get(i).firstX, mPathArray.get(i).firstY,
                        mPathArray.get(i).nextX, mPathArray.get(i).nextY);
                // 在画布上绘制指定路径线条
                mCanvas.drawPath(posPath, mPaint);
            }
            invalidate(); // 立刻刷新视图
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 在画布上绘制指定位图
        canvas.drawBitmap(mBitmap, 0, 0, null);
        // 在画布上绘制指定路径线条
        canvas.drawPath(mPath, mPaint);
    }

    // 在发生触摸事件时触发
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下
                // 移动到指定坐标点
                mPath.moveTo(event.getX(), event.getY());
                mPos.firstX = event.getX();
                mPos.firstY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE: // 手指移动
                // 连接上一个坐标点和当前坐标点
                mPath.quadTo(mLastX, mLastY, event.getX(), event.getY());
                mPos.nextX = event.getX();
                mPos.nextY = event.getY();
                // 往路径位置队列添加路径位置
                mPathArray.add(mPos);
                // 创建新的路径位置
                mPos = new PathPosition();
                mPos.firstX = event.getX();
                mPos.firstY = event.getY();
                break;
            case MotionEvent.ACTION_UP: // 手指松开
                // 在画布上绘制指定路径线条
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                break;
        }
        mLastX = event.getX();
        mLastY = event.getY();
        invalidate(); // 立刻刷新视图
        return true;
    }

    // 定义一个路径位置实体类，包括当前落点的横纵坐标，以及下个落点的横纵坐标
    private class PathPosition {
        public float firstX;
        public float firstY;
        public float nextX;
        public float nextY;

        public PathPosition() {
            firstX = 0;
            firstY = 0;
            nextX = 0;
            nextY = 0;
        }
    }

}
