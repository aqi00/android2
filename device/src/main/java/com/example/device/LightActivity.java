package com.example.device;

import com.example.device.util.SwitchUtil;
import com.example.device.util.DateUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint(value={"DefaultLocale","SetTextI18n"})
public class LightActivity extends AppCompatActivity implements
        OnCheckedChangeListener, SensorEventListener {
    private TextView tv_light;
    private SensorManager mSensorMgr; // 声明一个传感管理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        CheckBox ck_bright = findViewById(R.id.ck_bright);
        // 检查屏幕亮度是否为自动调节
        if (SwitchUtil.getAutoBrightStatus(this)) {
            ck_bright.setChecked(true);
        }
        ck_bright.setOnCheckedChangeListener(this);
        tv_light = findViewById(R.id.tv_light);
        // 从系统服务中获取传感管理器对象
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bright) {
            // 设置是否开启屏幕亮度的自动调节
            SwitchUtil.setAutoBrightStatus(this, isChecked);
        }
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
        // 给光线传感器注册传感监听器
        mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) { // 光线强度变更事件
            float light_strength = event.values[0];
            tv_light.setText(DateUtil.getNowTime() + " 当前光线强度为" + light_strength);
        }
    }

    //当传感器精度改变时回调该方法，一般无需处理
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
