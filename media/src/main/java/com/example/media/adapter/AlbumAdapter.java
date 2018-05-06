package com.example.media.adapter;

import com.example.media.util.Utils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class AlbumAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mImageRes;
    private int[] mBackColors;
    private int dip_pad;
    private int dip_radius;

    public AlbumAdapter(Context context, int[] imageRes, int[] backColors) {
        mContext = context;
        mImageRes = imageRes;
        mBackColors = backColors;
        dip_pad = Utils.dip2px(mContext, 20);
        dip_radius = Utils.dip2px(mContext, 5);
    }

    @Override
    public int getCount() {
        return mImageRes.length;
    }

    @Override
    public Object getItem(int position) {
        return mImageRes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 创建一个卡片视图
        CardView card = new CardView(mContext);
        // 设置卡片视图的布局参数
        // 这里不能使用LinearLayout.LayoutParams。否则会报错“java.lang.ClassCastException: android.widget.LinearLayout$LayoutParams cannot be cast to android.widget.Gallery$LayoutParams”
        card.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        // 设置卡片视图的圆角半径
        card.setRadius(dip_radius);
        // 设置卡片视图的内容间隔
        card.setContentPadding(dip_pad, dip_pad, dip_pad, dip_pad);
        // 设置卡片视图的阴影长度
        card.setCardElevation(3f);
        // 设置卡片视图的背景色
        card.setCardBackgroundColor(mBackColors[position]);
        // 创建一个图像视图
        ImageView iv = new ImageView(mContext);
        iv.setImageResource(mImageRes[position]);
        iv.setLayoutParams(new Gallery.LayoutParams(Utils.dip2px(mContext, 80), Utils.dip2px(mContext, 120)));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        // 把图像视图添加到卡片视图上
        card.addView(iv);
        return card;
    }

}
