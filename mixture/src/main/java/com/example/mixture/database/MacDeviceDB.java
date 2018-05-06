package com.example.mixture.database;

import java.util.ArrayList;

import com.example.mixture.bean.MacDevice;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MacDeviceDB {
    private static final String TAG = "MacDeviceDB";
    private static final String TABLE_NAME = "mac_device";
    private SQLiteDatabase mDB = null;

    public MacDeviceDB(SQLiteDatabase db) {
        mDB = db;
    }

    public int delete(String condition) {
        return mDB.delete(TABLE_NAME, condition, null);
    }

    public boolean insert(ArrayList<MacDevice> macArray) {
        for (int i = 0; i < macArray.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put("mac", macArray.get(i).mac);
            cv.put("device", macArray.get(i).device);
            long result = mDB.insert(TABLE_NAME, "", cv);
            // 添加成功后返回行号，失败后返回-1
            if (result == -1) {
                return false;
            }
        }
        return true;
    }

    public int update(MacDevice mac, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("mac", mac.mac);
        cv.put("device", mac.device);
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    private ArrayList<MacDevice> query(String sql) {
        Log.d(TAG, "sql=" + sql);
        ArrayList<MacDevice> macArray = new ArrayList<MacDevice>();
        Cursor cursor = mDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            MacDevice mac = new MacDevice();
            mac.xuhao = cursor.getInt(0);
            mac.mac = cursor.getString(1);
            mac.device = cursor.getString(2);
            macArray.add(mac);
        }
        cursor.close();
        return macArray;
    }

    public ArrayList<MacDevice> queryByMac(String mac) {
        String str = String.format("select _id,mac,device from %s where mac='%s';"
                , TABLE_NAME, mac);
        return query(str);
    }

}
