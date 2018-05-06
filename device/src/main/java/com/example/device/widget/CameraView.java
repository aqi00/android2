package com.example.device.widget;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.example.device.util.BitmapUtil;
import com.example.device.util.CameraUtil;
import com.example.device.util.DateUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraView extends SurfaceView {
    private static final String TAG = "CameraView";
    private Context mContext; // 声明一个上下文对象
    private Camera mCamera; // 声明一个相机对象
    private boolean isPreviewing = false; // 是否正在预览
    private Point mCameraSize; // 相机画面的尺寸
    private int mCameraType = CAMERA_BEHIND; // 摄像头类型
    public static int CAMERA_BEHIND = 0; // 后置摄像头
    public static int CAMERA_FRONT = 1; // 前置摄像头

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 获取表面视图的表面持有者
        SurfaceHolder holder = getHolder();
        // 给表面持有者添加表面变更监听器
        holder.addCallback(mSurfaceCallback);
        // 去除黑色背景。TRANSLUCENT半透明；TRANSPARENT透明
        holder.setFormat(PixelFormat.TRANSPARENT);
    }

    // 获取摄像头的类型
    public int getCameraType() {
        return mCameraType;
    }

    // 设置摄像头的类型
    public void setCameraType(int CameraType) {
        mCameraType = CameraType;
    }

    // 下面是单拍的代码
    // 执行拍照动作。外部调用该方法完成拍照
    public void doTakePicture() {
        if (isPreviewing && mCamera != null) {
            // 命令相机拍摄一张照片
            mCamera.takePicture(mShutterCallback, null, mPictureCallback);
        }
    }

    private String mPhotoPath; // 照片的保存路径
    // 获取照片的保存路径。外部调用该方法获得相片文件的路径
    public String getPhotoPath() {
        return mPhotoPath;
    }

    // 定义一个快门按下的回调监听器。可在此设置类似播放“咔嚓”声之类的操作，默认就是咔嚓。
    private ShutterCallback mShutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter...");
        }
    };

    // 定义一个获得拍照结果的回调监听器。可在此保存图片
    private PictureCallback mPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken...");
            Bitmap raw = null;
            if (null != data) {
                // 原始图像数据data是字节数组，需要将其解析成位图
                raw = BitmapFactory.decodeByteArray(data, 0, data.length);
                // 停止预览画面
                mCamera.stopPreview();
                isPreviewing = false;
            }
            // 旋转位图
            Bitmap bitmap = BitmapUtil.getRotateBitmap(raw,
                    (mCameraType == CAMERA_BEHIND) ? 90 : -90);
            // 获取本次拍摄的照片保存路径
            mPhotoPath = String.format("%s%s.jpg", BitmapUtil.getCachePath(mContext),
                    DateUtil.getNowDateTime());
            // 保存照片文件
            BitmapUtil.saveBitmap(mPhotoPath, bitmap, "jpg", 80);
            Log.d(TAG, "bitmap.size=" + (bitmap.getByteCount() / 1024) + "K" + ", path=" + mPhotoPath);
            try {
                Thread.sleep(1000); // 保存文件需要时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 再次进入预览画面
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

    // 预览画面状态变更时的回调监听器
    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        // 在表面视图创建时触发
        public void surfaceCreated(SurfaceHolder holder) {
            // 打开摄像头
            mCamera = Camera.open(mCameraType);
            try {
                // 设置相机的预览界面
                mCamera.setPreviewDisplay(holder);
                // 获得相机画面的尺寸
                mCameraSize = CameraUtil.getCameraSize(mCamera.getParameters(),
                        CameraUtil.getSize(mContext));
                Log.d(TAG, "width=" + mCameraSize.x + ", height=" + mCameraSize.y);
                // 获取相机的参数信息
                Camera.Parameters parameters = mCamera.getParameters();
                // 设置预览界面的尺寸
                parameters.setPreviewSize(mCameraSize.x, mCameraSize.y);
                // 设置图片的分辨率
                parameters.setPictureSize(mCameraSize.x, mCameraSize.y);
                // 如果想得到最大分辨率的图片，可使用下面两行代码设置最大的图片尺寸
                //Camera.Size maxSize = CameraUtil.getMaxPictureSize(mCamera.getParameters());
                //parameters.setPictureSize(maxSize.width, maxSize.height);
                // 设置图片的格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                // 设置对焦模式为自动对焦。前置摄像头似乎无法自动对焦
                if (mCameraType == CameraView.CAMERA_BEHIND) {
                    //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    // FOCUS_MODE_AUTO只会自动对焦一次，若想连续对焦则需用下面的FOCUS_MODE_CONTINUOUS_PICTURE
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                // 设置相机的参数信息
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
                mCamera.release(); // 遇到异常要释放相机资源
                mCamera = null;
            }
        }

        // 在表面视图变更时触发
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // 设置相机的展示角度
            mCamera.setDisplayOrientation(90);
            // 开始预览画面
            mCamera.startPreview();
            isPreviewing = true;
            // 开始自动对焦
            mCamera.autoFocus(null);
            // 设置相机的预览监听器。注意这里的setPreviewCallback给连拍功能使用
            mCamera.setPreviewCallback(mPreviewCallback);
        }

        // 在表面视图销毁时触发
        public void surfaceDestroyed(SurfaceHolder holder) {
            // 将预览监听器置空
            mCamera.setPreviewCallback(null);
            // 停止预览画面
            mCamera.stopPreview();
            // 释放相机资源
            mCamera.release();
            mCamera = null;
        }
    };

    // 下面是连拍的代码
    private boolean isShooting = false; // 是否正在连拍
    private int shooting_num = 0; // 已经拍摄的相片数量

    // 执行连拍动作。外部调用该方法完成连拍
    public void doTakeShooting() {
        mShootingArray = new ArrayList<String>();
        isShooting = true;
        shooting_num = 0;
    }

    private ArrayList<String> mShootingArray; // 连拍的相片保存路径队列
    // 获取连拍的相片保存路径队列。外部调用该方法获得连拍结果相片的路径队列
    public ArrayList<String> getShootingList() {
        return mShootingArray;
    }

    // 定义一个画面预览的回调监听器。在此可捕获动态的连续图片
    private PreviewCallback mPreviewCallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d(TAG, "onPreviewFrame isShooting=" + isShooting + ", shooting_num=" + shooting_num);
            if (!isShooting) {
                return;
            }
            // 获取相机的参数信息
            Camera.Parameters parameters = camera.getParameters();
            // 获得预览数据的格式
            int imageFormat = parameters.getPreviewFormat();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;
            Rect rect = new Rect(0, 0, width, height);
            // 创建一个YUV格式的图像对象
            YuvImage yuvImg = new YuvImage(data, imageFormat, width, height, null);
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                yuvImg.compressToJpeg(rect, 80, bos);
                // 从字节数组中解析出位图数据
                Bitmap raw = BitmapFactory.decodeByteArray(
                        bos.toByteArray(), 0, bos.size());
                // 旋转位图
                Bitmap bitmap = BitmapUtil.getRotateBitmap(raw,
                        (mCameraType == CAMERA_BEHIND) ? 90 : -90);
                // 获取本次拍摄的照片保存路径
                String path = String.format("%s%s.jpg", BitmapUtil.getCachePath(mContext),
                        DateUtil.getNowDateTimeFull());
                // 把位图保存为图片文件
                BitmapUtil.saveBitmap(path, bitmap, "jpg", 80);
                Log.d(TAG, "bitmap.size=" + (bitmap.getByteCount() / 1024) + "K" + ", path=" + path);
                // 再次进入预览画面
                camera.startPreview();
                shooting_num++;
                mShootingArray.add(path);
                if (shooting_num > 8) {  // 每次连拍9张
                    isShooting = false;
                    Toast.makeText(mContext, "已完成连拍，按返回键回到上页查看照片。", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}