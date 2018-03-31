package com.example.senior.adapter;

import java.util.ArrayList;

import com.example.senior.bean.GoodsInfo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImagePagerAdapater extends PagerAdapter {
    private Context mContext; // 声明一个上下文对象
    // 声明一个图像视图队列
    private ArrayList<ImageView> mViewList = new ArrayList<ImageView>();
    // 声明一个商品信息队列
    private ArrayList<GoodsInfo> mGoodsList = new ArrayList<GoodsInfo>();

    // 图像翻页适配器的构造函数，传入上下文与商品信息队列
    public ImagePagerAdapater(Context context, ArrayList<GoodsInfo> goodsList) {
        mContext = context;
        mGoodsList = goodsList;
        // 给每个商品分配一个专用的图像视图
        for (int i = 0; i < mGoodsList.size(); i++) {
            ImageView view = new ImageView(mContext);
            view.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            view.setImageResource(mGoodsList.get(i).pic);
            view.setScaleType(ScaleType.FIT_CENTER);
            // 把该商品的图像视图添加到图像视图队列
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

    // 获得指定页面的标题文本
    public CharSequence getPageTitle(int position) {
        return mGoodsList.get(position).name;
    }

}
