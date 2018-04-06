package com.example.group.widget;

import com.example.group.R;
import com.example.group.util.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class PagerIndicator extends LinearLayout {
    private static final String TAG = "PagerIndicator";
    private Context mContext; // 声明一个上下文对象
    private int mCount = 5; // 指示器的个数
    private int mPad; // 两个圆点之间的间隔
    private int mSeq = 0; // 当前指示器的序号
    private float mRatio = 0.0f; // 已经移动的距离百分比
    private Paint mPaint; // 声明一个画笔对象
    private Bitmap mBackImage; // 背景位图，通常是灰色圆点
    private Bitmap mForeImage; // 前景位图，通常是高亮的红色圆点

    public PagerIndicator(Context context) {
        this(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        // 创建一个新的画笔
        mPaint = new Paint();
        mPad = Utils.dip2px(mContext, 15);
        // 从资源图片icon_point_n.png中得到背景位图对象
        mBackImage = BitmapFactory.decodeResource(getResources(), R.drawable.icon_point_n);
        // 从资源图片icon_point_c.png中得到前景位图对象
        mForeImage = BitmapFactory.decodeResource(getResources(), R.drawable.icon_point_c);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int left = (getMeasuredWidth() - mCount * mPad) / 2;
        // 先绘制作为背景的几个灰色圆点
        for (int i = 0; i < mCount; i++) {
            canvas.drawBitmap(mBackImage, left + i * mPad, 0, mPaint);
        }
        // 再绘制作为前景的高亮红点，该红点随着翻页滑动而左右滚动
        canvas.drawBitmap(mForeImage, left + (mSeq + mRatio) * mPad, 0, mPaint);
    }

    // 设置指示器的个数，以及指示器之间的距离
    public void setCount(int count, int pad) {
        mCount = count;
        mPad = Utils.dip2px(mContext, pad);
        invalidate(); // 立刻刷新视图
    }

    // 设置指示器当前移动到的位置，及其位移比率
    public void setCurrent(int seq, float ratio) {
        mSeq = seq;
        mRatio = ratio;
        invalidate(); // 立刻刷新视图
    }

}
