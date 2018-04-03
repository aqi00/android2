package com.example.custom;

import com.example.custom.widget.CircleAnimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class CircleAnimationActivity extends AppCompatActivity implements OnClickListener {
    private CircleAnimation mAnimation; // 定义一个圆弧动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_animation);
        findViewById(R.id.btn_play).setOnClickListener(this);
        LinearLayout ll_layout = findViewById(R.id.ll_layout);
        // 创建一个新的圆弧动画
        mAnimation = new CircleAnimation(this);
        // 把圆弧动画添加到线性布局ll_layout之中
        ll_layout.addView(mAnimation);
        // 渲染圆弧动画。渲染操作包括初始化与播放两个动作
        mAnimation.render();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_play) {
            // 开始播放圆弧动画
            mAnimation.play();
        }
    }
}
