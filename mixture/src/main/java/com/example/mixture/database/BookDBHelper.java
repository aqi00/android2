package com.example.mixture.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.mixture.bean.BookInfo;

import java.util.ArrayList;

/**
 * Created by ouyangshen on 2018/1/9.
 */
public class BookDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "BookDBHelper";
    private static final String DB_NAME = "book.db";
    private static final int DB_VERSION = 1;
    private static BookDBHelper mHelper = null;
    private SQLiteDatabase mDB = null;
    private static final String TABLE_NAME = "book_info";

    private BookDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private BookDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    public static BookDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new BookDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new BookDBHelper(context);
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
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL ,"
                + "title VARCHAR NOT NULL," + "author VARCHAR NOT NULL,"
                + "path VARCHAR NOT NULL," + "page_number INTEGER NOT NULL,"
                + "size LONG NOT NULL" + ")";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        onCreate(db);
    }

    public int delete(String condition) {
        return mDB.delete(TABLE_NAME, condition, null);
    }

    public long insert(BookInfo info) {
        ArrayList<BookInfo> infoArray = new ArrayList<BookInfo>();
        infoArray.add(info);
        return insert(infoArray);
    }

    public long insert(ArrayList<BookInfo> infoArray) {
        long result = -1;
        for (BookInfo info : infoArray) {
            ArrayList<BookInfo> tempArray = new ArrayList<BookInfo>();
            // 如果存在同名记录，则更新记录
            // 注意条件语句的等号后面要用单引号括起来
            if (!TextUtils.isEmpty(info.path)) {
                String condition = String.format("path='%s'", info.path);
                tempArray = query(condition);
                if (tempArray.size() > 0) {
                    continue;
                }
            }
            // 不存在唯一性重复的记录，则插入新记录
            ContentValues cv = new ContentValues();
            cv.put("title", info.title);
            cv.put("author", info.author);
            cv.put("path", info.path);
            cv.put("page_number", info.page_number);
            cv.put("size", info.size);
            result = mDB.insert(TABLE_NAME, "", cv);
            // 添加成功后返回行号，失败后返回-1
            if (result == -1) {
                return result;
            }
        }
        return result;
    }

    public int update(BookInfo info, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("title", info.title);
        cv.put("author", info.author);
        cv.put("path", info.path);
        cv.put("page_number", info.page_number);
        cv.put("size", info.size);
        return mDB.update(TABLE_NAME, cv, condition, null);
    }

    public int update(BookInfo info) {
        return update(info, "_id=" + info.id);
    }

    public ArrayList<BookInfo> query(String condition) {
        String sql = String.format("select _id,title,author,path,page_number,size" +
                " from %s where %s;", TABLE_NAME, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<BookInfo> infoArray = new ArrayList<BookInfo>();
        Cursor cursor = mDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            BookInfo info = new BookInfo();
            info.id = cursor.getInt(0);
            info.title = cursor.getString(1);
            info.author = cursor.getString(2);
            info.path = cursor.getString(3);
            info.page_number = cursor.getInt(4);
            info.size = cursor.getLong(5);
            infoArray.add(info);
        }
        cursor.close();
        return infoArray;
    }

    public BookInfo queryByPath(String path) {
        BookInfo info = null;
        ArrayList<BookInfo> infoArray = query(String.format("path='%s'", path));
        if (infoArray.size() > 0) {
            info = infoArray.get(0);
        }
        return info;
    }

}
