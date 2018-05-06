package com.example.event.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.event.util.Utils;

@SuppressLint("ClickableViewAccessibility")
public class MeituView extends View {
    private Paint mPaintShade; // 声明一个阴影画笔对象
    private Bitmap mOrigBitmap = null; // 声明一个原始的位图对象
    private Bitmap mCropBitmap = null; // 声明一个裁剪后的位图对象
    private Rect mRect = new Rect(0, 0, 0, 0); // 矩形边界
    private int mInterval; // 与边缘线的间距阈值
    private float mOriginX, mOriginY; // 按下时候落点的横纵坐标
    private Rect mOriginRect; // 原始的矩形边界

    public MeituView(Context context) {
        this(context, null);
    }

    public MeituView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInterval = Utils.dip2px(context, 10);
        mPaintShade = new Paint(); // 创建一个画笔
        mPaintShade.setStyle(Style.FILL); // 设置画笔的风格
        mPaintShade.setColor(0x99000000); // 设置画笔的颜色
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
        // 根据指定的四周边界，裁剪相应尺寸的位图对象
        mCropBitmap = Bitmap.createBitmap(mOrigBitmap,
                mRect.left, mRect.top, mRect.right, mRect.bottom);
        postInvalidate(); // 立即刷新视图
        return true;
    }

    // 获取位图的矩形边界
    public Rect getBitmapRect() {
        return mRect;
    }

    private boolean bReset = false; // 是否重新按下
    private float mLastOffsetX, mLastOffsetY; // 首要落点的上次横纵坐标偏差
    private float mLastOffsetXTwo, mLastOffsetYTwo; // 次要落点的上次横纵坐标偏差
    private long mOriginTime; // 按下时候的系统时间

    // 在下级视图都绘制完成后触发
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mOrigBitmap == null) {
            return;
        }
        // 画外圈阴影
        Rect rectShade = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(rectShade, mPaintShade);
        // 画高亮处的图像
        canvas.drawBitmap(mCropBitmap, mRect.left, mRect.top, new Paint());
    }

    // 在发生触摸事件时触发
    public boolean onTouchEvent(MotionEvent event) {
        // 获得包括次要点在内的触摸行为
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN: // 手指按下
                mOriginTime = event.getEventTime();
                mOriginX = event.getX();
                mOriginY = event.getY();
                mOriginRect = mRect;
                // 根据落点坐标与矩形边界的相对位置，决定本次拖曳动作的类型
                mDragMode = getDragMode(mOriginX, mOriginY);
                bReset = true;
                break;
            case MotionEvent.ACTION_MOVE: // 手指移动
                int offsetX = (int) (event.getX() - mOriginX);
                int offsetY = (int) (event.getY() - mOriginY);
                Rect rect = null;
                int left = mOriginRect.left;
                int top = mOriginRect.top;
                int right = mOriginRect.right;
                int bottom = mOriginRect.bottom;
                if (mDragMode == DRAG_NONE) { // 无拖曳动作
                    return true;
                } else if (mDragMode == DRAG_WHOLE) { // 拖动整个矩形边界框
                    rect = new Rect(left + offsetX, top + offsetY, right, bottom);
                } else if (mDragMode == DRAG_LEFT) { // 拖动矩形边界的左边缘
                    rect = new Rect(left + offsetX, top, right - offsetX, bottom);
                } else if (mDragMode == DRAG_RIGHT) { // 拖动矩形边界的右边缘
                    rect = new Rect(left, top, right + offsetX, bottom);
                } else if (mDragMode == DRAG_TOP) { // 拖动矩形边界的上边缘
                    rect = new Rect(left, top + offsetY, right, bottom - offsetY);
                } else if (mDragMode == DRAG_BOTTOM) { // 拖动矩形边界的下边缘
                    rect = new Rect(left, top, right, bottom + offsetY);
                } else if (mDragMode == DRAG_LEFT_TOP) { // 拖动矩形边界的左上角
                    rect = new Rect(left + offsetX, top + offsetY, right - offsetX, bottom - offsetY);
                } else if (mDragMode == DRAG_RIGHT_TOP) { // 拖动矩形边界的右上角
                    rect = new Rect(left, top + offsetY, right + offsetX, bottom - offsetY);
                } else if (mDragMode == DRAG_LEFT_BOTTOM) { // 拖动矩形边界的左下角
                    rect = new Rect(left + offsetX, top, right - offsetX, bottom + offsetY);
                } else if (mDragMode == DRAG_RIGHT_BOTTOM) { // 拖动矩形边界的右下角
                    rect = new Rect(left, top, right + offsetX, bottom + offsetY);
                } else if (mDragMode == IMAGE_TRANSLATE) { // 平移图像
                    if (mListener != null) { // 执行平移图像动作
                        mListener.onImageTraslate(offsetX, offsetY, bReset);
                        bReset = false;
                    }
                } else if (mDragMode == IMAGE_SCALE_OR_ROTATE) { // 缩放或者旋转图像
                    if (mListener != null) {
                        // 当前两个触摸点之间的距离
                        float nowWholeDistance = distance(event.getX(), event.getY(),
                                event.getX(1), event.getY(1));
                        // 上次两个触摸点之间的距离
                        float preWholeDistance = distance(mLastOffsetX, mLastOffsetY,
                                mLastOffsetXTwo, mLastOffsetYTwo);
                        // 主要点在前后两次落点之间的距离
                        float primaryDistance = distance(event.getX(), event.getY(),
                                mLastOffsetX, mLastOffsetY);
                        // 次要点在前后两次落点之间的距离
                        float secondaryDistance = distance(event.getX(1), event.getY(1),
                                mLastOffsetXTwo, mLastOffsetYTwo);
                        if (Math.abs(nowWholeDistance - preWholeDistance) >
                                (float) Math.sqrt(2) / 2.0f * (primaryDistance + secondaryDistance)) {
                            // 倾向于在原始线段的相同方向上移动，则判作缩放图像
                            // 触发图像变更监听器的缩放图像动作
                            mListener.onImageScale(nowWholeDistance / preWholeDistance);
                        } else { // 倾向于在原始线段的垂直方向上移动，则判作旋转图像
                            // 计算上次触摸事件的旋转角度
                            int preDegree = degree(mLastOffsetX, mLastOffsetY,
                                    mLastOffsetXTwo, mLastOffsetYTwo);
                            // 计算本次触摸事件的旋转角度
                            int nowDegree = degree(event.getX(), event.getY(),
                                    event.getX(1), event.getY(1));
                            // 触发图像变更监听器的旋转图像动作
                            mListener.onImageRotate(nowDegree - preDegree);
                        }
                    }
                }
                if (mDragMode != IMAGE_TRANSLATE && mDragMode != IMAGE_SCALE_OR_ROTATE) {
                    setBitmapRect(rect); // 设置位图的矩形边界
                }
                break;
            case MotionEvent.ACTION_UP: // 手指松开
                // 判断点击和长按
                if (mListener != null && Math.abs(event.getX() - mOriginX) < mInterval &&
                        Math.abs(event.getY() - mOriginY) < mInterval) {
                    if (event.getEventTime() - mOriginTime < 500) { // 按住时间少于0.5秒，则判作点击
                        mListener.onImageClick();
                    } else { // 按住时间大于0.5秒，则判作长按
                        mListener.onImageLongClick();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 次要点按下
                // 多点触摸可能是缩放或者旋转
                mDragMode = IMAGE_SCALE_OR_ROTATE;
                bReset = true;
                break;
            case MotionEvent.ACTION_POINTER_UP: // 次要点松开
                mDragMode = DRAG_NONE;
                break;
            default:
                break;
        }
        mLastOffsetX = event.getX();
        mLastOffsetY = event.getY();
        if (event.getPointerCount() >= 2) { // 存在多点触摸
            mLastOffsetXTwo = event.getX(1);
            mLastOffsetYTwo = event.getY(1);
        }
        return true;
    }

    // 计算两个坐标点之间的距离
    private float distance(float x1, float y1, float x2, float y2) {
        float offsetX = x2 - x1;
        float offsetY = y2 - y1;
        return (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
    }

    // 计算两个坐标点构成的角度
    private int degree(float x1, float y1, float x2, float y2) {
        return (int) (Math.atan((y2 - y1) / (x2 - x1)) / Math.PI * 180);
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
    private int IMAGE_TRANSLATE = 10; // 平移图像
    private int IMAGE_SCALE_OR_ROTATE = 11; // 缩放或者旋转图像
    private int mDragMode = DRAG_NONE; // 拖曳动作的类型

    // 根据落点坐标与矩形边界的相对位置，决定本次拖曳动作的类型
    private int getDragMode(float x, float y) {
        int left = mRect.left;
        int top = mRect.top;
        int right = mRect.left + mRect.right;
        int bottom = mRect.top + mRect.bottom;
        if (Math.abs(x - left) <= mInterval && Math.abs(y - top) <= mInterval) {
            return DRAG_LEFT_TOP; // 拖动矩形边界的左上角
        } else if (Math.abs(x - right) <= mInterval && Math.abs(y - top) <= mInterval) {
            return DRAG_RIGHT_TOP; // 拖动矩形边界的右上角
        } else if (Math.abs(x - left) <= mInterval && Math.abs(y - bottom) <= mInterval) {
            return DRAG_LEFT_BOTTOM; // 拖动矩形边界的左下角
        } else if (Math.abs(x - right) <= mInterval && Math.abs(y - bottom) <= mInterval) {
            return DRAG_RIGHT_BOTTOM; // 拖动矩形边界的右下角
        } else if (Math.abs(x - left) <= mInterval && y > top + mInterval && y < bottom - mInterval) {
            return DRAG_LEFT; // 拖动矩形边界的左边缘
        } else if (Math.abs(x - right) <= mInterval && y > top + mInterval && y < bottom - mInterval) {
            return DRAG_RIGHT; // 拖动矩形边界的右边缘
        } else if (Math.abs(y - top) <= mInterval && x > left + mInterval && x < right - mInterval) {
            return DRAG_TOP; // 拖动矩形边界的上边缘
        } else if (Math.abs(y - bottom) <= mInterval && x > left + mInterval && x < right - mInterval) {
            return DRAG_BOTTOM; // 拖动矩形边界的下边缘
        } else if (x > left + mInterval && x < right - mInterval
                && y > top + mInterval && y < bottom - mInterval) {
            return DRAG_WHOLE; // 拖动整个矩形边界框
        } else if (x + mInterval < left || x - mInterval > right || y + mInterval < top || y - mInterval > bottom) {
            return IMAGE_TRANSLATE; // 平移图像
        } else {
            return DRAG_NONE; // 无拖曳动作
        }
    }

    private ImageChangetListener mListener; // 声明一个图像变更的监听器对象
    // 设置图像变更监听器
    public void setImageChangetListener(ImageChangetListener listener) {
        mListener = listener;
    }

    // 定义一个图像变更的监听器接口
    public interface ImageChangetListener {
        void onImageClick(); // 点击图像
        void onImageLongClick(); // 长按图像
        void onImageTraslate(int offsetX, int offsetY, boolean bReset); // 平移图像
        void onImageScale(float ratio); // 缩放图像
        void onImageRotate(int degree); // 旋转图像
    }

}
