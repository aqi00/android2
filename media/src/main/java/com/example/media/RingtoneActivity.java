package com.example.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
@SuppressLint("DefaultLocale")
public class RingtoneActivity extends AppCompatActivity {
    private TextView tv_volume;
    private Ringtone mRingtone; // 声明一个铃声对象
    private int RING_TYPE = AudioManager.STREAM_RING; // 音频流的铃声类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        tv_volume = findViewById(R.id.tv_volume);
        initVolumeInfo(); // 初始化音量信息
        initRingSpinner();
        // 生成本App自带的铃音文件res/raw/ring.ogg的Uri实例
        uriArray[uriArray.length - 1] = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring);
    }

    // 初始化音量信息
    private void initVolumeInfo() {
        // 从系统服务中获取音频管理器
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获取铃声的最大音量
        int maxVolume = audio.getStreamMaxVolume(RING_TYPE);
        // 获取铃声的当前音量
        int nowVolume = audio.getStreamVolume(RING_TYPE);
        String desc = String.format("当前铃声音量为%d，最大音量为%d，请先将铃声音量调至最大",
                nowVolume, maxVolume);
        tv_volume.setText(desc);
    }

    // 初始化铃声下拉框
    private void initRingSpinner() {
        ArrayAdapter<String> ringAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, ringArray);
        Spinner sp_ring = findViewById(R.id.sp_ring);
        sp_ring.setPrompt("请选择要播放的铃音");
        sp_ring.setAdapter(ringAdapter);
        sp_ring.setOnItemSelectedListener(new RingSelectedListener());
        sp_ring.setSelection(0);
    }

    private String[] ringArray = {"来电铃音", "通知铃音", "闹钟铃音",
            "相机快门声", "视频录制声", "门铃叮咚声"};
    private Uri[] uriArray = {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), // 来电铃音
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), // 通知铃音
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), // 闹钟铃音
            Uri.parse("file:///system/media/audio/ui/camera_click.ogg"), // 相机快门声
            Uri.parse("file:///system/media/audio/ui/VideoRecord.ogg"), // 视频录制声
            null
    };

    class RingSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (mRingtone != null) {
                // 停止播放铃声
                mRingtone.stop();
            }
            // 从铃音文件的URI中获取铃声对象
            mRingtone = RingtoneManager.getRingtone(RingtoneActivity.this, uriArray[arg2]);
            // 开始播放铃声
            mRingtone.play();
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 停止播放铃声
        mRingtone.stop();
    }

}
