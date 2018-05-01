package com.example.custom.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.custom.R;

/**
 * Created by ouyangshen on 2017/12/29.
 */
public class CustomMonthDialog implements View.OnClickListener {
    private Dialog dialog; // 声明一个对话框对象
    private View view; // 声明一个视图对象
    private TextView tv_title;
    private DatePicker dp_date; // 声明一个日期选择器对象

    public CustomMonthDialog(Context context) {
        // 根据布局文件dialog_date.xml生成视图对象
        view = LayoutInflater.from(context).inflate(R.layout.dialog_date, null);
        // 创建一个指定风格的对话框对象
        dialog = new Dialog(context, R.style.CustomDateDialog);
        tv_title = view.findViewById(R.id.tv_title);
        // 从布局文件中获取名叫dp_date的日期选择器
        dp_date = view.findViewById(R.id.dp_date);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
        tv_title.setText("请选择月份");
        // 获取年月日的下拉列表项
        ViewGroup vg = ((ViewGroup) ((ViewGroup) dp_date.getChildAt(0)).getChildAt(0));
        if (vg.getChildCount() == 3) {
            // 有的机型显示格式为“年月日”，此时隐藏第三个控件
            vg.getChildAt(2).setVisibility(View.GONE);
        } else if (vg.getChildCount() == 5) {
            // 有的机型显示格式为“年|月|日”，此时隐藏第四个和第五个控件（即“|日”）
            vg.getChildAt(3).setVisibility(View.GONE);
            vg.getChildAt(4).setVisibility(View.GONE);
        }
    }

    // 设置月份对话框的标题文本
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    // 设置月份对话框内部的年、月，以及月份变更监听器
    public void setDate(int year, int month, int day, OnMonthSetListener listener) {
        dp_date.init(year, month, day, null);
        mMonthSetListener = listener;
    }

    // 显示月份对话框
    public void show() {
        // 设置对话框窗口的内容视图
        dialog.getWindow().setContentView(view);
        // 设置对话框窗口的布局参数
        dialog.getWindow().setLayout(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.show(); // 显示对话框
    }

    // 关闭月份对话框
    public void dismiss() {
        // 如果对话框显示出来了，就关闭它
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // 判断月份对话框是否显示
    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) { // 点击了确定按钮
            dismiss(); // 关闭对话框
            if (mMonthSetListener != null) { // 如果存在月份变更监听器
                dp_date.clearFocus(); // 清除日期选择器的焦点
                // 回调监听器的onMonthSet方法
                mMonthSetListener.onMonthSet(dp_date.getYear(), dp_date.getMonth()+1);
            }
        }
    }

    // 声明一个月份变更监听器对象
    private OnMonthSetListener mMonthSetListener;
    // 定义一个月份变更监听器接口
    public interface OnMonthSetListener {
        void onMonthSet(int year, int monthOfYear);
    }

}
