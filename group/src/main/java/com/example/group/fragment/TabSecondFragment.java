package com.example.group.fragment;

import com.example.group.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabSecondFragment extends Fragment {
    private static final String TAG = "TabSecondFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        // 根据布局文件fragment_tab_second.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_tab_second, container, false);
        // 根据碎片标签栏传来的参数拼接文本字符串
        String desc = String.format("我是%s页面，来自%s",
                "分类", getArguments().getString("tag"));
        TextView tv_second = mView.findViewById(R.id.tv_second);
        tv_second.setText(desc);

        return mView;
    }

}
