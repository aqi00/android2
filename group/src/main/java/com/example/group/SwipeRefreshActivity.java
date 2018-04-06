package com.example.group;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/21.
 */
public class SwipeRefreshActivity extends AppCompatActivity implements OnRefreshListener {
    private TextView tv_simple;
    private SwipeRefreshLayout srl_simple; // 声明一个下拉刷新布局对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_refresh);
        tv_simple = findViewById(R.id.tv_simple);
        // 从布局文件中获取名叫srl_simple的下拉刷新布局
        srl_simple = findViewById(R.id.srl_simple);
        // 给srl_simple设置下拉刷新监听器
        srl_simple.setOnRefreshListener(this);
        // 设置下拉刷新布局的进度圆圈颜色
        srl_simple.setColorSchemeResources(
                R.color.red, R.color.orange, R.color.green, R.color.blue);
    }

    // 一旦在下拉刷新布局内部往下拉动页面，就触发下拉监听器的onRefresh方法
    public void onRefresh() {
        tv_simple.setText("正在刷新");
        // 延迟若干秒后启动刷新任务
        mHandler.postDelayed(mRefresh, 2000);
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            tv_simple.setText("刷新完成");
            // 结束下拉刷新布局的刷新动作
            srl_simple.setRefreshing(false);
        }
    };

}

