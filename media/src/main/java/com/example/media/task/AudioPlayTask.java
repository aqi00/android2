package com.example.media.task;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.os.AsyncTask;
import android.os.Handler;

public class AudioPlayTask extends AsyncTask<String, Integer, Void> {
    private final static String TAG = "AudioPlayTask";
    private Handler mHandler = new Handler();
    private int mPlayTime = 0; // 已播放时间

    // 线程正在后台处理
    protected Void doInBackground(String... arg0) {
        File recordFile = new File(arg0[0]); // 第一个参数是音频文件的保存路径
        int frequence = Integer.parseInt(arg0[1]); // 第二个参数是音频的采样频率，单位赫兹
        int channel = Integer.parseInt(arg0[2]); // 第三个参数是音频的声道配置
        int format = Integer.parseInt(arg0[3]); // 第四个参数是音频的编码格式
        try {
            // 定义输入流，将音频写入到AudioTrack类中，实现播放
            DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(recordFile)));
            // 根据定义好的几个配置，来获取合适的缓冲大小
            int bsize = AudioTrack.getMinBufferSize(frequence, channel, format);
            // 定义缓冲区
            short[] buffer = new short[bsize / 4];
            // 根据音频配置和缓冲区构建音轨播放实例
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    frequence, channel, format, bsize, AudioTrack.MODE_STREAM);
            // 设置需要通知的时间周期为1秒
            track.setPositionNotificationPeriod(1000);
            // 设置播放位置变化的监听器
            track.setPlaybackPositionUpdateListener(new PlaybackUpdateListener());
            // 开始播放音轨
            track.play();
            // 由于AudioTrack播放的是字节流，所以，我们需要一边播放一边读取
            while (!isCancelled() && dis.available() > 0) {
                int i = 0;
                // 把输入流中的数据循环读取到缓冲区
                while (dis.available() > 0 && i < buffer.length) {
                    buffer[i] = dis.readShort();
                    i++;
                }
                // 然后将数据写入到音轨AudioTrack中
                track.write(buffer, 0, buffer.length);
            }
            // 取消播放任务，或者读完了，都停止音轨播放
            track.stop();
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 准备启动线程
    protected void onPreExecute() {
        mPlayTime = 0;
        // 延迟1秒后启动刷新播放进度的任务
        mHandler.postDelayed(mPlayRun, 1000);
    }

    // 线程已经完成处理
    protected void onPostExecute(Void result) {
        if (mListener != null) {
            mListener.onPlayFinish();
        }
        // 移除刷新播放进度的任务
        mHandler.removeCallbacks(mPlayRun);
    }

    // 定义一个播放位置变化的监听器
    private class PlaybackUpdateListener implements OnPlaybackPositionUpdateListener {

        // 在标记到达时触发，对应setNotificationMarkerPosition方法的设置
        public void onMarkerReached(AudioTrack track) {}

        // 在周期到达时触发，对应setPositionNotificationPeriod方法的设置
        public void onPeriodicNotification(AudioTrack track) {
            if (mListener != null) {
                mListener.onPlayUpdate(mPlayTime);
            }
        }
    }

    // 定义一个刷新播放进度的任务
    private Runnable mPlayRun = new Runnable() {
        @Override
        public void run() {
            mPlayTime++;
            // 延迟1秒后再次启动刷新播放进度的任务
            mHandler.postDelayed(this, 1000);
        }
    };

    private OnPlayListener mListener; // 声明一个播放事件的监听器对象
    // 设置播放事件的监听器
    public void setOnPlayListener(OnPlayListener listener) {
        mListener = listener;
    }

    // 定义一个播放事件的监听器接口
    public interface OnPlayListener {
        void onPlayFinish(); // 播放完毕
        void onPlayUpdate(int duration); // 更新播放进度
    }

}
