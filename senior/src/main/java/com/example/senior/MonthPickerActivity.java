package com.example.senior;

import com.example.senior.widget.MonthPicker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/7.
 */
@SuppressLint("DefaultLocale")
public class MonthPickerActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_month;
    private MonthPicker mp_month; // 声明一个月份选择器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_picker);
        tv_month = findViewById(R.id.tv_month);
        // 从布局文件中获取名叫mp_month的月份选择器
        mp_month = findViewById(R.id.mp_month);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            // 获取月份选择器mp_month设定的年月
            String desc = String.format("您选择的月份是%d年%d月",
                    mp_month.getYear(), mp_month.getMonth() + 1);
            tv_month.setText(desc);
        }
    }

}
