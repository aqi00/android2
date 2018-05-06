package com.example.event;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/23.
 */
@SuppressLint("DefaultLocale")
public class TouchSingleActivity extends AppCompatActivity {
    private TextView tv_touch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_single);
        tv_touch = findViewById(R.id.tv_touch);
    }

    // 在发生触摸事件时触发
    public boolean onTouchEvent(MotionEvent event) {
        // 从开机到现在的毫秒数
        int seconds = (int) (event.getEventTime() / 1000);
        int hour = seconds / 3600;
        int minute = seconds % 3600 / 60;
        int second = seconds % 60;
        String desc = String.format("动作发生时间：开机距离现在%02d:%02d:%02d",
                hour, minute, second);
        desc = String.format("%s\n动作名称是：", desc);
        // 获得触摸事件的动作类型
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) { // 手指按下
            desc = String.format("%s按下", desc);
        } else if (action == MotionEvent.ACTION_MOVE) { // 手指移动
            desc = String.format("%s移动", desc);
        } else if (action == MotionEvent.ACTION_UP) { // 手指松开
            desc = String.format("%s提起", desc);
        } else if (action == MotionEvent.ACTION_CANCEL) { // 取消手势
            desc = String.format("%s取消", desc);
        }
        desc = String.format("%s\n动作发生位置是：横坐标%f，纵坐标%f",
                desc, event.getX(), event.getY());
        tv_touch.setText(desc);
        return super.onTouchEvent(event);
    }

}
