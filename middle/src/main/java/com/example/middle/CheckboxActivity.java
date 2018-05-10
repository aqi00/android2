package com.example.middle;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/9/24.
 */
@SuppressLint("DefaultLocale")
public class CheckboxActivity extends AppCompatActivity implements OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox);
        // 从布局文件中获取名叫ck_system的复选框
        CheckBox ck_system = findViewById(R.id.ck_system);
        // 从布局文件中获取名叫ck_custom的复选框
        CheckBox ck_custom = findViewById(R.id.ck_custom);
        // 给ck_system设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        ck_system.setOnCheckedChangeListener(this);
        // 给ck_custom设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        ck_custom.setOnCheckedChangeListener(this);
        // 给ck_system设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        //ck_system.setOnCheckedChangeListener(new CheckListener());
        // 给ck_custom设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        //ck_custom.setOnCheckedChangeListener(new CheckListener());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String desc = String.format("您%s了这个CheckBox", isChecked ? "勾选" : "取消勾选");
        buttonView.setText(desc);
    }

    // 定义一个勾选监听器，它实现了接口CompoundButton.OnCheckedChangeListener
    private class CheckListener implements CompoundButton.OnCheckedChangeListener{
        // 在用户点击复选框时触发
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String desc = String.format("您勾选了控件%d，状态为%b", buttonView.getId(), isChecked);
            Toast.makeText(CheckboxActivity.this, desc, Toast.LENGTH_LONG).show();
        }
    }
}
