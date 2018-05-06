package com.example.device.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaRecorder.OutputFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.device.R;
import com.example.device.util.MediaUtil;

public class AudioRecorder extends LinearLayout implements OnErrorListener,
        OnInfoListener, OnCheckedChangeListener {
    private static final String TAG = "AudioRecorder";
    private Context mContext; // 声明一个上下文对象
    private MediaRecorder mMediaRecorder; // 声明一个媒体录制器对象
    private ProgressBar pb_record; // 声明一个进度条对象
    private CheckBox ck_record;
    private Timer mTimer; // 计时器
    private int mRecordMaxTime = 10; // 一次录制的最长时间
    private int mTimeCount; // 时间计数
    private String mRecordFilePath; // 录制文件的保存路径

    public AudioRecorder(Context context) {
        this(context, null);
    }

    public AudioRecorder(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecorder(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        // 从布局文件audio_recorder.xml生成当前的布局视图
        LayoutInflater.from(context).inflate(R.layout.audio_recorder, this);
        // 从布局文件中获取名叫pb_record的进度条
        pb_record = findViewById(R.id.pb_record);
        // 设置进度条的最大值
        pb_record.setMax(mRecordMaxTime);
        ck_record = findViewById(R.id.ck_record);
        ck_record.setOnCheckedChangeListener(this);
    }

    // 开始录制
    public void start() {
        // 获取本次录制的媒体文件路径
        mRecordFilePath = MediaUtil.getRecordFilePath(mContext, "RecordAudio", ".amr");
        try {
            initRecord(); // 初始化录制操作
            mTimeCount = 0; // 时间计数清零
            mTimer = new Timer(); // 创建一个计时器
            // 计时器每隔一秒就更新进度条上的录制进度
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pb_record.setProgress(mTimeCount++);
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 停止录制
    public void stop() {
        if (mOnRecordFinishListener != null) {
            mOnRecordFinishListener.onRecordFinish();
        }
        pb_record.setProgress(0); // 进度条归零
        if (mTimer != null) {
            mTimer.cancel(); // 取消计时器
        }
        cancelRecord(); // 取消录制操作
    }

    // 获取录制好的媒体文件路径
    public String getRecordFilePath() {
        return mRecordFilePath;
    }

    // 初始化录制操作
    private void initRecord() {
        mMediaRecorder = new MediaRecorder(); // 创建一个媒体录制器
        mMediaRecorder.setOnErrorListener(this); // 设置媒体录制器的错误监听器
        mMediaRecorder.setOnInfoListener(this); // 设置媒体录制器的信息监听器
        mMediaRecorder.setAudioSource(AudioSource.MIC); // 设置音频源为麦克风
        mMediaRecorder.setOutputFormat(OutputFormat.AMR_NB); // 设置媒体的输出格式
        mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB); // 设置媒体的音频编码器
        // mMediaRecorder.setAudioSamplingRate(8); // 设置媒体的音频采样率。可选
        // mMediaRecorder.setAudioChannels(2); // 设置媒体的音频声道数。可选
        // mMediaRecorder.setAudioEncodingBitRate(1024); // 设置音频每秒录制的字节数。可选
        mMediaRecorder.setMaxDuration(10 * 1000); // 设置媒体的最大录制时长
        // mMediaRecorder.setMaxFileSize(1024*1024*10); // 设置媒体的最大文件大小
        // setMaxFileSize与setMaxDuration设置其一即可
        mMediaRecorder.setOutputFile(mRecordFilePath); // 设置媒体文件的保存路径
        try {
            mMediaRecorder.prepare(); // 媒体录制器准备就绪
            mMediaRecorder.start(); // 媒体录制器开始录制
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 取消录制操作
    private void cancelRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null); // 错误监听器置空
            mMediaRecorder.setPreviewDisplay(null); // 预览界面置空
            try {
                mMediaRecorder.stop(); // 媒体录制器停止录制
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaRecorder.release(); // 媒体录制器释放资源
            mMediaRecorder = null;
        }
    }

    private OnRecordFinishListener mOnRecordFinishListener; // 声明一个录制完成监听器对象
    // 定义一个录制完成监听器接口
    public interface OnRecordFinishListener {
        void onRecordFinish();
    }

    // 设置录制完成监听器
    public void setOnRecordFinishListener(OnRecordFinishListener listener) {
        mOnRecordFinishListener = listener;
    }

    // 在录制发生错误时触发
    public void onError(MediaRecorder mr, int what, int extra) {
        if (mr != null) {
            mr.reset(); // 重置媒体录制器
        }
    }

    // 在录制遇到状况时触发
    public void onInfo(MediaRecorder mr, int what, int extra) {
        // 录制达到最大时长，或者达到文件大小限制，都停止录制
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED
                || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            ck_record.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_record) {
            if (isChecked) { // 开始录制
                ck_record.setText("停止录制");
                start();
            } else { // 停止录制
                ck_record.setText("开始录制");
                stop();
            }
        }
    }

}
