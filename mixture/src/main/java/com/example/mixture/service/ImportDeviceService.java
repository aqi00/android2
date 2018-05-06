package com.example.mixture.service;

import java.util.ArrayList;

import com.example.mixture.bean.DeviceName;
import com.example.mixture.bean.MacDevice;
import com.example.mixture.database.DeviceDBHelper;
import com.example.mixture.database.DeviceNameDB;
import com.example.mixture.database.MacDeviceDB;
import com.example.mixture.util.AssetsUtil;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class ImportDeviceService extends IntentService {
    private static final String TAG = "ImportDeviceService";

    public ImportDeviceService() {
        super("com.example.mixture.service.ImportDeviceService");
    }

    // onStartCommand运行于主线程
    public int onStartCommand(Intent intent, int flags, int startid) {
        return super.onStartCommand(intent, flags, startid);
    }

    // onHandleIntent运行分主线程
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "begin onHandleIntent");
        importDevice(); // 导入手机厂商与MAC地址的对应关系
        Log.d(TAG, "end onHandleIntent");
    }

    // 导入手机厂商与MAC地址的对应关系
    private void importDevice() {
        SharedPreferences sps = getSharedPreferences("share", MODE_PRIVATE);
        int version = sps.getInt("version", 0);
        if (version >= DeviceDBHelper.DB_VERSION) { // 已经导过了
            Log.d(TAG, "当前无新数据导入");
            return;
        }
        DeviceDBHelper helper = DeviceDBHelper.getInstance(this, DeviceDBHelper.DB_VERSION);
        // 下面从device/device_name.txt导入手机厂商与手机品牌的对应关系
        ArrayList<DeviceName> nameArray = new ArrayList<DeviceName>();
        String nameContent = AssetsUtil.getTxtFromAssets(this, "device/device_name.txt");
        nameContent = nameContent.replace("\r", "");
        String[] nameList = nameContent.split("\n");
        for (String line : nameList) {
            String[] itemList = line.split(",");
            if (itemList.length >= 2) {
                DeviceName name = new DeviceName(itemList[0].toUpperCase(), itemList[1]);
                nameArray.add(name);
            }
        }
        Log.d(TAG, "nameArray.size()=" + nameArray.size());
        DeviceNameDB nameDB = new DeviceNameDB(helper.openLink());
        nameDB.insert(nameArray);
        // 下面从device/mac_device.txt导入手机厂商与MAC地址的对应关系
        ArrayList<MacDevice> macArray = new ArrayList<MacDevice>();
        String macContent = AssetsUtil.getTxtFromAssets(this, "device/mac_device.txt");
        macContent = macContent.replace("\r", "");
        String[] macList = macContent.split("\n");
        for (String line : macList) {
            String[] itemList = line.split(",");
            if (itemList.length >= 2) {
                MacDevice mac = new MacDevice(itemList[0], itemList[1]);
                macArray.add(mac);
            }
        }
        Log.d(TAG, "macArray.size()=" + macArray.size());
        MacDeviceDB macDB = new MacDeviceDB(helper.openLink());
        macDB.insert(macArray);
        helper.closeLink();
        // 在共享参数中写入导入成功的标志（以数据库版本号作为标记）
        SharedPreferences.Editor editor = sps.edit();
        editor.putInt("version", DeviceDBHelper.DB_VERSION);
        editor.apply();
        String desc = String.format("已成功导入%d条设备名称记录，导入%d条设备MAC记录",
                nameArray.size(), macArray.size());
        Log.d(TAG, desc);
    }

}
