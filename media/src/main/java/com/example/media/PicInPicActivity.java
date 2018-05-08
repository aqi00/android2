package com.example.media;

import android.annotation.TargetApi;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.example.media.widget.VideoController;

import java.util.Map;

/**
 * Created by ouyangshen on 2018/2/8.
 */
@TargetApi(Build.VERSION_CODES.O)
public class PicInPicActivity extends AppCompatActivity implements
        View.OnClickListener, FileSelectFragment.FileSelectCallbacks {
    private static final String TAG = "PicInPicActivity";
    private LinearLayout ll_btn;
    private VideoView vv_content; // 声明一个视频视图对象
    private VideoController vc_play; // 声明一个视频控制条对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_in_pic);
        ll_btn = findViewById(R.id.ll_btn);
        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_pic).setOnClickListener(this);
        // 从布局文件中获取名叫vv_content的视频视图
        vv_content = findViewById(R.id.vv_content);
        // 从布局文件中获取名叫vc_play的视频控制条
        vc_play = findViewById(R.id.vc_play);
        initDesktopRecevier(); // 初始化桌面广播
    }

    // 初始化桌面广播。用于在按下主页键和任务键时开启画中画模式
    private void initDesktopRecevier() {
        // 创建一个返回桌面的广播接收器
        mDesktopRecevier = new DesktopRecevier();
        // 创建一个意图过滤器，只接收关闭系统对话框（即返回桌面）的广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        // 给当前页面注册广播接收器
        registerReceiver(mDesktopRecevier, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除所有的处理器任务
        mHandler.removeCallbacksAndMessages(null);
        // 注销当前页面的广播接收器
        unregisterReceiver(mDesktopRecevier);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) { // 点击了打开文件按钮
            String[] videoExs = new String[]{"mp4", "3gp", "mkv", "mov", "avi"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, videoExs, null);
        } else if (v.getId() == R.id.btn_pic) { // 点击了进入画中画按钮
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Toast.makeText(this, "Android8.0及以上版本才支持画中画", Toast.LENGTH_SHORT).show();
            } else {
                enterPicInPic(); // 进入画中画模式
            }
        }
    }

    // 进入画中画模式
    private void enterPicInPic() {
        if (!isInPictureInPictureMode()) { // 当前未开启画中画，则开启画中画模式
            // 创建画中画模式的参数构建器
            PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder();
            // 设置宽高比例值，第一个参数表示分子，第二个参数表示分母
            // 下面的10/5=2，表示画中画窗口的宽度是高度的两倍
            Rational aspectRatio = new Rational(10,5);
            // 设置画中画窗口的宽高比例
            builder.setAspectRatio(aspectRatio);
            // 进入画中画模式，注意enterPictureInPictureMode是Android8.0之后新增的方法
            enterPictureInPictureMode(builder.build());
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
        String hint = String.format("App正处于%s模式", isInPictureInPictureMode()?"画中画":"全屏");
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

    // 在进入画中画模式/退出画中画模式时触发
    public void onPictureInPictureModeChanged(boolean isInPicInPicMode, Configuration newConfig) {
        Log.d(TAG, "onPictureInPictureModeChanged isInPicInPicMode="+isInPicInPicMode);
        super.onPictureInPictureModeChanged(isInPicInPicMode, newConfig);
        if (isInPicInPicMode) { // 进入画中画模式，则隐藏除视频画面之外的其它控件
            ll_btn.setVisibility(View.GONE);
            vc_play.setVisibility(View.GONE);
        } else { // 退出画中画模式，则显示除视频画面之外的其它控件
            ll_btn.setVisibility(View.VISIBLE);
            vc_play.setVisibility(View.VISIBLE);
        }
    }

    private DesktopRecevier mDesktopRecevier; // 声明一个返回桌面的广播接收器对象
    // 定义一个返回到桌面的广播接收器
    class DesktopRecevier extends BroadcastReceiver {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason"; // 键名
        private final String SYSTEM_DIALOG_REASON_HOME = "homekey"; // 主页键
        private final String SYSTEM_DIALOG_REASON_TASK = "recentapps"; // 任务键

        // 在收到返回桌面广播时触发
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (!TextUtils.isEmpty(reason)) {
                    // 如果是按下了主页键或者任务键，则当前窗口进入画中画模式
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME)
                            || reason.equals(SYSTEM_DIALOG_REASON_TASK)) {
                        enterPicInPic(); // 进入画中画模式
                    }
                }
            }
        }
    }
}
