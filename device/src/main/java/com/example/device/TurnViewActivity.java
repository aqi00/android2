package com.example.device;

import com.example.device.widget.TurnView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class TurnViewActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private TurnView tv_circle; // 声明一个转动视图对象
    private CheckBox ck_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_view);
        // 从布局文件中获取名叫tv_circle的转动视图
        tv_circle = findViewById(R.id.tv_circle);
        ck_control = findViewById(R.id.ck_control);
        ck_control.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_control) {
            if (isChecked) {
                ck_control.setText("停止");
                tv_circle.start(); // 转动视图开始转动
            } else {
                ck_control.setText("转动");
                tv_circle.stop(); // 转动视图停止转动
            }
        }
    }

}
