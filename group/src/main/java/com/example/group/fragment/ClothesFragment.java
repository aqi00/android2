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

public class ClothesFragment extends Fragment implements OnRefreshListener {
    private static final String TAG = "ClothesFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private SwipeRefreshLayout srl_clothes; // 声明一个下拉刷新布局对象
    private RecyclerView rv_clothes; // 声明一个循环视图对象
    private RecyclerStaggeredAdapter mAdapter; // 声明一个瀑布流适配器对象
    private ArrayList<GoodsInfo> mAllArray; // 服装信息队列

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        // 根据布局文件fragment_clothes.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_clothes, container, false);
        // 从布局文件中获取名叫srl_clothes的下拉刷新布局
        srl_clothes = mView.findViewById(R.id.srl_clothes);
        // 设置srl_clothes的下拉刷新监听器
        srl_clothes.setOnRefreshListener(this);
        // 设置srl_clothes的下拉变色资源数组
        srl_clothes.setColorSchemeResources(
                R.color.red, R.color.orange, R.color.green, R.color.blue);
        // 从布局文件中获取名叫rv_clothes的循环视图
        rv_clothes = mView.findViewById(R.id.rv_clothes);
        // 创建一个垂直方向的瀑布流布局管理器
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL);
        // 设置循环视图的布局管理器
        rv_clothes.setLayoutManager(manager);
        // 获取默认的服装信息队列
        mAllArray = GoodsInfo.getDefaultStag();
        // 构建一个服装列表的瀑布流适配器
        mAdapter = new RecyclerStaggeredAdapter(mContext, mAllArray);
        // 设置瀑布流列表的点击监听器
        mAdapter.setOnItemClickListener(mAdapter);
        // 设置瀑布流列表的长按监听器
        mAdapter.setOnItemLongClickListener(mAdapter);
        // 给rv_clothes设置服装瀑布流适配器
        rv_clothes.setAdapter(mAdapter);
        // 设置rv_clothes的默认动画效果
        rv_clothes.setItemAnimator(new DefaultItemAnimator());
        // 给rv_clothes添加列表项之间的空白装饰
        rv_clothes.addItemDecoration(new SpacesItemDecoration(3));

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
            srl_clothes.setRefreshing(false);
            // 更新服装信息队列
            for (int i = mAllArray.size() - 1, count = 0; count < 5; count++) {
                GoodsInfo item = mAllArray.get(i);
                mAllArray.remove(i);
                mAllArray.add(0, item);
            }
            // 通知适配器的列表数据发生变化
            mAdapter.notifyDataSetChanged();
            // 让循环视图滚动到第一项所在的位置
            rv_clothes.scrollToPosition(0);
        }
    };

}
