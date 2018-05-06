package com.example.event.widget;

import com.example.event.R;
import com.example.event.util.Utils;
import com.example.event.widget.PullDownScrollView.ScrollListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@SuppressLint("ClickableViewAccessibility")
public class PullDownRefreshLayout extends LinearLayout implements View.OnTouchListener, ScrollListener {
    private final static String TAG = "PullDownRefreshLayout";
    private Context mContext; // 声明一个上下文对象
    private PullDownScrollView mScrollView; // 声明一个下拉滚动视图对象
    private LinearLayout mLinearLayout; // 下拉刷新头部的线性布局
    private int mLayoutHeight; // 下拉刷新头部的高度
    private float mOriginX = 0; // 落点的横坐标
    private float mOriginY = 0; // 落点的纵坐标
    private float mCurrentHeight = 0; // 当前的下拉高度
    private int mCriticalDistance; // 触发工具栏变色的临界滑动距离

    public PullDownRefreshLayout(Context context) {
        this(context, null);
    }

    public PullDownRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 触发工具栏变色的临界滑动距离
        mCriticalDistance = Utils.dip2px(mContext, 10);
        // 获取默认的下拉刷新头部布局
        mLinearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.drag_drop_header, null);
        // 计算下拉刷新头部布局的高度
        mLayoutHeight = Utils.getRealHeight(mLinearLayout);
        Log.d(TAG, "realWidth=" + mLinearLayout.getMeasuredWidth() + ",realHeight=" + mLinearLayout.getMeasuredHeight());
        // 间隔是负值，表示不但不远离，反而插了进去
        mLinearLayout.setPadding(0, -1 * mLayoutHeight, 0, 0);
        // 立刻刷新线性视图
        mLinearLayout.invalidate();
        // 把下拉刷新头部布局添加到最前面
        addView(mLinearLayout, 0);
    }

    // 刷新完毕，恢复原页面，也就是仍把下拉头部缩了回去
    public void finishRefresh() {
        resumePage();
    }

    // 在添加下级视图时触发
    public void addView(View view, int index, ViewGroup.LayoutParams params) {
        // PullDownRefreshLayout下面要有个PullDownScrollView节点，不然会报错
        if ((view instanceof PullDownScrollView)) {
            mScrollView = ((PullDownScrollView) view);
            // 设置触摸监听器，目的是监控拉动的距离
            mScrollView.setOnTouchListener(this);
            // 设置滚动监听器，目的是判断是否拉到顶部或者拉到底部
            mScrollView.setScrollListener(this);
        }
        super.addView(view, index, params);
    }

    // 在拦截触摸事件时触发
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        // 按下动作，记录下拉的起始位置
        if (action == MotionEvent.ACTION_DOWN) { // 手指按下
            mOriginX = event.getRawX(); // 获得落点的横轴坐标
            mOriginY = event.getRawY(); // 获得落点的纵轴坐标
            mCurrentHeight = mScrollView.getScrollY(); // 获得滚动视图的纵轴滚动距离
        }
        return super.onInterceptTouchEvent(event);
    }

    // 在发生触摸事件时触发
    public boolean onTouch(View view, MotionEvent event) {
        // 垂直方向的滚动距离小于临界距离，表示接近初始页面，需要把工具栏和状态栏恢复原样。
        // 否则表示页面正在上拉，需要给工具栏和状态栏变色。
        if (mScrollView.getScrollY() <= mCriticalDistance) {
            mListener.pullDown();
        } else {
            mListener.pullUp();
        }
        int action = event.getAction();
        // 按下0，松开1，滑动2
        Log.d(TAG, "getAction=" + event.getAction() + ",getScrollY=" + mScrollView.getScrollY());
        Log.d(TAG, "getRawX()=" + event.getRawX() + ",mOriginX=" + mOriginX + ",getRawY()=" + event.getRawY() + ",mOriginY=" + mOriginY);
        if (Math.abs(event.getRawX() - mOriginX) > Math.abs(event.getRawY() - mOriginY)) {
            // 水平方向滚动，不处理
            return false;
        } else if (event.getRawY() <= mOriginY) {
            // 往上拉动，不处理
            return false;
        } else if (mScrollView.getScrollY() > 0) {
            // 未拉到顶部，不处理
            return false;
        } else if (mScrollView.getScrollY() <= 0) {
            // 正在下拉，则隐藏工具栏
            mListener.hideTitle();
        }
        // 计算下拉过程的拉动距离
        float offsetY = event.getRawY() - mOriginY - mCurrentHeight;
        Log.d(TAG, "mCurrentHeight=" + mCurrentHeight + ",offsetY=" + offsetY + ",getScrollY=" + mScrollView.getScrollY());
        if (action == MotionEvent.ACTION_DOWN) { // 手指按下
            mOriginY = event.getRawY();
        } else if (action == MotionEvent.ACTION_MOVE) { // 手指移动
            // 下拉刷新的实际距离减半，看起来不会太突兀
            int dragOffset = (-1 * mLayoutHeight) + (int) offsetY / 2;
            Log.d(TAG, "ACTION_MOVE dragOffset=" + dragOffset + ",offsetY=" + offsetY + ",event.getRawY()=" + event.getRawY());
            // 下拉刷新的头部布局露出庐山真面目啦
            mLinearLayout.setPadding(0, dragOffset, 0, 0);
            // 立刻刷新线性视图
            mLinearLayout.invalidate();
        } else if (action == MotionEvent.ACTION_UP) { // 手指松开
            // 下拉距离太短，则直接将页面恢复原状。只有下拉距离足够长，才会触发页面刷新动作
            if (offsetY <= Utils.dip2px(mContext, 150)) {
                resumePage();
            } else {
                mListener.pullRefresh();
            }
        }
        return true;
    }

    // 恢复主页面
    private void resumePage() {
        // 设置线性视图上方的空白距离
        mLinearLayout.setPadding(0, (-1 * mLayoutHeight), 0, 0);
        // 立刻刷新线性视图
        mLinearLayout.invalidate();
        mListener.showTitle();
    }

    // 在滚动到底部时触发
    public void onScrolledToBottom() {}

    // 在滚动到顶部时触发
    public void onScrolledToTop() {
        mListener.pullDown();
    }

    private PullRefreshListener mListener; // 声明一个下拉刷新的监听器对象
    // 设置下拉刷新监听器
    public void setOnRefreshListener(PullRefreshListener listener) {
        mListener = listener;
    }

    // 定义一个下拉刷新的监听器接口
    public interface PullRefreshListener {
        void pullUp(); // 正在上拉
        void pullDown();// 正在下拉
        void pullRefresh(); // 开始刷新动作
        void hideTitle(); // 隐藏标题栏
        void showTitle(); // 显示标题栏
    }

}
