package com.example.performance.adapter;

import java.util.ArrayList;

import com.example.performance.R;
import com.example.performance.bean.Planet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlanetAdapter extends BaseAdapter {
    private Context mContext;
    private int mLayoutId;
    private ArrayList<Planet> mPlanetList;
    private int mBackground;

    public PlanetAdapter(Context context, int layout_id, ArrayList<Planet> planet_list, int background) {
        mContext = context;
        mLayoutId = layout_id;
        mPlanetList = planet_list;
        mBackground = background;
    }

    @Override
    public int getCount() {
        return mPlanetList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mPlanetList.get(arg0);
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
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            holder.ll_item = convertView.findViewById(R.id.ll_item);
            holder.iv_icon = convertView.findViewById(R.id.iv_icon);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Planet planet = mPlanetList.get(position);
        holder.ll_item.setBackgroundColor(mBackground);
        holder.iv_icon.setImageResource(planet.image);
        holder.tv_name.setText(planet.name);
        holder.tv_desc.setText(planet.desc);
        return convertView;
    }

    public final class ViewHolder {
        LinearLayout ll_item;
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }

}
