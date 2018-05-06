package com.example.network.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.example.network.util.Utils;

public class TextProgressBar extends ProgressBar {
    private String mProgressText = ""; // 进度文本
    private Paint mPaint; // 声明一个画笔对象
    private int mTextColor = Color.WHITE; // 文字颜色
    private int mTextSize; // 文字大小

    public TextProgressBar(Context context) {
        this(context, null);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextSize = Utils.dip2px(context, 15);
        initPaint();
    }

    // 初始化画笔
    private void initPaint() {
        mPaint = new Paint(); // 创建一个画笔
        mPaint.setColor(mTextColor); // 设置画笔的颜色
        mPaint.setTextSize(mTextSize); // 设置文本的大小
    }

    // 设置进度文字的文本内容
    public void setProgressText(String text) {
        mProgressText = text;
    }

    // 获取进度文字的文本内容
    public String getProgressText() {
        return mProgressText;
    }

    // 设置文字颜色
    public void setTextColor(int color) {
        mTextColor = color;
    }

    // 获取文字颜色
    public int getTextColor() {
        return mTextColor;
    }

    // 设置文字大小
    public void setTextSize(int size) {
        mTextSize = size;
    }

    // 获取文字大小
    public int getTextSize() {
        return mTextSize;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        // 获得进度文字的矩形边界
        mPaint.getTextBounds(mProgressText, 0, mProgressText.length(), rect);
        // 计算进度文字左上角的横坐标
        int x = (getWidth() / 2) - rect.centerX();
        // 计算进度文字左上角的纵坐标
        int y = (getHeight() / 2) - rect.centerY();
        // 在画布上绘制进度文字
        canvas.drawText(mProgressText, x, y, this.mPaint);
    }

}
