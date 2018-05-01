package com.example.senior.adapter;

import java.util.ArrayList;

import com.example.senior.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LaunchSimpleAdapter extends PagerAdapter {
    private Context mContext; // 声明一个上下文对象
    private ArrayList<View> mViewList = new ArrayList<View>(); // 声明一个引导页的视图队列

    // 引导页适配器的构造函数，传入上下文与图片数组
    public LaunchSimpleAdapter(Context context, int[] imageArray) {
        mContext = context;
        for (int i = 0; i < imageArray.length; i++) {
            // 根据布局文件item_launch.xml生成视图对象
            View view = LayoutInflater.from(context).inflate(R.layout.item_launch, null);
            ImageView iv_launch = view.findViewById(R.id.iv_launch);
            RadioGroup rg_indicate = view.findViewById(R.id.rg_indicate);
            Button btn_start = view.findViewById(R.id.btn_start);
            // 设置引导页的全屏图片
            iv_launch.setImageResource(imageArray[i]);
            // 每张图片都分配一个对应的单选按钮RadioButton
            for (int j = 0; j < imageArray.length; j++) {
                RadioButton radio = new RadioButton(mContext);
                radio.setLayoutParams(new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                radio.setButtonDrawable(R.drawable.launch_guide);
                radio.setPadding(10, 10, 10, 10);
                // 把单选按钮添加到底部指示器的单选组
                rg_indicate.addView(radio);
            }
            // 当前位置的单选按钮要高亮显示，比如第二个引导页就高亮第二个单选按钮
            ((RadioButton) rg_indicate.getChildAt(i)).setChecked(true);
            // 如果是最后一个引导页，则显示入口按钮，以便用户点击按钮进入首页
            if (i == imageArray.length - 1) {
                btn_start.setVisibility(View.VISIBLE);
                btn_start.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "欢迎您开启美好生活",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            // 把该图片对应的引导页添加到引导页的视图队列
            mViewList.add(view);
        }
    }

    // 获取页面项的个数
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    // 从容器中销毁指定位置的页面
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    // 实例化指定位置的页面，并将其添加到容器中
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }
}
