package com.example.device.widget;

import java.util.Timer;
import java.util.TimerTask;

import com.example.device.R;
import com.example.device.util.MediaUtil;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class VideoRecorder extends LinearLayout implements
        OnErrorListener, OnInfoListener, OnCheckedChangeListener {
    private static final String TAG = "VideoRecorder";
    private Context mContext; // 声明一个上下文对象
    private SurfaceHolder mHolder; // 声明一个表面持有者对象
    private Camera mCamera; // 声明一个相机对象
    private MediaRecorder mMediaRecorder; // 声明一个媒体录制器对象
    private SurfaceView sv_record; // 声明一个表面视图对象
    private ProgressBar pb_record; // 声明一个进度条对象
    private CheckBox ck_record;
    private Timer mTimer; // 计时器
    private int mRecordMaxTime = 10; // 一次拍摄的最长时间
    private int mTimeCount; // 时间计数
    private String mRecordFilePath; // 录制文件的保存路径

    public VideoRecorder(Context context) {
        this(context, null);
    }

    public VideoRecorder(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecorder(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        // 从布局文件video_recorder.xml生成当前的布局视图
        LayoutInflater.from(context).inflate(R.layout.video_recorder, this);
        // 从布局文件中获取名叫sv_record的表面视图
        sv_record = findViewById(R.id.sv_record);
        // 从布局文件中获取名叫pb_record的进度条
        pb_record = findViewById(R.id.pb_record);
        // 设置进度条的最大值
        pb_record.setMax(mRecordMaxTime);
        ck_record = findViewById(R.id.ck_record);
        ck_record.setOnCheckedChangeListener(this);
        // 获取表面视图的表面持有者
        mHolder = sv_record.getHolder();
        // 给表面持有者添加表面变更监听器
        mHolder.addCallback(mSurfaceCallback);
    }

    // 开始录制
    public void start() {
        // 获取本次录制的媒体文件路径
        mRecordFilePath = MediaUtil.getRecordFilePath(mContext, "RecordVideo", ".mp4");
        try {
            initCamera(); // 初始化相机
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
        freeCamera(); // 释放相机资源
    }

    // 获取录制好的媒体文件路径
    public String getRecordFilePath() {
        return mRecordFilePath;
    }

    // 定义一个表面持有者的变更监听器
    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        // 在表面视图创建时触发
        public void surfaceCreated(SurfaceHolder holder) {
            initCamera(); // 初始化相机
        }

        // 在表面视图变更时触发
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

        // 在表面视图销毁时触发
        public void surfaceDestroyed(SurfaceHolder holder) {
            freeCamera(); // 释放相机资源
        }
    };

    // 初始化相机操作
    private void initCamera() {
        if (mCamera != null) {
            freeCamera();
        }
        try {
            // 打开摄像头，默认后置摄像头
            mCamera = Camera.open();
            // 设置相机的展示角度
            mCamera.setDisplayOrientation(90);
            // 设置相机的预览界面
            mCamera.setPreviewDisplay(mHolder);
            // 开始预览画面
            mCamera.startPreview();
            // 解锁相机，即打开相机
            mCamera.unlock();
        } catch (Exception e) {
            e.printStackTrace();
            freeCamera();
        }
    }

    // 初始化录制操作
    private void initRecord() {
        mMediaRecorder = new MediaRecorder(); // 创建一个媒体录制器
        mMediaRecorder.setCamera(mCamera); // 设置媒体录制器的摄像头
        mMediaRecorder.setOnErrorListener(this); // 设置媒体录制器的错误监听器
        mMediaRecorder.setOnInfoListener(this); // 设置媒体录制器的信息监听器
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface()); // 设置媒体录制器的预览界面
        mMediaRecorder.setVideoSource(VideoSource.CAMERA); // 设置视频源为摄像头
        mMediaRecorder.setAudioSource(AudioSource.MIC); // 设置音频源为麦克风
        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4); // 设置媒体的输出格式
        mMediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB); // 设置媒体的音频编码器
        // 如果录像报错：MediaRecorder start failed: -19
        // 试试把setVideoSize和setVideoFrameRate注释掉，因为尺寸设置必须为摄像头所支持，否则报错
        // mMediaRecorder.setVideoSize(mWidth, mHeight); // 设置视频的分辨率
        // mMediaRecorder.setVideoFrameRate(16); // 设置视频每秒录制的帧数
        // setVideoFrameRate与setVideoEncodingBitRate设置其一即可
        mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 512); // 设置视频每秒录制的字节数
        mMediaRecorder.setOrientationHint(90); // 输出旋转90度，也就是保持竖屏录制
        mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP); // 设置媒体的视频编码器
        mMediaRecorder.setMaxDuration(mRecordMaxTime * 1000); // 设置媒体的最大录制时长
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

    // 释放相机资源
    private void freeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview(); // 停止预览
            mCamera.lock(); // 锁定相机，即关闭相机
            mCamera.release(); // 释放相机资源
            mCamera = null;
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
            mr.reset();  // 重置媒体录制器
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
