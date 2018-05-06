package com.example.media.adapter;

import com.example.media.R;
import com.example.media.widget.RecyclerExtras.OnItemClickListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PhotoAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context mContext;
    private int[] mImageArray;

    public PhotoAdapter(Context context, int[] imageArray) {
        mContext = context;
        mImageArray = imageArray;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mImageArray.length;
    }

    // 创建列表项的视图持有者
    public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_photo.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_photo, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        // 从指定资源编号的图片文件中获取位图对象
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mImageArray[position]);
        LayoutParams params = holder.iv_photo.getLayoutParams();
        params.width = bitmap.getWidth() * params.height / bitmap.getHeight();
        holder.iv_photo.setLayoutParams(params);
        holder.iv_photo.setImageBitmap(bitmap);
        // 列表项的点击事件需要自己实现
        holder.ll_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    // 获取列表项的类型
    public int getItemViewType(int position) {
        return 0;
    }

    // 获取列表项的编号
    public long getItemId(int position) {
        return position;
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_photo;
        public ImageView iv_photo;

        public ItemHolder(View v) {
            super(v);
            ll_photo = v.findViewById(R.id.ll_photo);
            iv_photo = v.findViewById(R.id.iv_photo);
        }

    }

    // 声明列表项的点击监听器对象
    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
