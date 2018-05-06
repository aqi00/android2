package com.example.mixture.adapter;

import java.util.ArrayList;

import com.example.mixture.R;
import com.example.mixture.bean.WifiConnect;
import com.example.mixture.widget.InputDialogFragment;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class WifiListAdapter extends BaseAdapter {
    private static final String TAG = "WifiListAdapter";
    private Context mContext;
    private WifiManager mWifiManager;
    private ArrayList<WifiConnect> mWifiList;
    private int[] mSignalList = {
            R.drawable.signal_1, R.drawable.signal_2, R.drawable.signal_3, R.drawable.signal_4};

    public WifiListAdapter(Context context, WifiManager wifiMgr, ArrayList<WifiConnect> wifi_list) {
        mContext = context;
        mWifiManager = wifiMgr;
        mWifiList = wifi_list;
    }

    @Override
    public int getCount() {
        return mWifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWifiList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, null);
            holder.ck_wifi = convertView.findViewById(R.id.ck_wifi);
            holder.iv_wifi = convertView.findViewById(R.id.iv_wifi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WifiConnect client = mWifiList.get(position);
        holder.ck_wifi.setText(client.SSID);
        if (client.status) {
            holder.ck_wifi.setChecked(true);
        } else {
            holder.ck_wifi.setChecked(false);
        }
        holder.ck_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // 连接WIFI
                    if (client.networkId >= 0) { // 找到已保存的WIFI，则直接连接
                        // 启用指定网络编号的WIFI
                        mWifiManager.enableNetwork(client.networkId, true);
                    } else { // 未找到已保存的WIFI
                        if (client.type == 0) { // 该WIFI无密码，则直接添加并连接
                            // 创建一个WIFI配置信息
                            WifiConfiguration config = new WifiConfiguration();
                            config.SSID = "\"" + client.SSID + "\"";
                            config.wepKeys[0] = "";
                            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            config.wepTxKeyIndex = 0;
                            // 往无线网络管理器添加新的WIFI配置，并返回该WIFI的网络编号
                            int netId = mWifiManager.addNetwork(config);
                            // 启用指定网络编号的WIFI
                            mWifiManager.enableNetwork(netId, true);
                        } else { // 该WIFI需要密码，则弹窗提示用户输入密码
                            InputDialogFragment dialog = InputDialogFragment.newInstance(
                                    client.SSID, client.type, "请输入"+client.SSID+"的密码");
                            String fragTag = mContext.getResources().getString(R.string.app_name);
                            dialog.show(((Activity) mContext).getFragmentManager(), fragTag);
                        }
                    }
                } else { // 断开WIFI
                    mWifiManager.disconnect(); // 断开当前的WIFI连接
                }
            }
        });
        holder.iv_wifi.setImageResource(mSignalList[client.level]);
        return convertView;
    }

    public final class ViewHolder {
        public CheckBox ck_wifi;
        public ImageView iv_wifi;
    }

}
