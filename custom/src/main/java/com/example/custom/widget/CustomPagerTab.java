package com.example.custom.widget;

import com.example.custom.R;
import com.example.custom.util.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

public class CustomPagerTab extends PagerTabStrip {
    private final static String TAG = "CustomPagerTab";
    private int textColor = Color.BLACK; // 文本颜色
    private int textSize = 15; // 文本大小

    public CustomPagerTab(Context context) {
        super(context);
    }

    public CustomPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            // 根据CustomPagerTab的属性定义，从布局文件中获取属性数组描述
            TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomPagerTab);
            // 根据属性描述定义，获取布局文件中的文本颜色
            textColor = attrArray.getColor(R.styleable.CustomPagerTab_textColor, textColor);
            // 根据属性描述定义，获取布局文件中的文本大小
            // getDimension得到的是px值，需要转换为sp值
            textSize = Utils.px2sp(context, attrArray.getDimension(R.styleable.CustomPagerTab_textSize, textSize));
            int customBackground = attrArray.getResourceId(R.styleable.CustomPagerTab_customBackground, 0);
            int customOrientation = attrArray.getInt(R.styleable.CustomPagerTab_customOrientation, 0);
            int customGravity = attrArray.getInt(R.styleable.CustomPagerTab_customGravity, 0);
            Log.d(TAG, "textColor=" + textColor + ", textSize=" + textSize);
            Log.d(TAG, "customBackground=" + customBackground + ", customOrientation=" + customOrientation + ", customGravity=" + customGravity);
            // 回收属性数组描述
            attrArray.recycle();
        }
    }

//    //PagerTabStrip没有三个参数的构造函数
//    public CustomPagerTab(Context context, AttributeSet attrs, int defStyleAttr) {
//    }

    @Override
    protected void onDraw(Canvas canvas) { // 绘制函数
        setTextColor(textColor); // 设置标题文字的文本颜色
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize); // 设置标题文字的文本大小
        super.onDraw(canvas);
    }

}
