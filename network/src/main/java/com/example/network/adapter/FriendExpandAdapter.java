package com.example.network.adapter;

import java.util.ArrayList;

import com.example.network.ChatMainActivity;
import com.example.network.R;
import com.example.network.bean.Friend;
import com.example.network.bean.FriendGroup;
import com.example.network.util.DateUtil;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class FriendExpandAdapter implements ExpandableListAdapter, OnGroupClickListener, OnChildClickListener {
    private final static String TAG = "FriendExpandAdapter";
    private Context mContext; // 声明一个上下文对象
    private ArrayList<FriendGroup> mGroupList; // 好友分组队列
    private int[] mFaceArray = { // 好友头像的资源图片数组
            R.drawable.qq01, R.drawable.qq02, R.drawable.qq03, R.drawable.qq04, R.drawable.qq05,
            R.drawable.qq06, R.drawable.qq07, R.drawable.qq08, R.drawable.qq09, R.drawable.qq10,
            R.drawable.qq11, R.drawable.qq12, R.drawable.qq13, R.drawable.qq14, R.drawable.qq15,
            R.drawable.qq16, R.drawable.qq17, R.drawable.qq18, R.drawable.qq19, R.drawable.qq20,
            R.drawable.qq21, R.drawable.qq22, R.drawable.qq23, R.drawable.qq24, R.drawable.qq25,
            R.drawable.qq26, R.drawable.qq27, R.drawable.qq28, R.drawable.qq29, R.drawable.qq30,
            R.drawable.qq31, R.drawable.qq32, R.drawable.qq33, R.drawable.qq34, R.drawable.qq35,
            R.drawable.qq36, R.drawable.qq37, R.drawable.qq38, R.drawable.qq39, R.drawable.qq40,
    };

    public FriendExpandAdapter(Context context, ArrayList<FriendGroup> group_list) {
        mContext = context;
        mGroupList = group_list;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    // 获取分组的数目
    public int getGroupCount() {
        return mGroupList.size();
    }

    // 获取某个分组的孙子项数目
    public int getChildrenCount(int groupPosition) {
        return mGroupList.get(groupPosition).friend_list.size();
    }

    // 获取指定位置的分组
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    // 根据分组位置，以及孙子项的位置，获取对应的孙子项
    public Object getChild(int groupPosition, int childPosition) {
        return mGroupList.get(groupPosition).friend_list.get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup holder;
        if (convertView == null) {
            holder = new ViewHolderGroup();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group, null);
            holder.tv_group_name = convertView.findViewById(R.id.tv_group_name);
            holder.tv_group_count = convertView.findViewById(R.id.tv_group_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }
        FriendGroup group = mGroupList.get(groupPosition);
        holder.tv_group_name.setText(group.title);
        holder.tv_group_count.setText(group.friend_list.size() + "个好友");
        return convertView;
    }

    // 获取指定孙子项的视图
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderFriend holder;
        if (convertView == null) {
            holder = new ViewHolderFriend();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_friend, null);
            holder.iv_face = convertView.findViewById(R.id.iv_face);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_time = convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderFriend) convertView.getTag();
        }
        Friend item = mGroupList.get(groupPosition).friend_list.get(childPosition);
        holder.iv_face.setImageResource(mFaceArray[(int) (Math.random() * 200 % 40)]);
        holder.tv_name.setText(item.nick_name);
        if (groupPosition == 0) { // 第一个好友分组为在线好友列表
            holder.tv_time.setText(DateUtil.formatTime(item.login_time));
        } else {
            holder.tv_time.setText("");
        }
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

    // 定义一个好友分组的视图持有者
    public final class ViewHolderGroup {
        public TextView tv_group_name;
        public TextView tv_group_count;
    }

    // 定义一个好友概貌的视图持有者
    public final class ViewHolderFriend {
        public ImageView iv_face;
        public TextView tv_name;
        public TextView tv_time;
    }

    // 在孙子项被点击时触发
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (groupPosition == 0) { //在线好友列表
            Friend friend = mGroupList.get(groupPosition).friend_list.get(childPosition);
            // 跳转到聊天主页面
            Intent intent = new Intent(mContext, ChatMainActivity.class);
            intent.putExtra("otherId", friend.device_id); // 存入好友的设备编号
            intent.putExtra("otherName", friend.nick_name); // 存入好友的昵称
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            String desc = String.format("您点击了好友：%s", mGroupList.get(groupPosition).friend_list.get(childPosition).nick_name);
            Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    // 在分组标题被点击时触发。如果返回true，就不会展示子列表
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        String desc = String.format("您点击了分组：%s", mGroupList.get(groupPosition).title);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
        return false;
    }

}
