package com.example.performance;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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
public class ThreadPoolActivity extends AppCompatActivity {
    private final static String TAG = "ThreadPoolActivity";
    private TextView tv_desc;
    private String mDesc = "";
    private boolean isFirst = true; // 是否首次运行

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool);
        tv_desc = findViewById(R.id.tv_desc);
        initPoolSpinner();
    }

    // 初始化线程池下拉框
    private void initPoolSpinner() {
        ArrayAdapter<String> poolAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, poolArray);
        Spinner sp_thread_pool = findViewById(R.id.sp_thread_pool);
        sp_thread_pool.setPrompt("请选择普通线程池类型");
        sp_thread_pool.setAdapter(poolAdapter);
        sp_thread_pool.setOnItemSelectedListener(new PoolSelectedListener());
        sp_thread_pool.setSelection(0);
    }

    private String[] poolArray = {
            "单线程线程池", "多线程线程池", "无限制线程池", "自定义线程池"
    };

    class PoolSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isFirst) {
                isFirst = false;
                return;
            }
            if (arg2 == 0) { // 单线程线程池
                ExecutorService pool = Executors.newSingleThreadExecutor();
                startPool(pool);
            } else if (arg2 == 1) { // 多线程线程池
                ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
                startPool(pool);
            } else if (arg2 == 2) { // 无限制线程池
                ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
                startPool(pool);
            } else if (arg2 == 3) { // 自定义线程池
                ThreadPoolExecutor pool = new ThreadPoolExecutor(
                        2, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(19));
                startPool(pool);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 开始执行线程池处理
    private void startPool(ExecutorService pool) {
        mDesc = "";
        for (int i = 0; i < 20; i++) {
            // 创建一个新的消息分发任务
            MessageRunnable refresh = new MessageRunnable(i);
            pool.execute(refresh); // 命令线程池执行该任务
        }
    }

    // 声明一个消息处理器对象
    private Handler mHandler = new MessageHandler(this);

    // 定义一个消息处理器
    private static class MessageHandler extends Handler {
        public static WeakReference<ThreadPoolActivity> mActivity;

        public MessageHandler(ThreadPoolActivity activity) {
            mActivity = new WeakReference<ThreadPoolActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ThreadPoolActivity act = mActivity.get();
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
            ThreadPoolActivity act = MessageHandler.mActivity.get();
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
