package com.example.custom.util;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class MeasureUtil {

    // 获取指定文本的宽度（其实就是长度）
    public static float getTextWidth(String text, float textSize) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        Paint paint = new Paint(); // 创建一个画笔对象
        paint.setTextSize(textSize); // 设置画笔的文本大小
        return paint.measureText(text); // 利用画笔丈量指定文本的宽度
    }

    // 获取指定文本的高度
    public static float getTextHeight(String text, float textSize) {
        Paint paint = new Paint(); // 创建一个画笔对象
        paint.setTextSize(textSize); // 设置画笔的文本大小
        FontMetrics fm = paint.getFontMetrics(); // 获取画笔默认字体的度量衡
        return fm.descent - fm.ascent; // 返回文本自身的高度
        //return fm.bottom - fm.top + fm.leading;  // 返回文本所在行的行高
    }

    // 根据资源编号获得线性布局的实际高度（页面来源）
    public static float getRealHeight(Activity act, int resid) {
        LinearLayout llayout = act.findViewById(resid);
        return getRealHeight(llayout);
    }

    // 根据资源编号获得线性布局的实际高度（视图来源）
    public static float getRealHeight(View parent, int resid) {
        LinearLayout llayout = parent.findViewById(resid);
        return getRealHeight(llayout);
    }

    // 计算指定线性布局的实际高度
    public static float getRealHeight(View child) {
        LinearLayout llayout = (LinearLayout) child;
        // 获得线性布局的布局参数
        ViewGroup.LayoutParams params = llayout.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        // 获得布局参数里面的宽度规格
        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int heightSpec;
        if (params.height > 0) { // 高度大于0，说明这是明确的dp数值
            // 按照精确数值的情况计算高度规格
            heightSpec = View.MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        } else { // MATCH_PARENT=-1，WRAP_CONTENT=-2，所以二者都进入该分支
            // 按照不确定的情况计算高度规则
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        // 重新进行线性布局的宽高丈量
        llayout.measure(widthSpec, heightSpec);
        // 获得并返回线性布局丈量之后的高度数值。调用getMeasuredWidth方法可获得宽度数值
        return llayout.getMeasuredHeight();
    }

}
