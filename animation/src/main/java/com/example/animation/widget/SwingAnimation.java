package com.example.animation.widget;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SwingAnimation extends Animation {
    private float mMiddleDegrees; // 中间的角度
    private float mLeftDegrees; // 左边的角度
    private float mRightDegrees; // 右边的角度
    private int mPivotXType = ABSOLUTE; // 圆心的横坐标类型
    private int mPivotYType = ABSOLUTE; // 圆心的纵坐标类型
    private float mPivotXValue = 0.0f; // 圆心横坐标的数值比例
    private float mPivotYValue = 0.0f; // 圆心纵坐标的数值比例
    private float mPivotX; // 圆心横坐标的数值
    private float mPivotY; // 圆心纵坐标的数值

    public SwingAnimation(float middleDegrees, float leftDegrees, float rightDegrees) {
        this(middleDegrees, leftDegrees, rightDegrees, 0.0f, 0.0f);
    }

    public SwingAnimation(float middleDegrees, float leftDegrees,
                          float rightDegrees, float pivotX, float pivotY) {
        this(middleDegrees, leftDegrees, rightDegrees, ABSOLUTE, pivotX, ABSOLUTE, pivotY);
    }

    public SwingAnimation(float middleDegrees, float leftDegrees, float rightDegrees,
                          int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        mMiddleDegrees = middleDegrees;
        mLeftDegrees = leftDegrees;
        mRightDegrees = rightDegrees;
        mPivotXValue = pivotXValue;
        mPivotXType = pivotXType;
        mPivotYValue = pivotYValue;
        mPivotYType = pivotYType;
        initializePivotPoint();
    }

    // 初始化圆心的横纵坐标数值
    private void initializePivotPoint() {
        if (mPivotXType == ABSOLUTE) {
            mPivotX = mPivotXValue;
        }
        if (mPivotYType == ABSOLUTE) {
            mPivotY = mPivotYValue;
        }
    }

    // 在动画变换过程中调用
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degrees;
        float leftPos = (float) (1.0 / 4.0); // 摆到左边端点时的时间比例
        float rightPos = (float) (3.0 / 4.0); // 摆到右边端点时的时间比例
        if (interpolatedTime <= leftPos) { // 从中间线往左边端点摆
            degrees = mMiddleDegrees + ((mLeftDegrees - mMiddleDegrees) * interpolatedTime * 4);
        } else if (interpolatedTime > leftPos && interpolatedTime < rightPos) { // 从左边端点往右边端点摆
            degrees = mLeftDegrees + ((mRightDegrees - mLeftDegrees) * (interpolatedTime - leftPos) * 2);
        } else { // 从右边端点往中间线摆
            degrees = mRightDegrees + ((mMiddleDegrees - mRightDegrees) * (interpolatedTime - rightPos) * 4);
        }
        // 获得缩放比率
        float scale = getScaleFactor();
        if (mPivotX == 0.0f && mPivotY == 0.0f) {
            t.getMatrix().setRotate(degrees);
        } else {
            t.getMatrix().setRotate(degrees, mPivotX * scale, mPivotY * scale);
        }
    }

    // 在初始化时调用
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth);
        mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight);
    }
}
