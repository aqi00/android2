package com.example.performance;

import com.example.performance.util.DateUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/27.
 */
@SuppressLint("SetTextI18n")
public class RemoveTaskActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "RemoveTaskActivity";
    private CheckBox ck_remove;
    private TextView tv_remove;
    private Button btn_remove;
    private String mDesc = "";
    private boolean isRunning = false; // 定时任务是否正在运行
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_task);
        ck_remove = findViewById(R.id.ck_remove);
        tv_remove = findViewById(R.id.tv_remove);
        btn_remove = findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(this);
        TextView tv_start = findViewById(R.id.tv_start);
        tv_start.setText("页面打开时间为：" + DateUtil.getNowTime());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_remove) {
            if (!isRunning) {
                btn_remove.setText("取消定时任务");
                // 立即启动定时任务
                mHandler.post(mTask);
            } else {
                btn_remove.setText("开始定时任务");
                // 移除定时任务
                mHandler.removeCallbacks(mTask);
            }
            isRunning = !isRunning;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ck_remove.isChecked()) {
            // 移除定时任务
            mHandler.removeCallbacks(mTask);
        }
    }

    // 定义一个定时任务，用于定时发送广播
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(TASK_EVENT);
            // 通过本地的广播管理器来发送广播
            LocalBroadcastManager.getInstance(RemoveTaskActivity.this).sendBroadcast(intent);
            // 延迟2秒后再次启动定时任务
            mHandler.postDelayed(this, 2000);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // 创建一个定时任务的广播接收器
        taskReceiver = new TaskReceiver();
        // 创建一个意图过滤器，只处理指定事件来源的广播
        IntentFilter filter = new IntentFilter(TASK_EVENT);
        // 注册广播接收器，注册之后才能正常接收广播
        LocalBroadcastManager.getInstance(this).registerReceiver(taskReceiver, filter);
    }

    @Override
    public void onStop() {
        // 注销广播接收器，注销之后就不再接收广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(taskReceiver);
        super.onStop();
    }

    // 声明一个定时任务广播事件的标识串
    private String TASK_EVENT = "com.example.performance.task";
    // 声明一个定时任务的广播接收器
    private TaskReceiver taskReceiver;

    // 定义一个广播接收器，用于处理定时任务事件
    private class TaskReceiver extends BroadcastReceiver {
        // 在收到定时任务的广播时触发
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                mDesc = String.format("%s%s 打印了一行测试日志\n", mDesc, DateUtil.getNowTime());
                tv_remove.setText(mDesc);
            }
        }
    }

}
