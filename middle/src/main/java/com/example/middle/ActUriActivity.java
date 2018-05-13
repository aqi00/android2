package com.example.middle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * Created by ouyangshen on 2018/4/24.
 */
public class ActUriActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_phone; // 声明一个编辑框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_uri);
        findViewById(R.id.btn_call).setOnClickListener(this);
        findViewById(R.id.btn_dial).setOnClickListener(this);
        findViewById(R.id.btn_sms).setOnClickListener(this);
        // 从布局文件中获取名叫et_phone的编辑框
        et_phone = findViewById(R.id.et_phone);
    }

    @Override
    public void onClick(View v) {
        // 获取编辑框的输入文本
        String phone = et_phone.getText().toString();
        if (v.getId() == R.id.btn_call) { // 点击了直接拨号按钮
            // 拨号功能还需在AndroidManifest.xml中添加拨号权限配置
            Intent intent = new Intent(); // 创建一个新意图
            intent.setAction(Intent.ACTION_CALL); // 设置意图动作为直接拨号
            Uri uri = Uri.parse("tel:" + phone); // 声明一个拨号的Uri
            intent.setData(uri); // 设置意图前往的路径
            startActivity(intent); // 启动意图通往的活动页面
        } else if (v.getId() == R.id.btn_dial) { // 点击了准备拨号按钮
            // 拨号功能还需在AndroidManifest.xml中添加拨号权限配置
            Intent intent = new Intent(); // 创建一个新意图
            intent.setAction(Intent.ACTION_DIAL); // 设置意图动作为准备拨号
            Uri uri = Uri.parse("tel:" + phone); // 声明一个拨号的Uri
            intent.setData(uri); // 设置意图前往的路径
            startActivity(intent); // 启动意图通往的活动页面
        } else if (v.getId() == R.id.btn_sms) { // 点击了发送短信按钮
            // 发送短信还需在AndroidManifest.xml中添加发短信的权限配置
            Intent intent = new Intent(); // 创建一个新意图
            intent.setAction(Intent.ACTION_SENDTO); // 设置意图动作为发短信
            Uri uri = Uri.parse("smsto:" + phone); // 声明一个发送短信的Uri
            intent.setData(uri); // 设置意图前往的路径
            startActivity(intent); // 启动意图通往的活动页面
        }
    }

}
