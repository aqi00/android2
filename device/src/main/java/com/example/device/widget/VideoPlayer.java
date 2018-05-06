package com.example.device.widget;

import java.util.Timer;
import java.util.TimerTask;

import com.example.device.R;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class VideoPlayer extends LinearLayout implements
        OnCompletionListener, OnCheckedChangeListener {
    private static final String TAG = "VideoPlayer";
    private MediaPlayer mMediaPlayer; // 声明一个媒体播放器对象
    private SurfaceView sv_play; // 声明一个表面视图对象
    private ProgressBar pb_play; // 声明一个进度条对象
    private CheckBox ck_play;
    private Timer mTimer; // 计时器
    private String mVideoPath; // 视频文件的路径
    private boolean isFinished = true; // 是否播放结束

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 从布局文件video_player.xml生成当前的布局视图
        LayoutInflater.from(context).inflate(R.layout.video_player, this);
        // 从布局文件中获取名叫sv_play的表面视图
        sv_play = findViewById(R.id.sv_play);
        // 从布局文件中获取名叫pb_play的进度条
        pb_play = findViewById(R.id.pb_play);
        ck_play = findViewById(R.id.ck_play);
        ck_play.setOnCheckedChangeListener(this);
    }

    // 根据SD卡的文件路径，初始化媒体播放器
    public void init(String path) {
        mVideoPath = path;
        ck_play.setEnabled(true);
        ck_play.setTextColor(Color.BLACK);
        // 创建一个媒体播放器
        mMediaPlayer = new MediaPlayer();
        // 设置媒体播放器的播放完成监听器
        mMediaPlayer.setOnCompletionListener(this);
    }

    // 从头开始播放
    private void play() {
        try {
            mMediaPlayer.reset(); // 重置媒体播放器
            // 设置视频流的类型为音乐
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "video path = " + mVideoPath);
            // 录制完毕要等一秒钟再setDataSource，否则会报异常“java.io.IOException: setDataSourceFD failed”
            mMediaPlayer.setDataSource(mVideoPath);
            // 把视频画面输出到表面视图SurfaceView
            mMediaPlayer.setDisplay(sv_play.getHolder());
            mMediaPlayer.prepare(); // 媒体播放器准备就绪
            mMediaPlayer.start(); // 媒体播放器开始播放
            // 设置进度条的最大值，也就是媒体的播放时长
            pb_play.setMax(mMediaPlayer.getDuration());
            mTimer = new Timer(); // 创建一个计时器
            // 计时器每隔一秒就更新进度条上的播放进度
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pb_play.setProgress(mMediaPlayer.getCurrentPosition());
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 一旦发现媒体播放完毕，就触发播放完成监听器的onCompletion方法
    public void onCompletion(MediaPlayer mp) {
        isFinished = true;
        pb_play.setProgress(100);
        ck_play.setChecked(false);
        if (mTimer != null) {
            mTimer.cancel(); // 取消计时器
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_play) {
            if (isChecked) { // 开始播放
                ck_play.setText("暂停播放");
                if (isFinished) {
                    play(); // 重新播放
                } else {
                    mMediaPlayer.start(); // 媒体播放器恢复播放
                }
                isFinished = false;
            } else { // 暂停播放
                ck_play.setText("开始播放");
                mMediaPlayer.pause(); // 媒体播放器暂停播放
            }
        }
    }

}
