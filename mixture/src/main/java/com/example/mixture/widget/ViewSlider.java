package com.example.mixture.widget;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.example.mixture.util.Utils;

@SuppressLint("ClickableViewAccessibility")
public class ViewSlider extends FrameLayout implements BookView.OnScrollListener {
    private final static String TAG = "ViewSlider";
    private Context mContext;
    private int mWidth; // 视图宽度
    private float mLastX = 0; // 上次按下点的横坐标
    private ArrayList<String> mPathArray = new ArrayList<String>(); // 图片路径队列
    private int mPos = 0; // 当前书页的序号
    private BookView mPreView, mCurrentView, mNextView; // 上一个视图、当前视图、下一个视图
    private int mShowPage; // 显示页面类型
    private static int SHOW_NONE = 0; // 无页面
    private static int SHOW_PRE = 1; // 拉出上一个页面
    private static int SHOW_NEXT = 2; // 拉出下一个页面
    private boolean isScrolling = false; // 是否正在滚动

    public ViewSlider(Context context) {
        this(context, null);
    }

    public ViewSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    // 设置图片路径队列
    public void setFilePath(ArrayList<String> pathArray) {
        removeAllViews(); // 移除所有下级视图
        mPathArray = pathArray;
        if (mPathArray.size() > 0) {
            mCurrentView = getBookPage(0, true);
            addView(mCurrentView); // 添加当前书页视图
        }
        if (mPathArray.size() > 1) {
            mNextView = getBookPage(1, false);
            addView(mNextView, 0); // 添加下一个书页视图
        }
    }

    // 获取一个书页视图
    private BookView getBookPage(int position, boolean isUp) {
        // 创建一个书页视图
        BookView page = new BookView(mContext);
        MarginLayoutParams params = new LinearLayout.LayoutParams(
                mWidth, LayoutParams.WRAP_CONTENT);
        page.setLayoutParams(params); // 设置书页视图的布局参数
        Bitmap bitmap = BitmapFactory.decodeFile(mPathArray.get(position));
        int iv_height = bitmap.getHeight() < Utils.dip2px(mContext, 300)
                ? Utils.dip2px(mContext, 300) : bitmap.getHeight();
        MarginLayoutParams iv_params = new LinearLayout.LayoutParams(
                mWidth, iv_height);
        ImageView iv = new ImageView(mContext);
        iv.setLayoutParams(iv_params);
        iv.setScaleType(ScaleType.FIT_CENTER);
        iv.setImageBitmap(bitmap);
        page.addView(iv); // 把图像视图添加到书页视图
        page.setUp(isUp); // 设置是否高亮显示
        return page;
    }

    // 在发生触摸事件时触发
    public boolean onTouchEvent(MotionEvent event) {
        if (isScrolling) { // 正在滚动则忽略触摸事件
            return super.onTouchEvent(event);
        }
        int distanceX = (int) (event.getRawX() - mLastX);
        Log.d(TAG, "action=" + event.getAction() + ", distanceX=" + distanceX);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下
                mLastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE: // 手指移动
                if (distanceX > 0) {  // 拉出上一页
                    if (mPos == 0) {
                        mShowPage = SHOW_NONE;
                    } else {
                        mShowPage = SHOW_PRE;
                        mPreView.setUp(true); // 高亮显示上一个书页
                        mPreView.setMargin(-mWidth + distanceX); // 设置上一个书页的左侧边缘
                        mCurrentView.setUp(false); // 当前书页取消高亮
                    }
                } else {  // 拉出下一页
                    if (mPos == mPathArray.size() - 1 || mNextView == null) {
                        mShowPage = SHOW_NONE;
                    } else if (mNextView != null) {
                        mShowPage = SHOW_NEXT;
                        mCurrentView.setMargin(distanceX); // 设置当前书页的左侧边缘
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 手指松开
                if (mShowPage == SHOW_PRE) { // 原来在拉出上一页
                    // 根据已拉出的距离计算接下来要往哪个方向滚动
                    int direction = Math.abs(distanceX) < mWidth / 2 ? BookView.DIRECTION_LEFT : BookView.DIRECTION_RIGHT;
                    //Log.d(TAG, "direction="+direction+", mShowPage="+mShowPage+", distanceX="+distanceX);
                    // 命令上一个书页自行滚动到位
                    mPreView.scrollView(direction, -mWidth + distanceX, this);
                    isScrolling = true;
                } else if (mShowPage == SHOW_NEXT) { // 原来在拉出下一页
                    // 根据已拉出的距离计算接下来要往哪个方向滚动
                    int direction = Math.abs(distanceX) > mWidth / 2 ? BookView.DIRECTION_LEFT : BookView.DIRECTION_RIGHT;
                    //Log.d(TAG, "direction="+direction+", mShowPage="+mShowPage+", distanceX="+distanceX);
                    // 命令下一个书页自行滚动到位
                    mCurrentView.scrollView(direction, distanceX, this);
                    isScrolling = true;
                } else { // 没有拉出任何页面
                    isScrolling = false;
                }
                break;
        }
        return true;
    }

    // 在滚动完成后触发
    public void onScrollEnd(int direction) {
        //Log.d(TAG, "direction="+direction+", mPos="+mPos);
        if (mShowPage == SHOW_PRE) { // 原来在拉出上一页
            if (direction == BookView.DIRECTION_RIGHT) { // 往右滚动
                mPos--;
                if (mNextView != null) {
                    removeView(mNextView); // 移除下一页视图
                }
                mNextView = mCurrentView; // 之前的当前视图变成了现在的下一个视图
                mCurrentView = mPreView; // 之前的上一页视图变成了现在的当前视图
                if (mPos > 0) {
                    mPreView = getBookPage(mPos - 1, false);
                    addView(mPreView); // 添加现在的上一页视图
                    mPreView.setMargin(-mWidth);
                } else {
                    mPreView = null;
                }
            }
            mCurrentView.setUp(true);
        } else if (mShowPage == SHOW_NEXT) { // 原来在拉出下一页
            if (direction == BookView.DIRECTION_LEFT) { // 往左滚动
                mPos++;
                if (mPreView != null) {
                    removeView(mPreView); // 移除上一页视图
                }
                mPreView = mCurrentView; // 之前的当前视图变成了现在的上一个视图
                mCurrentView = mNextView; // 之前的下一页视图变成了现在的当前视图
                if (mPos < mPathArray.size() - 1) {
                    mNextView = getBookPage(mPos + 1, false);
                    addView(mNextView, 0); // 添加现在的下一页视图
                } else {
                    mNextView = null;
                }
            }
            mCurrentView.setUp(true); // 高亮显示当前书页
        }
        isScrolling = false;
    }

}
