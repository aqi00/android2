package com.example.senior.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.senior.MainApplication;
import com.example.senior.R;
import com.example.senior.ShoppingDetailActivity;
import com.example.senior.bean.GoodsInfo;

public class GoodsAdapter extends BaseAdapter implements OnItemClickListener {
    private Context mContext; // 声明一个上下文对象
    private ArrayList<GoodsInfo> mGoodsArray; // 声明一个商品信息队列

    // 商品适配器的构造函数，传入上下文、行星队列与加入购物车监听器
    public GoodsAdapter(Context context, ArrayList<GoodsInfo> goods_list, addCartListener listener) {
        mContext = context;
        mGoodsArray = goods_list;
        mAddCartListener = listener;
    }

    // 获取列表项的个数
    public int getCount() {
        return mGoodsArray.size();
    }

    // 获取列表项的数据
    public Object getItem(int arg0) {
        return mGoodsArray.get(arg0);
    }

    // 获取列表项的编号
    public long getItemId(int arg0) {
        return arg0;
    }

    // 获取指定位置的列表项视图
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // 转换视图为空
            holder = new ViewHolder(); // 创建一个新的视图持有者
            // 根据布局文件item_goods.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods, null);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.iv_thumb = convertView.findViewById(R.id.iv_thumb);
            holder.tv_price = convertView.findViewById(R.id.tv_price);
            holder.btn_add = convertView.findViewById(R.id.btn_add);
            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsInfo info = mGoodsArray.get(position);
        holder.tv_name.setText(info.name); // 显示商品的名称
        holder.iv_thumb.setImageBitmap(MainApplication.getInstance().mIconMap.get(info.rowid)); // 显示商品的图片
        holder.tv_price.setText("" + (int) info.price); // 显示商品的价格
        holder.btn_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 触发加入购物车监听器的添加动作
                mAddCartListener.addToCart(info.rowid);
                Toast.makeText(mContext,
                        "已添加一部" + info.name + "到购物车", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public TextView tv_name; // 声明商品名称的文本视图对象
        public ImageView iv_thumb; // 声明商品图片的图像视图对象
        public TextView tv_price; // 声明商品价格的文本视图对象
        public Button btn_add; // 声明加入购物车的按钮对象
    }

    // 声明一个加入购物车的监听器对象
    private addCartListener mAddCartListener;
    // 定义一个加入购物车的监听器接口
    public interface addCartListener {
        void addToCart(long goods_id);  // 在商品加入购物车时触发
    }

    // 处理列表项的点击事件，由接口OnItemClickListener触发
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoodsInfo info = mGoodsArray.get(position);
        // 携带商品编号跳转到商品详情页面
        Intent intent = new Intent(mContext, ShoppingDetailActivity.class);
        intent.putExtra("goods_id", info.rowid);
        mContext.startActivity(intent);
    }

}
