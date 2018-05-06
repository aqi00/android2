package com.example.performance;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.performance.util.DateUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/12/27.
 */
@SuppressLint("DefaultLocale")
public class SchedulePoolActivity extends AppCompatActivity {
    private final static String TAG = "SchedulePoolActivity";
    private TextView tv_desc;
    private String mDesc = "";
    private boolean isFirst = true; // 是否首次运行
    private int ONCE = 0; // 只启动一次
    private int RATE = 1; // 间隔若干时间周期启动
    private int DELAY = 2; // 固定延迟若干时间启动
    private ScheduledExecutorService mSinglePool; // 声明一个用于单线程的调度定时器对象
    private ScheduledExecutorService mMultiPool; // 声明一个用于多线程的调度定时器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pool);
        tv_desc = findViewById(R.id.tv_desc);
        initPoolSpinner();
    }

    // 初始化线程池下拉框
    private void initPoolSpinner() {
        ArrayAdapter<String> poolAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, poolArray);
        Spinner sp_schedule_pool = findViewById(R.id.sp_schedule_pool);
        sp_schedule_pool.setPrompt("请选择定时器线程池类型");
        sp_schedule_pool.setAdapter(poolAdapter);
        sp_schedule_pool.setOnItemSelectedListener(new PoolSelectedListener());
        sp_schedule_pool.setSelection(0);
    }

    private String[] poolArray = {
            "单线程定时器延迟一次", "单线程定时器固定速率", "单线程定时器固定延迟",
            "多线程定时器延迟一次", "多线程定时器固定速率", "多线程定时器固定延迟"
    };

    class PoolSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isFirst) {
                isFirst = false;
                return;
            }
            // 根据选项分配调度定时器实例
            ScheduledExecutorService poolService = (arg2 < 3) ? mSinglePool : mMultiPool;
            startPool(poolService, arg2 % 3); // 开始执行调度定时器的线程池处理
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    protected void onStart() {
        // 创建一个单线程的调度定时器
        mSinglePool = Executors.newSingleThreadScheduledExecutor();
        // 创建一个多线程的调度定时器（线程个数为3）
        mMultiPool = Executors.newScheduledThreadPool(3);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (!mSinglePool.isTerminated()) {
            mSinglePool.shutdown(); // 关闭单线程的调度定时器
        }
        if (!mMultiPool.isTerminated()) {
            mMultiPool.shutdown(); // 关闭多线程的调度定时器
        }
        super.onStop();
    }

    // 开始执行调度定时器的线程池处理
    private void startPool(ScheduledExecutorService pool, int type) {
        mDesc = "";
        for (int i = 0; i < 3; i++) {
            // 创建一个新的消息分发任务
            MessageRunnable refresh = new MessageRunnable(i);
            if (type == ONCE) { // 只启动一次
                pool.schedule(refresh, 1, TimeUnit.SECONDS);
            } else if (type == RATE) { // 间隔若干时间周期启动
                pool.scheduleAtFixedRate(refresh, 0, 3, TimeUnit.SECONDS);
            } else if (type == DELAY) { // 固定延迟若干时间启动
                pool.scheduleWithFixedDelay(refresh, 0, 3, TimeUnit.SECONDS);
            }
        }
    }

    // 声明一个消息处理器对象
    private Handler mHandler = new MessageHandler(this);

    // 定义一个消息处理器
    private static class MessageHandler extends Handler {
        public static WeakReference<SchedulePoolActivity> mActivity;

        public MessageHandler(SchedulePoolActivity activity) {
            mActivity = new WeakReference<SchedulePoolActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SchedulePoolActivity act = mActivity.get();
            if (act != null) {
                act.mDesc = String.format("%s\n%s 当前序号是%d",
                        act.mDesc, DateUtil.getNowTime(), msg.arg1);
                act.tv_desc.setText(act.mDesc);
            }
        }
    }

    // 定义一个消息分发任务
    private static class MessageRunnable implements Runnable {
        private int mIndex;

        public MessageRunnable(int index) {
            mIndex = index;
        }

        @Override
        public void run() {
            SchedulePoolActivity act = MessageHandler.mActivity.get();
            if (act != null) {
                Message msg = act.mHandler.obtainMessage();
                msg.arg1 = mIndex;
                act.mHandler.sendMessage(msg);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
