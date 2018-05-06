package com.example.performance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.performance.util.DateUtil;

/**
 * Created by ouyangshen on 2018/4/29.
 */
@SuppressLint(value={"SetTextI18n","StaticFieldLeak"})
public class ReferStrongActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "ReferStrongActivity";
    private static TextView tv_strong;
    private Button btn_strong;
    private static String mDesc = "";
    private boolean isRunning = false; // 定时任务是否正在运行

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_strong);
        tv_strong = findViewById(R.id.tv_strong);
        btn_strong = findViewById(R.id.btn_strong);
        btn_strong.setOnClickListener(this);
        TextView tv_start = findViewById(R.id.tv_start);
        tv_start.setText("页面打开时间为：" + DateUtil.getNowTime());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_strong) {
            if (!isRunning) {
                btn_strong.setText("取消定时任务");
                // 立即启动定时任务
                mHandler.post(mTask);
            } else {
                btn_strong.setText("开始定时任务");
                // 移除定时任务
                mHandler.removeCallbacks(mTask);
            }
            isRunning = !isRunning;
        }
    }

    // 定义一个定时任务，用于定时发送广播
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            // 往处理器发送一个空消息
            mHandler.sendEmptyMessage(0);
            // 延迟2秒后再次启动定时任务
            mHandler.postDelayed(this, 2000);
        }
    };

    // 声明一个强引用的处理器对象
    private StrongHandler mHandler = new StrongHandler();

    // 定义一个强引用的处理器，平时用的Handler默认就是强引用
    private static class StrongHandler extends Handler {
        // 在收到消息时触发
        public void handleMessage(Message msg) {
            mDesc = String.format("%s%s 打印了一行测试日志\n",
                    mDesc, DateUtil.getNowTime());
            tv_strong.setText(mDesc);
        }
    }

}
