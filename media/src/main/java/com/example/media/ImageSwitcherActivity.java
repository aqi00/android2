package com.example.media;

import com.example.media.adapter.GalleryAdapter;
import com.example.media.task.GestureTask;
import com.example.media.task.GestureTask.GestureCallback;
import com.example.media.util.Utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * Created by ouyangshen on 2017/12/4.
 */
@SuppressLint("ClickableViewAccessibility")
public class ImageSwitcherActivity extends AppCompatActivity implements
        OnTouchListener, OnItemClickListener, GestureCallback {
    private ImageSwitcher is_switcher; // 声明一个图像切换器对象
    private Gallery gl_switcher; // 声明一个画廊视图对象
    // 画廊需要的图片资源编号数组
    private int[] mImageRes = {
            R.drawable.scene1, R.drawable.scene2, R.drawable.scene3,
            R.drawable.scene4, R.drawable.scene5, R.drawable.scene6};
    private GestureDetector mGesture; // 声明一个手势检测器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_switcher);
        initImageSwitcher(); // 初始化图像切换器
        initGallery(); // 初始化画廊视图
    }

    // 初始化图像切换器
    private void initImageSwitcher() {
        // 从布局文件中获取名叫is_switcher的图像切换器
        is_switcher = findViewById(R.id.is_switcher);
        // 设置图像切换器的视图工厂
        is_switcher.setFactory(new ViewFactoryImpl());
        // 给图像切换器设置图片的资源编号
        is_switcher.setImageResource(mImageRes[0]);
        // 创建一个手势监听器
        GestureTask gestureListener = new GestureTask();
        // 创建一个手势检测器
        mGesture = new GestureDetector(this, gestureListener);
        // 设置手势监听器的手势回调对象
        gestureListener.setGestureCallback(this);
        // 给图像切换器设置触摸监听器
        is_switcher.setOnTouchListener(this);
    }

    // 初始化画廊视图
    private void initGallery() {
        int dip_pad = Utils.dip2px(this, 20);
        // 从布局文件中获取名叫gl_switcher的画廊视图
        gl_switcher = findViewById(R.id.gl_switcher);
        gl_switcher.setPadding(0, dip_pad, 0, dip_pad);
        // 设置画廊视图各单项之间的空白距离
        gl_switcher.setSpacing(dip_pad);
        // 设置画廊视图未选中部分的透明度
        gl_switcher.setUnselectedAlpha(0.5f);
        // 给画廊视图设置画廊适配器
        gl_switcher.setAdapter(new GalleryAdapter(this, mImageRes));
        // 给画廊视图设置单项点击监听器
        gl_switcher.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 给图像切换器设置淡入动画
        is_switcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        // 给图像切换器设置淡出动画
        is_switcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        // 给图像切换器设置图片的资源编号
        is_switcher.setImageResource(mImageRes[position]);
    }

    // 定义一个视图工厂
    public class ViewFactoryImpl implements ViewFactory {
        // 在补足视图时触发。图像切换器允许动态添加新视图，就要通过视图工厂生成新视图
        public View makeView() {
            // 创建一个新的图像视图
            ImageView iv = new ImageView(ImageSwitcherActivity.this);
            iv.setBackgroundColor(Color.WHITE);
            iv.setScaleType(ScaleType.FIT_XY);
            iv.setLayoutParams(new ImageSwitcher.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return iv;
        }
    }

    // 在发生触摸事件时触发
    public boolean onTouch(View v, MotionEvent event) {
        // 由手势检测器接管触摸事件
        mGesture.onTouchEvent(event);
        return true;
    }

    // 在切换到下一页时触发
    public void gotoNext() {
        // 给图像切换器设置向左淡入动画
        is_switcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        // 给图像切换器设置向左淡出动画
        is_switcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
        // 计算画廊视图段的下一项编号
        int next_pos = (int) (gl_switcher.getSelectedItemId() + 1);
        if (next_pos >= mImageRes.length) {
            next_pos = 0;
        }
        // 给图像切换器设置图片的资源编号
        is_switcher.setImageResource(mImageRes[next_pos]);
        // 设置画廊视图的选中项
        gl_switcher.setSelection(next_pos);
    }

    // 在切换到上一页时触发
    public void gotoPre() {
        // 给图像切换器设置向右淡入动画
        is_switcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
        // 给图像切换器设置向右淡出动画
        is_switcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
        // 计算画廊视图的上一项编号
        int pre_pos = (int) (gl_switcher.getSelectedItemId() - 1);
        if (pre_pos < 0) {
            pre_pos = mImageRes.length - 1;
        }
        // 给图像切换器设置图片的资源编号
        is_switcher.setImageResource(mImageRes[pre_pos]);
        // 设置画廊视图的选中项
        gl_switcher.setSelection(pre_pos);
    }

}
