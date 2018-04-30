package com.example.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.group.adapter.RecyclerCombineAdapter;
import com.example.group.bean.GoodsInfo;
import com.example.group.widget.SpacesItemDecoration;

/**
 * Created by ouyangshen on 2017/10/21.
 */
public class RecyclerCombineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_combine);
        initRecyclerCombine(); // 初始化合并网格布局的循环视图
    }

    // 初始化合并网格布局的循环视图
    private void initRecyclerCombine() {
        // 从布局文件中获取名叫rv_combine的循环视图
        RecyclerView rv_combine = findViewById(R.id.rv_combine);
        // 创建一个四列的网格布局管理器
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        // 设置网格布局管理器的占位规则
        // 以下占位规则的意思是：第一项和第二项占两列，其它项占一列；
        // 如果网格的列数为四，那么第一项和第二项平分第一行，第二行开始每行有四项。
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == 1) { // 为第一项或者第二项
                    return 2; // 占据两列
                } else { // 为其它项
                    return 1; // 占据一列
                }
            }
        });
        // 设置循环视图的布局管理器
        rv_combine.setLayoutManager(manager);
        // 构建一个猜你喜欢的网格适配器
        RecyclerCombineAdapter adapter = new RecyclerCombineAdapter(
                this, GoodsInfo.getDefaultCombine());
        // 设置网格列表的点击监听器
        adapter.setOnItemClickListener(adapter);
        // 设置网格列表的长按监听器
        adapter.setOnItemLongClickListener(adapter);
        // 给rv_combine设置猜你喜欢网格适配器
        rv_combine.setAdapter(adapter);
        // 设置rv_combine的默认动画效果
        rv_combine.setItemAnimator(new DefaultItemAnimator());
        // 给rv_combine添加列表项之间的空白装饰
        rv_combine.addItemDecoration(new SpacesItemDecoration(1));
    }

}
