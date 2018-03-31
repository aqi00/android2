package com.example.senior.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.senior.R;
import com.example.senior.bean.Planet;

import java.util.ArrayList;

/**
 * Created by ouyangshen on 2018/3/29.
 */

@SuppressLint("DefaultLocale")
public class PlanetGridAdapter extends BaseAdapter implements
        OnItemClickListener, OnItemLongClickListener {
    private Context mContext; // 声明一个上下文对象
    private ArrayList<Planet> mPlanetList; // 声明一个行星信息队列

    // 行星适配器的构造函数，传入上下文与行星队列
    public PlanetGridAdapter(Context context, ArrayList<Planet> planet_list) {
        mContext = context;
        mPlanetList = planet_list;
    }

    // 获取网格项的个数
    public int getCount() {
        return mPlanetList.size();
    }

    // 获取网格项的数据
    public Object getItem(int arg0) {
        return mPlanetList.get(arg0);
    }

    // 获取网格项的编号
    public long getItemId(int arg0) {
        return arg0;
    }

    // 获取指定位置的网格项视图
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // 转换视图为空
            holder = new ViewHolder(); // 创建一个新的视图持有者
            // 根据布局文件item_grid.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid, null);
            holder.iv_icon = convertView.findViewById(R.id.iv_icon);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        Planet planet = mPlanetList.get(position);
        holder.iv_icon.setImageResource(planet.image); // 显示行星的图片
        holder.tv_name.setText(planet.name); // 显示行星的名称
        holder.tv_desc.setText(planet.desc); // 显示行星的描述
        return convertView;
    }

    // 定义一个视图持有者，以便重用网格项的视图资源
    public final class ViewHolder {
        public ImageView iv_icon; // 声明行星图片的图像视图对象
        public TextView tv_name; // 声明行星名称的文本视图对象
        public TextView tv_desc; // 声明行星描述的文本视图对象
    }

    // 处理网格项的点击事件，由接口OnItemClickListener触发
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String desc = String.format("您点击了第%d个行星，它的名字是%s", position + 1,
                mPlanetList.get(position).name);
        Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
    }

    // 处理网格项的长按事件，由接口OnItemLongClickListener触发
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String desc = String.format("您长按了第%d个行星，它的名字是%s", position + 1,
                mPlanetList.get(position).name);
        Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
        return true;
    }
}