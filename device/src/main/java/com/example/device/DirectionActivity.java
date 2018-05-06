package com.example.device;

import java.util.List;

import com.example.device.widget.CompassView;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class DirectionActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tv_direction;
    private CompassView cv_sourth; // 声明一个罗盘视图对象
    private SensorManager mSensorMgr;// 声明一个传感管理器对象
    private float[] mAcceValues; // 加速度变更值的数组
    private float[] mMagnValues; // 磁场强度变更值的数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        tv_direction = findViewById(R.id.tv_direction);
        // 从布局文件中获取名叫cv_sourth的罗盘视图
        cv_sourth = findViewById(R.id.cv_sourth);
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
        int suitable = 0;
        // 获取当前设备支持的传感器列表
        List<Sensor> sensorList = mSensorMgr.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // 找到加速度传感器
                suitable += 1;
            } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) { // 找到磁场传感器
                suitable += 10;
            }
        }
        if (suitable / 10 > 0 && suitable % 10 > 0) {
            // 给加速度传感器注册传感监听器
            mSensorMgr.registerListener(this,
                    mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            // 给磁场传感器注册传感监听器
            mSensorMgr.registerListener(this,
                    mSensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            cv_sourth.setVisibility(View.GONE);
            tv_direction.setText("当前设备不支持指南针，请检查是否存在加速度和磁场传感器");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // 加速度变更事件
            mAcceValues = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) { // 磁场强度变更事件
            mMagnValues = event.values;
        }
        if (mAcceValues != null && mMagnValues != null) {
            calculateOrientation(); // 加速度和磁场强度两个都有了，才能计算磁极的方向
        }
    }

    //当传感器精度改变时回调该方法，一般无需处理
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // 计算指南针的方向
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, mAcceValues, mMagnValues);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);
        // 设置罗盘视图中的指南针方向
        cv_sourth.setDirection((int) values[0]);
        if (values[0] >= -10 && values[0] < 10) {
            tv_direction.setText("手机上部方向是正北");
        } else if (values[0] >= 10 && values[0] < 80) {
            tv_direction.setText("手机上部方向是东北");
        } else if (values[0] >= 80 && values[0] <= 100) {
            tv_direction.setText("手机上部方向是正东");
        } else if (values[0] >= 100 && values[0] < 170) {
            tv_direction.setText("手机上部方向是东南");
        } else if ((values[0] >= 170 && values[0] <= 180)
                || (values[0]) >= -180 && values[0] < -170) {
            tv_direction.setText("手机上部方向是正南");
        } else if (values[0] >= -170 && values[0] < -100) {
            tv_direction.setText("手机上部方向是西南");
        } else if (values[0] >= -100 && values[0] < -80) {
            tv_direction.setText("手机上部方向是正西");
        } else if (values[0] >= -80 && values[0] < -10) {
            tv_direction.setText("手机上部方向是西北");
        }
    }
}
