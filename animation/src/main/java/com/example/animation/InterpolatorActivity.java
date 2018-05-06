package com.example.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class InterpolatorActivity extends AppCompatActivity implements AnimatorListener {
    private TextView tv_interpolator; // 声明一个图像视图对象
    private ObjectAnimator animAcce, animDece, animLinear, animBounce; // 分别声明四个属性动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interpolator);
        // 从布局文件中获取名叫tv_interpolator的图像视图
        tv_interpolator = findViewById(R.id.tv_interpolator);
        initAnimator(); // 初始化属性动画
        initInterpolatorSpinner();
    }

    // 初始化插值器类型的下拉框
    private void initInterpolatorSpinner() {
        ArrayAdapter<String> interpolatorAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, interpolatorArray);
        Spinner sp_interpolator = findViewById(R.id.sp_interpolator);
        sp_interpolator.setPrompt("请选择插值器类型");
        sp_interpolator.setAdapter(interpolatorAdapter);
        sp_interpolator.setOnItemSelectedListener(new InterpolatorSelectedListener());
        sp_interpolator.setSelection(0);
    }

    private String[] interpolatorArray = {
            "背景色+加速插值器+颜色估值器",
            "旋转+减速插值器+浮点型估值器",
            "裁剪+匀速插值器+矩形估值器",
            "文字大小+震荡插值器+浮点型估值器"};

    class InterpolatorSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            showInterpolator(arg2); // 根据插值器类型展示属性动画
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化属性动画
    private void initAnimator() {
        // 构造一个在背景色上变化的属性动画
        animAcce = ObjectAnimator.ofInt(tv_interpolator, "backgroundColor", Color.RED, Color.GRAY);
        // 给属性动画设置加速插值器
        animAcce.setInterpolator(new AccelerateInterpolator());
        // 给属性动画设置颜色估值器
        animAcce.setEvaluator(new ArgbEvaluator());
        // 构造一个围绕中心点旋转的属性动画
        animDece = ObjectAnimator.ofFloat(tv_interpolator, "rotation", 0f, 360f);
        // 给属性动画设置减速插值器
        animDece.setInterpolator(new DecelerateInterpolator());
        // 给属性动画设置浮点型估值器
        animDece.setEvaluator(new FloatEvaluator());
        // 构造一个在文字大小上变化的属性动画
        animBounce = ObjectAnimator.ofFloat(tv_interpolator, "textSize", 20f, 60f);
        // 给属性动画设置震荡插值器
        animBounce.setInterpolator(new BounceInterpolator());
        // 给属性动画设置浮点型估值器
        animBounce.setEvaluator(new FloatEvaluator());
    }

    // 根据插值器类型展示属性动画
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void showInterpolator(int type) {
        ObjectAnimator anim = null;
        if (type == 0) { // 背景色+加速插值器+颜色估值器
            anim = animAcce;
        } else if (type == 1) { // 旋转+减速插值器+浮点型估值器
            anim = animDece;
        } else if (type == 2) { // 裁剪+匀速插值器+矩形估值器
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Toast.makeText(this, "矩形估值器需要Android4.3及以上版本",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            int width = tv_interpolator.getWidth();
            int height = tv_interpolator.getHeight();
            // 构造一个从四周向中间裁剪的属性动画，同时指定了矩形估值器RectEvaluator
            animLinear = ObjectAnimator.ofObject(tv_interpolator, "clipBounds",
                    new RectEvaluator(), new Rect(0, 0, width, height),
                    new Rect(width / 3, height / 3, width / 3 * 2, height / 3 * 2),
                    new Rect(0, 0, width, height));
            // 给属性动画设置匀速插值器
            animLinear.setInterpolator(new LinearInterpolator());
            anim = animLinear;
        } else if (type == 3) { // 文字大小+震荡插值器+浮点型估值器
            anim = animBounce;
            // 给属性动画添加动画事件监听器。目的是在动画结束时恢复文字大小
            anim.addListener(this);
        }
        if (anim != null) {
            anim.setDuration(2000); // 设置动画的播放时长
            anim.start(); // 开始播放属性动画
        }
    }

    // 在属性动画开始播放时触发
    public void onAnimationStart(Animator animation) {}

    // 在属性动画结束播放时触发
    public void onAnimationEnd(Animator animation) {
        if (animation.equals(animBounce)) { // 震荡动画
            // 构造一个在文字大小上变化的属性动画
            ObjectAnimator anim = ObjectAnimator.ofFloat(tv_interpolator, "textSize", 60f, 20f);
            // 给属性动画设置震荡插值器
            anim.setInterpolator(new BounceInterpolator());
            // 给属性动画设置浮点型估值器
            anim.setEvaluator(new FloatEvaluator());
            anim.setDuration(2000); // 设置动画的播放时长
            anim.start(); // 开始播放属性动画
        }
    }

    // 在属性动画取消播放时触发
    public void onAnimationCancel(Animator animation) {}

    // 在属性动画重复播放时触发
    public void onAnimationRepeat(Animator animation) {}

}
