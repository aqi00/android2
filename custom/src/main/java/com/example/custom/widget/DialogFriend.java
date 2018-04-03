package com.example.custom.widget;

import java.util.List;

import com.example.custom.R;
import com.example.custom.adapter.FriendAdapter;
import com.example.custom.adapter.FriendAdapter.OnDeleteListener;
import com.example.custom.bean.Friend;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class DialogFriend implements OnClickListener, OnDeleteListener {
    private Context mContext; // 声明一个上下文对象
    private Dialog dialog; // 声明一个对话框对象
    private View view; // 声明一个视图对象
    private TextView tv_title;
    private ListView lv_friend; // 声明一个列表视图
    private List<Friend> mFriendList;
    private FriendAdapter friendAdapter; // 声明一个朋友信息适配器

    // 对话框的构造函数，传入上下文、朋友队列，以及完成添加监听器
    public DialogFriend(Context context, List<Friend> friendList, onAddFriendListener listener) {
        mContext = context;
        // 根据布局文件dialog_friend.xml生成视图对象
        view = LayoutInflater.from(context).inflate(R.layout.dialog_friend, null);
        // 创建一个指定风格的对话框对象
        dialog = new Dialog(context, R.style.dialog_layout_bottom);
        tv_title = view.findViewById(R.id.tv_title);
        // 从布局文件中获取名叫lv_friend的列表视图
        lv_friend = view.findViewById(R.id.lv_friend);
        view.findViewById(R.id.tv_ok).setOnClickListener(this);
        mOnAddFriendListener = listener;
        mFriendList = friendList;
        // 构建一个朋友信息适配器
        friendAdapter = new FriendAdapter(mContext, mFriendList, this);
        // 给lv_friend设置朋友适配器
        lv_friend.setAdapter(friendAdapter);
    }

    // 显示对话框
    public void show() {
        // 设置对话框窗口的内容视图
        dialog.getWindow().setContentView(view);
        // 设置对话框窗口的布局参数
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // 设置对话框为靠下对齐
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        // 允许取消对话框
        dialog.setCancelable(true);
        dialog.show(); // 显示对话框
    }

    // 关闭对话框
    public void dismiss() {
        // 如果对话框显示出来了，就关闭它
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // 判断对话框是否显示
    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    // 设置对话框的标题
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    // 声明一个添加完成的监听器对象
    private onAddFriendListener mOnAddFriendListener;
    // 定义一个添加完成的监听器接口
    public interface onAddFriendListener {
        void addFriend(List<Friend> friendList);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_ok) { // 点击了确定按钮
            dialog.dismiss(); // 关闭对话框
            if (mOnAddFriendListener != null) { // 如果存在添加完成监听器
                // 回调监听器的addFriend方法
                mOnAddFriendListener.addFriend(friendAdapter.getFriends());
            }
        }
    }

    // 点击了列表中的删除按钮，就触发这里的onDeleteClick方法
    public void onDeleteClick(int position) {
        // 移除队列中的该记录
        mFriendList.remove(position);
        // 通知适配器发生了数据变更
        friendAdapter.notifyDataSetChanged();
    }

}
