package com.example.animation;

import com.example.animation.util.Utils;

import android.annotation.TargetApi;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PaySuccessActivity extends AppCompatActivity implements OnClickListener {
    private Button btn_success; // 声明一个按钮控件对象
    private Drawable mDrawable; // 声明一个图形对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        // 从布局文件中获取名叫btn_success的按钮控件
        btn_success = findViewById(R.id.btn_success);
        findViewById(R.id.btn_pay).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pay) {
            btn_success.setVisibility(View.VISIBLE);
            // 开始播放画圈的矢量动画
            startVectorAnim(R.drawable.animated_pay_circle);
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
        // 从指定资源编号的矢量文件中获取图形对象
        mDrawable = getResources().getDrawable(drawableId);
        int dip_50 = Utils.dip2px(this, 50);
        // 设置该图形的四周间隔
        mDrawable.setBounds(0, 0, dip_50, dip_50);
        // 设置按钮控件的左侧图形
        btn_success.setCompoundDrawables(mDrawable, null, null, null);
        // 将该图形强制转换为动画图形，并开始播放
        ((Animatable) mDrawable).start();
    }

    // 定义一个动画图形的监听器
    @TargetApi(Build.VERSION_CODES.M)
    private class VectorAnimListener extends Animatable2.AnimationCallback {
        // 在动画图形开始播放时触发
        public void onAnimationStart(Drawable drawable) {}

        // 在动画图形结束播放时触发
        public void onAnimationEnd(Drawable drawable) {
            // 开始播放打勾的矢量动画
            startVectorAnim(R.drawable.animated_pay_success);
        }
    }

    // 定义一个打勾动画的播放任务
    private Runnable mHookRunnable = new Runnable() {
        @Override
        public void run() {
            // 开始播放打勾的矢量动画
            startVectorAnim(R.drawable.animated_pay_success);
        }
    };

    // 允许getDrawable方法访问矢量图形资源文件
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
