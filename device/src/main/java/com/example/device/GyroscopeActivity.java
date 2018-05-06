package com.example.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("DefaultLocale")
public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {
    private static final float NS2S = 1.0f / 1000000000.0f; // 将纳秒转化为秒
    private TextView tv_gyroscope;
    private SensorManager mSensorMgr; // 声明一个传感管理器对象
    private float mTimestamp; // 记录上次的时间戳
    private float mAngle[] = new float[3]; // 记录xyz三个方向上的旋转角度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        tv_gyroscope = findViewById(R.id.tv_gyroscope);
        // 从系统服务中获取传感管理器对象
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销当前活动的传感监听器
        mSensorMgr.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取当前设备支持的传感器列表
        List<Sensor> sensorList = mSensorMgr.getSensorList(Sensor.TYPE_ALL);
        boolean isSuitable = false;
        for (Sensor sensor : sensorList) {
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) { // 找到陀螺仪
                isSuitable = true;
                break;
            }
        }
        if (isSuitable) {
            // 给陀螺仪传感器注册传感监听器
            mSensorMgr.registerListener(this,
                    mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            tv_gyroscope.setText("当前设备不支持陀螺仪，请检查是否存在陀螺仪传感器");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) { // 陀螺仪角度变更事件
            if (mTimestamp != 0) {
                final float dT = (event.timestamp - mTimestamp) * NS2S;
                mAngle[0] += event.values[0] * dT;
                mAngle[1] += event.values[1] * dT;
                mAngle[2] += event.values[2] * dT;
                // x轴的旋转角度，手机平放桌上，然后绕侧边转动
                float angleX = (float) Math.toDegrees(mAngle[0]);
                // y轴的旋转角度，手机平放桌上，然后绕底边转动
                float angleY = (float) Math.toDegrees(mAngle[1]);
                // z轴的旋转角度，手机平放桌上，然后水平旋转
                float angleZ = (float) Math.toDegrees(mAngle[2]);
                String desc = String.format("陀螺仪检测到当前x轴方向的转动角度为%f，y轴方向的转动角度为%f，z轴方向的转动角度为%f",
                        angleX, angleY, angleZ);
                tv_gyroscope.setText(desc);
            }
            mTimestamp = event.timestamp;
        }
    }

    //当传感器精度改变时回调该方法，一般无需处理
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
