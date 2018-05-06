package com.example.animation;

import android.annotation.TargetApi;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class VectorHookActivity extends AppCompatActivity implements OnClickListener {
    private ImageView iv_vector_hook; // 声明一个图像视图对象
    private Drawable mDrawable; // 声明一个图形对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector_hook);
        // 从布局文件中获取名叫iv_vector_hook的图像视图
        iv_vector_hook = findViewById(R.id.iv_vector_hook);
        findViewById(R.id.btn_vector_pay).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_vector_pay) {
            // 开始播放画圈的矢量动画
            startVectorAnim(R.drawable.animated_vector_pay_circle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 为画圈动画注册一个矢量动画图形的监听器
                ((AnimatedVectorDrawable) mDrawable)
                        .registerAnimationCallback(new VectorAnimListener());
            } else {
                // 延迟1秒后启动打勾动画的播放任务
                new Handler().postDelayed(mHookRunnable, 1000);
            }
        }
    }

    // 开始播放矢量动画
    private void startVectorAnim(int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 从指定资源编号的矢量文件中获取图形对象
            mDrawable = getResources().getDrawable(drawableId);
            // 设置图像视图的图形对象
            iv_vector_hook.setImageDrawable(mDrawable);
            // 将该图形强制转换为动画图形，并开始播放
            ((Animatable) mDrawable).start();
        } else {
            // 设置图像视图的图像资源编号
            iv_vector_hook.setImageResource(drawableId);
            // 将图像视图承载的图形强制转换为动画图形，然后再进行播放
            ((Animatable) iv_vector_hook.getDrawable()).start();
        }
    }

    // 定义一个动画图形的监听器
    // Android6.0以后系统采取监听器Animatable2.AnimationCallback监控动画播放事件
    @TargetApi(Build.VERSION_CODES.M)
    private class VectorAnimListener extends Animatable2.AnimationCallback {
        // 在动画图形开始播放时触发
        public void onAnimationStart(Drawable drawable) {}

        // 在动画图形结束播放时触发
        public void onAnimationEnd(Drawable drawable) {
            // 开始播放打勾的矢量动画
            startVectorAnim(R.drawable.animated_vector_pay_success);
        }
    }

    // 定义一个打勾动画的播放任务
    // Android4.*和5.*系统，只能利用定时任务来延迟执行新动画的播放
    private Runnable mHookRunnable = new Runnable() {
        @Override
        public void run() {
            // 开始播放打勾的矢量动画
            startVectorAnim(R.drawable.animated_vector_pay_success);
        }
    };

}
