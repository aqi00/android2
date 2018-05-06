package com.example.event;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/23.
 */
@SuppressLint("DefaultLocale")
public class KeySoftActivity extends AppCompatActivity implements OnKeyListener {
    private TextView tv_result;
    private String desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_soft);
        // 从布局文件中获取名叫et_soft的编辑框
        EditText et_soft = findViewById(R.id.et_soft);
        // 设置编辑框的按键监听器
        et_soft.setOnKeyListener(this);
        tv_result = findViewById(R.id.tv_result);
    }

    // 在发生按键动作时触发
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            desc = String.format("%s输入的软按键编码是%d,动作是按下", desc, keyCode);
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                desc = String.format("%s, 按键为回车键", desc);
            } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                desc = String.format("%s, 按键为删除键", desc);
            } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                desc = String.format("%s, 按键为搜索键", desc);
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
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
            return true;
        } else {
            // 返回true表示处理完了不再输入该字符，返回false表示给你输入该字符吧
            return false;
        }
    }

    // 定义一个页面关闭任务
    private Runnable mFinish = new Runnable() {
        @Override
        public void run() {
            finish(); // 关闭当前页面
        }
    };

}
