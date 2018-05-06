package com.example.device;

import com.example.device.widget.TurnTextureView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class TurnTextureActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private TurnTextureView ttv_circle; // 声明一个转动纹理视图对象
    private CheckBox ck_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_texture);
        // 从布局文件中获取名叫ttv_circle的转动纹理视图
        ttv_circle = findViewById(R.id.ttv_circle);
        // 给ttv_circle设置表面纹理监听器
        ttv_circle.setSurfaceTextureListener(ttv_circle);
        ck_control = findViewById(R.id.ck_control);
        ck_control.setOnCheckedChangeListener(this);
        initAlphaSpinner();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_control) {
            if (isChecked) {
                ck_control.setText("停止");
                ttv_circle.start(); // 转动纹理视图开始转动
            } else {
                ck_control.setText("转动");
                ttv_circle.stop(); // 转动纹理视图停止转动
            }
        }
    }

    // 初始化透明度下拉框
    private void initAlphaSpinner() {
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, alphaArray);
        starAdapter.setDropDownViewResource(R.layout.item_select);
        Spinner sp_alpha = findViewById(R.id.sp_alpha);
        sp_alpha.setPrompt("请选择透明度");
        sp_alpha.setAdapter(starAdapter);
        sp_alpha.setSelection(1);
        sp_alpha.setOnItemSelectedListener(new MySelectedListener());
    }

    private String[] alphaArray = {"0.0", "0.2", "0.4", "0.6", "0.8", "1.0"};
    class MySelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // 设置转动纹理视图的透明度
            ttv_circle.setAlpha(Float.parseFloat(alphaArray[arg2]));
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
