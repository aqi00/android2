package com.example.mixture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mixture.R;
import com.example.mixture.bean.BlueDevice;

import java.util.ArrayList;

// 展示蓝牙设备列表的适配器
public class BlueListAdapter extends BaseAdapter {
    private static final String TAG = "BlueListAdapter";
    private Context mContext;
    private ArrayList<BlueDevice> mBlueList = new ArrayList<BlueDevice>();
    private String[] mStateArray = {"未绑定", "绑定中", "已绑定", "已连接"};
    public static int CONNECTED = 13;

    public BlueListAdapter(Context context, ArrayList<BlueDevice> blue_list) {
        mContext = context;
        mBlueList = blue_list;
    }

    @Override
    public int getCount() {
        return mBlueList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBlueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth, null);
            holder.tv_blue_name = convertView.findViewById(R.id.tv_blue_name);
            holder.tv_blue_address = convertView.findViewById(R.id.tv_blue_address);
            holder.tv_blue_state = convertView.findViewById(R.id.tv_blue_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BlueDevice device = mBlueList.get(position);
        holder.tv_blue_name.setText(device.name); // 显示蓝牙设备的名称
        holder.tv_blue_address.setText(device.address); // 显示蓝牙设备的地址
        holder.tv_blue_state.setText(mStateArray[device.state-10]); // 显示蓝牙设备的状态
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_blue_name;
        public TextView tv_blue_address;
        public TextView tv_blue_state;
    }

}
