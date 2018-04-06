package com.example.group.adapter;

import com.example.group.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecyclerCollapseAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final static String TAG = "RecyclerCollapseAdapter";
    private Context mContext; // 声明一个上下文对象
    private String[] mTitleArray; // 标题文字数组

    public RecyclerCollapseAdapter(Context context, String[] titleArray) {
        mContext = context;
        mTitleArray = titleArray;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mTitleArray.length;
    }

    // 创建列表项的视图持有者
    public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_collapse.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_collapse, vg, false);
        return new TitleHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(ViewHolder vh, final int position) {
        TitleHolder holder = (TitleHolder) vh;
        holder.tv_seq.setText("" + (position + 1));
        holder.tv_title.setText(mTitleArray[position]);
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
    public class TitleHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_item; // 声明列表项的线性布局
        public TextView tv_seq; // 声明列表项序号的文本视图
        public TextView tv_title; // 声明列表项标题的文本视图

        public TitleHolder(View v) {
            super(v);
            ll_item = v.findViewById(R.id.ll_item);
            tv_seq = v.findViewById(R.id.tv_seq);
            tv_title = v.findViewById(R.id.tv_title);
        }
    }

}
