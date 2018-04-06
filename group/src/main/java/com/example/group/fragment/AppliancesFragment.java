package com.example.group.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.group.R;
import com.example.group.adapter.RecyclerStaggeredAdapter;
import com.example.group.bean.GoodsInfo;
import com.example.group.widget.SpacesItemDecoration;

public class AppliancesFragment extends Fragment implements OnRefreshListener {
    private static final String TAG = "AppliancesFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private SwipeRefreshLayout srl_appliances; // 声明一个下拉刷新布局对象
    private RecyclerView rv_appliances; // 声明一个循环视图对象
    private RecyclerStaggeredAdapter mAdapter; // 声明一个瀑布流适配器对象
    private ArrayList<GoodsInfo> mAllArray; // 电器信息队列

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        // 根据布局文件fragment_appliances.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_appliances, container, false);
        // 从布局文件中获取名叫srl_appliances的下拉刷新布局
        srl_appliances = mView.findViewById(R.id.srl_appliances);
        // 设置srl_appliances的下拉刷新监听器
        srl_appliances.setOnRefreshListener(this);
        // 设置srl_appliances的下拉变色资源数组
        srl_appliances.setColorSchemeResources(
                R.color.red, R.color.orange, R.color.green, R.color.blue);
        // 从布局文件中获取名叫rv_appliances的循环视图
        rv_appliances = mView.findViewById(R.id.rv_appliances);
        // 创建一个垂直方向的瀑布流布局管理器
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL);
        // 设置循环视图的布局管理器
        rv_appliances.setLayoutManager(manager);
        // 获取默认的电器信息队列
        mAllArray = GoodsInfo.getDefaultAppi();
        // 构建一个电器列表的瀑布流适配器
        mAdapter = new RecyclerStaggeredAdapter(mContext, mAllArray);
        // 设置瀑布流列表的点击监听器
        mAdapter.setOnItemClickListener(mAdapter);
        // 设置瀑布流列表的长按监听器
        mAdapter.setOnItemLongClickListener(mAdapter);
        // 给rv_appliances设置电器瀑布流适配器
        rv_appliances.setAdapter(mAdapter);
        // 设置rv_appliances的默认动画效果
        rv_appliances.setItemAnimator(new DefaultItemAnimator());
        // 给rv_appliances添加列表项之间的空白装饰
        rv_appliances.addItemDecoration(new SpacesItemDecoration(3));

        return mView;
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
            srl_appliances.setRefreshing(false);
            // 更新电器信息队列
            for (int i = mAllArray.size() - 1, count = 0; count < 5; count++) {
                GoodsInfo item = mAllArray.get(i);
                mAllArray.remove(i);
                mAllArray.add(0, item);
            }
            // 通知适配器的列表数据发生变化
            mAdapter.notifyDataSetChanged();
            // 让循环视图滚动到第一项所在的位置
            rv_appliances.scrollToPosition(0);
        }
    };

}
