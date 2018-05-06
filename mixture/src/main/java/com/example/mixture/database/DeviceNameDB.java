package com.example.mixture.database;

import java.util.ArrayList;

import com.example.mixture.bean.DeviceName;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DeviceNameDB {
    private static final String TAG = "DeviceNameDB";
    private static final String TABLE_NAME = "device_name";
    private SQLiteDatabase mDB = null;

    public DeviceNameDB(SQLiteDatabase db) {
        mDB = db;
    }

    public int delete(String condition) {
        return mDB.delete(TABLE_NAME, condition, null);
    }

    public boolean insert(ArrayList<DeviceName> deviceArray) {
        for (int i = 0; i < deviceArray.size(); i++) {
            DeviceName item = deviceArray.get(i);
            ContentValues cv = new ContentValues();
            cv.put("device", item.device);
            cv.put("name", item.name);
            Log.d(TAG, "i=" + i + ",device=" + item.device + ",name=" + item.name);
            long result = mDB.insert(TABLE_NAME, "", cv);
            // 添加成功后返回行号，失败后返回-1
            if (result == -1) {
                return false;
            }
        }
        return true;
    }

    public int update(DeviceName device, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("device", device.device);
        cv.put("name", device.name);
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    private ArrayList<DeviceName> query(String sql) {
        Log.d(TAG, "sql=" + sql);
        ArrayList<DeviceName> deviceArray = new ArrayList<DeviceName>();
        Cursor cursor = mDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            DeviceName device = new DeviceName();
            device.xuhao = cursor.getInt(0);
            device.device = cursor.getString(1);
            device.name = cursor.getString(2);
            deviceArray.add(device);
        }
        cursor.close();
        return deviceArray;
    }

    public ArrayList<DeviceName> queryByDevice(String device) {
        String str = String.format("select _id,device,name from %s where device='%s';"
                , TABLE_NAME, device);
        return query(str);
    }

}
