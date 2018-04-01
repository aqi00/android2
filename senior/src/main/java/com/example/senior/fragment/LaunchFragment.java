package com.example.senior.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.senior.R;

public class LaunchFragment extends Fragment {
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private int mPosition; // 位置序号
    private int mImageId; // 图片的资源编号
    private int mCount = 4; // 引导页的数量

    // 获取该碎片的一个实例
    public static LaunchFragment newInstance(int position, int image_id) {
        LaunchFragment fragment = new LaunchFragment(); // 创建该碎片的一个实例
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putInt("position", position); // 往包裹存入位置序号
        bundle.putInt("image_id", image_id); // 往包裹存入图片的资源编号
        fragment.setArguments(bundle); // 把包裹塞给碎片
        return fragment; // 返回碎片实例
    }

    // 创建碎片视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        if (getArguments() != null) { // 如果碎片携带有包裹，则打开包裹获取参数信息
            mPosition = getArguments().getInt("position", 0);
            mImageId = getArguments().getInt("image_id", 0);
        }
        // 根据布局文件item_launch.xml生成视图对象
        mView = inflater.inflate(R.layout.item_launch, container, false);
        ImageView iv_launch = mView.findViewById(R.id.iv_launch);
        RadioGroup rg_indicate = mView.findViewById(R.id.rg_indicate);
        Button btn_start = mView.findViewById(R.id.btn_start);
        // 设置引导页的全屏图片
        iv_launch.setImageResource(mImageId);
        // 每张图片都分配一个对应的单选按钮RadioButton
        for (int j = 0; j < mCount; j++) {
            RadioButton radio = new RadioButton(mContext);
            radio.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            radio.setButtonDrawable(R.drawable.launch_guide);
            radio.setPadding(10, 10, 10, 10);
            // 把单选按钮添加到底部指示器的单选组
            rg_indicate.addView(radio);
        }
        // 当前位置的单选按钮要高亮显示，比如第二个引导页就高亮第二个单选按钮
        ((RadioButton) rg_indicate.getChildAt(mPosition)).setChecked(true);
        // 如果是最后一个引导页，则显示入口按钮，以便用户点击按钮进入首页
        if (mPosition == mCount - 1) {
            btn_start.setVisibility(View.VISIBLE);
            btn_start.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "欢迎您开启美好生活", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return mView; // 返回该碎片的视图对象
    }
}
