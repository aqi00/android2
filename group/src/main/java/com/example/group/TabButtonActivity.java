package com.example.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by ouyangshen on 2017/10/21.
 */
public class TabButtonActivity extends AppCompatActivity implements
        OnClickListener, OnCheckedChangeListener {
    private TextView tv_tab_button; // 声明一个标签按钮对象
    private CheckBox ck_select; // 声明一个复选框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_button);
        // 从布局文件中获取名叫tv_tab_button的标签按钮
        tv_tab_button = findViewById(R.id.tv_tab_button);
        tv_tab_button.setOnClickListener(this);
        // 从布局文件中获取名叫ck_select的复选框
        ck_select = findViewById(R.id.ck_select);
        // 给复选框ck_select设置勾选监听器
        ck_select.setOnCheckedChangeListener(this);
    }

    // 一旦勾选或者取消勾选复选框，就触发勾选监听器的onCheckedChanged方法
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_select) {
            // 设置标签按钮的选中状态
            tv_tab_button.setSelected(isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_tab_button) {
            // 将复选框的状态置反
            ck_select.setChecked(!ck_select.isChecked());
        }
    }
}
