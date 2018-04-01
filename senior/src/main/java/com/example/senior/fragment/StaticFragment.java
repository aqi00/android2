package com.example.senior.fragment;

import com.example.senior.R;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StaticFragment extends Fragment implements OnClickListener {
    private static final String TAG = "StaticFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象

    // 创建碎片视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        // 根据布局文件fragment_static.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_static, container, false);
        TextView tv_adv = mView.findViewById(R.id.tv_adv);
        ImageView iv_adv = mView.findViewById(R.id.iv_adv);
        tv_adv.setOnClickListener(this);
        iv_adv.setOnClickListener(this);
        Log.d(TAG, "onCreateView");
        return mView; // 返回该碎片的视图对象
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_adv) {
            Toast.makeText(mContext, "您点击了广告文本", Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.iv_adv) {
            Toast.makeText(mContext, "您点击了广告图片", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(Activity activity) { // 把碎片贴到页面上
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { // 页面创建
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() { // 页面销毁
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() { // 销毁碎片视图
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDetach() { // 把碎片从页面撕下来
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onPause() { // 页面暂停
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() { // 页面恢复
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStart() { // 页面启动
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() { // 页面停止
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) { //在活动页面创建之后
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

}