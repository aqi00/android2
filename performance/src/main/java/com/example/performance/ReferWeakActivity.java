package com.example.performance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.performance.util.DateUtil;

import java.lang.ref.WeakReference;

/**
 * Created by ouyangshen on 2018/4/29.
 */
@SuppressLint("SetTextI18n")
public class ReferWeakActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "ReferWeakActivity";
    private TextView tv_weak;
    private Button btn_weak;
    private String mDesc = "";
    private boolean isRunning = false; // 定时任务是否正在运行

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_weak);
        tv_weak = findViewById(R.id.tv_weak);
        btn_weak = findViewById(R.id.btn_weak);
        btn_weak.setOnClickListener(this);
        TextView tv_start = findViewById(R.id.tv_start);
        tv_start.setText("页面打开时间为：" + DateUtil.getNowTime());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_weak) {
            if (!isRunning) {
                btn_weak.setText("取消定时任务");
                // 立即启动定时任务
                mHandler.post(mTask);
            } else {
                btn_weak.setText("开始定时任务");
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

    // 声明一个弱引用的处理器对象
    private WeakHandler mHandler = new WeakHandler(this);

    // 定义一个弱引用的处理器，其内部只持有目标页面的弱引用
    private static class WeakHandler extends Handler {
        // 声明一个目标页面的弱引用
        public static WeakReference<ReferWeakActivity> mActivity;
        public WeakHandler(ReferWeakActivity activity) {
            mActivity = new WeakReference<ReferWeakActivity>(activity);
        }

        // 在收到消息时触发
        public void handleMessage(Message msg) {
            // 从目标页面的弱引用中获得一个实例
            ReferWeakActivity act = mActivity.get();
            if (act != null) {
                act.mDesc = String.format("%s%s 打印了一行测试日志\n",
                        act.mDesc, DateUtil.getNowTime());
                act.tv_weak.setText(act.mDesc);
            }
        }
    }

}
