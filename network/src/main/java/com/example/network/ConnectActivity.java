package com.example.network;

import com.example.network.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint("DefaultLocale")
public class ConnectActivity extends AppCompatActivity {
    private TextView tv_connect;
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private String[] mNetStateArray = {"正在连接", "已连接", "暂停", "正在断开", "已断开", "未知"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        tv_connect = findViewById(R.id.tv_connect);
        // 延迟50毫秒后启动刷新任务
        mHandler.postDelayed(mRefresh, 50);
    }

    // 定义一个网络连接状态的刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            getAvailableNet();
            // 延迟1秒后再次启动刷新任务
            mHandler.postDelayed(this, 1000);
        }
    };

    // 获得可用的网络连接
    private void getAvailableNet() {
        String desc;
        // 从系统服务中获取电话管理器
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 从系统服务中获取连接管理器
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 从连接管理器中获取可用的网络信息对象
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.getState() == NetworkInfo.State.CONNECTED) { // 网络已连上
                desc = String.format("当前网络连接的状态是%s", mNetStateArray[info.getState().ordinal()]);
                if (info.getType() == ConnectivityManager.TYPE_WIFI) { // WIFI网络
                    desc = String.format("%s\n当前联网的网络类型是WIFI", desc);
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) { // 4G等移动网络
                    int mobile_type = info.getSubtype(); // 获取网络子类型
                    desc = String.format("%s\n当前联网的网络类型是%s %s", desc,
                            Utils.getNetworkTypeName(tm, mobile_type),
                            Utils.getClassName(tm, mobile_type));
                } else {
                    desc = String.format("%s\n当前联网的网络类型是%d", desc, info.getType());
                }
            } else { // 网络未连上
                desc = "\n当前无上网连接";
            }
        } else {
            desc = "\n当前无上网连接";
        }
        tv_connect.setText(desc);
    }

}
