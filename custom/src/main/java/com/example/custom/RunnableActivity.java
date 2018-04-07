package com.example.custom;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/14.
 */
@SuppressLint("SetTextI18n")
public class RunnableActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "RunnableActivity";
    private Button btn_runnable;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runnable);
        btn_runnable = findViewById(R.id.btn_runnable);
        tv_result = findViewById(R.id.tv_result);
        btn_runnable.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_runnable) {
            if (!isStarted) { // 不在计数，则开始计数
                btn_runnable.setText("停止计数");
                // 立即启动计数任务
                mHandler.post(mCounter);
            } else { // 已在计数，则停止计数
                btn_runnable.setText("开始计数");
                // 立即取消计数任务
                mHandler.removeCallbacks(mCounter);
            }
            isStarted = !isStarted;
        }
    }

    private boolean isStarted = false; // 是否开始计数
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private int mCount = 0; // 计数值
    // 定义一个计数任务
    private Runnable mCounter = new Runnable() {
        @Override
        public void run() {
            mCount++;
            tv_result.setText("当前计数值为：" + mCount);
            // 延迟一秒后重复计数任务
            mHandler.postDelayed(this, 1000);
        }
    };

}
