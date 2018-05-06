package com.example.network;

import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint(value={"HandlerLeak","SetTextI18n"})
public class NetAddressActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_host_name;
    private TextView tv_host_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_address);
        et_host_name = findViewById(R.id.et_host_name);
        tv_host_name = findViewById(R.id.tv_host_name);
        findViewById(R.id.btn_host_name).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_host_name) { // 点击了“检查主机名”按钮
            // 启动主机检查线程
            new CheckThread(et_host_name.getText().toString()).start();
        }
    }

    // 创建一个检查结果的接收处理器
    private Handler mHandler = new Handler() {
        // 在收到结果消息时触发
        public void handleMessage(Message msg) {
            tv_host_name.setText("主机检查结果如下：\n" + msg.obj);
        }
    };

    // 定义一个主机检查线程
    private class CheckThread extends Thread {
        private String mHostName; // 主机名称

        public CheckThread(String host_name) {
            mHostName = host_name;
        }

        @Override
        public void run() {
            // 获得一个默认的消息对象
            Message message = Message.obtain();
            try {
                // 根据主机名称获得主机名称对象
                InetAddress host = InetAddress.getByName(mHostName);
                // 检查该主机在规定时间内能否连上
                boolean isReachable = host.isReachable(5000);
                String desc = (isReachable) ? "可以连接" : "无法连接";
                if (isReachable) { // 可以连接
                    desc = String.format("%s\n主机名为%s\n主机地址为%s",
                            desc, host.getHostName(), host.getHostAddress());
                }
                message.what = 0; // 消息类型
                message.obj = desc; // 消息描述
            } catch (Exception e) {
                e.printStackTrace();
                message.what = -1; // 消息类型
                message.obj = e.getMessage(); // 消息描述
            }
            // 向接收处理器发送检查结果消息
            mHandler.sendMessage(message);
        }
    }

}
