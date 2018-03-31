package com.example.senior;

import java.util.ArrayList;

import com.example.senior.adapter.GoodsAdapter;
import com.example.senior.adapter.GoodsAdapter.addCartListener;
import com.example.senior.bean.CartInfo;
import com.example.senior.bean.GoodsInfo;
import com.example.senior.database.CartDBHelper;
import com.example.senior.database.GoodsDBHelper;
import com.example.senior.util.DateUtil;
import com.example.senior.util.SharedUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/7.
 */
@SuppressLint("SetTextI18n")
public class ShoppingChannelActivity extends AppCompatActivity implements
        OnClickListener, addCartListener {
    private final static String TAG = "ShoppingChannelActivity";
    private TextView tv_count;
    private GridView gv_channel; // 声明一个网格视图对象
    private int mCount; // 购物车中的商品数量
    private GoodsDBHelper mGoodsHelper; // 声明一个商品数据库的帮助器对象
    private CartDBHelper mCartHelper; // 声明一个购物车数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        // 从布局视图中获取名叫gv_channel的网格视图
        gv_channel = findViewById(R.id.gv_channel);
        findViewById(R.id.iv_cart).setOnClickListener(this);
        tv_title.setText("手机商场");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_cart) { // 点击了购物车图标
            // 跳转到购物车页面
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
        }
    }

    // 把指定编号的商品添加到购物车
    public void addToCart(long goods_id) {
        mCount++;
        tv_count.setText("" + mCount);
        // 把购物车中的商品数量写入共享参数
        SharedUtil.getIntance(this).writeInt("count", mCount);
        // 根据商品编号查询购物车数据库中的商品记录
        CartInfo info = mCartHelper.queryByGoodsId(goods_id);
        if (info != null) { // 购物车已存在该商品记录
            info.count++; // 该商品的数量加一
            info.update_time = DateUtil.getNowDateTime("");
            // 更新购物车数据库中的商品记录信息
            mCartHelper.update(info);
        } else { // 购物车不存在该商品记录
            info = new CartInfo();
            info.goods_id = goods_id;
            info.count = 1;
            info.update_time = DateUtil.getNowDateTime("");
            // 往购物车数据库中添加一条新的商品记录
            mCartHelper.insert(info);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取共享参数保存的购物车中的商品数量
        mCount = SharedUtil.getIntance(this).readInt("count", 0);
        tv_count.setText("" + mCount);
        // 获取商品数据库的帮助器对象
        mGoodsHelper = GoodsDBHelper.getInstance(this, 1);
        // 打开商品数据库的读连接
        mGoodsHelper.openReadLink();
        // 获取购物车数据库的帮助器对象
        mCartHelper = CartDBHelper.getInstance(this, 1);
        // 打开购物车数据库的写连接
        mCartHelper.openWriteLink();
        showGoods(); // 展示商品列表
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭商品数据库的数据库连接
        mGoodsHelper.closeLink();
        // 关闭购物车数据库的数据库连接
        mCartHelper.closeLink();
    }

    private void showGoods() {
        // 判断全局内存中的图表映射是否为空
        if (MainApplication.getInstance().mIconMap.size() <= 0) {
            // 模拟从网络上下载图片，从而构建简单的图片缓存机制
            ShoppingCartActivity.downloadGoods(this, "false", mGoodsHelper);
        }
        // 查询商品数据库中的所有商品记录
        ArrayList<GoodsInfo> goodsArray = mGoodsHelper.query("1=1");
        // 构建商场中商品网格的适配器对象
        GoodsAdapter adapter = new GoodsAdapter(this, goodsArray, this);
        // 给gv_channel设置商品网格适配器
        gv_channel.setAdapter(adapter);
        // 给gv_channel设置网格项点击监听器
        gv_channel.setOnItemClickListener(adapter);
    }

}
