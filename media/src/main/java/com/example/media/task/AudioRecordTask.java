package com.example.media.task;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class AudioRecordTask extends AsyncTask<String, Integer, Void> {
    private final static String TAG = "AudioRecordTask";
    private Handler mHandler = new Handler();
    private int mRecordTime = 0; // 已录制时间

    @Override
    protected Void doInBackground(String... arg0) {
        File recordFile = new File(arg0[0]); // 第一个参数是音频文件的保存路径
        int frequence = Integer.parseInt(arg0[1]); // 第二个参数是音频的采样频率，单位赫兹
        int channel = Integer.parseInt(arg0[2]); // 第三个参数是音频的声道配置
        int format = Integer.parseInt(arg0[3]); // 第四个参数是音频的编码格式
        try {
            // 开通输出流到指定的文件
            DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(recordFile)));
            // 根据定义好的几个配置，来获取合适的缓冲大小
            int bsize = AudioRecord.getMinBufferSize(frequence, channel, format);
            // 定义缓冲区
            short[] buffer = new short[bsize];
            // 根据音频配置和缓冲区构建音轨录制实例
            AudioRecord record = new AudioRecord(AudioSource.MIC,
                    frequence, channel, format, bsize);
            // 设置需要通知的时间周期为1秒
            record.setPositionNotificationPeriod(1000);
            // 设置录制位置变化的监听器
            record.setRecordPositionUpdateListener(new RecordUpdateListener());
            // 开始录制音轨
            record.startRecording();
            // 没有取消录制，则持续读取缓冲区
            while (!isCancelled()) {
                int bufferReadResult = record.read(buffer, 0, buffer.length);
                // 循环将缓冲区中的音频数据写入到输出流
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeShort(buffer[i]);
                }
            }
            // 取消录制任务，则停止音轨录制
            record.stop();
            dos.close();
            Log.d(TAG, "file_path=" + arg0[0] + ", length=" + recordFile.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 准备启动线程
    protected void onPreExecute() {
        mRecordTime = 0;
        // 延迟1秒后启动刷新录制进度的任务
        mHandler.postDelayed(mRecordRun, 1000);
    }

    // 线程已经完成处理
    protected void onPostExecute(Void result) {
        if (mListener != null) {
            mListener.onRecordFinish();
        }
        // 移除刷新录制进度的任务
        mHandler.removeCallbacks(mRecordRun);
    }

    // 定义一个录制位置变化的监听器
    private class RecordUpdateListener implements OnRecordPositionUpdateListener {

        // 在标记到达时触发，对应setNotificationMarkerPosition方法的设置
        public void onMarkerReached(AudioRecord recorder) {}

        // 在周期到达时触发，对应setPositionNotificationPeriod方法的设置
        public void onPeriodicNotification(AudioRecord recorder) {
            if (mListener != null) {
                mListener.onRecordUpdate(mRecordTime);
            }
        }
    }

    // 定义一个刷新录制进度的任务
    private Runnable mRecordRun = new Runnable() {
        @Override
        public void run() {
            mRecordTime++;
            // 延迟1秒后再次启动刷新录制进度的任务
            mHandler.postDelayed(this, 1000);
        }
    };

    private OnRecordListener mListener; // 声明一个录制事件的监听器对象
    // 设置录制事件的监听器
    public void setOnRecordListener(OnRecordListener listener) {
        mListener = listener;
    }

    // 定义一个录制事件的监听器接口
    public interface OnRecordListener {
        void onRecordFinish(); // 录制完毕
        void onRecordUpdate(int duration); // 更新录制进度
    }

}
