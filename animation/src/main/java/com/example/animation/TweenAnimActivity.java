package com.example.animation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.animation.util.Utils;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class TweenAnimActivity extends AppCompatActivity implements AnimationListener {
    private ImageView iv_tween_anim; // 声明一个图像视图对象
    private Animation alphaAnim, translateAnim, scaleAnim, rotateAnim; // 分别声明四个补间动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tween_anim);
        // 从布局文件中获取名叫iv_tween_anim的图像视图
        iv_tween_anim = findViewById(R.id.iv_tween_anim);
        initAnimation(); // 初始化补间动画
        initTweenSpinner();
    }

    // 初始化补间动画
    private void initAnimation() {
        // 创建一个灰度动画。从完全透明变为即将不透明
        alphaAnim = new AlphaAnimation(1.0f, 0.1f);
        alphaAnim.setDuration(3000); // 设置动画的播放时长
        alphaAnim.setFillAfter(true); // 设置维持结束画面
        // 创建一个平移动画。向左平移100dp
        translateAnim = new TranslateAnimation(1.0f, Utils.dip2px(this, -100), 1.0f, 1.0f);
        translateAnim.setDuration(3000); // 设置动画的播放时长
        translateAnim.setFillAfter(true); // 设置维持结束画面
        // 创建一个缩放动画。宽度不变，高度变为原来的二分之一
        scaleAnim = new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.5f);
        scaleAnim.setDuration(3000); // 设置动画的播放时长
        scaleAnim.setFillAfter(true); // 设置维持结束画面
        // 创建一个旋转动画。围绕着圆心顺时针旋转360度
        rotateAnim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(3000); // 设置动画的播放时长
        rotateAnim.setFillAfter(true); // 设置维持结束画面
    }

    // 初始化动画类型下拉框
    private void initTweenSpinner() {
        ArrayAdapter<String> tweenAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, tweenArray);
        Spinner sp_tween = findViewById(R.id.sp_tween);
        sp_tween.setPrompt("请选择补间动画类型");
        sp_tween.setAdapter(tweenAdapter);
        sp_tween.setOnItemSelectedListener(new TweenSelectedListener());
        sp_tween.setSelection(0);
    }

    private String[] tweenArray = {"灰度动画", "平移动画", "缩放动画", "旋转动画"};
    class TweenSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 == 0) {
                // 命令图像视图开始播放灰度动画
                iv_tween_anim.startAnimation(alphaAnim);
                // 给灰度动画设置动画事件监听器
                alphaAnim.setAnimationListener(TweenAnimActivity.this);
            } else if (arg2 == 1) {
                // 命令图像视图开始播放平移动画
                iv_tween_anim.startAnimation(translateAnim);
                // 给平移动画设置动画事件监听器
                translateAnim.setAnimationListener(TweenAnimActivity.this);
            } else if (arg2 == 2) {
                // 命令图像视图开始播放缩放动画
                iv_tween_anim.startAnimation(scaleAnim);
                // 给缩放动画设置动画事件监听器
                scaleAnim.setAnimationListener(TweenAnimActivity.this);
            } else if (arg2 == 3) {
                // 命令图像视图开始播放旋转动画
                iv_tween_anim.startAnimation(rotateAnim);
                // 给旋转动画设置动画事件监听器
                rotateAnim.setAnimationListener(TweenAnimActivity.this);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 在补间动画开始播放时触发
    public void onAnimationStart(Animation animation) {}

    // 在补间动画结束播放时触发
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(alphaAnim)) { // 灰度动画
            // 创建一个灰度动画。从即将不透明变为完全透明
            Animation alphaAnim2 = new AlphaAnimation(0.1f, 1.0f);
            alphaAnim2.setDuration(3000); // 设置动画的播放时长
            alphaAnim2.setFillAfter(true); // 设置维持结束画面
            // 命令图像视图开始播放灰度动画
            iv_tween_anim.startAnimation(alphaAnim2);
        } else if (animation.equals(translateAnim)) { // 平移动画
            // 创建一个平移动画。向右平移100dp
            Animation translateAnim2 = new TranslateAnimation(Utils.dip2px(this, -100), 1.0f, 1.0f, 1.0f);
            translateAnim2.setDuration(3000); // 设置动画的播放时长
            translateAnim2.setFillAfter(true); // 设置维持结束画面
            // 命令图像视图开始播放平移动画
            iv_tween_anim.startAnimation(translateAnim2);
        } else if (animation.equals(scaleAnim)) { // 缩放动画
            // 创建一个缩放动画。宽度不变，高度变为原来的两倍
            Animation scaleAnim2 = new ScaleAnimation(1.0f, 1.0f, 0.5f, 1.0f);
            scaleAnim2.setDuration(3000); // 设置动画的播放时长
            scaleAnim2.setFillAfter(true); // 设置维持结束画面
            // 命令图像视图开始播放缩放动画
            iv_tween_anim.startAnimation(scaleAnim2);
        } else if (animation.equals(rotateAnim)) { // 旋转动画
            // 创建一个旋转动画。围绕着圆心逆时针旋转360度
            Animation rotateAnim2 = new RotateAnimation(0f, -360f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim2.setDuration(3000); // 设置动画的播放时长
            rotateAnim2.setFillAfter(true); // 设置维持结束画面
            // 命令图像视图开始播放旋转动画
            iv_tween_anim.startAnimation(rotateAnim2);
        }
    }

    // 在补间动画重复播放时触发
    public void onAnimationRepeat(Animation animation) {}

}
