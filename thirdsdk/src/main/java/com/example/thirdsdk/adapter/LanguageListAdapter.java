package com.example.thirdsdk.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.thirdsdk.R;
import com.example.thirdsdk.bean.Language;

@SuppressLint("DefaultLocale")
public class LanguageListAdapter extends BaseAdapter {
    private Context mContext; // 声明一个上下文对象
    private ArrayList<Language> mLanguageList; // 声明一个语言信息队列

    // 语言适配器的构造函数，传入上下文与语言队列
    public LanguageListAdapter(Context context, ArrayList<Language> planet_list) {
        mContext = context;
        mLanguageList = planet_list;
    }

    // 获取列表项的个数
    public int getCount() {
        return mLanguageList.size();
    }

    // 获取列表项的数据
    public Object getItem(int arg0) {
        return mLanguageList.get(arg0);
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
            // 根据布局文件item_language.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_language, null);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        Language planet = mLanguageList.get(position);
        holder.tv_name.setText(planet.name); // 显示语言的名称
        holder.tv_desc.setText(planet.desc); // 显示语言的描述
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public TextView tv_name; // 声明语言名称的文本视图对象
        public TextView tv_desc; // 声明语言描述的文本视图对象
    }
}
