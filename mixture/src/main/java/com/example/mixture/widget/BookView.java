package com.example.mixture.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.widget.FrameLayout;

public class BookView extends FrameLayout {
    private final static String TAG = "BookView";
    private int mWidth; // 视图的宽度
    private boolean isUpToTop = false; // 是否高亮显示
    private MarginLayoutParams mParams; // 空白边缘的布局参数
    public static int DIRECTION_LEFT = -1; // 左边方向
    public static int DIRECTION_RIGHT = 1; // 右边方向

    public BookView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isUpToTop) { // 已经是最上面一页
            // 给画布涂上透明颜色，也就是去掉遮罩
            canvas.drawColor(Color.TRANSPARENT);
        } else { // 不是最上面一页
            // 给画布涂上半透明颜色，也就是加上遮罩
            canvas.drawColor(0x55000000);
        }
    }

    // 设置是否高亮显示
    public void setUp(boolean isUp) {
        isUpToTop = isUp;
        invalidate(); // 立即刷新视图
    }

    // 设置视图的左侧边缘
    public void setMargin(int margin) {
        mParams = (MarginLayoutParams) getLayoutParams();
        mParams.leftMargin = margin;
        setLayoutParams(mParams); // 设置视图的布局参数
        invalidate(); // 立即刷新视图
    }

    // 开始滚动视图
    public void scrollView(int direction, int distance, OnScrollListener listener) {
        mListener = listener;
        // 延迟若干时间后启动滚动任务
        mHandler.postDelayed(new ScrollRunnable(direction, distance), mTimeGap);
    }

    private OnScrollListener mListener; // 声明一个滚动监听器对象
    // 定义一个滚动监听器接口，用于在滚动结束后触发onScrollEnd方法
    public interface OnScrollListener {
        void onScrollEnd(int direction);
    }

    private int mTimeGap = 20; // 每次滚动的时间间隔，单位毫秒
    private int mDistanceGap = 20; // 每次滚动的间距大小
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    // 定义一个滚动任务，用于在手指松开时自动判断继续往左滚还是往右滚
    private class ScrollRunnable implements Runnable {
        private int mDirection; // 滚动方向
        private int mDistance; // 剩余距离

        public ScrollRunnable(int direction, int distance) {
            mDirection = direction;
            mDistance = distance;
        }

        @Override
        public void run() {
            if (mDirection == DIRECTION_LEFT && mDistance > -mWidth) { // 往左滚，且尚未滚完
                mDistance -= mDistanceGap;
                if (mDistance < -mWidth) {
                    mDistance = -mWidth;
                }
                mParams.leftMargin = mDistance;
                setLayoutParams(mParams); // 设置视图的布局参数
                // 延迟若干时间后再次启动滚动任务
                mHandler.postDelayed(new ScrollRunnable(mDirection, mDistance), mTimeGap);
            } else if (mDirection == DIRECTION_RIGHT && mDistance < 0) { // 往右滚，且尚未滚完
                mDistance += mDistanceGap;
                if (mDistance > 0) {
                    mDistance = 0;
                }
                mParams.leftMargin = mDistance;
                setLayoutParams(mParams); // 设置视图的布局参数
                // 延迟若干时间后再次启动滚动任务
                mHandler.postDelayed(new ScrollRunnable(mDirection, mDistance), mTimeGap);
            } else if (mListener != null) { // 已经滚动完毕
                mListener.onScrollEnd(mDirection);
            }
        }
    }
}
