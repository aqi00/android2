package com.example.media;

import com.example.media.task.AudioPlayTask;
import com.example.media.task.AudioPlayTask.OnPlayListener;
import com.example.media.task.AudioRecordTask;
import com.example.media.task.AudioRecordTask.OnRecordListener;
import com.example.media.util.MediaUtil;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.AudioFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
@SuppressLint("SetTextI18n")
public class AudioTrackActivity extends AppCompatActivity implements
        OnCheckedChangeListener, OnRecordListener, OnPlayListener {
    private static final String TAG = "AudioTrackActivity";
    private int frequence = 8000; // 音轨的频率
    // 只能取值CHANNEL_IN_STEREO或者CHANNEL_OUT_STEREO
    // 如果取值CHANNEL_OUT_DEFAULT，会报错“getMinBufferSize(): Invalid channel configuration.”
    // 如果取值CHANNEL_OUT_MONO，会报错“java.lang.IllegalArgumentException: Unsupported channel configuration.”
    private int channel = AudioFormat.CHANNEL_IN_STEREO; // 音轨的声道
    // AudioRecord只能录制PCM格式
    private int format = AudioFormat.ENCODING_PCM_16BIT; // 音轨的格式
    private TextView tv_audio_record;
    private CheckBox ck_audio_record;
    private TextView tv_audio_play;
    private CheckBox ck_audio_play;
    private String mRecordPath; // 录制文件的保存路径
    private AudioRecordTask mRecordTask; // 声明一个音轨录制线程对象
    private AudioPlayTask mPlayTask; // 声明一个音轨播放线程对象

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_track);
        tv_audio_record = findViewById(R.id.tv_audio_record);
        ck_audio_record = findViewById(R.id.ck_audio_record);
        ck_audio_record.setOnCheckedChangeListener(this);
        tv_audio_play = findViewById(R.id.tv_audio_play);
        ck_audio_play = findViewById(R.id.ck_audio_play);
        ck_audio_play.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_audio_record) {
            if (isChecked) { // 开始录制
                // 生成音轨录制的声音文件路径
                mRecordPath = MediaUtil.getRecordFilePath(this, "RecordAudio", ".pcm");
                ck_audio_record.setText("停止录制");
                // 创建一个音轨录制线程
                mRecordTask = new AudioRecordTask();
                // 设置音轨录制线程的录制事件监听器
                mRecordTask.setOnRecordListener(this);
                // 执行音轨录制线程的事务处理
                mRecordTask.execute(mRecordPath, "" + frequence, "" + channel, "" + format);
            } else { // 停止录制
                ck_audio_record.setText("开始录制");
                if (mRecordTask.getStatus() != AsyncTask.Status.FINISHED) {
                    mRecordTask.cancel(false); // 音轨录制线程取消处理
                }
                ck_audio_play.setEnabled(true);
                ck_audio_play.setTextColor(Color.BLACK);
            }
        } else if (buttonView.getId() == R.id.ck_audio_play) {
            if (isChecked) { // 开始播放
                ck_audio_play.setText("暂停播放");
                // 创建一个音轨播放线程
                mPlayTask = new AudioPlayTask();
                // 设置音轨播放线程的播放事件监听器
                mPlayTask.setOnPlayListener(this);
                // 执行音轨播放线程的事务处理
                mPlayTask.execute(mRecordPath, "" + frequence, "" + channel, "" + format);
            } else { // 停止播放
                ck_audio_play.setText("开始播放");
                if (mPlayTask.getStatus() != AsyncTask.Status.FINISHED) {
                    mPlayTask.cancel(false); // 音轨播放线程取消处理
                }
            }
        }
    }

    // 在录制完成时触发
    public void onRecordFinish() {
        ck_audio_record.setChecked(false);
    }

    // 在录制进度更新时触发
    public void onRecordUpdate(int duration) {
        tv_audio_record.setText(duration + "秒");
    }

    // 在播放完成时触发
    public void onPlayFinish() {
        ck_audio_play.setChecked(false);
    }

    // 在播放进度更新时触发
    public void onPlayUpdate(int duration) {
        tv_audio_play.setText(duration + "秒");
    }

}
