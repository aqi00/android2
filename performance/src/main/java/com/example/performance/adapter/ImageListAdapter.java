package com.example.performance.adapter;

import com.example.performance.R;
import com.example.performance.cache.ImageCache;
import com.example.performance.cache.ImageCacheConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SetTextI18n")
public class ImageListAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mUrlArray; // 图片地址数组
    private ImageCache mCache; // 声明一个图片缓存对象

    public ImageListAdapter(Context context, String[] urlList) {
        mContext = context;
        mUrlArray = urlList;
        // 获得图片缓存的唯一实例
        mCache = ImageCache.getInstance(mContext);
        // 初始化图片缓存的配置，这里的图片缓存采取默认配置
        mCache.initConfig(new ImageCacheConfig.Builder().build());
    }

    @Override
    public int getCount() {
        return mUrlArray.length;
    }

    @Override
    public Object getItem(int arg0) {
        return mUrlArray[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image, null);
            holder.tv_seq = convertView.findViewById(R.id.tv_seq);
            holder.iv_scene = convertView.findViewById(R.id.iv_scene);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_seq.setText("" + (position + 1));
        // 通过图片缓存给图像视图加载网络图片
        mCache.show(mUrlArray[position], holder.iv_scene);
        return convertView;
    }

    public final class ViewHolder {
        TextView tv_seq;
        ImageView iv_scene;
    }

}
