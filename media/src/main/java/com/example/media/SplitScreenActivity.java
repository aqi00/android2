package com.example.media;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.example.media.widget.VideoController;

import java.util.Map;

/**
 * Created by ouyangshen on 2018/2/8.
 */
@TargetApi(Build.VERSION_CODES.N)
public class SplitScreenActivity extends AppCompatActivity implements
        View.OnClickListener, FileSelectFragment.FileSelectCallbacks {
    private static final String TAG = "SplitScreenActivity";
    private VideoView vv_content; // 声明一个视频视图对象
    private VideoController vc_play; // 声明一个视频控制条对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_screen);
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
        Log.d(TAG, "file_path=" + file_path);
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
        String hint = String.format("App正处于%s模式", isInMultiWindowMode()?"分屏":"全屏");
        Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
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

    // 兼容分屏模式。当前页面被拖到分屏窗口中，就立即恢复播放视频
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart isPlaying="+vv_content.isPlaying()+", getDuration="+vv_content.getDuration()+", mCurrentPosition="+mCurrentPosition);
        // 恢复页面时立即从上次断点开始播放视频
        if (mCurrentPosition>0 && !vv_content.isPlaying()) {
            // 命令视频视图从指定位置开始播放
            vv_content.seekTo(mCurrentPosition);
            // 视频视图开始播放
            vv_content.start();
        }
    }

    // 兼容分屏模式。App处于停止状态时，则保存当前的播放进度
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop isPlaying="+vv_content.isPlaying());
        // 暂停页面时保存当前的播放进度
        if (vv_content.isPlaying()) { // 视频视图正在播放
            // 获得视频视图当前的播放位置
            mCurrentPosition = vv_content.getCurrentPosition();
            // 视频视图暂停播放
            vv_content.pause();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (vv_content.isPlaying()) { // 视频视图正在播放
            // 获得视频视图当前的播放位置
            mCurrentPosition = vv_content.getCurrentPosition();
        }
    }

    // 如果AndroidManifest.xml给activity配置了configChanges属性，则视频播放页面在分屏/全屏切换之时不会触发生命周期流程，也就不会回调onMultiWindowModeChanged方法。
    // 如果没配置configChanges属性，则分屏/全屏切换之时会触发完整的生命周期流程，此时才会回调onMultiWindowModeChanged方法。
    // 在分屏模式/全屏模式之间切换时触发
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        Log.d(TAG, "onMultiWindowModeChanged isInMultiWindowMode="+isInMultiWindowMode);
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
    }
}
