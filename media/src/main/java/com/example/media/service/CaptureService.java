package com.example.media.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.media.MainApplication;
import com.example.media.R;
import com.example.media.util.DateUtil;
import com.example.media.util.Utils;
import com.example.media.util.FileUtil;
import com.example.media.widget.FloatWindow;
import com.example.media.widget.FloatWindow.FloatClickListener;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CaptureService extends Service implements FloatClickListener {
    private static final String TAG = "CaptureService";
    private MediaProjectionManager mMpMgr; // 声明一个媒体投影管理器对象
    private MediaProjection mMP; // 声明一个媒体投影对象
    private ImageReader mImageReader; // 声明一个图像读取器对象
    private String mImagePath, mImageName; // 文件路径，文件名
    private int mScreenWidth, mScreenHeight, mScreenDensity; // 屏幕宽度，屏幕高度，每英寸中的像素数
    private VirtualDisplay mVirtualDisplay; // 声明一个虚拟显示层对象
    private FloatWindow mFloatWindow; // 声明一个悬浮窗对象

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 生成截图文件的保存路径
        mImagePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ScreenShots/";
        // 从全局变量中获取媒体投影管理器
        mMpMgr = MainApplication.getInstance().getMpMgr();
        // 获得屏幕的宽度
        mScreenWidth = Utils.getScreenWidth(this);
        // 获得屏幕的高度
        mScreenHeight = Utils.getScreenHeight(this);
        // 获得屏幕每英寸中的像素数
        mScreenDensity = Utils.getScreenDensityDpi(this);
        // 根据屏幕宽高创建一个新的图像读取器
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2);
        if (mFloatWindow == null) {
            // 创建一个新的悬浮窗
            mFloatWindow = new FloatWindow(MainApplication.getInstance());
            // 设置悬浮窗的布局内容
            mFloatWindow.setLayout(R.layout.float_capture);
        }
        // 设置悬浮窗的点击监听器
        mFloatWindow.setOnFloatListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFloatWindow != null && !mFloatWindow.isShow()) {
            mFloatWindow.show(); // 显示悬浮窗
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // 在点击悬浮窗时触发
    public void onFloatClick(View v) {
        Toast.makeText(this, "准备截图", Toast.LENGTH_SHORT).show();
        // 延迟100毫秒后启动屏幕准备任务
        mHandler.postDelayed(mStartVirtual, 100); // 准备屏幕
        // 延迟500毫秒后启动屏幕截取任务
        mHandler.postDelayed(mCapture, 500); // 进行截图
        // 延迟1000毫秒后启动屏幕释放任务
        mHandler.postDelayed(mStopVirtual, 1000); // 释放屏幕
    }

    // 创建一个处理器对象
    private Handler mHandler = new Handler();
    // 定义一个屏幕准备任务
    private Runnable mStartVirtual = new Runnable() {
        @Override
        public void run() {
            // 截图过程中先隐藏悬浮窗
            mFloatWindow.mContentView.setVisibility(View.INVISIBLE);
            if (mMP == null) {
                // 根据结果代码和结果意图，从媒体投影管理器中创建一个新的媒体投影
                mMP = mMpMgr.getMediaProjection(MainApplication.getInstance().getResultCode(),
                        MainApplication.getInstance().getResultIntent());
            }
            // 从媒体投影管理器中创建一个新的虚拟显示层
            mVirtualDisplay = mMP.createVirtualDisplay("capture_screen", mScreenWidth, mScreenHeight,
                    mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
        }
    };

    // 定义一个屏幕截取任务
    private Runnable mCapture = new Runnable() {
        @Override
        public void run() {
            // 生成截图的文件名
            mImageName = DateUtil.getNowDateTime() + ".png";
            Log.d(TAG, "mImageName=" + mImageName);
            // 从图像读取器中获取最近的一个Image对象
            Image image = mImageReader.acquireLatestImage();
            // 把Image对象转换成位图对象
            Bitmap bitmap = FileUtil.getBitmap(image);
            if (bitmap != null) {
                // 创建文件。如果文件所在的目录不存在，就先创建目录
                FileUtil.createFile(mImagePath, mImageName);
                // 把位图对象保存为图片文件
                FileUtil.saveBitmap(mImagePath + mImageName, bitmap, "PNG", 100);
                Toast.makeText(CaptureService.this, "截图成功：" + mImagePath + mImageName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CaptureService.this, "截图失败：未截到屏幕图片", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // 定义一个屏幕释放任务
    private Runnable mStopVirtual = new Runnable() {
        @Override
        public void run() {
            // 完成截图后再恢复悬浮窗
            mFloatWindow.mContentView.setVisibility(View.VISIBLE);
            if (mVirtualDisplay != null) {
                mVirtualDisplay.release(); // 释放虚拟显示层资源
                mVirtualDisplay = null;
            }
        }
    };

    @Override
    public void onDestroy() {
        if (mFloatWindow != null && mFloatWindow.isShow()) {
            mFloatWindow.close(); // 关闭悬浮窗
        }
        if (mMP != null) {
            mMP.stop(); // 停止媒体投影
        }
        super.onDestroy();
    }

}
