package com.example.media;

import com.example.media.adapter.PhotoAdapter;
import com.example.media.widget.RecyclerExtras.OnItemClickListener;
import com.example.media.widget.SpacesItemDecoration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by ouyangshen on 2017/12/4.
 */
public class RecyclerViewActivity extends AppCompatActivity implements OnItemClickListener {
    private ImageView iv_photo; // 声明一个用于展示大图的图像视图
    private RecyclerView rv_photo; // 声明一个循环视图对象
    // 画廊需要的图片资源编号数组
    private int[] mImageRes = {
            R.drawable.scene1, R.drawable.scene2, R.drawable.scene3,
            R.drawable.scene4, R.drawable.scene5, R.drawable.scene6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        // 从布局文件中获取名叫iv_gallery的图像视图
        iv_photo = findViewById(R.id.iv_photo);
        // 给图像视图设置图片的资源编号
        iv_photo.setImageResource(mImageRes[0]);
        initRecyclerView(); // 初始化循环视图
    }

    // 初始化循环视图
    private void initRecyclerView() {
        // 从布局文件中获取名叫rv_photo的循环视图
        rv_photo = findViewById(R.id.rv_photo);
        // 创建一个水平方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false);
        // 设置循环视图的布局管理器
        rv_photo.setLayoutManager(manager);
        // 构建一个相片列表的线性适配器
        PhotoAdapter adapter = new PhotoAdapter(this, mImageRes);
        // 设置线性列表的点击监听器
        adapter.setOnItemClickListener(this);
        // 给rv_photo设置相片线性适配器
        rv_photo.setAdapter(adapter);
        // 设置rv_photo的默认动画效果
        rv_photo.setItemAnimator(new DefaultItemAnimator());
        // 给rv_photo添加列表项之间的空白装饰
        rv_photo.addItemDecoration(new SpacesItemDecoration(20));
    }

    @Override
    public void onItemClick(View view, int position) {
        iv_photo.setImageResource(mImageRes[position]);
        // 让循环视图滚动到指定位置
        rv_photo.scrollToPosition(position);
    }

}
