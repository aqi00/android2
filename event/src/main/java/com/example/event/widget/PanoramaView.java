package com.example.event.widget;

import com.example.event.R;
import com.example.event.opengl.PanoramaRender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

@SuppressLint("ClickableViewAccessibility")
public class PanoramaView extends RelativeLayout implements SensorEventListener {
    private static final float NS2S = 1.0f / 1000000000.0f; // 将纳秒转化为秒
    private Context mContext; // 声明一个上下文对象
    private GLSurfaceView glsv_panorama; // 声明一个图形库表面视图对象
    private PanoramaRender mRender; // 声明一个全景渲染器
    private SensorManager mSensorMgr; // 声明一个传感管理器对象
    private Sensor mGyroscopeSensor; // 声明一个传感器对象
    private float mPreviousXs, mPreviousYs; // 记录陀螺仪感应的上一次xy坐标位置
    private float mTimestamp; // 记录上一次的陀螺仪感应时间戳
    private float mAngle[] = new float[3]; // 记录陀螺仪感应到的三个方向的旋转角度

    public PanoramaView(Context context) {
        this(context, null);
    }

    public PanoramaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanoramaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(); // 初始化视图
        initSensor(); // 初始化陀螺仪
    }

    // 初始化视图
    private void initView() {
        // 根据布局文件layout_panorama.xml生成转换视图对象
        LayoutInflater.from(mContext).inflate(R.layout.layout_panorama, this);
        // 从布局文件中获取名叫glsv_panorama的图形库表面视图
        glsv_panorama = findViewById(R.id.glsv_panorama);
    }

    // 初始化陀螺仪
    private void initSensor() {
        // 从系统服务中获取传感管理器对象
        mSensorMgr = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        // 获得陀螺仪传感器
        mGyroscopeSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 给陀螺仪传感器注册传感监听器
        mSensorMgr.registerListener(this, mGyroscopeSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void releaseSensor() {
        // 注销当前活动的传感监听器
        mSensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 检测到陀螺仪的感应事件
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (mTimestamp != 0) {
                final float dT = (event.timestamp - mTimestamp) * NS2S;
                mAngle[0] += event.values[0] * dT;
                mAngle[1] += event.values[1] * dT;
                mAngle[2] += event.values[2] * dT;
                float angleX = (float) Math.toDegrees(mAngle[0]);
                float angleY = (float) Math.toDegrees(mAngle[1]);
                float angleZ = (float) Math.toDegrees(mAngle[2]);
                // 计算本次的旋转角度偏移
                float dy = angleY - mPreviousYs;
                float dx = angleX - mPreviousXs;
                // 更新全景照片的旋转角度
                mRender.yAngle += dx * 2.0f;
                mRender.xAngle += dy * 0.5f;
                // 计算本次的旋转角度数值
                mPreviousYs = angleY;
                mPreviousXs = angleX;
            }
            mTimestamp = event.timestamp;
        }
    }

    //当传感器精度改变时回调该方法，一般无需处理
    public void onAccuracyChanged(Sensor sensor, int i) {}

    // 在发生触摸事件时触发
    public boolean onTouchEvent(MotionEvent event) {
        // 发生触摸时先注销陀螺仪感应，避免产生冲突
        mSensorMgr.unregisterListener(this);
        float y = event.getY();
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: // 手指移动
                // 移动手势，则令全景照片旋转相应的角度
                float dy = y - mPreviousYs;
                float dx = x - mPreviousXs;
                mRender.yAngle += dx * 0.3f;
                mRender.xAngle += dy * 0.3f;
                break;
            case MotionEvent.ACTION_UP: // 手指松开
                // 手势松开，则重新注册陀螺仪传感器
                mSensorMgr.registerListener(this, mGyroscopeSensor,
                        SensorManager.SENSOR_DELAY_FASTEST);
                break;
        }
        // 保存本次的触摸坐标数值
        mPreviousYs = y;
        mPreviousXs = x;
        return true;
    }

    // 传入全景照片的资源编号
    public void initRender(int drawableId) {
        // 声明使用OpenGL ES的版本号为2.0
        glsv_panorama.setEGLContextClientVersion(2);
        // 创建一个全新的全景渲染器
        mRender = new PanoramaRender(mContext);
        setDrawableId(drawableId);
        // 设置全景照片的渲染器
        glsv_panorama.setRenderer(mRender);
    }

    // 传入全景图片路径
    public void setDrawableId(int drawableId) {
        mRender.setDrawableId(drawableId);
    }

}
