package com.example.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("DefaultLocale")
public class SensorActivity extends AppCompatActivity {
    private TextView tv_sensor;
    private String[] mSensorType = {
            "加速度", "磁场", "方向", "陀螺仪", "光线",
            "压力", "温度", "距离", "重力", "线性加速度",
            "旋转矢量", "湿度", "环境温度", "无标定磁场", "无标定旋转矢量",
            "未校准陀螺仪", "特殊动作", "步行检测", "计步器", "地磁旋转矢量",
            "心跳", "倾斜检测", "唤醒手势", "瞥一眼", "捡起来"};
    private Map<Integer, String> mapSensor = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        tv_sensor = findViewById(R.id.tv_sensor);
        showSensorInfo(); // 显示手机自带的传感器信息
    }

    private void showSensorInfo() {
        // 从系统服务中获取传感管理器对象
        SensorManager mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 获取当前设备支持的传感器列表
        List<Sensor> sensorList = mSensorMgr.getSensorList(Sensor.TYPE_ALL);
        String show_content = "当前支持的传感器包括：\n";
        for (Sensor sensor : sensorList) {
            if (sensor.getType() >= mSensorType.length) {
                continue;
            }
            mapSensor.put(sensor.getType(), sensor.getName());
        }
        for (Map.Entry<Integer, String> item_map : mapSensor.entrySet()) {
            int type = item_map.getKey();
            String name = item_map.getValue();
            String content = String.format("%d %s：%s\n", type, mSensorType[type - 1], name);
            show_content += content;
        }
        tv_sensor.setText(show_content);
    }

}
