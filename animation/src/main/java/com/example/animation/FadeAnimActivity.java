package com.example.animation;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class FadeAnimActivity extends AppCompatActivity implements OnClickListener {
    private ImageView iv_fade_anim; // 声明一个图像视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fade_anim);
        // 从布局文件中获取名叫iv_fade_anim的图像视图
        iv_fade_anim = findViewById(R.id.iv_fade_anim);
        iv_fade_anim.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFadeAnimation(); // 开始播放淡入淡出动画
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_fade_anim) {
            showFadeAnimation(); // 开始播放淡入淡出动画
        }
    }

    // 开始播放淡入淡出动画
    private void showFadeAnimation() {
        // 淡入淡出动画需要先定义一个图形资源数组，用于变换图片
        Drawable[] drawableArray = {
                getResources().getDrawable(R.drawable.fade_begin),
                getResources().getDrawable(R.drawable.fade_end)
        };
        // 创建一个用于淡入淡出动画的过渡图形
        TransitionDrawable td_fade = new TransitionDrawable(drawableArray);
        // 设置图像视图的图像为过渡图形
        iv_fade_anim.setImageDrawable(td_fade);
        // 开始过渡图形的变换过程，其中变换时长为三秒
        td_fade.startTransition(3000);
    }

}
