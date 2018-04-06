package com.example.group;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/21.
 */
@SuppressLint("SetTextI18n")
public class SearchResultActvity extends AppCompatActivity {
    private static final String TAG = "SearchResultActvity";
    private TextView tv_search_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        // 从布局文件中获取名叫tl_result的工具栏
        Toolbar tl_result = findViewById(R.id.tl_result);
        // 设置工具栏的背景
        tl_result.setBackgroundResource(R.color.blue_light);
        // 设置工具栏的标志图片
        tl_result.setLogo(R.drawable.ic_app);
        // 设置工具栏的标题文字
        tl_result.setTitle("搜索结果页");
        // 设置工具栏的导航图标
        tl_result.setNavigationIcon(R.drawable.ic_back);
        // 使用tl_result替换系统自带的ActionBar
        setSupportActionBar(tl_result);
        tv_search_result = findViewById(R.id.tv_search_result);
        // 执行搜索查询操作
        doSearchQuery(getIntent());
    }

    // 解析搜索请求页面传来的搜索信息，并据此执行搜索查询操作
    private void doSearchQuery(Intent intent) {
        if (intent != null) {
            // 如果是通过ACTION_SEARCH来调用，即为搜索框来源
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                // 获取额外的搜索数据
                Bundle bundle = intent.getBundleExtra(SearchManager.APP_DATA);
                String value = bundle.getString("hi");
                // 获取实际的搜索文本
                String queryString = intent.getStringExtra(SearchManager.QUERY);
                tv_search_result.setText("您输入的搜索文字是："+queryString+", 额外信息："+value);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从menu_null.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_null, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // 点击了工具栏左边的返回箭头
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
