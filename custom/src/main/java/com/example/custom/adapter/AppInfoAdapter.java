package com.example.custom.adapter;

import java.util.ArrayList;

import com.example.custom.R;
import com.example.custom.bean.AppInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SetTextI18n")
public class AppInfoAdapter extends BaseAdapter {
    private Context mContext; // 声明一个上下文对象
    private ArrayList<AppInfo> mAppInfoList; // 声明一个应用信息队列

    // 应用信息适配器的构造函数，传入上下文与应用队列
    public AppInfoAdapter(Context context, ArrayList<AppInfo> appinfoList) {
        mContext = context;
        mAppInfoList = appinfoList;
    }

    // 获取列表项的个数
    public int getCount() {
        return mAppInfoList.size();
    }

    // 获取列表项的数据
    public Object getItem(int arg0) {
        return mAppInfoList.get(arg0);
    }

    // 获取列表项的编号
    public long getItemId(int arg0) {
        return arg0;
    }

    // 获取指定位置的列表项视图
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // 转换视图为空
            holder = new ViewHolder(); // 创建一个新的视图持有者
            // 根据布局文件item_appinfo.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_appinfo, null);
            holder.iv_icon = convertView.findViewById(R.id.iv_icon);
            holder.tv_label = convertView.findViewById(R.id.tv_label);
            holder.tv_package_name = convertView.findViewById(R.id.tv_package_name);
            holder.tv_uid = convertView.findViewById(R.id.tv_uid);
            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo item = mAppInfoList.get(position);
        holder.iv_icon.setImageDrawable(item.icon); // 显示应用的图标
        holder.tv_label.setText(item.label); // 显示应用的名称
        holder.tv_package_name.setText(item.package_name); // 显示应用的包名
        holder.tv_uid.setText("" + item.uid); // 显示应用的编号
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public ImageView iv_icon; // 声明应用图标的图像视图对象
        public TextView tv_label; // 声明应用名称的文本视图对象
        public TextView tv_package_name; // 声明应用包名的文本视图对象
        public TextView tv_uid; // 声明应用编号的文本视图对象
    }

}
