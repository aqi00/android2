package com.example.device.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.device.R;

public class AudioPlayer extends LinearLayout implements
        OnCompletionListener, OnCheckedChangeListener {
    private static final String TAG = "AudioPlayer";
    private Context mContext; // 声明一个上下文对象
    private MediaPlayer mMediaPlayer; // 声明一个媒体播放器对象
    private ProgressBar pb_play; // 声明一个进度条对象
    private CheckBox ck_play;
    private Timer mTimer; // 计时器
    private String mAudioPath; // 音频文件的路径
    private boolean isFinished = true; // 是否播放结束
    private int mRawId; // raw目录下的资源编号

    public AudioPlayer(Context context) {
        this(context, null);
    }

    public AudioPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 从布局文件audio_player.xml生成当前的布局视图
        LayoutInflater.from(context).inflate(R.layout.audio_player, this);
        // 从布局文件中获取名叫pb_play的进度条
        pb_play = findViewById(R.id.pb_play);
        ck_play = findViewById(R.id.ck_play);
        ck_play.setOnCheckedChangeListener(this);
    }

    // 根据SD卡的文件路径，初始化媒体播放器
    public void init(String path) {
        mAudioPath = path;
        ck_play.setEnabled(true);
        ck_play.setTextColor(Color.BLACK);
        // 创建一个媒体播放器
        mMediaPlayer = new MediaPlayer();
        // 设置媒体播放器的播放完成监听器
        mMediaPlayer.setOnCompletionListener(this);
        Log.d(TAG, "audio path = " + mAudioPath);
    }

    // 根据raw目录的资源编号，初始化媒体播放器
    public void initFromRaw(Context context, int raw_id) {
        mContext = context;
        mRawId = raw_id;
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
            // mMediaPlayer.setVolume(0.5f, 0.5f); // 设置音量，可选
            // 设置音频流的类型为音乐
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (!TextUtils.isEmpty(mAudioPath)) {
                // 录制完毕要等一秒钟再setDataSource，因为此时可能尚未完成写入。否则会报异常“java.io.IOException: setDataSourceFD failed”
                // 设置媒体数据的文件路径
                mMediaPlayer.setDataSource(mAudioPath);
                mMediaPlayer.prepare(); // 媒体播放器准备就绪
            } else {
                // 设置指定目录路径的音乐文件
                //mMediaPlayer = MediaPlayer.create(context, Uri.parse(path));
                // 设置指定资源编号的音乐文件
                mMediaPlayer = MediaPlayer.create(mContext, mRawId);
                // 通过create方法创建的播放器实例，无需再调用prepare方法，因为create内部已经调用过了
            }
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
