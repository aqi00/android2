package com.example.senior.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

/**
 * Created by ouyangshen on 2018/1/2.
 */
// 由日期选择器派生出月份选择器
public class MonthPicker extends DatePicker {
    public MonthPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取年月日的下拉列表项
        ViewGroup vg = ((ViewGroup) ((ViewGroup) getChildAt(0)).getChildAt(0));
        if (vg.getChildCount() == 3) {
            // 有的机型显示格式为“年月日”，此时隐藏第三个控件
            vg.getChildAt(2).setVisibility(View.GONE);
        } else if (vg.getChildCount() == 5) {
            // 有的机型显示格式为“年|月|日”，此时隐藏第四个和第五个控件（即“|日”）
            vg.getChildAt(3).setVisibility(View.GONE);
            vg.getChildAt(4).setVisibility(View.GONE);
        }
    }
}
