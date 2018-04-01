package com.example.senior.fragment;

import com.example.senior.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DynamicFragment extends Fragment {
    private static final String TAG = "DynamicFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private int mPosition; // 位置序号
    private int mImageId; // 图片的资源编号
    private String mDesc; // 商品的文字描述

    // 获取该碎片的一个实例
    public static DynamicFragment newInstance(int position, int image_id, String desc) {
        DynamicFragment fragment = new DynamicFragment(); // 创建该碎片的一个实例
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putInt("position", position); // 往包裹存入位置序号
        bundle.putInt("image_id", image_id); // 往包裹存入图片的资源编号
        bundle.putString("desc", desc); // 往包裹存入商品的文字描述
        fragment.setArguments(bundle); // 把包裹塞给碎片
        return fragment; // 返回碎片实例
    }

    // 创建碎片视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        if (getArguments() != null) { // 如果碎片携带有包裹，则打开包裹获取参数信息
            mPosition = getArguments().getInt("position", 0);
            mImageId = getArguments().getInt("image_id", 0);
            mDesc = getArguments().getString("desc");
        }
        // 根据布局文件fragment_dynamic.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ImageView iv_pic = mView.findViewById(R.id.iv_pic);
        TextView tv_desc = mView.findViewById(R.id.tv_desc);
        iv_pic.setImageResource(mImageId);
        tv_desc.setText(mDesc);
        Log.d(TAG, "onCreateView position=" + mPosition);
        return mView; // 返回该碎片的视图对象
    }

    @Override
    public void onAttach(Activity activity) { // 把碎片贴到页面上
        super.onAttach(activity);
        Log.d(TAG, "onAttach position=" + mPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { // 页面创建
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate position=" + mPosition);
    }

    @Override
    public void onDestroy() { // 页面销毁
        super.onDestroy();
        Log.d(TAG, "onDestroy position=" + mPosition);
    }

    @Override
    public void onDestroyView() { // 销毁碎片视图
        super.onDestroyView();
        Log.d(TAG, "onDestroyView position=" + mPosition);
    }

    @Override
    public void onDetach() { // 把碎片从页面撕下来
        super.onDetach();
        Log.d(TAG, "onDetach position=" + mPosition);
    }

    @Override
    public void onPause() { // 页面暂停
        super.onPause();
        Log.d(TAG, "onPause position=" + mPosition);
    }

    @Override
    public void onResume() { // 页面恢复
        super.onResume();
        Log.d(TAG, "onResume position=" + mPosition);
    }

    @Override
    public void onStart() { // 页面启动
        super.onStart();
        Log.d(TAG, "onStart position=" + mPosition);
    }

    @Override
    public void onStop() { // 页面停止
        super.onStop();
        Log.d(TAG, "onStop position=" + mPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) { //在活动页面创建之后
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated position=" + mPosition);
    }

}
