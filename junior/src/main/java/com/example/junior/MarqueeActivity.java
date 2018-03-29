package com.example.junior;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/9/14.
 */
public class MarqueeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_marquee; // 声明一个文本视图对象
    private boolean isPaused = false; // 跑马灯文字是否暂停滚动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquee);
        // 从布局文件中获取名叫tv_marquee的文本视图
        tv_marquee = findViewById(R.id.tv_marquee);
        // 给tv_marquee设置点击监听器
        tv_marquee.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_marquee) { // 点击了文本视图tv_marquee
            isPaused = !isPaused;
            if (isPaused) {
                tv_marquee.setFocusable(false); // 不允许获得焦点
                tv_marquee.setFocusableInTouchMode(false); // 不允许在触摸时获得焦点
            } else {
                tv_marquee.setFocusable(true); // 允许获得焦点
                tv_marquee.setFocusableInTouchMode(true); // 允许在触摸时获得焦点
                tv_marquee.requestFocus(); // 强制获得焦点，让跑马灯滚起来
            }
        }
    }
}
