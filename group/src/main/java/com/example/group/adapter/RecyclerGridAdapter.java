package com.example.group.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group.R;
import com.example.group.bean.GoodsInfo;
import com.example.group.widget.RecyclerExtras.OnItemClickListener;
import com.example.group.widget.RecyclerExtras.OnItemLongClickListener;

@SuppressLint("DefaultLocale")
public class RecyclerGridAdapter extends RecyclerView.Adapter<ViewHolder> implements
        OnItemClickListener, OnItemLongClickListener {
    private final static String TAG = "RecyclerGridAdapter";
    private Context mContext; // 声明一个上下文对象
    private ArrayList<GoodsInfo> mGoodsArray;

    public RecyclerGridAdapter(Context context, ArrayList<GoodsInfo> goodsArray) {
        mContext = context;
        mGoodsArray = goodsArray;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mGoodsArray.size();
    }

    // 创建列表项的视图持有者
    public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_grid.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_grid, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        holder.iv_pic.setImageResource(mGoodsArray.get(position).pic_id);
        holder.tv_title.setText(mGoodsArray.get(position).title);
        // 列表项的点击事件需要自己实现
        holder.ll_item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
        // 列表项的长按事件需要自己实现
        holder.ll_item.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(v, position);
                }
                return true;
            }
        });
    }

    // 获取列表项的类型
    public int getItemViewType(int position) {
        return 0;
    }

    // 获取列表项的编号
    public long getItemId(int position) {
        return position;
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_item; // 声明列表项的线性布局
        public ImageView iv_pic; // 声明列表项图标的图像视图
        public TextView tv_title; // 声明列表项标题的文本视图

        public ItemHolder(View v) {
            super(v);
            ll_item = v.findViewById(R.id.ll_item);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_title = v.findViewById(R.id.tv_title);
        }
    }

    // 声明列表项的点击监听器对象
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // 声明列表项的长按监听器对象
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    // 处理列表项的点击事件
    public void onItemClick(View view, int position) {
        String desc = String.format("您点击了第%d项，栏目名称是%s", position + 1,
                mGoodsArray.get(position).title);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    // 处理列表项的长按事件
    public void onItemLongClick(View view, int position) {
        String desc = String.format("您长按了第%d项，栏目名称是%s", position + 1,
                mGoodsArray.get(position).title);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

}
