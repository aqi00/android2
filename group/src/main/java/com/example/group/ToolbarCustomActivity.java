package com.example.group;

import java.util.Calendar;

import com.example.group.util.DateUtil;
import com.example.group.util.MenuUtil;
import com.example.group.widget.CustomDateDialog;
import com.example.group.widget.CustomDateDialog.OnDateSetListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/10/21.
 */
@SuppressLint(value = {"DefaultLocale", "SetTextI18n"})
public class ToolbarCustomActivity extends AppCompatActivity implements
        OnClickListener, OnDateSetListener {
    private final static String TAG = "ToolbarCustomActivity";
    private TextView tv_day;
    private TextView tv_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_custom);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);
        tv_day = findViewById(R.id.tv_day);
        tv_desc = findViewById(R.id.tv_desc);
        tv_day.setText(DateUtil.getNowDateTime("yyyy年MM月dd日"));
        tv_day.setOnClickListener(this);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        // 显示菜单项左侧的图标
        MenuUtil.setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从menu_overflow.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { // 点击了工具栏左边的返回箭头
            finish();
        } else if (id == R.id.menu_refresh) { // 点击了刷新图标
            tv_desc.setText("当前刷新时间: " + DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss"));
            return true;
        } else if (id == R.id.menu_about) { // 点击了关于菜单项
            Toast.makeText(this, "这个是工具栏的演示demo", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.menu_quit) { // 点击了退出菜单项
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        String date = String.format("%d年%d月%d日", year, month, day);
        tv_day.setText(date);
        tv_desc.setText("您选择的日期是" + date);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_day) {
            Calendar calendar = Calendar.getInstance();
            // 弹出自定义的日期选择对话框
            CustomDateDialog dialog = new CustomDateDialog(this);
            dialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), this);
            dialog.show();
        }
    }

}
