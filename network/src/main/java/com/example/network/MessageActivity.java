package com.example.network;

import com.example.network.util.DateUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint("HandlerLeak")
public class MessageActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_message; // 声明一个文本视图对象
    private boolean isPlaying = false; // 是否正在播放新闻
    private int BEGIN = 0, SCROLL = 1, END = 2; // 0为开始，1为滚动，2为结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        // 从布局文件中获取名叫tv_control的文本视图
        tv_message = findViewById(R.id.tv_message);
        // 设置tv_message内部文字的对齐方式为靠左且靠下
        tv_message.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        tv_message.setLines(8); // 设置tv_message高度为八行文字那么高
        tv_message.setMaxLines(8); // 设置tv_message最多显示八行文字
        // 设置tv_message内部文本的移动方式为滚动形式
        tv_message.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.btn_start_message).setOnClickListener(this);
        findViewById(R.id.btn_stop_message).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_message) { // 点击了开始播放新闻的按钮
            if (!isPlaying) {
                isPlaying = true;
                new PlayThread().start(); // 创建并启动新闻播放线程
            }
        } else if (v.getId() == R.id.btn_stop_message) { // 点击了结束播放新闻的按钮
            isPlaying = false;
        }
    }

    private String[] mNewsArray = { "北斗三号卫星发射成功，定位精度媲美GPS",
            "美国赌城拉斯维加斯发生重大枪击事件", "日本在越南承建的跨海大桥未建完已下沉",
            "南水北调功在当代，数亿人喝上长江水", "马克龙呼吁重建可与中国匹敌的强大欧洲"
    };

    // 定义一个新闻播放线程
    private class PlayThread extends Thread {
        @Override
        public void run() {
            // 向处理器发送播放开始的空消息
            mHandler.sendEmptyMessage(BEGIN);
            while (isPlaying) {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain(); // 获得一个默认的消息对象
                message.what = SCROLL; // 消息类型
                message.obj = mNewsArray[(int) (Math.random() * 30 % 5)]; // 消息描述
                mHandler.sendMessage(message); // 向处理器发送消息
            }
            isPlaying = true;
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(END); // 向处理器发送播放结束的空消息
            // 如果只要简单处理，也可绕过Handler，直接调用runOnUiThread方法操作界面
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String desc = String.format("%s\n%s %s", tv_message.getText().toString(), DateUtil.getNowTime(), "新闻播放结束，谢谢观看");
//                    tv_message.setText(desc);
//                }
//            });
            isPlaying = false;
        }
    }

    // 创建一个处理器对象
    private Handler mHandler = new Handler() {
        // 在收到消息时触发
        public void handleMessage(Message msg) {
            String desc = tv_message.getText().toString();
            if (msg.what == BEGIN) { // 开始播放
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), "开始播放新闻");
            } else if (msg.what == SCROLL) { // 滚动播放
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), msg.obj);
            } else if (msg.what == END) { // 结束播放
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), "新闻播放结束");
            }
            tv_message.setText(desc);
        }
    };

}
