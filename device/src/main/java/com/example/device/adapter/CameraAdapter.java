package com.example.device.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.device.R;
import com.example.device.bean.CameraInfo;

@SuppressLint(value={"DefaultLocale","SetTextI18n"})
// 展示相机信息列表的适配器
public class CameraAdapter extends BaseAdapter {
    private ArrayList<CameraInfo> mCameraList;
    private Context mContext;

    public CameraAdapter(Context context, ArrayList<CameraInfo> cameraList) {
        mContext = context;
        mCameraList = cameraList;
    }

    @Override
    public int getCount() {
        return mCameraList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mCameraList.get(arg0);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_camera, null);
            holder.tv_camera_type = convertView.findViewById(R.id.tv_camera_type);
            holder.tv_flash_mode = convertView.findViewById(R.id.tv_flash_mode);
            holder.tv_focus_mode = convertView.findViewById(R.id.tv_focus_mode);
            holder.tv_scene_mode = convertView.findViewById(R.id.tv_scene_mode);
            holder.tv_color_effect = convertView.findViewById(R.id.tv_color_effect);
            holder.tv_scene_mode = convertView.findViewById(R.id.tv_scene_mode);
            holder.tv_white_balance = convertView.findViewById(R.id.tv_white_balance);
            holder.tv_max_zoom = convertView.findViewById(R.id.tv_max_zoom);
            holder.tv_zoom = convertView.findViewById(R.id.tv_zoom);
            holder.tv_resolution = convertView.findViewById(R.id.tv_resolution);
            holder.tv_resolution_list = convertView.findViewById(R.id.tv_resolution_list);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CameraInfo item = mCameraList.get(position);
        holder.tv_camera_type.setText("" + item.camera_type);
        holder.tv_flash_mode.setText(item.flash_mode);
        holder.tv_focus_mode.setText(item.focus_mode);
        holder.tv_scene_mode.setText(item.scene_mode);
        holder.tv_color_effect.setText(item.color_effect);
        holder.tv_white_balance.setText(item.white_balance);
        holder.tv_max_zoom.setText(item.max_zoom + "");
        holder.tv_zoom.setText(item.zoom + "");
        String desc = "";
        for (int j = 0; j < item.resolutionList.size(); j++) {
            Camera.Size size = item.resolutionList.get(j);
            desc = String.format("%s分辨率%d为：宽%d*高%d\n", desc, j + 1, size.width, size.height);
        }
        holder.tv_resolution_list.setText(desc);
        listeneClick(holder);
        return convertView;
    }

    private void listeneClick(final ViewHolder holder) {
        holder.tv_resolution.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = holder.tv_resolution_list.getVisibility();
                if (visibility == View.GONE) {
                    holder.tv_resolution.setText("收起");
                    holder.tv_resolution_list.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_resolution.setText("展示");
                    holder.tv_resolution_list.setVisibility(View.GONE);
                }
            }
        });
    }

    public final class ViewHolder {
        public TextView tv_camera_type;
        public TextView tv_flash_mode;
        public TextView tv_focus_mode;
        public TextView tv_scene_mode;
        public TextView tv_color_effect;
        public TextView tv_white_balance;
        public TextView tv_max_zoom;
        public TextView tv_zoom;
        public TextView tv_resolution;
        public TextView tv_resolution_list;
    }

}
