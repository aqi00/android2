package com.example.media;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.media.widget.VideoController;

import java.util.Map;

public class CustomControllerActivity extends AppCompatActivity implements
        View.OnClickListener, FileSelectCallbacks {
    private VideoView vv_content; // 声明一个视频视图对象
    private VideoController vc_play; // 声明一个视频控制条对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_controller);
        findViewById(R.id.btn_open).setOnClickListener(this);
        // 从布局文件中获取名叫vv_content的视频视图
        vv_content = findViewById(R.id.vv_content);
        // 从布局文件中获取名叫vc_play的视频控制条
        vc_play = findViewById(R.id.vc_play);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除所有的处理器任务
        mHandler.removeCallbacksAndMessages(null);
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
        // 设置视频视图的视频路径
        vv_content.setVideoPath(file_path);
        // 视频视图请求获得焦点
        vv_content.requestFocus();
        // 给视频视图设置播放准备监听器
        vv_content.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 启用视频控制条
                vc_play.enableController();
                // 给视频控制条设置相关联的视频视图
                vc_play.setVideoView(vv_content);
                // 立即启动进度刷新任务
                mHandler.post(mRefresh);
            }
        });
        // 给视频视图设置播放完成监听器
        vv_content.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 重置视频控制条的播放进度
                vc_play.setCurrentTime(0, 0);
            }
        });
        // 视频视图开始播放
        vv_content.start();
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    // 定义一个控制条的进度刷新任务。实时刷新控制条的播放进度，每隔0.5秒刷新一次
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (vv_content.isPlaying()) { // 视频视图正在播放
                // 给视频控制条设置当前的播放位置和缓冲百分比
                vc_play.setCurrentTime(vv_content.getCurrentPosition(),
                        vv_content.getBufferPercentage());
            }
            // 延迟500毫秒后再次启动进度刷新任务
            mHandler.postDelayed(this, 500);
        }
    };

    private int mCurrentPosition = 0; // 当前的播放位置

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复页面时立即从上次断点开始播放视频
        if (mCurrentPosition>0 && !vv_content.isPlaying()) {
            // 命令视频视图从指定位置开始播放
            vv_content.seekTo(mCurrentPosition);
            // 视频视图开始播放
            vv_content.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停页面时保存当前的播放进度
        if (vv_content.isPlaying()) { // 视频视图正在播放
            // 获得视频视图当前的播放位置
            mCurrentPosition = vv_content.getCurrentPosition();
            // 视频视图暂停播放
            vv_content.pause();
        }
    }

}
