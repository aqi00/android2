package com.example.media;

import com.example.media.bean.MediaInfo;
import com.example.media.widget.MovieView;
import com.example.media.widget.VideoController;
import com.example.media.widget.VideoController.OnSeekChangeListener;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
@SuppressLint("ClickableViewAccessibility")
public class MovieDetailActivity extends AppCompatActivity implements OnSeekChangeListener {
    private static final String TAG = "MovieDetailActivity";
    private MediaInfo mMovie; // 声明一个媒体信息对象
    private MovieView mv_content; // 声明一个电影视图对象
    private VideoController vc_play; // 声明一个视频控制条对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        // 从前一个页面传来的意图中获取媒体信息
        mMovie = getIntent().getParcelableExtra("movie");
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(mMovie.getTitle());
        // 从布局文件中获取名叫mv_content的电影视图
        mv_content = findViewById(R.id.mv_content);
        // 从布局文件中获取名叫vc_play的视频控制条
        vc_play = findViewById(R.id.vc_play);
        // 给视频控制条设置拖动变更监听器
        vc_play.setOnSeekChangeListener(this);
        RelativeLayout rl_top = findViewById(R.id.rl_top);
        // 为电影视图准备顶部视图和底部视图
        mv_content.prepare(rl_top, vc_play);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除所有的处理器任务
        mHandler.removeCallbacksAndMessages(null);
    }

    // 开始播放电影
    private void playVideo(String video_path) {
        // 设置电影视图的视频路径
        mv_content.setVideoPath(video_path);
        // 电影视图请求获得焦点
        mv_content.requestFocus();
        // 给电影视图设置播放准备监听器
        mv_content.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 电影视图开始播放
                mv_content.begin(mp);
                // 给视频控制条设置相关联的电影视图
                vc_play.setVideoView(mv_content);
                // 移除顶部与底部视图的伸缩任务
                mHandler.removeCallbacks(mHide);
                // 延迟若干时间后启动顶部与底部视图的伸缩任务
                mHandler.postDelayed(mHide, MovieView.HIDE_TIME);
                // 立即启动进度刷新任务
                mHandler.post(mRefresh);
            }
        });
        // 给电影视图设置播放完成监听器
        mv_content.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 电影视图结束播放
                mv_content.end(mp);
                // 重置视频控制条的播放进度
                vc_play.setCurrentTime(0, 0);
            }
        });
        // 给电影视图设置触摸监听器
        mv_content.setOnTouchListener(mv_content);
        // 给电影视图设置按键监听器
        mv_content.setOnKeyListener(mv_content);
    }

    // 定义一个顶部与底部视图的伸缩任务
    private Runnable mHide = new Runnable() {
        @Override
        public void run() {
            // 显示或者隐藏电影视图的顶部与底部视图
            mv_content.showOrHide();
        }
    };

    // 定义一个控制条的进度刷新任务。实时刷新控制条的播放进度，每隔0.5秒刷新一次
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (mv_content.isPlaying()) { // 电影视图正在播放
                // 给视频控制条设置当前的播放位置和缓冲百分比
                vc_play.setCurrentTime(mv_content.getCurrentPosition(),
                        mv_content.getBufferPercentage());
            }
            // 延迟500毫秒后再次启动进度刷新任务
            mHandler.postDelayed(this, 500);
        }
    };

    // 在开始拖动进度时触发
    public void onStartSeek() {
        // 移除顶部与底部视图的伸缩任务
        mHandler.removeCallbacks(mHide);
    }

    // 在停止拖动进度时触发
    public void onStopSeek() {
        // 延迟若干时间后启动顶部与底部视图的伸缩任务
        mHandler.postDelayed(mHide, MovieView.HIDE_TIME);
    }

    private int mCurrentPosition = 0; // 当前的播放位置

    // 兼容分屏模式。当前页面被拖到分屏窗口中，就立即恢复播放视频
    protected void onStart() {
        super.onStart();
        // 恢复页面时立即从上次断点开始播放视频
        if (mCurrentPosition>0 && !mv_content.isPlaying()) {
            // 命令电影视图从指定位置开始播放
            mv_content.seekTo(mCurrentPosition);
            // 电影视图开始播放
            mv_content.start();
        } else if (mCurrentPosition == 0 ) { // 进入页面后的首次播放
            // 启用视频控制条
            vc_play.enableController();
            // 开始播放电影
            playVideo(mMovie.getUrl());
        }
    }

    // 兼容分屏模式。App处于停止状态时，则保存当前的播放进度
    protected void onStop() {
        super.onStop();
        // 暂停页面时保存当前的播放进度
        if (mv_content.isPlaying()) { // 电影视图正在播放
            // 获得电影视图当前的播放位置
            mCurrentPosition = mv_content.getCurrentPosition();
            // 电影视图暂停播放
            mv_content.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mv_content.isPlaying()) { // 电影视图正在播放
            // 获得电影视图当前的播放位置
            mCurrentPosition = mv_content.getCurrentPosition();
        }
    }

}
