package com.example.device;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("DefaultLocale")
public class StepActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tv_step;
    private SensorManager mSensorMgr; // 声明一个传感管理器对象
    private int mStepDetector = 0; // 累加的步行检测次数
    private int mStepCounter = 0; // 计步器统计的步伐数目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        tv_step = findViewById(R.id.tv_step);
        // 从系统服务中获取传感管理器对象
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销当前活动的传感监听器
        mSensorMgr.unregisterListener(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        int suitable = 0;
        // 获取当前设备支持的传感器列表
        List<Sensor> sensorList = mSensorMgr.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) { // 找到步行检测传感器
                suitable += 1;
            } else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) { // 找到计步器
                suitable += 10;
            }
        }
        if (suitable / 10 > 0 && suitable % 10 > 0) {
            // 给步行检测传感器注册传感监听器
            mSensorMgr.registerListener(this,
                    mSensorMgr.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                    SensorManager.SENSOR_DELAY_NORMAL);
            // 给计步器注册传感监听器
            mSensorMgr.registerListener(this,
                    mSensorMgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            tv_step.setText("当前设备不支持计步器，请检查是否存在步行检测传感器和计步器");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) { // 步行检测事件
            if (event.values[0] == 1.0f) {
                mStepDetector++;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) { // 计步器事件
            mStepCounter = (int) event.values[0];
        }
        String desc = String.format("设备检测到您当前走了%d步，总计数为%d步",
                mStepDetector, mStepCounter);
        tv_step.setText(desc);
    }

    //当传感器精度改变时回调该方法，一般无需处理
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
