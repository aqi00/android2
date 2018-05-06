package com.example.network;

import com.example.network.bean.UserInfo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * Created by ouyangshen on 2017/9/24.
 */
@SuppressLint(value={"DefaultLocale","SetTextI18n"})
public class JsonConvertActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_json;
    private UserInfo mUser = new UserInfo(); // 创建一个用户信息对象
    private String mJsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_convert);
        // 以下对用户信息赋初始值
        mUser.name = "阿四";
        mUser.age = 25;
        mUser.height = 165L;
        mUser.weight = 50.0f;
        mUser.married = false;
        // 把用户信息对象mUser转换为json串
        mJsonStr = new Gson().toJson(mUser);
        tv_json = findViewById(R.id.tv_json);
        findViewById(R.id.btn_origin_json).setOnClickListener(this);
        findViewById(R.id.btn_convert_json).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_origin_json) {
            // 把用户信息对象mUser转换为json串
            mJsonStr = new Gson().toJson(mUser);
            tv_json.setText("json串内容如下：\n" + mJsonStr);
        } else if (v.getId() == R.id.btn_convert_json) {
            // 把json串转换为UserInfo类型的数据对象newUser
            UserInfo newUser = new Gson().fromJson(mJsonStr, UserInfo.class);
            String desc = String.format("\n\t姓名=%s\n\t年龄=%d\n\t身高=%d\n\t体重=%f\n\t婚否=%b",
                    newUser.name, newUser.age, newUser.height, newUser.weight, newUser.married);
            tv_json.setText("从json串解析而来的用户信息如下：" + desc);
        }
    }

}
