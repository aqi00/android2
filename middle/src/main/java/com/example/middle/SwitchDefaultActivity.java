package com.example.middle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/9/24.
 */
public class SwitchDefaultActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private Switch sw_status; // 声明一个开关按钮对象
    private TextView tv_result; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_default);
        // 从布局文件中获取名叫sw_status的开关按钮
        sw_status = findViewById(R.id.sw_status);
        // 从布局文件中获取名叫tv_result的文本视图
        tv_result = findViewById(R.id.tv_result);
        // 给开关按钮设置选择监听器，一旦用户点击它，就触发监听器的onCheckedChanged方法
        sw_status.setOnCheckedChangeListener(this);
        refreshResult();
    }

    // 刷新Switch按钮的开关状态说明
    private void refreshResult() {
        String result = String.format("Switch按钮的状态是%s",
                (sw_status.isChecked()) ? "开" : "关");
        tv_result.setText(result);
    }

    // 选择事件的处理方法
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        refreshResult();
    }

}
