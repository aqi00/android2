package com.example.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;

public class OffsetLayout extends AbsoluteLayout {
    private int mOffsetHorizontal = 0; // 水平方向的偏移量
    private int mOffsetVertical = 0; // 垂直方向的偏移量

    public OffsetLayout(Context context) {
        super(context);
    }

    public OffsetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 重写onLayout方法，意在调整下级视图的方位
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i); // 获得第i个子视图
            if (child.getVisibility() != GONE) {
                // 计算子视图的左边偏移数值
                int new_left = (r - 1) / 2 - child.getMeasuredWidth() / 2 + mOffsetHorizontal;
                // 计算子视图的上方偏移数值
                int new_top = (b - t) / 2 - child.getMeasuredHeight() / 2 + mOffsetVertical;
                // 根据最新的上下左右四周边界，重新放置该子视图
                child.layout(new_left, new_top,
                        new_left + child.getMeasuredWidth(), new_top + child.getMeasuredHeight());
            }
        }
    }

    // 设置水平方向上的偏移量
    public void setOffsetHorizontal(int offset) {
        mOffsetHorizontal = offset;
        mOffsetVertical = 0;
        // 请求重新布局，此时会触发onLayout方法
        requestLayout();
    }

    // 设置垂直方向上的偏移量
    public void setOffsetVertical(int offset) {
        mOffsetHorizontal = 0;
        mOffsetVertical = offset;
        // 请求重新布局，此时会触发onLayout方法
        requestLayout();
    }
}
