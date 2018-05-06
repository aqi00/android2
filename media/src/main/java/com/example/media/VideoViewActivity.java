package com.example.media;

import java.util.Map;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.VideoView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
public class VideoViewActivity extends AppCompatActivity implements
        OnClickListener, FileSelectCallbacks, OnSeekBarChangeListener {
    private static final String TAG = "VideoViewActivity";
    private VideoView vv_play; // 声明一个视频视图对象
    private SeekBar sb_play; // 声明一个拖动条对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        findViewById(R.id.btn_open).setOnClickListener(this);
        // 从布局文件中获取名叫vv_content的视频视图
        vv_play = findViewById(R.id.vv_content);
        // 从布局文件中获取名叫sb_play的拖动条
        sb_play = findViewById(R.id.sb_play);
        // 设置拖动条的拖动变更监听器
        sb_play.setOnSeekBarChangeListener(this);
        // 禁用拖动条
        sb_play.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) {
            String[] videoExs = new String[]{"mp4", "3gp", "mkv", "mov", "avi"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, videoExs, null);
        }
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        // 拼接文件的完整路径
        String file_path = absolutePath + "/" + fileName;
        Log.d(TAG, "file_path=" + file_path);
        // 设置视频视图的视频路径
        vv_play.setVideoPath(file_path);
        // 视频视图请求获得焦点
        vv_play.requestFocus();
        // 视频视图开始播放
        vv_play.start();
        // 启用拖动条
        sb_play.setEnabled(true);
        // 立即启动进度刷新任务
        mHandler.post(mRefresh);
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    // 定义一个拖动条的进度刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 通过播放时长与当前位置，计算视频已播放的百分比
            int progress = 100 * vv_play.getCurrentPosition() / vv_play.getDuration();
            // 设置拖动条的当前进度
            sb_play.setProgress(progress);
            // 延迟500毫秒后再次启动进度刷新任务
            mHandler.postDelayed(this, 500);
        }
    };

    // 在进度变更时触发。第三个参数为true表示用户拖动，为false表示代码设置进度
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    // 在开始拖动进度时触发
    public void onStartTrackingTouch(SeekBar seekBar) {}

    // 在停止拖动进度时触发
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 通过进度百分比与播放时长，计算视频当前的播放位置
        int pos = seekBar.getProgress() * vv_play.getDuration() / 100;
        // 命令视频视图从指定位置开始播放
        vv_play.seekTo(pos);
    }

}
