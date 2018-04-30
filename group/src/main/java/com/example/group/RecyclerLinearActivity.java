package com.example.group;

import com.example.group.adapter.RecyclerLinearAdapter;
import com.example.group.bean.GoodsInfo;
import com.example.group.widget.SpacesItemDecoration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

/**
 * Created by ouyangshen on 2017/10/21.
 */
public class RecyclerLinearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_linear);
        initRecyclerLinear(); // 初始化线性布局的循环视图
    }

    // 初始化线性布局的循环视图
    private void initRecyclerLinear() {
        // 从布局文件中获取名叫rv_linear的循环视图
        RecyclerView rv_linear = findViewById(R.id.rv_linear);
        // 创建一个垂直方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        // 设置循环视图的布局管理器
        rv_linear.setLayoutManager(manager);
        // 构建一个公众号列表的线性适配器
        RecyclerLinearAdapter adapter = new RecyclerLinearAdapter(this, GoodsInfo.getDefaultList());
        // 设置线性列表的点击监听器
        adapter.setOnItemClickListener(adapter);
        // 设置线性列表的长按监听器
        adapter.setOnItemLongClickListener(adapter);
        // 给rv_linear设置公众号线性适配器
        rv_linear.setAdapter(adapter);
        // 设置rv_linear的默认动画效果
        rv_linear.setItemAnimator(new DefaultItemAnimator());
        // 给rv_linear添加列表项之间的空白装饰
        rv_linear.addItemDecoration(new SpacesItemDecoration(1));
    }

}
