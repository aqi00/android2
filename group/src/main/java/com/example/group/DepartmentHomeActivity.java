package com.example.group;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.example.group.adapter.RecyclerCombineAdapter;
import com.example.group.adapter.RecyclerGridAdapter;
import com.example.group.bean.GoodsInfo;
import com.example.group.constant.ImageList;
import com.example.group.util.DateUtil;
import com.example.group.util.MenuUtil;
import com.example.group.util.Utils;
import com.example.group.widget.BannerPager;
import com.example.group.widget.SpacesItemDecoration;
import com.example.group.widget.BannerPager.BannerClickListener;

/**
 * Created by ouyangshen on 2017/10/21.
 */
@SuppressLint("DefaultLocale")
public class DepartmentHomeActivity extends AppCompatActivity implements BannerClickListener {
    private final static String TAG = "DepartmentHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_home);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置工具栏的标题文字
        tl_head.setTitle("商城首页");
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);
        initBanner(); // 初始化广告轮播条
        initGrid(); // 初始化市场网格列表
        initCombine(); // 初始化猜你喜欢的商品展示网格
    }

    private void initBanner() {
        // 从布局文件中获取名叫banner_pager的横幅轮播条
        BannerPager banner = findViewById(R.id.banner_pager);
        // 获取横幅轮播条的布局参数
        LayoutParams params = (LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        // 设置横幅轮播条的布局参数
        banner.setLayoutParams(params);
        // 设置横幅轮播条的广告图片队列
        banner.setImage(ImageList.getDefault());
        // 设置横幅轮播条的广告点击监听器
        banner.setOnBannerListener(this);
        // 开始广告图片的轮播滚动
        banner.start();
    }

    // 一旦点击了广告图，就回调监听器的onBannerClick方法
    public void onBannerClick(int position) {
        String desc = String.format("您点击了第%d张图片", position + 1);
        Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
    }

    private void initGrid() {
        // 从布局文件中获取名叫rv_grid的循环视图
        RecyclerView rv_grid = findViewById(R.id.rv_grid);
        // 创建一个垂直方向的网格布局管理器
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        // 设置循环视图的布局管理器
        rv_grid.setLayoutManager(manager);
        // 构建一个市场列表的网格适配器
        RecyclerGridAdapter adapter = new RecyclerGridAdapter(this, GoodsInfo.getDefaultGrid());
        // 设置网格列表的点击监听器
        adapter.setOnItemClickListener(adapter);
        // 设置网格列表的长按监听器
        adapter.setOnItemLongClickListener(adapter);
        // 给rv_grid设置市场网格适配器
        rv_grid.setAdapter(adapter);
        // 设置rv_grid的默认动画效果
        rv_grid.setItemAnimator(new DefaultItemAnimator());
        // 给rv_grid添加列表项之间的空白装饰
        rv_grid.addItemDecoration(new SpacesItemDecoration(1));
    }

    private void initCombine() {
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
        RecyclerCombineAdapter adapter = new RecyclerCombineAdapter(this, GoodsInfo.getDefaultCombine());
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

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        // 显示菜单项左侧的图标
        MenuUtil.setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从menu_home.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { // 点击了工具栏左边的返回箭头
            finish();
        } else if (id == R.id.menu_search) { // 点击了搜索图标
            // 跳转到搜索页面
            Intent intent = new Intent(this, SearchViewActivity.class);
            intent.putExtra("collapse", false);
            startActivity(intent);
        } else if (id == R.id.menu_refresh) { // 点击了刷新图标
            Toast.makeText(this, "当前刷新时间: " +
                    DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss"), Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.menu_about) { // 点击了关于菜单项
            Toast.makeText(this, "这个是商城首页", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.menu_quit) { // 点击了退出菜单项
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}