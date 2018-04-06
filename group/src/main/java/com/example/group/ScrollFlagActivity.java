package com.example.group;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout.LayoutParams;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.group.adapter.RecyclerCollapseAdapter;

/**
 * Created by ouyangshen on 2017/9/3.
 */
public class ScrollFlagActivity extends AppCompatActivity {
    private CollapsingToolbarLayout ctl_title; // 声明一个可折叠布局对象
    private String[] yearArray = {"鼠年", "牛年", "虎年", "兔年", "龙年", "蛇年",
            "马年", "羊年", "猴年", "鸡年", "狗年", "猪年"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_flag);
        // 从布局文件中获取名叫tl_title的工具栏
        Toolbar tl_title = findViewById(R.id.tl_title);
        // 设置工具栏的背景
        tl_title.setBackgroundColor(Color.YELLOW);
        // 使用tl_title替换系统自带的ActionBar
        setSupportActionBar(tl_title);
        // 从布局文件中获取名叫ctl_title的可折叠布局
        ctl_title = findViewById(R.id.ctl_title);
        // 设置可折叠布局的标题文字
        ctl_title.setTitle("滚动标志");
        initFlagSpinner();
        // 从布局文件中获取名叫rv_main的循环视图
        RecyclerView rv_main = findViewById(R.id.rv_main);
        // 创建一个垂直方向的线性布局管理器
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        // 设置循环视图的布局管理器
        rv_main.setLayoutManager(llm);
        // 构建一个十二生肖的线性适配器
        RecyclerCollapseAdapter adapter = new RecyclerCollapseAdapter(this, yearArray);
        // 给rv_main设置十二生肖线性适配器
        rv_main.setAdapter(adapter);
    }

    // 定义一个滚动标志说明的字符串数组
    private String[] descArray = {
            "scroll",
            "scroll|enterAlways",
            "scroll|exitUntilCollapsed",
            "scroll|enterAlways|enterAlwaysCollapsed",
            "scroll|snap"};
    // 定义一个滚动标志位的整型数组
    private int[] flagArray = {
            LayoutParams.SCROLL_FLAG_SCROLL,
            LayoutParams.SCROLL_FLAG_SCROLL | LayoutParams.SCROLL_FLAG_ENTER_ALWAYS,
            LayoutParams.SCROLL_FLAG_SCROLL | LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED,
            LayoutParams.SCROLL_FLAG_SCROLL | LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED,
            LayoutParams.SCROLL_FLAG_SCROLL | LayoutParams.SCROLL_FLAG_SNAP};
    // 初始化滚动标志的下拉框
    private void initFlagSpinner() {
        ArrayAdapter<String> flagAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, descArray);
        flagAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp_style = findViewById(R.id.sp_flag);
        sp_style.setPrompt("请选择滚动标志");
        sp_style.setAdapter(flagAdapter);
        // 设置下拉框列表的选择监听器
        sp_style.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // 获取可折叠布局的布局参数
                LayoutParams params = (LayoutParams) ctl_title.getLayoutParams();
                // 设置布局参数中的滚动标志位
                params.setScrollFlags(flagArray[arg2]);
                // 设置可折叠布局的布局参数。注意：第三种滚动标志一定要调用setLayoutParams
                ctl_title.setLayoutParams(params);
            }

            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        sp_style.setSelection(0);
    }

}
