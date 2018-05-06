package com.example.animation;

import com.example.animation.widget.SwingAnimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class SwingAnimActivity extends AppCompatActivity implements OnClickListener {
    private ImageView iv_swing; // 声明一个图像视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing_anim);
        // 从布局文件中获取名叫iv_swing的图像视图
        iv_swing = findViewById(R.id.iv_swing);
        findViewById(R.id.ll_swing).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSwingAnimation(); // 开始播放摇摆动画
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_swing) {
            showSwingAnimation(); // 开始播放摇摆动画
        }
    }

    // 开始播放摇摆动画
    private void showSwingAnimation() {
        // 创建一个摇摆动画
        // 参数取值说明：中间度数、摆到左侧的度数、摆到右侧的度数、圆心X坐标类型、圆心X坐标相对比例、圆心Y坐标类型、圆心Y坐标相对比例
        // 坐标类型有三种：ABSOLUTE 绝对坐标，RELATIVE_TO_SELF 相对自身的坐标，RELATIVE_TO_PARENT 相对上级视图的坐标
        // X坐标相对比例，为0时表示左边顶点，为1表示右边顶点，为0.5表示水平中心点
        // Y坐标相对比例，为0时表示上边顶点，为1表示下边顶点，为0.5表示垂直中心点
        SwingAnimation swingAnimation = new SwingAnimation(
                0f, 60f, -60f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
        swingAnimation.setDuration(4000); // 设置动画的播放时长
        swingAnimation.setRepeatCount(0); // 设置动画的重播次数
        swingAnimation.setFillAfter(false); // 设置维持结束画面
        swingAnimation.setStartOffset(500); // 设置动画的启动延迟
        // 命令图像视图开始播放摇摆动画
        iv_swing.startAnimation(swingAnimation);
//        iv_swing.clearAnimation();
//        iv_swing.setAnimation(swingAnimation);
//        swingAnimation.startNow();
//        // setAnimation可能只会播放一次或不播放。如果想点击播放，要用startAnimation
//        iv_swing.setAnimation(swingAnimation);
//        swingAnimation.startNow();
    }

}
