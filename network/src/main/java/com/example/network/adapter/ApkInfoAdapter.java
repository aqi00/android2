package com.example.network.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.network.R;
import com.example.network.bean.ApkInfo;

import java.util.ArrayList;

/**
 * Created by ouyangshen on 2018/1/17.
 */
// 展示APK文件信息列表
public class ApkInfoAdapter extends BaseAdapter {
    private final static String TAG = "ApkInfoAdapter";
    private Context mContext;
    private ArrayList<ApkInfo> mApkList;

    public ApkInfoAdapter(Context context, ArrayList<ApkInfo> apkList) {
        mContext = context;
        mApkList = apkList;
    }

    @Override
    public int getCount() {
        return mApkList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mApkList.get(arg0);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_apk, null);
            holder.tv_file_name = convertView.findViewById(R.id.tv_file_name);
            holder.tv_package_name = convertView.findViewById(R.id.tv_package_name);
            holder.tv_version_name = convertView.findViewById(R.id.tv_version_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ApkInfo item = mApkList.get(position);
        holder.tv_file_name.setText(item.file_name);
        holder.tv_package_name.setText(item.package_name);
        holder.tv_version_name.setText(item.version_name);
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_file_name;
        public TextView tv_package_name;
        public TextView tv_version_name;
    }

}
