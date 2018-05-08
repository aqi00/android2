package com.example.event;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.event.util.DateUtil;

/**
 * Created by ouyangshen on 2017/11/23.
 */
@SuppressLint("DefaultLocale")
public class KeyHardActivity extends AppCompatActivity {
    private TextView tv_result;
    private String desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_hard);
        tv_result = findViewById(R.id.tv_result);
        initDesktopRecevier(); // 初始化桌面广播
    }

    // 在发生物理按键动作时触发
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        desc = String.format("%s物理按键的编码是%d", desc, keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            desc = String.format("%s, 按键为返回键", desc);
            // 延迟3秒后启动页面关闭任务
            new Handler().postDelayed(mFinish, 3000);
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            desc = String.format("%s, 按键为菜单键", desc);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            desc = String.format("%s, 按键为加大音量键", desc);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            desc = String.format("%s, 按键为减小音量键", desc);
        }
        desc = desc + "\n";
        tv_result.setText(desc);
        // 返回true表示不再响应系统动作，返回false表示继续响应系统动作
        return true;
    }

    // 定义一个页面关闭任务
    private Runnable mFinish = new Runnable() {
        @Override
        public void run() {
            finish(); // 关闭当前页面
        }
    };

    // 初始化桌面广播。用于监听按下主页键和任务键
    private void initDesktopRecevier() {
        // 创建一个返回桌面的广播接收器
        mDesktopRecevier = new DesktopRecevier();
        // 创建一个意图过滤器，只接收关闭系统对话框（即返回桌面）的广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        // 给当前页面注册广播接收器
        registerReceiver(mDesktopRecevier, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销当前页面的广播接收器
        unregisterReceiver(mDesktopRecevier);
    }

    private DesktopRecevier mDesktopRecevier; // 声明一个返回桌面的广播接收器对象
    // 定义一个返回到桌面的广播接收器
    class DesktopRecevier extends BroadcastReceiver {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason"; // 键名
        private final String SYSTEM_DIALOG_REASON_HOME = "homekey"; // 主页键
        private final String SYSTEM_DIALOG_REASON_TASK = "recentapps"; // 任务键

        // 在收到返回桌面广播时触发
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (!TextUtils.isEmpty(reason)) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME)) { // 按下了主页键
                        desc = String.format("%s%s\t 按键为主页键\n", desc, DateUtil.getNowTime());
                        tv_result.setText(desc);
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_TASK)) { // 按下了任务键
                        desc = String.format("%s%s\t 按键为任务键\n", desc, DateUtil.getNowTime());
                        tv_result.setText(desc);
                    }
                }
            }
        }
    }
}
