package com.example.custom;

import java.util.Calendar;

import com.example.custom.widget.CustomDateDialog;
import com.example.custom.widget.CustomDateDialog.OnDateSetListener;
import com.example.custom.widget.CustomMonthDialog;
import com.example.custom.widget.CustomMonthDialog.OnMonthSetListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/14.
 */
@SuppressLint("DefaultLocale")
public class DialogDateActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_date;
    private TextView tv_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_date);
        tv_date = findViewById(R.id.tv_date);
        findViewById(R.id.btn_date).setOnClickListener(this);
        tv_month = findViewById(R.id.tv_month);
        findViewById(R.id.btn_month).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_date) { // 点击了“选择日期”按钮
            showDateDialog(); // 显示自定义的日期对话框
        } else if (v.getId() == R.id.btn_month) { // 点击了“选择月份”按钮
            showMonthDialog(); // 显示自定义的月份对话框
        }
    }

    // 显示自定义的日期对话框
    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        // 创建一个自定义的日期对话框实例
        CustomDateDialog dialog = new CustomDateDialog(this);
        // 设置日期对话框上面的年、月、日，并指定日期变更监听器
        dialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DateListener());
        dialog.show(); // 显示日期对话框
    }

    // 定义一个日期变更监听器，一旦点击对话框的确定按钮，就触发监听器的onDateSet方法
    private class DateListener implements OnDateSetListener {
        @Override
        public void onDateSet(int year, int month, int day) {
            String desc = String.format("您选择的日期是%d年%d月%d日", year, month, day);
            tv_date.setText(desc);
        }
    }

    // 显示自定义的月份对话框
    private void showMonthDialog() {
        Calendar calendar = Calendar.getInstance();
        // 创建一个自定义的月份对话框实例
        CustomMonthDialog dialog = new CustomMonthDialog(this);
        // 设置月份对话框上面的年、月，并指定月份变更监听器
        dialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new MonthListener());
        dialog.show(); // 显示月份对话框
    }

    // 定义一个月份变更监听器，一旦点击对话框的确定按钮，就触发监听器的onMonthSet方法
    private class MonthListener implements OnMonthSetListener {
        @Override
        public void onMonthSet(int year, int month) {
            String desc = String.format("您选择的月份是%d年%d月", year, month);
            tv_month.setText(desc);
        }
    }

}
