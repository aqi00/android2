package com.example.network.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.network.util.Utils;

@SuppressLint("DrawAllocation")
public class TextProgressCircle extends View {
    private Paint mPaintBack; // 声明一个背景画笔对象
    private Paint mPaintFore; // 声明一个前景画笔对象
    private Paint mPaintText; // 声明一个文本画笔对象
    private int mLineWidth = 10; // 线条的宽度
    private int mLineColor = Color.GREEN; // 线条的颜色
    private float mTextSize; // 文字大小
    private int mProgress = 0; // 进度值

    public TextProgressCircle(Context context) {
        this(context, null);
    }

    public TextProgressCircle(Context context, AttributeSet attr) {
        super(context, attr);
        mTextSize = Utils.dip2px(context, 40);
        initPaint();
    }

    // 初始化画笔
    private void initPaint() {
        // 以下初始化背景画笔
        mPaintBack = new Paint();
        mPaintBack.setAntiAlias(true);
        mPaintBack.setColor(Color.LTGRAY);
        mPaintBack.setStrokeWidth(mLineWidth);
        mPaintBack.setStyle(Style.STROKE);
        // 以下初始化前景画笔
        mPaintFore = new Paint();
        mPaintFore.setAntiAlias(true);
        mPaintFore.setColor(mLineColor);
        mPaintFore.setStrokeWidth(mLineWidth);
        mPaintFore.setStyle(Style.STROKE);
        // 以下初始化文本画笔
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.BLUE);
        mPaintText.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        int diameter = Math.min(width, height);
        RectF rectF = new RectF((width - diameter) / 2 + mLineWidth, (height - diameter) / 2 + mLineWidth,
                (width + diameter) / 2 - mLineWidth, (height + diameter) / 2 - mLineWidth);
        // 在画布上绘制完整的背景圆圈
        canvas.drawArc(rectF, 0, 360, false, mPaintBack);
        // 在画布上绘制规定进度的前景圆弧
        canvas.drawArc(rectF, 0, mProgress * 360 / 100, false, mPaintFore);
        String text = mProgress + "%";
        Rect rect = new Rect();
        // 获得进度文字的矩形边界
        mPaintText.getTextBounds(text, 0, text.length(), rect);
        // 计算进度文字左上角的横坐标
        int x = (getWidth() / 2) - rect.centerX();
        // 计算进度文字左上角的纵坐标
        int y = (getHeight() / 2) - rect.centerY();
        // 在画布上绘制进度文字
        canvas.drawText(text, x, y, mPaintText);
    }

    // 设置进度值和文字大小
    public void setProgress(int progress, float textSize) {
        mProgress = progress;
        if (textSize > 0) {
            mTextSize = textSize;
            mPaintText.setTextSize(mTextSize);
        }
        invalidate(); // 立刻刷新视图
    }

    // 设置圆圈的线宽和颜色
    public void setProgressStyle(int lineWidth, int lineColor) {
        if (lineWidth > 0) {
            mLineWidth = lineWidth;
            mPaintFore.setStrokeWidth(mLineWidth);
        }
        if (lineColor > 0) {
            mLineColor = lineColor;
            mPaintFore.setColor(mLineColor);
        }
        invalidate(); // 立刻刷新视图
    }

}
