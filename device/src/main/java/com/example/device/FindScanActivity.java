package com.example.device;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.app.zxing.camera.CameraManager;
import com.app.zxing.decoding.CaptureActivityHandler;
import com.app.zxing.decoding.InactivityTimer;
import com.app.zxing.view.ViewfinderView;
import com.google.zxing.Result;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("DefaultLocale")
public class FindScanActivity extends Activity implements SurfaceHolder.Callback {
    private final static String TAG = "FindScanActivity";
    private CaptureActivityHandler mHandler;
    private ViewfinderView vv_finder; // 定义一个扫码视图对象
    private boolean hasSurface = false; // 是否创建了渲染表面
    private InactivityTimer mTimer;
    private MediaPlayer mPlayer; // 声明一个媒体播放器对象
    private boolean hasBeep; // 是否支持响铃

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_scan);
        CameraManager.init(getApplication(), CameraManager.QR_CODE);
        // 从布局文件中获取名叫vv_finder的扫码视图
        vv_finder = findViewById(R.id.vv_finder);
        mTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从布局文件中获取名叫sv_scan的表面视图
        SurfaceView sv_scan = findViewById(R.id.sv_scan);
        // 从表面视图获取表面持有者
        SurfaceHolder surfaceHolder = sv_scan.getHolder();
        if (hasSurface) { // 已创建渲染表面
            initCamera(surfaceHolder);
        } else { // 未创建渲染表面
            surfaceHolder.addCallback(this);
        }
        hasBeep = true;
        // 从系统服务中获取音频管理器
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            hasBeep = false;
        }
        initBeepSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        mTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode) {
        mTimer.onActivity();
        beepAndVibrate();
        // 读取二维码分析后的结果字符串
        String resultString = result.getText();
        if (resultString == null || resultString.length() <= 0) {
            Toast.makeText(this, "Scan failed or result is null", Toast.LENGTH_SHORT).show();
        } else {
            String desc = String.format("barcode width=%d,height=%d",
                    barcode.getWidth(), barcode.getHeight());
            Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
            // 跳到扫描结果页面
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra("result", resultString);
            startActivity(intent);
        }
    }

    // 初始化相机
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 在渲染表面变更时触发
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    // 在渲染表面创建时触发
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    // 在渲染表面销毁时触发
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return vv_finder;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void drawViewfinder() {
        vv_finder.drawViewfinder();
    }

    // 初始化哔哔音效
    private void initBeepSound() {
        if (hasBeep && mPlayer == null) {
            // 设置当前页面的音频流类型
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            // 创建一个媒体播放器
            mPlayer = new MediaPlayer();
            // 设置媒体播放器的音频流类型
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置媒体播放器的播放结束监听器
            mPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                // 设置媒体播放器的媒体数据来源
                mPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                // 设置媒体播放器的左右声道音量
                mPlayer.setVolume(0.1f, 0.1f);
                // 执行媒体播放器的准备动作
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                mPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L; // 震动时长

    private void beepAndVibrate() {
        if (hasBeep && mPlayer != null) {
            // 媒体播放器开始播放音频
            mPlayer.start();
        }
        // 从系统服务中获取震动器
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 命令震动器震动若干秒
        vibrator.vibrate(VIBRATE_DURATION);
    }

    // 定义一个播放结束监听器。一旦音频播放完毕，就触发监听器的onCompletion方法
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mPlayer) {
            // 把媒体播放器的播放进度拖到最开始，即0秒处
            mPlayer.seekTo(0);
        }
    };

}