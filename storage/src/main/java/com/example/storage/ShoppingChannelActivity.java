package com.example.storage;

import java.util.ArrayList;

import com.example.storage.bean.CartInfo;
import com.example.storage.bean.GoodsInfo;
import com.example.storage.database.CartDBHelper;
import com.example.storage.database.GoodsDBHelper;
import com.example.storage.util.DateUtil;
import com.example.storage.util.SharedUtil;
import com.example.storage.util.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

/**
 * Created by ouyangshen on 2017/10/1.
 */
@SuppressLint("SetTextI18n")
public class ShoppingChannelActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_count;
    private LinearLayout ll_channel;
    private int mCount; // 购物车中的商品数量
    private GoodsDBHelper mGoodsHelper; // 声明一个商品数据库的帮助器对象
    private CartDBHelper mCartHelper; // 声明一个购物车数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        ll_channel = findViewById(R.id.ll_channel);
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
    private void addToCart(long goods_id) {
        mCount++;
        tv_count.setText("" + mCount);
        // 把购物车中的商品数量写入共享参数
        SharedUtil.getIntance(this).writeShared("count", "" + mCount);
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
        mCount = Integer.parseInt(SharedUtil.getIntance(this).readShared("count", "0"));
        tv_count.setText("" + mCount);
        // 获取商品数据库的帮助器对象
        mGoodsHelper = GoodsDBHelper.getInstance(this, 1);
        // 打开商品数据库的读连接
        mGoodsHelper.openReadLink();
        // 获取购物车数据库的帮助器对象
        mCartHelper = CartDBHelper.getInstance(this, 1);
        // 打开购物车数据库的写连接
        mCartHelper.openWriteLink();
        // 展示商品列表
        showGoods();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭商品数据库的数据库连接
        mGoodsHelper.closeLink();
        // 关闭购物车数据库的数据库连接
        mCartHelper.closeLink();
    }

    private LayoutParams mFullParams, mHalfParams;

    private void showGoods() {
        // 移除线性布局ll_channel下面的所有子视图
        ll_channel.removeAllViews();
        // mFullParams这个布局参数的宽度占了一整行
        mFullParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // mHalfParams这个布局参数的宽度与其它布局平均分
        mHalfParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        // 给mHalfParams设置四周的空白距离
        mHalfParams.setMargins(Utils.dip2px(this, 2), Utils.dip2px(this, 2), Utils.dip2px(this, 2), Utils.dip2px(this, 2));
        // 创建一行的线性布局
        LinearLayout ll_row = newLinearLayout(LinearLayout.HORIZONTAL, 0);
        // 查询商品数据库中的所有商品记录
        ArrayList<GoodsInfo> goodsArray = mGoodsHelper.query("1=1");
        int i = 0;
        for (; i < goodsArray.size(); i++) {
            final GoodsInfo info = goodsArray.get(i);
            // 创建一个商品项的垂直线性布局，从上到下依次列出商品标题、商品图片、商品价格
            LinearLayout ll_goods = newLinearLayout(LinearLayout.VERTICAL, 1);
            ll_goods.setBackgroundColor(Color.WHITE);
            // 添加商品标题
            TextView tv_name = new TextView(this);
            tv_name.setLayoutParams(mFullParams);
            tv_name.setGravity(Gravity.CENTER);
            tv_name.setText(info.name);
            tv_name.setTextColor(Color.BLACK);
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            ll_goods.addView(tv_name);
            // 添加商品小图
            ImageView iv_thumb = new ImageView(this);
            iv_thumb.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, Utils.dip2px(this, 150)));
            iv_thumb.setScaleType(ScaleType.FIT_CENTER);
            iv_thumb.setImageBitmap(MainApplication.getInstance().mIconMap.get(info.rowid));
            iv_thumb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShoppingChannelActivity.this, ShoppingDetailActivity.class);
                    intent.putExtra("goods_id", info.rowid);
                    startActivity(intent);
                }
            });
            ll_goods.addView(iv_thumb);
            // 添加商品价格
            LinearLayout ll_bottom = newLinearLayout(LinearLayout.HORIZONTAL, 0);
            TextView tv_price = new TextView(this);
            tv_price.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));
            tv_price.setGravity(Gravity.CENTER);
            tv_price.setText("" + (int) info.price);
            tv_price.setTextColor(Color.RED);
            tv_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            ll_bottom.addView(tv_price);
            // 添加购物车按钮
            Button btn_add = new Button(this);
            btn_add.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 3));
            btn_add.setGravity(Gravity.CENTER);
            btn_add.setText("加入购物车");
            btn_add.setTextColor(Color.BLACK);
            btn_add.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            btn_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCart(info.rowid);
                    Toast.makeText(ShoppingChannelActivity.this,
                            "已添加一部" + info.name + "到购物车", Toast.LENGTH_SHORT).show();
                }
            });
            ll_bottom.addView(btn_add);
            ll_goods.addView(ll_bottom);
            // 把商品项添加到该行上
            ll_row.addView(ll_goods);
            // 每行放两个商品项，所以放满两个商品后，就要重新创建下一行的线性视图
            if (i % 2 == 1) {
                ll_channel.addView(ll_row);
                ll_row = newLinearLayout(LinearLayout.HORIZONTAL, 0);
            }
        }
        // 最后一行只有一个商品项，则补上一个空白格，然后把最后一行添加到ll_channel
        if (i % 2 == 0) {
            ll_row.addView(newLinearLayout(LinearLayout.VERTICAL, 1));
            ll_channel.addView(ll_row);
        }
    }

    // 创建一个线性视图的框架
    private LinearLayout newLinearLayout(int orientation, int weight) {
        LinearLayout ll_new = new LinearLayout(this);
        ll_new.setLayoutParams((weight == 0) ? mFullParams : mHalfParams);
        ll_new.setOrientation(orientation);
        return ll_new;
    }

}
