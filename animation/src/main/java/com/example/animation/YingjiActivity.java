package com.example.animation;

import com.example.animation.widget.MosaicView;
import com.example.animation.widget.ShutterView;

import android.animation.Animator;
import android.animation.RectEvaluator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/27.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class YingjiActivity extends AppCompatActivity implements
        AnimatorListener, AnimationListener, OnClickListener {
    private RelativeLayout rl_yingji; // 声明一个相对布局对象
    private TextView tv_anim_title;
    private ImageView view1, view4, view5, view6; // 分别声明四个图像视图对象
    private ShutterView view2; // 声明一个百叶窗视图对象
    private MosaicView view3; // 声明一个马赛克视图对象
    // 定义一个用于播放动感影集的风景照片资源数组
    private int[] mImageArray = {
            R.drawable.bdg01, R.drawable.bdg02, R.drawable.bdg03, R.drawable.bdg04, R.drawable.bdg05,
            R.drawable.bdg06, R.drawable.bdg07, R.drawable.bdg08, R.drawable.bdg09, R.drawable.bdg10
    };
    private ObjectAnimator anim1, anim2, anim3, anim4; // 分别声明四个属性动画对象
    private Animation translateAnim, setAnim; // 分别声明两个补间动画对象
    private int mDuration = 5000; // 每个动画的播放时长

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yingji);
        // 从布局文件中获取名叫rl_yingji的相对布局
        rl_yingji = findViewById(R.id.rl_yingji);
        tv_anim_title = findViewById(R.id.tv_anim_title);
        playYingji(); // 开始播放动感影集
    }

    // 初始化各视图
    private void initView() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view1 = new ImageView(this);
        view1.setLayoutParams(params);
        view1.setImageResource(mImageArray[0]);
        view1.setScaleType(ScaleType.FIT_START);
        view1.setAlpha(0f);
        // 创建一个百叶窗视图
        view2 = new ShutterView(this);
        view2.setLayoutParams(params);
        view2.setImageBitmap(BitmapFactory.decodeResource(getResources(), mImageArray[1]));
        view2.setMode(PorterDuff.Mode.DST_OUT);
        // 创建一个马赛克视图
        view3 = new MosaicView(this);
        view3.setLayoutParams(params);
        view3.setImageBitmap(BitmapFactory.decodeResource(getResources(), mImageArray[2]));
        view3.setMode(PorterDuff.Mode.DST_OUT);
        view3.setRatio(-5);

        view4 = new ImageView(this);
        view4.setLayoutParams(params);
        view4.setImageResource(mImageArray[3]);
        view4.setScaleType(ScaleType.FIT_START);

        view5 = new ImageView(this);
        view5.setLayoutParams(params);
        view5.setImageResource(mImageArray[5]);
        view5.setScaleType(ScaleType.FIT_START);

        view6 = new ImageView(this);
        view6.setLayoutParams(params);
        view6.setImageResource(mImageArray[6]);
        view6.setScaleType(ScaleType.FIT_START);
    }

    // 开始播放动感影集
    private void playYingji() {
        rl_yingji.removeAllViews(); // 移除相对布局下面的所有子视图
        initView(); // 初始化各视图
        rl_yingji.addView(view1); // 往相对布局添加一个图像视图
        // 构造一个在透明度上变化的属性动画
        anim1 = ObjectAnimator.ofFloat(view1, "alpha", 0f, 1f);
        anim1.setDuration(mDuration); // 设置动画的播放时长
        anim1.addListener(this); // 给属性动画添加动画事件监听器
        anim1.start(); // 属性动画开始播放
    }

    // 在属性动画开始播放时触发
    public void onAnimationStart(Animator animation) {
        if (animation.equals(anim1)) {
            tv_anim_title.setText("正在播放灰度动画");
        } else if (animation.equals(anim2)) {
            tv_anim_title.setText("正在播放裁剪动画");
        } else if (animation.equals(anim3)) {
            tv_anim_title.setText("正在播放百叶窗动画");
        } else if (animation.equals(anim4)) {
            tv_anim_title.setText("正在播放马赛克动画");
        }
    }

    // 在属性动画结束播放时触发
    public void onAnimationEnd(Animator animation) {
        if (animation.equals(anim1)) {
            rl_yingji.addView(view2, 0);
            // 从指定资源编号的图片文件中获取位图对象
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mImageArray[0]);
            int width = view1.getWidth();
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            // 构造一个从四周向中间裁剪的属性动画
            anim2 = ObjectAnimator.ofObject(view1, "clipBounds",
                    new RectEvaluator(), new Rect(0, 0, width, height),
                    new Rect(width / 2, height / 2, width / 2, height / 2));
            anim2.setDuration(mDuration); // 设置动画的播放时长
            anim2.addListener(this); // 给属性动画添加动画事件监听器
            anim2.start(); // 属性动画开始播放
        } else if (animation.equals(anim2)) {
            rl_yingji.removeView(view1);
            rl_yingji.addView(view3, 0);
            // 构造一个按比率逐步展开的属性动画
            anim3 = ObjectAnimator.ofInt(view2, "ratio", 0, 100);
            anim3.setDuration(mDuration); // 设置动画的播放时长
            anim3.addListener(this); // 给属性动画添加动画事件监听器
            anim3.start(); // 属性动画开始播放
        } else if (animation.equals(anim3)) {
            rl_yingji.removeView(view2);
            rl_yingji.addView(view4, 0);
            int offset = 5;
            // 设置偏差比例
            view3.setOffset(offset);
            // 构造一个按比率逐步展开的属性动画
            anim4 = ObjectAnimator.ofInt(view3, "ratio", 0 - offset, 101 + offset);
            anim4.setDuration(mDuration); // 设置动画的播放时长
            anim4.addListener(this); // 给属性动画添加动画事件监听器
            anim4.start(); // 属性动画开始播放
        } else if (animation.equals(anim4)) {
            rl_yingji.removeView(view3);
            // 淡入淡出动画需要先定义一个图形资源数组，用于变换图片
            Drawable[] drawableArray = {getResources().getDrawable(mImageArray[3]),
                    getResources().getDrawable(mImageArray[4])};
            // 创建一个用于淡入淡出动画的过渡图形
            TransitionDrawable td_fade = new TransitionDrawable(drawableArray);
            td_fade.setCrossFadeEnabled(false);
            // 设置图像视图的图像为过渡图形
            view4.setImageDrawable(td_fade);
            int delay = mDuration;
            // 开始过渡图形的变换过程
            td_fade.startTransition(delay);
            tv_anim_title.setText("正在播放淡入淡出动画");
            // 延迟若干秒后启动平移动画的播放任务
            mHandler.postDelayed(mTransitionEnd, delay);
        }
    }

    private Handler mHandler = new Handler();
    // 定义一个过渡图形变换结束后的动画任务
    private Runnable mTransitionEnd = new Runnable() {
        @Override
        public void run() {
            rl_yingji.addView(view5, 0);
            // 创建一个平移动画
            translateAnim = new TranslateAnimation(0f, -view4.getWidth(), 0f, 0f);
            translateAnim.setDuration(mDuration); // 设置动画的播放时长
            translateAnim.setFillAfter(true); // 设置维持结束画面
            view4.startAnimation(translateAnim); // 平移动画开始播放
            translateAnim.setAnimationListener(YingjiActivity.this); // 给平移动画设置动画事件监听器
        }
    };

    // 开始播放集合动画
    private void startSetAnim() {
        // 创建一个灰度动画
        Animation alpha = new AlphaAnimation(1.0f, 0.1f);
        alpha.setDuration(mDuration); // 设置动画的播放时长
        alpha.setFillAfter(true); // 设置维持结束画面
        // 创建一个平移动画
        Animation translate = new TranslateAnimation(1.0f, -200f, 1.0f, 1.0f);
        translate.setDuration(mDuration); // 设置动画的播放时长
        translate.setFillAfter(true); // 设置维持结束画面
        // 创建一个缩放动画
        Animation scale = new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.5f);
        scale.setDuration(mDuration); // 设置动画的播放时长
        scale.setFillAfter(true); // 设置维持结束画面
        // 创建一个旋转动画
        Animation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(mDuration); // 设置动画的播放时长
        rotate.setFillAfter(true); // 设置维持结束画面
        // 创建一个集合动画
        setAnim = new AnimationSet(true);
        ((AnimationSet) setAnim).addAnimation(alpha); // 给集合动画添加灰度动画
        ((AnimationSet) setAnim).addAnimation(translate); // 给集合动画添加平移动画
        ((AnimationSet) setAnim).addAnimation(scale); // 给集合动画添加缩放动画
        ((AnimationSet) setAnim).addAnimation(rotate); // 给集合动画添加旋转动画
        setAnim.setFillAfter(true); // 设置维持结束画面
        view5.startAnimation(setAnim); // 集合动画开始播放
        setAnim.setAnimationListener(this); // 给集合动画设置动画事件监听器
    }

    // 在属性动画取消播放时触发
    public void onAnimationCancel(Animator animation) {}

    // 在属性动画重复播放时触发
    public void onAnimationRepeat(Animator animation) {}

    // 在补间动画开始播放时触发
    public void onAnimationStart(Animation animation) {
        if (animation.equals(translateAnim)) {
            tv_anim_title.setText("正在播放平移动画");
        } else if (animation.equals(setAnim)) {
            tv_anim_title.setText("正在播放集合动画");
        }
    }

    // 在补间动画结束播放时触发
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(translateAnim)) {
            rl_yingji.removeView(view4);
            rl_yingji.addView(view6, 0);
            startSetAnim(); // 开始播放集合动画
        } else if (animation.equals(setAnim)) {
            rl_yingji.removeView(view5);
            tv_anim_title.setText("动感影集播放结束，谢谢观看");
            view6.setOnClickListener(this);
        }
    }

    // 在补间动画重复播放时触发
    public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onClick(View v) {
        if (v.equals(view6)) {
            playYingji();
        }
    }

}
