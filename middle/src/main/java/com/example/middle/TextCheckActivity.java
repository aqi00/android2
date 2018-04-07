package com.example.middle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint(value={"SetTextI18n","DefaultLocale"})
public class TextCheckActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_input; // 声明一个编辑框对象
    private TextView tv_result; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_check);
        // 从布局文件中获取名叫et_input的编辑框
        et_input = findViewById(R.id.et_input);
        // 从布局文件中获取名叫tv_result的文本视图
        tv_result = findViewById(R.id.tv_result);
        // 下面通过四个按钮分别演示TextUtils的四种常用方法
        findViewById(R.id.btn_empty).setOnClickListener(this);
        findViewById(R.id.btn_trim_length).setOnClickListener(this);
        findViewById(R.id.btn_digit).setOnClickListener(this);
        findViewById(R.id.btn_ellipsize).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_empty) {
            // 判断字符串是否为空值
            boolean isEmpty = TextUtils.isEmpty(et_input.getText());
            String desc = String.format("输入框的文本%s空的", isEmpty ? "是" : "不是");
            tv_result.setText(desc);
        } else if (v.getId() == R.id.btn_trim_length) {
            // 获取字符串去除头尾空格之后的长度
            int length = TextUtils.getTrimmedLength(et_input.getText());
            String desc = String.format("输入框的文本去掉左右空格后的长度是%d", length);
            tv_result.setText(desc);
        } else if (v.getId() == R.id.btn_digit) {
            // 判断字符串是否全部由数字组成
            boolean isDigit = TextUtils.isDigitsOnly(et_input.getText());
            String desc = String.format("输入框的文本%s纯数字", isDigit ? "是" : "不是");
            tv_result.setText(desc);
        } else if (v.getId() == R.id.btn_ellipsize) {
            // 总共显示十个字符（因为省略号占了一个，所以还剩九个可显示汉字）
            float avail = et_input.getTextSize() * 10;
            // 如果字符串超过十位，则返回在尾部截断并添加省略号的字串
            CharSequence ellips = TextUtils.ellipsize(et_input.getText(), et_input.getPaint(), avail, TruncateAt.END);
            tv_result.setText("输入框的文本加省略号的样式为：" + ellips);
        }
    }

}
