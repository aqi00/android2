package com.example.group;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.group.adapter.LinearDynamicAdapter;
import com.example.group.bean.GoodsInfo;
import com.example.group.widget.SpacesItemDecoration;
import com.example.group.widget.RecyclerExtras.OnItemClickListener;
import com.example.group.widget.RecyclerExtras.OnItemDeleteClickListener;
import com.example.group.widget.RecyclerExtras.OnItemLongClickListener;

/**
 * Created by ouyangshen on 2017/10/21.
 */
@SuppressLint("DefaultLocale")
public class SwipeRecyclerActivity extends AppCompatActivity implements OnRefreshListener,
        OnItemClickListener, OnItemLongClickListener, OnItemDeleteClickListener {
    private SwipeRefreshLayout srl_dynamic; // 声明一个下拉刷新布局对象
    private RecyclerView rv_dynamic; // 声明一个循环视图对象
    private LinearDynamicAdapter mAdapter; // 声明一个线性适配器对象
    private ArrayList<GoodsInfo> mPublicArray; // 当前公众号信息队列
    private ArrayList<GoodsInfo> mAllArray; // 所有公众号信息队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_recycler);
        // 从布局文件中获取名叫srl_dynamic的下拉刷新布局
        srl_dynamic = findViewById(R.id.srl_dynamic);
        // 给srl_dynamic设置下拉刷新监听器
        srl_dynamic.setOnRefreshListener(this);
        // 设置下拉刷新布局的进度圆圈颜色
        srl_dynamic.setColorSchemeResources(
                R.color.red, R.color.orange, R.color.green, R.color.blue);
        initRecyclerDynamic(); // 初始化动态线性布局的循环视图
    }

    // 初始化动态线性布局的循环视图
    private void initRecyclerDynamic() {
        // 从布局文件中获取名叫rv_dynamic的循环视图
        rv_dynamic = findViewById(R.id.rv_dynamic);
        // 创建一个垂直方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        // 设置循环视图的布局管理器
        rv_dynamic.setLayoutManager(manager);
        // 获取默认的所有公众号信息队列
        mAllArray = GoodsInfo.getDefaultList();
        // 获取默认的当前公众号信息队列
        mPublicArray = GoodsInfo.getDefaultList();
        // 构建一个公众号列表的线性适配器
        mAdapter = new LinearDynamicAdapter(this, mPublicArray);
        // 设置线性列表的点击监听器
        mAdapter.setOnItemClickListener(this);
        // 设置线性列表的长按监听器
        mAdapter.setOnItemLongClickListener(this);
        // 设置线性列表的删除按钮监听器
        mAdapter.setOnItemDeleteClickListener(this);
        // 给rv_dynamic设置公众号线性适配器
        rv_dynamic.setAdapter(mAdapter);
        // 设置rv_dynamic的默认动画效果
        rv_dynamic.setItemAnimator(new DefaultItemAnimator());
        // 给rv_dynamic添加列表项之间的空白装饰
        rv_dynamic.addItemDecoration(new SpacesItemDecoration(1));
    }

    // 一旦在下拉刷新布局内部往下拉动页面，就触发下拉监听器的onRefresh方法
    public void onRefresh() {
        // 延迟若干秒后启动刷新任务
        mHandler.postDelayed(mRefresh, 2000);
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 结束下拉刷新布局的刷新动作
            srl_dynamic.setRefreshing(false);
            int position = (int) (Math.random() * 100 % mAllArray.size());
            GoodsInfo old_item = mAllArray.get(position);
            GoodsInfo new_item = new GoodsInfo(old_item.pic_id,
                    old_item.title, old_item.desc);
            mPublicArray.add(0, new_item);
            // 通知适配器列表在第一项插入数据
            mAdapter.notifyItemInserted(0);
            // 让循环视图滚动到第一项所在的位置
            rv_dynamic.scrollToPosition(0);
            // 当循环视图的列表项已经占满整个屏幕时，再往顶部添加一条新记录，
            // 感觉屏幕没有发生变化，也没看到插入动画。
            // 此时就要调用scrollToPosition(0)方法，表示滚动到第一条记录。
        }
    };

    // 一旦点击循环适配器的列表项，就触发点击监听器的onItemClick方法
    public void onItemClick(View view, int position) {
        String desc = String.format("您点击了第%d项，标题是%s", position + 1,
                mPublicArray.get(position).title);
        Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
    }

    // 一旦长按循环适配器的列表项，就触发长按监听器的onItemLongClick方法
    public void onItemLongClick(View view, int position) {
        GoodsInfo item = mPublicArray.get(position);
        item.bPressed = !item.bPressed;
        mPublicArray.set(position, item);
        // 通知适配器列表在第几项发生变更
        mAdapter.notifyItemChanged(position);
    }

    // 一旦点击循环适配器列表项的删除按钮，就触发删除监听器的onItemDeleteClick方法
    public void onItemDeleteClick(View view, int position) {
        mPublicArray.remove(position);
        // 通知适配器列表在第几项删除数据
        mAdapter.notifyItemRemoved(position);
    }

}
