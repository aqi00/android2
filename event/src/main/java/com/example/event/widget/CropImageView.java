package com.example.event.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.event.util.Utils;

@SuppressLint("ClickableViewAccessibility")
public class CropImageView extends ImageView {
    private Bitmap mOrigBitmap = null; // 声明一个原始的位图对象
    private Bitmap mCropBitmap = null; // 声明一个裁剪后的位图对象
    private Rect mRect = new Rect(0, 0, 0, 0); // 矩形边界
    private int mInterval; // 与边缘线的间距阈值
    private float mOriginX, mOriginY; // 按下时候落点的横纵坐标
    private Rect mOriginRect; // 原始的矩形边界

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInterval = Utils.dip2px(context, 10);
    }

    // 设置原始的位图对象
    public void setOrigBitmap(Bitmap orig) {
        mOrigBitmap = orig;
    }

    // 获得裁剪后的位图对象
    public Bitmap getCropBitmap() {
        return mCropBitmap;
    }

    // 设置位图的矩形边界
    public boolean setBitmapRect(Rect rect) {
        if (mOrigBitmap == null) { // 原始位图为空
            return false;
        } else if (rect.left < 0 || rect.left > mOrigBitmap.getWidth()) { // 左侧边界非法
            return false;
        } else if (rect.top < 0 || rect.top > mOrigBitmap.getHeight()) { // 上方边界非法
            return false;
        } else if (rect.right <= 0 || rect.left + rect.right > mOrigBitmap.getWidth()) { // 右侧边界非法
            return false;
        } else if (rect.bottom <= 0 || rect.top + rect.bottom > mOrigBitmap.getHeight()) { // 下方边界非法
            return false;
        }
        mRect = rect;
        // 设置视图的四周间隔
        setPadding(mRect.left, mRect.top, 0, 0);
        // 根据指定的四周边界，裁剪相应尺寸的位图对象
        mCropBitmap = Bitmap.createBitmap(mOrigBitmap,
                mRect.left, mRect.top, mRect.right, mRect.bottom);
        // 设置图像视图的位图内容
        setImageBitmap(mCropBitmap);
        postInvalidate(); // 立即刷新视图
        return true;
    }

    // 在发生触摸事件时触发
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下
                mOriginX = event.getX();
                mOriginY = event.getY();
                mOriginRect = mRect;
                // 根据落点坐标与矩形边界的相对位置，决定本次拖曳动作的类型
                mDragMode = getDragMode(mOriginX, mOriginY);
                break;
            case MotionEvent.ACTION_MOVE: // 手指移动
                int offsetX = (int) (event.getX() - mOriginX);
                int offsetY = (int) (event.getY() - mOriginY);
                Rect rect = null;
                if (mDragMode == DRAG_NONE) { // 无拖曳动作
                    return true;
                } else if (mDragMode == DRAG_WHOLE) { // 拖动整个矩形边界框
                    rect = new Rect(mOriginRect.left + offsetX, mOriginRect.top + offsetY, mOriginRect.right, mOriginRect.bottom);
                } else if (mDragMode == DRAG_LEFT) { // 拖动矩形边界的左边缘
                    rect = new Rect(mOriginRect.left + offsetX, mOriginRect.top, mOriginRect.right - offsetX, mOriginRect.bottom);
                } else if (mDragMode == DRAG_RIGHT) { // 拖动矩形边界的右边缘
                    rect = new Rect(mOriginRect.left, mOriginRect.top, mOriginRect.right + offsetX, mOriginRect.bottom);
                } else if (mDragMode == DRAG_TOP) { // 拖动矩形边界的上边缘
                    rect = new Rect(mOriginRect.left, mOriginRect.top + offsetY, mOriginRect.right, mOriginRect.bottom - offsetY);
                } else if (mDragMode == DRAG_BOTTOM) { // 拖动矩形边界的下边缘
                    rect = new Rect(mOriginRect.left, mOriginRect.top, mOriginRect.right, mOriginRect.bottom + offsetY);
                } else if (mDragMode == DRAG_LEFT_TOP) { // 拖动矩形边界的左上角
                    rect = new Rect(mOriginRect.left + offsetX, mOriginRect.top + offsetY, mOriginRect.right - offsetX, mOriginRect.bottom - offsetY);
                } else if (mDragMode == DRAG_RIGHT_TOP) { // 拖动矩形边界的右上角
                    rect = new Rect(mOriginRect.left, mOriginRect.top + offsetY, mOriginRect.right + offsetX, mOriginRect.bottom - offsetY);
                } else if (mDragMode == DRAG_LEFT_BOTTOM) { // 拖动矩形边界的左下角
                    rect = new Rect(mOriginRect.left + offsetX, mOriginRect.top, mOriginRect.right - offsetX, mOriginRect.bottom + offsetY);
                } else if (mDragMode == DRAG_RIGHT_BOTTOM) { // 拖动矩形边界的右下角
                    rect = new Rect(mOriginRect.left, mOriginRect.top, mOriginRect.right + offsetX, mOriginRect.bottom + offsetY);
                }
                setBitmapRect(rect); // 设置位图的矩形边界
                break;
            case MotionEvent.ACTION_UP: // 手指松开
                break;
            default:
                break;
        }
        return true;
    }

    private int DRAG_NONE = 0; // 无拖曳动作
    private int DRAG_WHOLE = 1; // 拖动整个矩形边界框
    private int DRAG_LEFT = 2; // 拖动矩形边界的左边缘
    private int DRAG_RIGHT = 3; // 拖动矩形边界的右边缘
    private int DRAG_TOP = 4; // 拖动矩形边界的上边缘
    private int DRAG_BOTTOM = 5; // 拖动矩形边界的下边缘
    private int DRAG_LEFT_TOP = 6; // 拖动矩形边界的左上角
    private int DRAG_RIGHT_TOP = 7; // 拖动矩形边界的右上角
    private int DRAG_LEFT_BOTTOM = 8; // 拖动矩形边界的左下角
    private int DRAG_RIGHT_BOTTOM = 9; // 拖动矩形边界的右下角
    private int mDragMode = DRAG_NONE; // 拖曳动作的类型

    // 根据落点坐标与矩形边界的相对位置，决定本次拖曳动作的类型
    private int getDragMode(float f, float g) {
        int left = mRect.left;
        int top = mRect.top;
        int right = mRect.left + mRect.right;
        int bottom = mRect.top + mRect.bottom;
        if (Math.abs(f - left) <= mInterval && Math.abs(g - top) <= mInterval) {
            return DRAG_LEFT_TOP; // 拖动矩形边界的左上角
        } else if (Math.abs(f - right) <= mInterval && Math.abs(g - top) <= mInterval) {
            return DRAG_RIGHT_TOP; // 拖动矩形边界的右上角
        } else if (Math.abs(f - left) <= mInterval && Math.abs(g - bottom) <= mInterval) {
            return DRAG_LEFT_BOTTOM; // 拖动矩形边界的左下角
        } else if (Math.abs(f - right) <= mInterval && Math.abs(g - bottom) <= mInterval) {
            return DRAG_RIGHT_BOTTOM; // 拖动矩形边界的右下角
        } else if (Math.abs(f - left) <= mInterval && g > top + mInterval && g < bottom - mInterval) {
            return DRAG_LEFT; // 拖动矩形边界的左边缘
        } else if (Math.abs(f - right) <= mInterval && g > top + mInterval && g < bottom - mInterval) {
            return DRAG_RIGHT; // 拖动矩形边界的右边缘
        } else if (Math.abs(g - top) <= mInterval && f > left + mInterval && f < right - mInterval) {
            return DRAG_TOP; // 拖动矩形边界的上边缘
        } else if (Math.abs(g - bottom) <= mInterval && f > left + mInterval && f < right - mInterval) {
            return DRAG_BOTTOM; // 拖动矩形边界的下边缘
        } else if (f > left + mInterval && f < right - mInterval
                && g > top + mInterval && g < bottom - mInterval) {
            return DRAG_WHOLE; // 拖动整个矩形边界框
        } else {
            return DRAG_NONE; // 无拖曳动作
        }
    }

}
