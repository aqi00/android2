package com.example.network.adapter;

import java.util.ArrayList;

import com.example.network.R;
import com.example.network.bean.MailBox;
import com.example.network.bean.MailItem;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MailExpandAdapter implements ExpandableListAdapter, OnGroupClickListener, OnChildClickListener {
    private final static String TAG = "MailExpandAdapter";
    private Context mContext; // 声明一个上下文对象
    private ArrayList<MailBox> mBoxList; // 邮箱队列

    public MailExpandAdapter(Context context, ArrayList<MailBox> box_list) {
        mContext = context;
        mBoxList = box_list;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    // 获取分组的数目
    public int getGroupCount() {
        return mBoxList.size();
    }

    // 获取某个分组的孙子项数目
    public int getChildrenCount(int groupPosition) {
        return mBoxList.get(groupPosition).mail_list.size();
    }

    // 获取指定位置的分组
    public Object getGroup(int groupPosition) {
        return mBoxList.get(groupPosition);
    }

    // 根据分组位置，以及孙子项的位置，获取对应的孙子项
    public Object getChild(int groupPosition, int childPosition) {
        return mBoxList.get(groupPosition).mail_list.get(childPosition);
    }

    // 获取分组的编号
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 根据分组位置，以及孙子项的位置，获取对应的孙子项编号
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 获取指定分组的视图
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewHolderBox holder;
        if (convertView == null) {
            holder = new ViewHolderBox();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_box, null);
            holder.iv_box = convertView.findViewById(R.id.iv_box);
            holder.tv_box = convertView.findViewById(R.id.tv_box);
            holder.tv_count = convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderBox) convertView.getTag();
        }
        MailBox box = mBoxList.get(groupPosition);
        holder.iv_box.setImageResource(box.box_icon);
        holder.tv_box.setText(box.box_title);
        holder.tv_count.setText(box.mail_list.size() + "封邮件");
        return convertView;
    }

    // 获取指定孙子项的视图
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderMail holder;
        if (convertView == null) {
            holder = new ViewHolderMail();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mail, null);
            holder.ck_mail = convertView.findViewById(R.id.ck_mail);
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderMail) convertView.getTag();
        }
        MailItem item = mBoxList.get(groupPosition).mail_list.get(childPosition);
        holder.ck_mail.setFocusable(false);
        holder.ck_mail.setFocusableInTouchMode(false);
        holder.ck_mail.setText(item.mail_title);
        holder.ck_mail.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MailBox box = mBoxList.get(groupPosition);
                MailItem item = box.mail_list.get(childPosition);
                String desc = String.format("您点击了%s的邮件，标题是%s", box.box_title, item.mail_title);
                Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
            }
        });
        holder.tv_date.setText(item.mail_date);
        return convertView;
    }

    // 判断孙子项是否允许选择。如果子条目需要响应点击事件，这里要返回true
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {}

    @Override
    public void onGroupCollapsed(int groupPosition) {}

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    // 定义一个邮箱分组的视图持有者
    public final class ViewHolderBox {
        public ImageView iv_box;
        public TextView tv_box;
        public TextView tv_count;
    }

    // 定义一个邮件条目的视图持有者
    public final class ViewHolderMail {
        public CheckBox ck_mail;
        public TextView tv_date;
    }

    // 在孙子项被点击时触发
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        ViewHolderMail holder = (ViewHolderMail) v.getTag();
        holder.ck_mail.setChecked(!(holder.ck_mail.isChecked()));
        return true;
    }

    // 在分组标题被点击时触发。如果返回true，就不会展示子列表
    public boolean onGroupClick(ExpandableListView parent, View v,
                                int groupPosition, long id) {
        String desc = String.format("您点击了%s", mBoxList.get(groupPosition).box_title);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
        return false;
    }

    // 子项目响应点击事件，需满足下面三个条件：
    // 1、isChildSelectable方法要返回true
    // 2、注册监听器setOnChildClickListener，并重写onChildClick方法
    // 3、子项目中若有Button、EditText等默认占用焦点的控件，要去除焦点占用，setFocusable和setFocusableInTouchMode设置为false
}
