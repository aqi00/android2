package com.example.custom;

import java.util.ArrayList;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.example.custom.adapter.TrafficInfoAdapter;
import com.example.custom.bean.AppInfo;
import com.example.custom.util.AppUtil;
import com.example.custom.util.StringUtil;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class TrafficInfoActivity extends AppCompatActivity {
    private final static String TAG = "TrafficInfoActivity";
    private TextView tv_traffic; // 声明一个列表视图对象
    private ListView lv_traffic;
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_info);
        tv_traffic = findViewById(R.id.tv_traffic);
        // 从布局文件中获取名叫lv_traffic的列表视图
        lv_traffic = findViewById(R.id.lv_traffic);
        // 延迟50毫秒后开始刷新应用流量数据
        mHandler.postDelayed(mRefresh, 50);
    }

    // 定义一个刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            String desc = String.format("当前总共接收流量：%s\n　　其中接收数据流量：%s" +
                            "\n当前总共发送流量：%s\n　　其中发送数据流量：%s",
                    StringUtil.formatData(TrafficStats.getTotalRxBytes()), // 获取总共接收的流量数据
                    StringUtil.formatData(TrafficStats.getMobileRxBytes()), // 获取数据流量的接收数据
                    StringUtil.formatData(TrafficStats.getTotalTxBytes()), // 获取总共发送的流量数据
                    StringUtil.formatData(TrafficStats.getMobileTxBytes())); // 获取数据流量的发送数据
            tv_traffic.setText(desc);
            // 获取已安装的应用信息队列
            ArrayList<AppInfo> appinfoList = AppUtil.getAppInfo(TrafficInfoActivity.this, 1);
            for (int i = 0; i < appinfoList.size(); i++) {
                AppInfo item = appinfoList.get(i);
                // 根据应用编号获取该应用的接收流量数据
                item.traffic = TrafficStats.getUidRxBytes(item.uid);
                appinfoList.set(i, item);
            }
            // 构建一个流量信息的列表适配器
            TrafficInfoAdapter adapter = new TrafficInfoAdapter(TrafficInfoActivity.this, appinfoList);
            // 给lv_traffic设置流量信息列表适配器
            lv_traffic.setAdapter(adapter);
        }
    };

}
