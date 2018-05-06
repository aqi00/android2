package com.example.animation;

import com.example.animation.widget.PieAnimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class PieActivity extends AppCompatActivity implements OnClickListener {
    private PieAnimation pa_circle; // 声明一个饼图动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);
        // 从布局文件中获取名叫pa_circle的饼图动画
        pa_circle = findViewById(R.id.pa_circle);
        // 设置饼图动画的点击监听器
        pa_circle.setOnClickListener(this);
        // 开始播放饼图动画
        pa_circle.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pa_circle) {
            if (!pa_circle.isRunning()) { // 判断饼图动画是否正在播放
                // 不在播放，则开始播放饼图动画
                pa_circle.start();
            }
        }
    }

}
