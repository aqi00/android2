package com.example.network;

import com.example.network.thread.ClientThread;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/11.
 */
public class QQLoginActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_login);
        et_name = findViewById(R.id.et_name);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            String nickName = et_name.getText().toString().trim();
            if (TextUtils.isEmpty(nickName)) { // 未输入昵称
                Toast.makeText(this, "请输入您的昵称", Toast.LENGTH_SHORT).show();
            } else { // 已输入昵称
                // 设置当前用户昵称的全局变量
                MainApplication.getInstance().setNickName(nickName);
                // 向后端服务器发送登录消息
                MainApplication.getInstance().sendAction(ClientThread.LOGIN, "", "");
                // 跳转到聊天主页面
                Intent intent = new Intent(this, QQChatActivity.class);
                startActivity(intent);
                finish(); // 关闭当前页面
            }
        }
    }
}
