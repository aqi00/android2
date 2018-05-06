package com.example.performance.widget;

import com.example.performance.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class CustomDialog implements OnClickListener {
    private Dialog dialog; // 声明一个对话框对象
    private View view; // 声明一个视图对象
    private TextView tv_title;
    private TextView tv_message;

    public CustomDialog(Context context, int style) {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_background, null);
        dialog = new Dialog(context, style);
        tv_title = view.findViewById(R.id.tv_title);
        tv_message = view.findViewById(R.id.tv_message);
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    // 设置对话框的标题
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    // 设置对话框的消息
    public void setMessage(String message) {
        tv_message.setText(message);
    }

    // 显示对话框
    public void show() {
        // 设置对话框窗口的内容视图
        dialog.getWindow().setContentView(view);
        // 设置对话框窗口的布局参数
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.show(); // 显示对话框
    }

    // 关闭对话框
    public void dismiss() {
        // 如果对话框显示出来了，就关闭它
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // 关闭对话框
        }
    }

    // 判断对话框是否显示
    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        dismiss(); // 关闭对话框
    }

}
