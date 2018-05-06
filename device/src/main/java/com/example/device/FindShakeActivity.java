package com.example.device;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.device.widget.BettingView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("SetTextI18n")
public class FindShakeActivity extends AppCompatActivity implements SensorEventListener {
    private final static String TAG = "FindShakeActivity";
    private TextView tv_cake;
    private BettingView bv_cake; // 声明一个博饼视图对象
    private SensorManager mSensorMgr; // 声明一个传感管理器对象
    private Vibrator mVibrator; // 声明一个震动器对象
    private ArrayList<Integer> mDiceList = new ArrayList<Integer>(); // 骰子队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_shake);
        tv_cake = findViewById(R.id.tv_cake);
        // 从布局文件中获取名叫bv_cake的博饼视图
        bv_cake = findViewById(R.id.bv_cake);
        // 从系统服务中获取传感管理器对象
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 从系统服务中获取震动器对象
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        for (int i = 0; i < 6; i++) { // 一共六个骰子
            mDiceList.add(i);
        }
        // 设置博饼视图的骰子队列
        bv_cake.setDiceList(mDiceList);
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
        // 给加速度传感器注册传感监听器
        mSensorMgr.registerListener(this,
                mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // 加速度变更事件
            // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float[] values = event.values;
            if ((Math.abs(values[0]) > 15 || Math.abs(values[1]) > 15
                    || Math.abs(values[2]) > 15)) {
                if (!isShaking) {
                    isShaking = true;
                    mCount = 0;
                    mHandler.post(mShake);
                    // 系统检测到摇一摇事件后，震动手机提示用户
                    mVibrator.vibrate(500);
                }
            }
        }
    }

    // 当传感器精度改变时回调该方法，一般无需处理
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private boolean isShaking = false; // 是否正在摇骰子
    private int mCount = 0; // 骰子滚动的次数，暂定每次摇骰子滚动十次
    private Handler mHandler = new Handler();

    // 定义一个摇骰子任务
    private Runnable mShake = new Runnable() {
        @Override
        public void run() {
            if (mCount < 10) { // 尚未结束摇骰子
                mCount++;
                bv_cake.setRandom();
                mHandler.postDelayed(this, 150);
            } else { // 已经结束摇骰子
                mDiceList = new ArrayList<Integer>();
                for (int i = 0; i < 6; i++) {
                    mDiceList.add((int) (Math.random() * 30 % 6));
                }
                bv_cake.setDiceList(mDiceList);
                String desc = calculatePrize();
                tv_cake.setText("恭喜，您的博饼结果为：" + desc);
                isShaking = false;
            }
        }
    };

    // 计算中奖等级
    private String calculatePrize() {
        int four_count = checkCount(4);
        if (four_count == 6) { // 出现六个红四
            return "状元(六杯红)";
        } else if (checkCount(1) == 6) { // 出现六个红一
            return "状元(遍地锦)";
        } else if (four_count == 5) { // 出现五个红四
            return "状元(五红)";
        } else if (four_count == 4) {
            if (checkCount(1) == 2) { // 出现四个红四加两个红一
                return "状元插金花";
            } else {
                return "状元(四点红)"; // 出现四个红四，没有两个红一
            }
        } else if (four_count == 3) { // 出现三个红四
            return "三红";
        } else if (checkCount(6) == 6) { // 出现六个黑六
            return "黑六勃";
        } else if (checkCount(1) == 1 && checkCount(2) == 1 && checkCount(3) == 1
                && checkCount(4) == 1 && checkCount(5) == 1 && checkCount(6) == 1) { // 123456的骰子各出现一个
            return "对堂";
        } else if (checkCount(1) == 5 || checkCount(2) == 5 || checkCount(3) == 5
                || checkCount(5) == 5 || checkCount(6) == 5) { // 出现五个相同的点数（五个红四除外）
            return "状元(五子登科)";
        } else if (checkCount(1) == 4 || checkCount(2) == 4 || checkCount(3) == 4
                || checkCount(5) == 4 || checkCount(6) == 4) { // 出现四个相同的点数（四个红四除外）
            return "四进";
        } else if (four_count == 2) { // 出现两个红四
            return "二举";
        } else if (four_count == 1) { // 出现一个红四
            return "一秀";
        } else {
            return "别着急，再来一次";
        }
    }

    // 检查某个点数的数目
    private int checkCount(int number) {
        int count = 0;
        for (int i = 0; i < mDiceList.size(); i++) {
            if (mDiceList.get(i) + 1 == number) {
                count++;
            }
        }
        return count;
    }

}
