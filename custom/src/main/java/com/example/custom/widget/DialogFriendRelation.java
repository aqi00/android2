package com.example.custom.widget;

import com.example.custom.R;
import com.example.custom.adapter.FriendRelationAdapter;
import com.example.custom.util.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

public class DialogFriendRelation implements OnItemClickListener, OnDismissListener {
    private Context mContext; // 声明一个上下文对象
    private Dialog dialog; // 声明一个对话框对象
    private View view; // 声明一个视图对象
    private GridView gv_relation; // 声明一个网格视图
    private LinearLayout ll_relation_gap;
    private FriendRelationAdapter friendRelationAdapter; // 声明一个朋友关系适配器
    private String[] relation_name_array; // 关系名称数组
    private String[] relation_value_array; // 关系值数组
    private int mGap; // 空白行。关系选择对话框紧贴着朋友列表上方，所以要计算下面空出多少行
    private int mSelected; // 当前选中的记录序号

    // 对话框的构造函数，传入上下文，以及关系选择监听器
    public DialogFriendRelation(Context context, onSelectRelationListener listener) {
        mContext = context;
        // 根据布局文件dialog_friend_relation.xml生成视图对象
        view = LayoutInflater.from(context).inflate(R.layout.dialog_friend_relation, null);
        // 创建一个指定风格的对话框对象
        dialog = new Dialog(context, R.style.dialog_layout_bottom_transparent);
        // 从布局文件中获取名叫gv_relation的网格视图
        gv_relation = view.findViewById(R.id.gv_relation);
        ll_relation_gap = view.findViewById(R.id.ll_relation_gap);
        // 从资源文件arrays.xml中获取关系名称的字符串数组
        relation_name_array = context.getResources().getStringArray(R.array.relation_name);
        // 从资源文件arrays.xml中获取关系值的字符串数组
        relation_value_array = context.getResources().getStringArray(R.array.relation_value);
        mOnSelectRelationListener = listener;
    }

    // 显示对话框
    public void show(final int gap, final int selected) {
        mGap = gap;
        mSelected = selected;
        int dip_48 = Utils.dip2px(mContext, 48);
        int dip_2 = Utils.dip2px(mContext, 2);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dip_48 * (gap + 1) - dip_2 + gap);
        ll_relation_gap.setLayoutParams(params);
        // 设置空白处的点击事件。一旦点击了空白处，就自动关闭对话框
        ll_relation_gap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // 构建一个朋友关系适配器
        friendRelationAdapter = new FriendRelationAdapter(mContext, relation_name_array, selected);
        // 给gv_relation设置朋友关系适配器
        gv_relation.setAdapter(friendRelationAdapter);
        gv_relation.setOnItemClickListener(this);
        // 设置对话框窗口的内容视图
        dialog.getWindow().setContentView(view);
        // 设置对话框窗口的布局参数
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // 设置对话框为靠下对齐
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show(); // 显示对话框
        // 设置对话框的消失监听器，在关闭对话框时触发监听器的onDismiss方法
        dialog.setOnDismissListener(this);
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

    // 点击某个网格项的时候，便触发onItemClick方法
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelected = position;
        dismiss(); // 关闭对话框
    }

    // 声明一个关系选择的监听器对象
    private onSelectRelationListener mOnSelectRelationListener;
    // 定义一个关系选择的监听器接口
    public interface onSelectRelationListener {
        void setRelation(int gap, String name, String value);
    }

    // 关闭对话框的时候，便触发onDismiss方法
    public void onDismiss(DialogInterface dialog) {
        if (mOnSelectRelationListener != null) { // 如果存在关系选择监听器
            // 回调监听器的setRelation方法
            mOnSelectRelationListener.setRelation(mGap,
                    relation_name_array[mSelected], relation_value_array[mSelected]);
        }
    }

}
