package com.example.mixture;

import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mixture.adapter.ClientListAdapter;
import com.example.mixture.bean.ClientScanResult;
import com.example.mixture.task.GetClientListTask;
import com.example.mixture.task.GetClientListTask.GetClientListener;
import com.example.mixture.util.GetClientName;

import java.util.ArrayList;

public class NetbiosActivity extends AppCompatActivity implements
        GetClientListener, OnItemClickListener {
    private TextView tv_hint;
    private ListView lv_device;
    private Handler mHandler = new Handler();
    private ArrayList<ClientScanResult> mClientArray = new ArrayList<ClientScanResult>(); // 已连接设备队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netbios);
        tv_hint = findViewById(R.id.tv_hint);
        lv_device = findViewById(R.id.lv_device);
        // 延迟50毫秒后启动已连接设备的扫描任务
        mHandler.postDelayed(mClientTask, 50);
    }

    // 定义一个已连接设备的扫描任务
    private Runnable mClientTask = new Runnable() {
        @Override
        public void run() {
            // 下面开启分线程扫描已连接设备
            GetClientListTask getClientTask = new GetClientListTask();
            getClientTask.setGetClientListener(NetbiosActivity.this);
            getClientTask.execute();
            // 延迟3秒后再次启动已连接设备的扫描任务
            mHandler.postDelayed(this, 3000);
        }
    };

    // 在找到已连接设备后触发
    public void onGetClient(ArrayList<ClientScanResult> clientList) {
        mClientArray = clientList;
        if (mClientArray.size() > 0) {
            tv_hint.setVisibility(View.VISIBLE);
        } else {
            tv_hint.setVisibility(View.GONE);
        }
        // 下面把已连接设备队列通过列表视图展示出来
        ClientListAdapter clientAdapter = new ClientListAdapter(this, mClientArray);
        lv_device.setAdapter(clientAdapter);
        lv_device.setOnItemClickListener(this);
    }

    // 在点击某条设备记录时触发
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String ip = mClientArray.get(position).getIpAddr();
        // 开启分线程根据IP地址查找该设备的主机名称
        new Thread() {
            public void run() {
                // 下面以java方式获取主机名
                try {
                    GetClientName client = new GetClientName(ip);
                    showDeviceName(client.getRemoteInfo());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 显示设备的主机名
    private void showDeviceName(String info) {
        if (!TextUtils.isEmpty(info)) {
            final String[] split = info.split("\\|");
            if (split.length > 1 && split[1].length() > 0) {
                // 回到UI主线程来操作界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String desc = String.format("%s的计算机名称是%s", split[0], split[1]);
                        // 下面弹出提醒对话框展示找到的主机名
                        AlertDialog.Builder builder = new AlertDialog.Builder(NetbiosActivity.this);
                        builder.setMessage(desc);
                        builder.setPositiveButton("确定", null);
                        builder.create().show();
                    }
                });
            }
        }
    }

}
