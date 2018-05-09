package com.example.media.widget;

import com.example.media.MainApplication;
import com.example.media.R;
import com.example.media.util.DateUtil;
import com.example.media.util.Utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class AudioController extends RelativeLayout implements OnClickListener, OnSeekBarChangeListener {
    private static final String TAG = "AudioController";
    private Context mContext; // 声明一个上下文对象
    private ImageView mImagePlay; // 声明用于播放控制的图像视图对象
    private TextView mCurrentTime; // 声明用于展示当前时间的文本视图对象
    private TextView mTotalTime; // 声明用于展示播放时长的文本视图对象
    private SeekBar mSeekBar; // 声明一个拖动条对象
    private MainApplication app; // 声明一个全局应用对象
    private int mBeginViewId = 0x7F24FFF0; // 临时视图的起始视图编号
    private int dip_10, dip_40;
    private int mCurrent = 0; // 当前的播放时间，单位毫秒
    private int mBuffer = 0; // 缓冲进度
    private int mDuration = 0; // 音频的播放时长，单位毫秒
    private boolean isPaused = false; // 是否暂停

    public AudioController(Context context) {
        this(context, null);
    }

    public AudioController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dip_10 = Utils.dip2px(mContext, 10);
        dip_40 = Utils.dip2px(mContext, 40);
        initView(); // 初始化视图
        app = MainApplication.getInstance();
    }

    // 创建一个新的文本视图
    private TextView newTextView(Context context, int id) {
        TextView tv = new TextView(context);
        tv.setId(id);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        // 该视图在上级布局的垂直居中
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        tv.setLayoutParams(params);
        return tv;
    }

    // 初始化视图
    private void initView() {
        // 下面初始化一个用于播放控制（暂停/恢复）的图像视图
        mImagePlay = new ImageView(mContext);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(dip_40, dip_40);
        // 该视图在上级布局的垂直居中
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        mImagePlay.setLayoutParams(imageParams);
        mImagePlay.setId(mBeginViewId);
        mImagePlay.setOnClickListener(this);
        // 下面初始化一个用于展示当前时间的文本视图
        mCurrentTime = newTextView(mContext, mBeginViewId + 1);
        RelativeLayout.LayoutParams currentParams = (LayoutParams) mCurrentTime.getLayoutParams();
        currentParams.setMargins(dip_10, 0, 0, 0);
        // 该视图位于暂停/恢复图标的右边
        currentParams.addRule(RelativeLayout.RIGHT_OF, mImagePlay.getId());
        mCurrentTime.setLayoutParams(currentParams);
        // 下面初始化一个用于展示播放时长的文本视图
        mTotalTime = newTextView(mContext, mBeginViewId + 2);
        RelativeLayout.LayoutParams totalParams = (LayoutParams) mTotalTime.getLayoutParams();
        // 该视图与上级布局的右侧对齐
        totalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mTotalTime.setLayoutParams(totalParams);
        // 创建一个新的拖动条
        mSeekBar = new SeekBar(mContext);
        RelativeLayout.LayoutParams seekParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        totalParams.setMargins(dip_10, 0, dip_10, 0);
        // 拖动条在上级布局的垂直居中
        seekParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        // 拖动条位于当前时间的右边
        seekParams.addRule(RelativeLayout.RIGHT_OF, mCurrentTime.getId());
        // 拖动条位于播放时长的左边
        seekParams.addRule(RelativeLayout.LEFT_OF, mTotalTime.getId());
        // 设置拖动条的布局参数
        mSeekBar.setLayoutParams(seekParams);
        // 设置拖动条的最大进度
        mSeekBar.setMax(100);
        // 设置拖动条的最小高度
        mSeekBar.setMinimumHeight(100);
        // 设置拖动条当前进度图标的偏移量
        mSeekBar.setThumbOffset(0);
        // 设置拖动条的编号
        mSeekBar.setId(mBeginViewId + 3);
        // 设置拖动条的拖动变更监听器
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    // 重置播放控制条
    private void reset() {
        if (mCurrent == 0 || isPaused) { // 在开头或者处于暂停状态
            // 控制图像显示播放图标
            mImagePlay.setImageResource(R.drawable.btn_play);
        } else { // 处于播放状态
            // 控制图像显示暂停图标
            mImagePlay.setImageResource(R.drawable.btn_pause);
        }
        // 在文本视图上显示当前时间
        mCurrentTime.setText(DateUtil.formatTime(mCurrent));
        // 显示拖动条的缓冲进度
        mSeekBar.setSecondaryProgress(mBuffer);
        // 在文本视图上显示播放时长
        mTotalTime.setText(DateUtil.formatTime(mDuration));
        if (mDuration == 0) { // 播放时长为零
            // 设置拖动条的当前进度为零
            mSeekBar.setProgress(0);
        } else { // 播放时长非零
            // 设置拖动条的当前进度为播放进度
            mSeekBar.setProgress((mCurrent == 0) ? 0 : (mCurrent * 100 / mDuration));
        }
    }

    // 刷新播放控制条
    private void refresh() {
        invalidate(); // 立即刷新视图
        requestLayout(); // 立即调整布局
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        removeAllViews(); // 移除所有的下级视图
        reset(); // 重置播放控制条
        addView(mImagePlay); // 添加用于播放控制的图像视图
        addView(mCurrentTime); // 添加用于展示当前时间的文本视图
        addView(mTotalTime); // 添加用于展示播放时长的文本视图
        addView(mSeekBar); // 添加拖动条
    }

    // 在进度变更时触发。第三个参数为true表示用户拖动，为false表示代码设置进度
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    // 在开始拖动进度时触发
    public void onStartTrackingTouch(SeekBar seekBar) {}

    // 在停止拖动进度时触发
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 计算拖动后的当前时间进度
        int time = seekBar.getProgress() * mDuration / 100;
        // 拖动播放器的当前进度到指定位置
        app.mMediaPlayer.seekTo(time);
        if (mSeekListener != null) {
            // 触发监听器的拖动操作
            mSeekListener.onMusicSeek(app.mMediaPlayer.getCurrentPosition(), time);
        }
    }

    private OnSeekChangeListener mSeekListener; // 声明一个拖动条变更的监听器对象
    // 设置拖动条变更监听器
    public void setOnSeekChangeListener(OnSeekChangeListener listener) {
        mSeekListener = listener;
    }

    // 定义一个拖动条变更的监听器接口
    public interface OnSeekChangeListener {
        void onMusicSeek(int current, int seekto); // 拖动音乐到指定的播放进度
        void onMusicPause(); // 音乐暂停播放
        void onMusicResume(); // 音乐恢复播放
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mImagePlay.getId()) {
            if (app.mMediaPlayer.getDuration() <= 0) { // 播放时长为0，则不进行任何操作
                return;
            }
            if (app.mMediaPlayer.isPlaying()) { // 播放器正在播放
                app.mMediaPlayer.pause(); // 播放器暂停播放
                isPaused = true;
                if (mSeekListener != null) {
                    mSeekListener.onMusicPause(); // 触发监听器的暂停操作
                }
            } else { // 播放器不在播放
                if ((mCurrent==0 || mCurrent>app.mMediaPlayer.getDuration()-500)
                        && mSeekListener != null) {
                    mSeekListener.onMusicSeek(0, 0);
                }
                app.mMediaPlayer.start(); // 播放器开始播放
                isPaused = false;
                if (mSeekListener != null) {
                    mSeekListener.onMusicResume(); // 触发监听器的恢复操作
                }
            }
        }
        refresh(); // 刷新播放控制条
    }

    // 设置当前的播放时间
    public void setCurrentTime(int current_time, int buffer_time) {
        // 获得媒体播放器的播放时长
        mDuration = app.mMediaPlayer.getDuration();
        mCurrent = current_time;
        mBuffer = buffer_time;
        // 媒体播放器是否正在播放
        isPaused = !app.mMediaPlayer.isPlaying();
        refresh(); // 刷新播放控制条
    }

}
