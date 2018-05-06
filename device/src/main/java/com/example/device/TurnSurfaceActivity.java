package com.example.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.device.widget.TurnSurfaceView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class TurnSurfaceActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private TurnSurfaceView tfv_circle; // 声明一个转动表面视图
    private CheckBox ck_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_surface);
        // 从布局文件中获取名叫tfv_circle的转动表面视图
        tfv_circle = findViewById(R.id.tfv_circle);
        ck_control = findViewById(R.id.ck_control);
        ck_control.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_control) {
            if (isChecked) {
                ck_control.setText("停止");
                tfv_circle.start(); // 转动表面视图开始转动
            } else {
                ck_control.setText("转动");
                tfv_circle.stop(); // 转动表面视图停止转动
            }
        }
    }

}
