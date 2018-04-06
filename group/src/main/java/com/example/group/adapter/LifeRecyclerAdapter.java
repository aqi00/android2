package com.example.group.adapter;

import java.util.ArrayList;

import com.example.group.R;
import com.example.group.bean.LifeItem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LifeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext; // 声明一个上下文对象
    private ArrayList<LifeItem> mItemArray;

    public LifeRecyclerAdapter(Context context, ArrayList<LifeItem> itemArray) {
        mContext = context;
        mItemArray = itemArray;
    }

    // 创建列表项的视图持有者
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 根据布局文件item_life.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_life, parent, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        ItemHolder holder = (ItemHolder) vh;
        holder.iv_pic.setImageResource(mItemArray.get(position).pic);
        holder.tv_title.setText(mItemArray.get(position).title);
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mItemArray.size();
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
        public ImageView iv_pic; // 声明列表项图标的图像视图
        public TextView tv_title; // 声明列表项标题的文本视图

        public ItemHolder(View v) {
            super(v);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_title = v.findViewById(R.id.tv_title);
        }
    }

}
