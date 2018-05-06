package com.example.media.widget;

import com.example.media.R;
import com.example.media.util.Utils;
import com.example.media.widget.VolumeDialog.VolumeAdjustListener;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.VideoView;

// 电影视图支持以下功能：自动全屏、调节音量、收缩控制栏、设置背景
public class MovieView extends VideoView implements
        OnTouchListener, OnKeyListener, VolumeAdjustListener {
    private static final String TAG = "MovieView";
    private Context mContext; // 声明一个上下文对象
    private int screenWidth, screenHeight; // 屏幕的宽高
    private int videoWidth, videoHeight; // 视频的宽高
    private int realWidth, realHeight; // 实际的宽高
    private int mXpos, mYpos; // 手指按下时的横纵坐标
    private int mOffset; // 判定为点击动作的偏移区间
    public static final int HIDE_TIME = 5000; // 自动隐藏顶部和底部视图的间隔时间
    private View mTopView; // 头部的标题栏视图
    private View mBottomView; // 底部的控制条视图
    private AudioManager mAudioMgr; // 声明一个音频管理器对象
    private VolumeDialog dialog; // 声明一个音量对话框对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    public MovieView(Context context) {
        this(context, null);
    }

    public MovieView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        screenWidth = Utils.getScreenWidth(mContext);
        screenHeight = Utils.getScreenHeight(mContext);
        mOffset = Utils.dip2px(mContext, 10);
        // 从系统服务中获取音频管理器
        mAudioMgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    // 重写onMeasure方法的目的是：自动将电影视图扩大至全屏显示
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // realWidth和realHeight是MediaPlayer的宽度和高度
        int width = getDefaultSize(realWidth, widthMeasureSpec);
        int height = getDefaultSize(realHeight, heightMeasureSpec);
        if (realWidth > 0 && realHeight > 0) {
            if (realWidth * height > width * realHeight) {
                height = width * realHeight / realWidth;
            } else if (realWidth * height < width * realHeight) {
                width = height * realWidth / realHeight;
            }
        }
        // 重新设置视图的宽度和高度
        setMeasuredDimension(width, height);
    }

    // 接管触摸事件，判断是否需要弹出顶部和底部的控制条
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下
                mXpos = (int) event.getX();
                mYpos = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP: // 手指松开
                // 松开手指，则弹出或关闭相关的控件（如顶部的标题栏和底部的控制条）
                if (Math.abs(event.getX() - mXpos) < mOffset &&
                        Math.abs(event.getY() - mYpos) < mOffset) {
                    showOrHide(); // 显示或者隐藏顶部与底部视图
                }
                break;
            default:
                break;
        }
        return true;
    }

    // 准备头部视图和底部视图
    public void prepare(View topTiew, View bottomView) {
        mTopView = topTiew;
        mBottomView = bottomView;
    }

    // 开始播放视频
    public void begin(MediaPlayer mp) {
        setBackground(null);
        if (mp != null) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
        }
        realWidth = videoWidth;
        realHeight = videoHeight;
        start(); // 视频视图开始播放
    }

    // 结束播放视频
    public void end(MediaPlayer mp) {
        setBackgroundResource(R.drawable.video_bg1);
        realWidth = screenWidth;
        realHeight = screenHeight;
    }

    // 利用动画效果弹出或者隐藏上下两端的控件
    public void showOrHide() {
        if (mTopView == null || mBottomView == null) {
            return;
        }
        if (mTopView.getVisibility() == View.VISIBLE) {
            // 下面进行顶部视图的动画处理
            mTopView.clearAnimation(); // 清除顶部视图的动画效果
            // 从动画资源文件中获取顶部视图的离开动画效果
            Animation animTop = AnimationUtils.loadAnimation(mContext, R.anim.leave_from_top);
            // 给顶部视图动画设置动画事件监听器
            animTop.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mTopView.setVisibility(View.GONE);
                }
            });
            mTopView.startAnimation(animTop); // 顶部视图开始播放动画
            // 下面进行底部视图的动画处理
            mBottomView.clearAnimation(); // 清除底部视图的动画效果
            // 从动画资源文件中获取底部视图的离开动画效果
            Animation animBottom = AnimationUtils.loadAnimation(mContext, R.anim.leave_from_bottom);
            // 给底部视图动画设置动画事件监听器
            animBottom.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mBottomView.setVisibility(View.GONE);
                }
            });
            mBottomView.startAnimation(animBottom); // 底部视图开始播放动画
        } else {
            // 下面进行顶部视图的动画处理
            mTopView.setVisibility(View.VISIBLE);
            mTopView.clearAnimation(); // 清除顶部视图的动画效果
            // 从动画资源文件中获取顶部视图的展开动画效果
            Animation animTop = AnimationUtils.loadAnimation(mContext, R.anim.entry_from_top);
            mTopView.startAnimation(animTop); // 顶部视图开始播放动画
            // 下面进行底部视图的动画处理
            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.clearAnimation(); // 清除底部视图的动画效果
            // 从动画资源文件中获取底部视图的展开动画效果
            Animation animBottom = AnimationUtils.loadAnimation(mContext, R.anim.entry_from_bottom);
            mBottomView.startAnimation(animBottom); // 底部视图开始播放动画
            // 移除顶部与底部视图的伸缩任务
            mHandler.removeCallbacks(mHide);
            // 延迟若干时间后启动顶部与底部视图的伸缩任务
            mHandler.postDelayed(mHide, HIDE_TIME);
        }
    }

    // 定义一个顶部与底部视图的伸缩任务
    private Runnable mHide = new Runnable() {
        @Override
        public void run() {
            showOrHide(); // 显示或者隐藏顶部与底部视图
        }
    };

    // 声明一个默认的动画事件监听器
    private class AnimationImp implements AnimationListener {
        public void onAnimationEnd(Animation animation) {}
        public void onAnimationRepeat(Animation animation) {}
        public void onAnimationStart(Animation animation) {}
    }

    // 在发生按键事件时触发，方便音量对话框调节音量大小
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { // 按下了音量+键
            // 显示音量对话框，并将音量调大一级
            showVolumeDialog(AudioManager.ADJUST_RAISE);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // 按下了音量-键
            // 显示音量对话框，并将音量调小一级
            showVolumeDialog(AudioManager.ADJUST_LOWER);
            return true;
        }
        return false;
    }

    // 显示音量对话框，同时在指定方向调节音量
    private void showVolumeDialog(int direction) {
        if (dialog == null || !dialog.isShowing()) {
            // 创建一个音量对话框
            dialog = new VolumeDialog(mContext);
            // 设置音量对话框的音量调节监听器
            dialog.setVolumeAdjustListener(this);
            dialog.show(); // 显示音量对话框
        }
        // 调节音量大小
        dialog.adjustVolume(direction, true);
        onVolumeAdjust(mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    // 在调节音量时触发
    public void onVolumeAdjust(int volume) {}

}
