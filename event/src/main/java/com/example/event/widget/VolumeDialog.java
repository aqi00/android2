package com.example.event.widget;

import com.example.event.R;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VolumeDialog implements OnSeekBarChangeListener, OnKeyListener {
    private Dialog dialog; // 声明一个对话框对象
    private View view; // 声明一个视图对象
    private SeekBar sb_music; // 声明一个拖动条对象
    private AudioManager mAudioMgr; // 声明一个音频管理器对象
    private int MUSIC = AudioManager.STREAM_MUSIC; // 音乐的音频流类型
    private int mMaxVolume; // 最大音量
    private int mNowVolume; // 当前音量

    public VolumeDialog(Context context) {
        // 从系统服务中获取音频管理器
        mAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 获取指定音频类型的最大音量
        mMaxVolume = mAudioMgr.getStreamMaxVolume(MUSIC);
        // 获取指定音频类型的当前音量
        mNowVolume = mAudioMgr.getStreamVolume(MUSIC);
        // 根据布局文件dialog_volume.xml生成视图对象
        view = LayoutInflater.from(context).inflate(R.layout.dialog_volume, null);
        // 从布局文件中获取名叫sb_music的拖动条
        sb_music = view.findViewById(R.id.sb_music);
        // 设置拖动条的拖动变更监听器
        sb_music.setOnSeekBarChangeListener(this);
        // 设置拖动条的拖动进度
        sb_music.setProgress(sb_music.getMax() * mNowVolume / mMaxVolume);
        // 创建一个指定风格的对话框对象
        dialog = new Dialog(context, R.style.VolumeDialog);
    }

    // 显示对话框
    public void show() {
        // 设置对话框窗口的内容视图
        dialog.getWindow().setContentView(view);
        // 设置对话框窗口的布局参数
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.show(); // 显示对话框
        // 设置拖动条允许获得焦点
        sb_music.setFocusable(true);
        // 设置拖动条在触摸情况下允许获得焦点
        sb_music.setFocusableInTouchMode(true);
        // 设置拖动条的按键监听器
        sb_music.setOnKeyListener(this);
    }

    // 关闭对话框
    public void dismiss() {
        // 如果对话框显示出来了，就关闭它
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // 关闭对话框
        }
    }

    // 判断对话框是否显示
    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    // 按音量方向调整音量
    public void adjustVolume(int direction, boolean fromActivity) {
        if (direction == AudioManager.ADJUST_RAISE) { // 调大音量
            mNowVolume++;
        } else { // 调小音量
            mNowVolume--;
        }
        // 设置拖动条的当前进度
        sb_music.setProgress(sb_music.getMax() * mNowVolume / mMaxVolume);
        // 把该音频类型的当前音量往指定方向调整
        mAudioMgr.adjustStreamVolume(MUSIC, direction, AudioManager.FLAG_PLAY_SOUND);
        if (mListener != null && !fromActivity) {
            mListener.onVolumeAdjust(mNowVolume);
        }
        close();
    }

    // 准备关闭对话框
    private void close() {
        // 移除原来的对话框关闭任务
        mHandler.removeCallbacks(mClose);
        // 延迟两秒后启动对话框关闭任务
        mHandler.postDelayed(mClose, 2000);
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 声明一个关闭对话框任务
    private Runnable mClose = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    // 在进度变更时触发。第三个参数为true表示用户拖动，为false表示代码设置进度
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    // 在开始拖动进度时触发
    public void onStartTrackingTouch(SeekBar seekBar) {}

    // 在停止拖动进度时触发
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 计算拖动后的当前音量
        mNowVolume = mMaxVolume * seekBar.getProgress() / seekBar.getMax();
        // 设置该音频类型的当前音量
        mAudioMgr.setStreamVolume(MUSIC, mNowVolume, AudioManager.FLAG_PLAY_SOUND);
        if (mListener != null) {
            mListener.onVolumeAdjust(mNowVolume);
        }
        close();
    }

    // 在发生按键动作时触发
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                && event.getAction() == KeyEvent.ACTION_DOWN) { // 按下了音量加键
            adjustVolume(AudioManager.ADJUST_RAISE, false); // 调大音量
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                && event.getAction() == KeyEvent.ACTION_DOWN) { // 按下了音量减键
            adjustVolume(AudioManager.ADJUST_LOWER, false); // 调小音量
            return true;
        } else {
            return false;
        }
    }

    private VolumeAdjustListener mListener; // 声明一个音量调节的监听器对象
    // 设置音量调节监听器
    public void setVolumeAdjustListener(VolumeAdjustListener listener) {
        mListener = listener;
    }

    // 定义一个音量调节的监听器接口
    public interface VolumeAdjustListener {
        void onVolumeAdjust(int volume);
    }

}
