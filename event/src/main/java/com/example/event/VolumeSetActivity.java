package com.example.event;

import com.example.event.widget.VolumeDialog;
import com.example.event.widget.VolumeDialog.VolumeAdjustListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/23.
 */
@SuppressLint("SetTextI18n")
public class VolumeSetActivity extends AppCompatActivity implements VolumeAdjustListener {
    private TextView tv_volume;
    private VolumeDialog dialog; // 声明一个音量调节对话框对象
    private AudioManager mAudioMgr; // 声明一个音量管理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_set);
        tv_volume = findViewById(R.id.tv_volume);
        // 从系统服务中获取音量管理器
        mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    // 在发生物理按键动作时触发
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                && event.getAction() == KeyEvent.ACTION_DOWN) { // 按下音量加键
            // 显示音量调节对话框，并将音量调大一级
            showVolumeDialog(AudioManager.ADJUST_RAISE);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                && event.getAction() == KeyEvent.ACTION_DOWN) { // 按下音量减键
            // 显示音量调节对话框，并将音量调小一级
            showVolumeDialog(AudioManager.ADJUST_LOWER);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) { // 按下返回键
            finish(); // 关闭当前页面
            return false;
        } else { // 其它按键
            return false;
        }
    }

    // 显示音量调节对话框
    private void showVolumeDialog(int direction) {
        if (dialog == null || !dialog.isShowing()) {
            // 创建一个音量调节对话框
            dialog = new VolumeDialog(this);
            // 设置音量调节对话框的音量调节监听器
            dialog.setVolumeAdjustListener(this);
            // 显示音量调节对话框
            dialog.show();
        }
        // 令音量调节对话框按音量方向调整音量
        dialog.adjustVolume(direction, true);
        onVolumeAdjust(mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    // 在音量调节完成后触发
    public void onVolumeAdjust(int volume) {
        tv_volume.setText("调节后的音乐音量大小为：" + volume);
    }

}
