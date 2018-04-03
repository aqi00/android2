package com.example.custom.database;

import java.util.ArrayList;

import com.example.custom.bean.AppInfo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class TrafficDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "TrafficDBHelper";
    private static final String DB_NAME = "traffic.db"; // 数据库的名称
    private static final int DB_VERSION = 1; // 数据库的版本号
    private static TrafficDBHelper mHelper = null; // 数据库帮助器的实例
    private SQLiteDatabase mDB = null; // 数据库的实例
    private static final String TABLE_NAME = "traffic_info"; // 表的名称

    private TrafficDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private TrafficDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    // 利用单例模式获取数据库帮助器的唯一实例
    public static TrafficDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new TrafficDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new TrafficDBHelper(context);
        }
        return mHelper;
    }

    // 打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    // 打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    // 关闭数据库连接
    public void closeLink() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
            mDB = null;
        }
    }

    // 创建数据库，执行建表语句
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "month INTEGER NOT NULL," + "day INTEGER NOT NULL,"
                + "uid INTEGER NOT NULL," + "label VARCHAR NOT NULL,"
                + "package_name VARCHAR NOT NULL," + "icon_path VARCHAR NOT NULL,"
                + "traffic LONG NOT NULL"
                + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    // 修改数据库，执行表结构变更语句
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // 根据指定条件删除表记录
    public int delete(String condition) {
        // 执行删除记录动作，该语句返回删除记录的数目
        return mDB.delete(TABLE_NAME, condition, null);
    }

    // 删除该表的所有记录
    public int deleteAll() {
        // 执行删除记录动作，该语句返回删除记录的数目
        return mDB.delete(TABLE_NAME, "1=1", null);
    }

    // 往该表添加一条记录
    public long insert(AppInfo info) {
        ArrayList<AppInfo> infoArray = new ArrayList<AppInfo>();
        infoArray.add(info);
        return insert(infoArray);
    }

    // 往该表添加多条记录
    public long insert(ArrayList<AppInfo> infoArray) {
        long result = -1;
        for (AppInfo info : infoArray) {
            // 如果存在相同rowid的记录，则更新记录
            if (info.rowid > 0) {
                String condition = String.format("rowid='%d'", info.rowid);
                update(info, condition);
                result = info.rowid;
                continue;
            }
            // 如果存在同样日期的uid，则更新记录
            if (info.day > 0 && info.uid > 0) {
                String condition = String.format("day=%d and uid=%d", info.day, info.uid);
                ArrayList<AppInfo> tempArray = new ArrayList<AppInfo>();
                tempArray = query(condition);
                if (tempArray.size() > 0) {
                    update(info, condition);
                    result = tempArray.get(0).rowid;
                    continue;
                }
            }
            // 不存在唯一性重复的记录，则插入新记录
            ContentValues cv = new ContentValues();
            cv.put("month", info.month);
            cv.put("day", info.day);
            cv.put("uid", info.uid);
            cv.put("label", info.label);
            cv.put("package_name", info.package_name);
            cv.put("icon_path", info.icon_path);
            cv.put("traffic", info.traffic);
            // 执行插入记录动作，该语句返回插入记录的行号
            result = mDB.insert(TABLE_NAME, "", cv);
            // 添加成功后返回行号，失败后返回-1
            if (result == -1) {
                return result;
            }
        }
        return result;
    }

    // 根据条件更新指定的表记录
    public int update(AppInfo info, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("month", info.month);
        cv.put("day", info.day);
        cv.put("uid", info.uid);
        cv.put("label", info.label);
        cv.put("package_name", info.package_name);
        cv.put("icon_path", info.icon_path);
        cv.put("traffic", info.traffic);
        // 执行更新记录动作，该语句返回记录更新的数目
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    public int update(AppInfo info) {
        // 执行更新记录动作，该语句返回记录更新的数目
        return update(info, "rowid=" + info.rowid);
    }

    // 根据指定条件查询记录，并返回结果数据队列
    public ArrayList<AppInfo> query(String condition) {
        String sql = String.format("select rowid,_id,month,day,uid,label,package_name,icon_path,traffic" +
                " from %s where %s;", TABLE_NAME, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<AppInfo> infoArray = new ArrayList<AppInfo>();
        // 执行记录查询动作，该语句返回结果集的游标
        Cursor cursor = mDB.rawQuery(sql, null);
        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()) {
            AppInfo info = new AppInfo();
            info.rowid = cursor.getLong(0);
            info.xuhao = cursor.getInt(1);
            info.month = cursor.getInt(2);
            info.day = cursor.getInt(3);
            info.uid = cursor.getInt(4);
            info.label = cursor.getString(5);
            info.package_name = cursor.getString(6);
            info.icon_path = cursor.getString(7);
            info.traffic = cursor.getLong(8);
            infoArray.add(info);
        }
        cursor.close(); // 查询完毕，关闭游标
        return infoArray;
    }

    // 根据行号查询指定记录
    public AppInfo queryById(long rowid) {
        AppInfo info = null;
        ArrayList<AppInfo> infoArray = query(String.format("rowid='%d'", rowid));
        if (infoArray.size() > 0) {
            info = infoArray.get(0);
        }
        return info;
    }

}
