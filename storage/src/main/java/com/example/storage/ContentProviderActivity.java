package com.example.storage;

import java.util.ArrayList;

import com.example.storage.bean.UserInfo;
import com.example.storage.provider.UserInfoContent;
import com.example.storage.util.DateUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
@SuppressLint("DefaultLocale")
public class ContentProviderActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "ContentProviderActivity";
    private EditText et_name;
    private EditText et_age;
    private EditText et_height;
    private EditText et_weight;
    private TextView tv_read_user;
    private String mUserCount = "";
    private String mUserResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        tv_read_user = findViewById(R.id.tv_read_user);
        findViewById(R.id.btn_add_user).setOnClickListener(this);
        tv_read_user.setOnClickListener(this);
        showUserInfo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_user) {
            UserInfo user = new UserInfo();
            user.name = et_name.getText().toString().trim();
            user.age = Integer.parseInt(et_age.getText().toString().trim());
            user.height = Integer.parseInt(et_height.getText().toString().trim());
            user.weight = Float.parseFloat(et_weight.getText().toString().trim());
            addUser(getContentResolver(), user);
            showUserInfo();
        } else if (v.getId() == R.id.tv_read_user) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(mUserCount);
            builder.setMessage(mUserResult);
            builder.setPositiveButton("确定", null);
            builder.create().show();
        }
    }

    // 显示所有用户信息
    private void showUserInfo() {
        mUserResult = readAllUser(getContentResolver());
        String[] split = mUserResult.split("\n");
        int count = (!mUserResult.contains("\n")) ? 0 : split.length;
        mUserCount = String.format("当前共找到%d位用户信息", count);
        tv_read_user.setText(mUserCount);
    }

    // 添加一条用户记录
    private void addUser(ContentResolver resolver, UserInfo user) {
        ContentValues name = new ContentValues();
        name.put("name", user.name);
        name.put("age", user.age);
        name.put("height", user.height);
        name.put("weight", user.weight);
        name.put("married", false);
        name.put("update_time", DateUtil.getNowDateTime(""));
        // 通过内容解析器往指定Uri中添加用户信息
        resolver.insert(UserInfoContent.CONTENT_URI, name);
    }

    // 读取所有的用户记录
    private String readAllUser(ContentResolver resolver) {
        ArrayList<UserInfo> userArray = new ArrayList<UserInfo>();
        // 通过内容解析器从指定Uri中获取用户记录的游标
        Cursor cursor = resolver.query(UserInfoContent.CONTENT_URI, null, null, null, null);
        // 循环取出游标指向的每条用户记录
        while (cursor.moveToNext()) {
            UserInfo user = new UserInfo();
            user.name = cursor.getString(cursor.getColumnIndex(UserInfoContent.USER_NAME));
            user.age = cursor.getInt(cursor.getColumnIndex(UserInfoContent.USER_AGE));
            user.height = cursor.getInt(cursor.getColumnIndex(UserInfoContent.USER_HEIGHT));
            user.weight = cursor.getFloat(cursor.getColumnIndex(UserInfoContent.USER_WEIGHT));
            userArray.add(user); // 添加到用户信息队列
        }
        cursor.close(); // 关闭数据库游标

        String result = "";
        for (UserInfo user : userArray) {
            // 遍历用户信息队列，逐个拼接到结果字符串
            result = String.format("%s%s	年龄%d	身高%d	体重%f\n", result,
                    user.name, user.age, user.height, user.weight);
        }
        Log.d(TAG, "result=" + result);
        return result;
    }

}
