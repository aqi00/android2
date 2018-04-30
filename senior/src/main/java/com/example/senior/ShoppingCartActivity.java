package com.example.senior;

import java.util.ArrayList;

import com.example.senior.adapter.CartAdapter;
import com.example.senior.bean.CartInfo;
import com.example.senior.bean.GoodsInfo;
import com.example.senior.database.CartDBHelper;
import com.example.senior.database.GoodsDBHelper;
import com.example.senior.util.FileUtil;
import com.example.senior.util.SharedUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Created by ouyangshen on 2017/10/7.
 */
@SuppressLint("SetTextI18n")
public class ShoppingCartActivity extends Activity implements
        OnClickListener, OnItemClickListener, OnItemLongClickListener {
    private final static String TAG = "ShoppingCartActivity";
    private ImageView iv_menu;
    private TextView tv_count;
    private TextView tv_total_price;
    private LinearLayout ll_content;
    private LinearLayout ll_empty;
    private ListView lv_cart; // 声明一个列表视图对象
    private int mCount; // 购物车中的商品数量
    private GoodsDBHelper mGoodsHelper; // 声明一个商品数据库的帮助器对象
    private CartDBHelper mCartHelper; // 声明一个购物车数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shopping_cart);
        iv_menu = findViewById(R.id.iv_menu);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        tv_total_price = findViewById(R.id.tv_total_price);
        ll_content = findViewById(R.id.ll_content);
        ll_empty = findViewById(R.id.ll_empty);
        // 从布局视图中获取名叫lv_cart的列表视图
        lv_cart = findViewById(R.id.lv_cart);
        iv_menu.setOnClickListener(this);
        findViewById(R.id.btn_shopping_channel).setOnClickListener(this);
        findViewById(R.id.btn_settle).setOnClickListener(this);
        iv_menu.setVisibility(View.VISIBLE);
        tv_title.setText("购物车");
    }

    // 显示购物车图标中的商品数量
    private void showCount(int count) {
        mCount = count;
        tv_count.setText("" + mCount);
        if (mCount == 0) {
            ll_content.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        } else {
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_menu) { // 点击了菜单图标
            openOptionsMenu();
        } else if (v.getId() == R.id.btn_shopping_channel) { // 点击了“商场”按钮
            // 跳转到手机商场页面
            Intent intent = new Intent(this, ShoppingChannelActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_settle) { // 点击了“结算”按钮
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("结算商品");
            builder.setMessage("客官抱歉，支付功能尚未开通，请下次再来");
            builder.setPositiveButton("我知道了", null);
            builder.create().show();
        }
    }

    private CartInfo mCurrentGood;  // 声明当前的商品对象
    private View mCurrentView;  // 声明一个当前视图的对象

    // 商品项的点击事件
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCurrentGood = mCartArray.get(position);
        goDetail(mCurrentGood.goods_id);
    }

    // 商品项的长按事件
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mCurrentGood = mCartArray.get(position);
        // 保存当前长按的列表项视图
        mCurrentView = view;
        // 延迟100毫秒后执行任务mPopupMenu，留出时间让长按事件走完流程
        mHandler.postDelayed(mPopupMenu, 100);
        return true;
    }

    private Handler mHandler = new Handler();  // 声明一个处理器对象
    // 定义一个上下文菜单的弹出任务
    private Runnable mPopupMenu = new Runnable() {
        @Override
        public void run() {
            // 取消lv_cart的长按监听器
            lv_cart.setOnItemLongClickListener(null);
            // 注册列表项视图的上下文菜单
            registerForContextMenu(mCurrentView);
            // 为该列表项视图弹出上下文菜单
            openContextMenu(mCurrentView);
            // 注销列表项视图的上下文菜单
            unregisterForContextMenu(mCurrentView);
            // 重新设置lv_cart的长按监听器
            lv_cart.setOnItemLongClickListener(ShoppingCartActivity.this);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从menu_cart.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_shopping) { // 点击了菜单项“去商场购物”
            // 跳转到商场页面
            Intent intent = new Intent(this, ShoppingChannelActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_clear) { // 点击了菜单项“清空购物车”
            // 清空购物车数据库
            mCartHelper.deleteAll();
            // 把最新的商品数量写入共享参数
            SharedUtil.getIntance(this).writeInt("count", 0);
            // 显示最新的商品数量
            showCount(0);
            ll_content.setVisibility(View.GONE);
            Toast.makeText(this, "购物车已清空", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_return) { // 点击了菜单项“返回”
            finish();
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // 从menu_goods.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_goods, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_detail) { // 点击了菜单项“查看商品详情”
            // 跳转到查看商品详情页面
            goDetail(mCurrentGood.goods_id);
        } else if (id == R.id.menu_delete) { // 点击了菜单项“从购物车删除”
            // 从购物车删除商品的数据库操作
            mCartHelper.delete("goods_id=" + mCurrentGood.goods_id);
            // 更新购物车中的商品数量
            int left_count = mCount - mCurrentGood.count;
            // 把最新的商品数量写入共享参数
            SharedUtil.getIntance(this).writeInt("count", left_count);
            // 显示最新的商品数量
            showCount(left_count);
            Toast.makeText(this, "已从购物车删除" + mCurrentGood.goods.name, Toast.LENGTH_SHORT).show();
            // 刷新购物车列表
            showCart();
        }
        return true;
    }

    // 跳转到商品详情页面
    private void goDetail(long rowid) {
        Intent intent = new Intent(this, ShoppingDetailActivity.class);
        intent.putExtra("goods_id", rowid);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取共享参数保存的购物车中的商品数量
        mCount = SharedUtil.getIntance(this).readInt("count", 0);
        showCount(mCount);
        // 获取商品数据库的帮助器对象
        mGoodsHelper = GoodsDBHelper.getInstance(this, 1);
        // 打开商品数据库的写连接
        mGoodsHelper.openWriteLink();
        // 获取购物车数据库的帮助器对象
        mCartHelper = CartDBHelper.getInstance(this, 1);
        // 打开购物车数据库的写连接
        mCartHelper.openWriteLink();
        // 获取共享参数保存的是否首次打开参数
        mFirst = SharedUtil.getIntance(this).readString("first", "true");
        // 模拟从网络下载商品图片
        downloadGoods(this, mFirst, mGoodsHelper);
        // 把是否首次打开写入共享参数
        SharedUtil.getIntance(this).writeString("first", "false");
        // 展示购物车中的商品列表
        showCart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭商品数据库的数据库连接
        mGoodsHelper.closeLink();
        // 关闭购物车数据库的数据库连接
        mCartHelper.closeLink();
    }

    // 声明一个购物车中的商品信息队列
    private ArrayList<CartInfo> mCartArray = new ArrayList<CartInfo>();

    // 展示购物车中的商品列表
    private void showCart() {
        // 查询购物车数据库中所有的商品记录
        mCartArray = mCartHelper.query("1=1");
        if (mCartArray == null || mCartArray.size() <= 0) {
            return;
        }
        for (int i = 0; i < mCartArray.size(); i++) {
            CartInfo info = mCartArray.get(i);
            // 根据商品编号查询商品数据库中的商品记录
            GoodsInfo goods = mGoodsHelper.queryById(info.goods_id);
            info.goods = goods;
            // 补充商品记录的商品详情
            mCartArray.set(i, info);
        }
        // 构建购物车商品列表的适配器对象
        CartAdapter adapter = new CartAdapter(this, mCartArray);
        // 给lv_cart设置商品列表适配器
        lv_cart.setAdapter(adapter);
        // 给lv_cart设置列表项点击监听器
        lv_cart.setOnItemClickListener(this);
        // 给lv_cart设置列表项长按监听器
        lv_cart.setOnItemLongClickListener(this);
        // 重新计算购物车中的商品总金额
        refreshTotalPrice();
    }

    // 重新计算购物车中的商品总金额
    private void refreshTotalPrice() {
        int total_price = 0;
        for (CartInfo info : mCartArray) {
            total_price += info.goods.price * info.count;
        }
        tv_total_price.setText("" + total_price);
    }

    private String mFirst = "true"; // 是否首次打开
    //模拟网络数据，初始化数据库中的商品信息
    public static void downloadGoods(Context ctx, String isFirst, GoodsDBHelper helper) {
        // 获取当前App的私有存储路径
        String path = MainApplication.getInstance().getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        if (isFirst.equals("true")) { // 如果是首次打开
            ArrayList<GoodsInfo> goodsList = GoodsInfo.getDefaultList();
            for (int i = 0; i < goodsList.size(); i++) {
                GoodsInfo info = goodsList.get(i);
                // 往商品数据库插入一条该商品的记录
                long rowid = helper.insert(info);
                info.rowid = rowid;
                // 往全局内存写入商品小图
                Bitmap thumb = BitmapFactory.decodeResource(ctx.getResources(), info.thumb);
                MainApplication.getInstance().mIconMap.put(rowid, thumb);
                String thumb_path = path + rowid + "_s.jpg";
                FileUtil.saveImage(thumb_path, thumb);
                info.thumb_path = thumb_path;
                // 往SD卡保存商品大图
                Bitmap pic = BitmapFactory.decodeResource(ctx.getResources(), info.pic);
                String pic_path = path + rowid + ".jpg";
                FileUtil.saveImage(pic_path, pic);
                pic.recycle();
                info.pic_path = pic_path;
                // 更新商品数据库中该商品记录的图片路径
                helper.update(info);
            }
        } else { // 不是首次打开
            // 查询商品数据库中所有商品记录
            ArrayList<GoodsInfo> goodsArray = helper.query("1=1");
            for (int i = 0; i < goodsArray.size(); i++) {
                GoodsInfo info = goodsArray.get(i);
                Log.d(TAG, "rowid=" + info.rowid + ", thumb_path=" + info.thumb_path);
                // 从指定路径读取图片文件的位图数据
                Bitmap thumb = BitmapFactory.decodeFile(info.thumb_path);
                // 把该位图对象保存到应用实例的全局变量中
                MainApplication.getInstance().mIconMap.put(info.rowid, thumb);
            }
        }
    }

}
