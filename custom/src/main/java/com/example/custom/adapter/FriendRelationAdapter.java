package com.example.custom.adapter;

import java.util.ArrayList;
import java.util.Collections;

import com.example.custom.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FriendRelationAdapter extends BaseAdapter {
    private Context mContext; // 声明一个上下文对象
    private ArrayList<String> mContentList; // 声明一个关系名称队列
    private int mSelected; // 选中记录的序号

    // 朋友关系适配器的构造函数，传入上下文、关系队列，以及选中记录的序号
    public FriendRelationAdapter(Context context, String[] content_list, int selected) {
        mContext = context;
        mContentList = new ArrayList<String>();
        Collections.addAll(mContentList, content_list);
        mSelected = selected;
    }

    @Override
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mContentList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // 根据布局文件item_friend_relation.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friend_relation, null);
            holder.tv_friend_relation = convertView.findViewById(R.id.tv_friend_relation);
            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else {
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_friend_relation.setText(mContentList.get(position));
        // 如果当前元素正是已选中的记录，则要高亮显示
        if (position == mSelected) {
            holder.tv_friend_relation.setBackgroundResource(R.color.blue);
            holder.tv_friend_relation.setTextColor(mContext.getResources().getColor(R.color.white));
        }
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public TextView tv_friend_relation;
    }

}
