package com.example.senior;

import com.example.senior.bean.CartInfo;
import com.example.senior.bean.GoodsInfo;
import com.example.senior.database.CartDBHelper;
import com.example.senior.database.GoodsDBHelper;
import com.example.senior.util.DateUtil;
import com.example.senior.util.SharedUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/10/7.
 */
@SuppressLint("SetTextI18n")
public class ShoppingDetailActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_title;
    private TextView tv_count;
    private TextView tv_goods_price;
    private TextView tv_goods_desc;
    private ImageView iv_goods_pic;
    private int mCount; // 购物车中的商品数量
    private long mGoodsId; // 当前商品的商品编号
    private GoodsDBHelper mGoodsHelper; // 声明一个商品数据库的帮助器对象
    private CartDBHelper mCartHelper; // 声明一个购物车数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_detail);
        tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        tv_goods_price = findViewById(R.id.tv_goods_price);
        tv_goods_desc = findViewById(R.id.tv_goods_desc);
        iv_goods_pic = findViewById(R.id.iv_goods_pic);
        findViewById(R.id.iv_cart).setOnClickListener(this);
        findViewById(R.id.btn_add_cart).setOnClickListener(this);
        // 获取共享参数保存的购物车中的商品数量
        mCount = SharedUtil.getIntance(this).readInt("count", 0);
        tv_count.setText("" + mCount);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_cart) { // 点击了购物车图标
            // 跳转到购物车页面
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_add_cart) { // 点击了“添加”按钮
            // 把该商品添加到购物车
            addToCart(mGoodsId);
            Toast.makeText(this, "成功添加至购物车", Toast.LENGTH_SHORT).show();
        }
    }

    // 把指定编号的商品添加到购物车
    private void addToCart(long goods_id) {
        mCount++;
        tv_count.setText("" + mCount);
        // 把购物车中的商品数量写入共享参数
        SharedUtil.getIntance(this).writeInt("count", mCount);
        // 根据商品编号查询购物车中的商品记录
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
        // 获取商品数据库的帮助器对象
        mGoodsHelper = GoodsDBHelper.getInstance(this, 1);
        // 打开商品数据库的读连接
        mGoodsHelper.openReadLink();
        // 获取购物车数据库的帮助器对象
        mCartHelper = CartDBHelper.getInstance(this, 1);
        // 打开购物车数据库的写连接
        mCartHelper.openWriteLink();
        // 展示商品详情
        showDetail();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭商品数据库的数据库连接
        mGoodsHelper.closeLink();
        // 关闭购物车数据库的数据库连接
        mCartHelper.closeLink();
    }

    private void showDetail() {
        // 获取前一个页面传来的商品编号
        mGoodsId = getIntent().getLongExtra("goods_id", 0L);
        if (mGoodsId > 0) {
            // 根据商品编号查询商品数据库中的商品记录
            GoodsInfo info = mGoodsHelper.queryById(mGoodsId);
            tv_title.setText(info.name);
            tv_goods_desc.setText(info.desc);
            tv_goods_price.setText("" + info.price);
            // 从指定路径读取图片文件的位图数据
            Bitmap pic = BitmapFactory.decodeFile(info.pic_path);
            iv_goods_pic.setImageBitmap(pic);
        }
    }

}
