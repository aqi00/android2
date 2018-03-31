package com.example.senior.database;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class DbHelper extends SQLiteOpenHelper {
	protected static String TAG;
	protected Context mContext;
	protected int mVersion;
	public static String db_name = "schedule.sqlite";
	protected SQLiteDatabase mReadDB;
	protected SQLiteDatabase mWriteDB;
	protected String mTableName;
	protected String mSelectSQL;
	protected String mCreateSQL;

	public DbHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
		mVersion = version;
		mWriteDB = this.getWritableDatabase();
		mReadDB = this.getReadableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		mCreateSQL = "CREATE TABLE IF NOT EXISTS ScheduleArrange (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				"month VARCHAR NOT NULL, day VARCHAR NOT NULL," +
				"hour VARCHAR NOT NULL, minute VARCHAR NOT NULL," +
				"title VARCHAR NOT NULL, content VARCHAR NOT NULL," +
				"update_time VARCHAR, alarm_type INTEGER NOT NULL" +
				");";
		Log.d(TAG, "create_sql:" + mCreateSQL);
		db.execSQL(mCreateSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void delete(int xuhao) {
		String delete_sql = String.format("delete from %s where id=%d;", mTableName, xuhao);
		Log.d(TAG, "delete sql="+delete_sql);
		mWriteDB.execSQL(delete_sql);
	}
	
	protected List<?> queryInfo(String sql) {
		List<?> objList = new ArrayList<Object>();
		return objList;
	}

	public List<?> queryInfoById(int Id) {
		String sql = mSelectSQL + " a._id=" + Id + ";";
		return queryInfo(sql);
	}

	public int queryCount(String sql) {
		int count = 0;
		Cursor cursor = mReadDB.rawQuery(sql, null);
		count = cursor.getCount();
		cursor.close();
		Log.d(TAG, "count="+count+",sql="+sql);
		return count;
	}
}
