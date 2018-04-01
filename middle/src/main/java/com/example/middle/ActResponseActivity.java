package com.example.middle;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.example.middle.util.DateUtil;

/**
 * Created by ouyangshen on 2017/9/24.
 */
public class ActResponseActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_response; // 声明一个编辑框对象
    private TextView tv_response; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_response);
        findViewById(R.id.btn_act_response).setOnClickListener(this);
        // 从布局文件中获取名叫et_response的编辑框
        et_response = findViewById(R.id.et_response);
        // 从布局文件中获取名叫tv_response的文本视图
        tv_response = findViewById(R.id.tv_response);
        // 从前一个页面传来的意图中获取快递包裹
        Bundle bundle = getIntent().getExtras();
        // 从包裹中取出名叫request_time的字符串
        String request_time = bundle.getString("request_time");
        // 从包裹中取出名叫request_content的字符串
        String request_content = bundle.getString("request_content");
        String desc = String.format("收到请求消息：\n请求时间为%s\n请求内容为%s",
                request_time, request_content);
        // 把请求消息的详情显示在文本视图上
        tv_response.setText(desc);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_act_response) {
            Intent intent = new Intent(); // 创建一个新意图
            Bundle bundle = new Bundle(); // 创建一个新包裹
            // 往包裹存入名叫response_time的字符串
            bundle.putString("response_time", DateUtil.getNowTime());
            // 往包裹存入名叫response_content的字符串
            bundle.putString("response_content", et_response.getText().toString());
            intent.putExtras(bundle); // 把快递包裹塞给意图
            setResult(Activity.RESULT_OK, intent); // 携带意图返回前一个页面
            finish(); // 关闭当前页面
        }
    }
}
