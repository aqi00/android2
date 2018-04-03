package com.example.custom;

import java.util.ArrayList;
import java.util.List;

import com.example.custom.bean.Friend;
import com.example.custom.widget.DialogFriend;
import com.example.custom.widget.DialogFriend.onAddFriendListener;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class DialogMultiActivity extends AppCompatActivity implements
        OnClickListener, onAddFriendListener {
    private TextView tv_result;
    private String[] phoneArray = {"15960238696", "15805910591", "18905710571"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_multi);
        TextView tv_origin = findViewById(R.id.tv_origin);
        tv_result = findViewById(R.id.tv_result);
        findViewById(R.id.btn_friend).setOnClickListener(this);
        String origin = "";
        for (String phone : phoneArray) {
            origin = String.format("%s  %s", origin, phone);
        }
        tv_origin.setText(origin.trim());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_friend) {
            ArrayList<Friend> friendList = new ArrayList<Friend>();
            for (String phone : phoneArray) {
                friendList.add(new Friend(phone));
            }
            // 在页面底部弹出一个添加好友对话框，其中第三个参数为确认监听器
            DialogFriend dialog = new DialogFriend(this, friendList, this);
            dialog.show();
        }
    }

    // 一旦用户点击对话框上的确定按钮，就触发监听器的addFriend方法
    public void addFriend(List<Friend> friendList) {
        String result = "添加的好友信息如下：";
        // 循环取出好友的关系信息，并拼接成描述文字
        for (Friend item : friendList) {
            result = String.format("%s\n号码%s，关系是%s，%s访问朋友圈", result,
                    item.phone, item.relation, (item.admit_circle) ? "允许" : "禁止");
        }
        tv_result.setText(result);
    }

}
