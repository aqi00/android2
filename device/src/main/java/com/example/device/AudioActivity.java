package com.example.device;

import com.example.device.widget.AudioPlayer;
import com.example.device.widget.AudioRecorder;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class AudioActivity extends AppCompatActivity implements AudioRecorder.OnRecordFinishListener {
    private static final String TAG = "AudioActivity";
    private AudioRecorder ar_music; // 声明一个音频录制器对象
    private AudioPlayer ap_music; // 声明一个音频播放器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        // 从布局文件中获取名叫ar_music的音频录制器
        ar_music = findViewById(R.id.ar_music);
        // 给音频录制器设置录制完成监听器
        ar_music.setOnRecordFinishListener(this);
        // 从布局文件中获取名叫ap_music的音频播放器
        ap_music = findViewById(R.id.ap_music);
    }

    // 音频录制一旦完成，就触发监听器的onRecordFinish方法
    public void onRecordFinish() {
        // 延迟1秒后启动准备播放任务，好让系统有时间生成音频文件
        mHandler.postDelayed(mPreplay, 1000);
    }

    private Handler mHandler = new Handler();
    // 定义一个准备播放任务
    private Runnable mPreplay = new Runnable() {
        @Override
        public void run() {
            // 为音频播放器初始化待播放的音频文件
            ap_music.init(ar_music.getRecordFilePath());
        }
    };

}
