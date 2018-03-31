package com.example.senior.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.senior.bean.GoodsInfo;

import java.util.ArrayList;

@SuppressLint("DefaultLocale")
public class GoodsDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "GoodsDBHelper";
    private static final String DB_NAME = "goods.db"; // 数据库的名称
    private static final int DB_VERSION = 1; // 数据库的版本号
    private static GoodsDBHelper mHelper = null; // 数据库帮助器的实例
    private SQLiteDatabase mDB = null; // 数据库的实例
    private static final String TABLE_NAME = "goods_info"; // 表的名称

    private GoodsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private GoodsDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    // 利用单例模式获取数据库帮助器的唯一实例
    public static GoodsDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new GoodsDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new GoodsDBHelper(context);
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
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "name VARCHAR NOT NULL," + "desc VARCHAR NOT NULL,"
                + "price FLOAT NOT NULL," + "thumb_path VARCHAR NOT NULL,"
                + "pic_path VARCHAR NOT NULL"
                + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    // 修改数据库，执行表结构变更语句
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

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
    public long insert(GoodsInfo info) {
        ArrayList<GoodsInfo> infoArray = new ArrayList<GoodsInfo>();
        infoArray.add(info);
        return insert(infoArray);
    }

    // 往该表添加多条记录
    public long insert(ArrayList<GoodsInfo> infoArray) {
        long result = -1;
        for (GoodsInfo info : infoArray) {
            // 如果存在相同rowid的记录，则更新记录
            if (info.rowid > 0) {
                String condition = String.format("rowid='%d'", info.rowid);
                update(info, condition);
                result = info.rowid;
                continue;
            }
            // 不存在唯一性重复的记录，则插入新记录
            ContentValues cv = new ContentValues();
            cv.put("name", info.name);
            cv.put("desc", info.desc);
            cv.put("price", info.price);
            cv.put("thumb_path", info.thumb_path);
            cv.put("pic_path", info.pic_path);
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
    public int update(GoodsInfo info, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("name", info.name);
        cv.put("desc", info.desc);
        cv.put("price", info.price);
        cv.put("thumb_path", info.thumb_path);
        cv.put("pic_path", info.pic_path);
        // 执行更新记录动作，该语句返回记录更新的数目
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    public int update(GoodsInfo info) {
        // 执行更新记录动作，该语句返回记录更新的数目
        return update(info, "rowid=" + info.rowid);
    }

    // 根据指定条件查询记录，并返回结果数据队列
    public ArrayList<GoodsInfo> query(String condition) {
        String sql = String.format("select rowid,_id,name,desc,price,thumb_path,pic_path" +
                " from %s where %s;", TABLE_NAME, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<GoodsInfo> infoArray = new ArrayList<GoodsInfo>();
        // 执行记录查询动作，该语句返回结果集的游标
        Cursor cursor = mDB.rawQuery(sql, null);
        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()) {
            GoodsInfo info = new GoodsInfo();
            info.rowid = cursor.getLong(0);
            info.xuhao = cursor.getInt(1);
            info.name = cursor.getString(2);
            info.desc = cursor.getString(3);
            info.price = cursor.getFloat(4);
            info.thumb_path = cursor.getString(5);
            info.pic_path = cursor.getString(6);
            infoArray.add(info);
        }
        cursor.close(); // 查询完毕，关闭游标
        return infoArray;
    }

    // 根据行号查询指定记录
    public GoodsInfo queryById(long rowid) {
        GoodsInfo info = null;
        ArrayList<GoodsInfo> infoArray = query(String.format("rowid='%d'", rowid));
        if (infoArray.size() > 0) {
            info = infoArray.get(0);
        }
        return info;
    }

}
