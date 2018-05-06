package com.example.device;

import com.example.device.widget.VideoPlayer;
import com.example.device.widget.VideoRecorder;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class VideoActivity extends AppCompatActivity implements VideoRecorder.OnRecordFinishListener {
    private static final String TAG = "VideoActivity";
    private VideoRecorder vr_movie; // 声明一个视频录制器对象
    private VideoPlayer vp_movie; // 声明一个视频播放器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        // 从布局文件中获取名叫vr_movie的视频录制器
        vr_movie = findViewById(R.id.vr_movie);
        // 给视频录制器设置录制完成监听器
        vr_movie.setOnRecordFinishListener(this);
        // 从布局文件中获取名叫vp_movie的视频播放器
        vp_movie = findViewById(R.id.vp_movie);
    }

    // 视频录制一旦完成，就触发监听器的onRecordFinish方法
    public void onRecordFinish() {
        // 延迟1秒后启动准备播放任务，好让系统有时间生成视频文件
        mHandler.postDelayed(mPreplay, 1000);
    }

    private Handler mHandler = new Handler();
    // 定义一个准备播放任务
    private Runnable mPreplay = new Runnable() {
        @Override
        public void run() {
            // 为视频播放器初始化待播放的视频文件
            vp_movie.init(vr_movie.getRecordFilePath());
        }
    };

}
