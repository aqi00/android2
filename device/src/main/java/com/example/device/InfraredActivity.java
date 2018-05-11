package com.example.device;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.device.util.DateUtil;

/**
 * Created by ouyangshen on 2018/1/29.
 */
@SuppressLint("DefaultLocale")
@TargetApi(Build.VERSION_CODES.KITKAT)
public class InfraredActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "InfraredActivity";
    private TextView tv_infrared;
    private ConsumerIrManager cim; // 声明一个红外遥控管理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infrared);
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_receive).setOnClickListener(this);
        tv_infrared = findViewById(R.id.tv_infrared);
        initInfrared();
    }

    // 初始化红外遥控管理器
    private void initInfrared() {
        // 从系统服务中获取红外遥控管理器
        cim = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
        if (!cim.hasIrEmitter()) { // 判断当前设备是否支持红外功能
            tv_infrared.setText("当前手机不支持红外遥控");
        } else {
            tv_infrared.setText("当前手机支持红外遥控");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            // NEC协议的红外编码格式通常为：
            // 引导码+用户码+数据码+数据反码+结束码。
            // 下面是一个扫地机器人的开关按键编码，用户码为4055，数据码为44。
            int[] pattern = {9000,4500, // 开头两个数字表示引导码
                    // 下面两行表示用户码
                    560,560, 560,560, 560,560, 560,560, 560,560, 560,560, 560,1680, 560,560,
                    560,1680, 560,560, 560,1680, 560,560, 560,1680, 560,560, 560,1680, 560,560,
                    // 下面一行表示数据码
                    560,560, 560,560, 560,1680, 560,560, 560,560, 560,560, 560,1680, 560,560,
                    // 下面一行表示数据反码
                    560,1680, 560,1680, 560,560, 560,1680, 560,1680, 560,1680, 560,560, 560,1680,
                    560,20000}; // 末尾两个数字表示结束码
            // 发射指定编码格式的红外信号。普通家电的红外发射频率一般为38KHz
            cim.transmit(38000, pattern);
            String hint = DateUtil.getNowTime()+"：已发射红外信号，请观察扫地机器人是否有反应";
            tv_infrared.setText(hint);
        } else if (v.getId() == R.id.btn_receive) {
            // 获得可用的载波频率范围
            CarrierFrequencyRange[] freqs = cim.getCarrierFrequencies();
            String result = "当前手机的红外载波频率范围为：\n";
            // 遍历获取所有的频率段
            for (CarrierFrequencyRange range : freqs) {
                result = String.format("%s    %d - %d\n", result,
                        range.getMinFrequency(), range.getMaxFrequency());
            }
            tv_infrared.setText(result);
        }
    }

}
