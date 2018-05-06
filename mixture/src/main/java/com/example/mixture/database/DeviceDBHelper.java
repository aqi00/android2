package com.example.mixture.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DeviceDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DeviceDBHelper";
    private static final String DB_NAME = "device.db";
    public static final int DB_VERSION = 10;
    private static DeviceDBHelper mHelper = null;
    private SQLiteDatabase mDB = null;

    private DeviceDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private DeviceDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    public static DeviceDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new DeviceDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new DeviceDBHelper(context);
        }
        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        Log.d(TAG, "openReadLink");
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    public SQLiteDatabase openLink() {
        Log.d(TAG, "openLink");
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    public void closeLink() {
        Log.d(TAG, "closeLink");
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
            mDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS device_name;";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS device_name ("
                + "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
                + "device VARCHAR NOT NULL," + "name VARCHAR NOT NULL" + ")";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);

        drop_sql = "DROP TABLE IF EXISTS mac_device;";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        create_sql = "CREATE TABLE IF NOT EXISTS mac_device ("
                + "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
                + "mac VARCHAR NOT NULL," + "device VARCHAR NOT NULL" + ")";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        onCreate(db);
    }

}
