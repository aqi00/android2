package com.example.senior.database;

import java.util.ArrayList;
import java.util.List;

import com.example.senior.bean.ScheduleArrange;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class ScheduleArrangeHelper extends DbHelper {

    public ScheduleArrangeHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        TAG = "ScheduleArrangeHelper";
        mTableName = "ScheduleArrange";
        mSelectSQL = String.format("select _id,month,day,hour,minute,title,content,update_time,alarm_type from %s where "
                , mTableName);
    }

    public boolean add(ScheduleArrange data) {
        List<ScheduleArrange> data_list = new ArrayList<ScheduleArrange>();
        data_list.add(data);
        return add(data_list);
    }

    public boolean add(List<ScheduleArrange> dataList) {
        for (ScheduleArrange data : dataList) {
            // ContentValues对象
            ContentValues cv = new ContentValues();
            cv.put("month", data.month);
            cv.put("day", data.day);
            cv.put("hour", data.hour);
            cv.put("minute", data.minute);
            cv.put("title", data.title);
            cv.put("content", data.content);
            cv.put("update_time", data.update_time);
            cv.put("alarm_type", data.alarm_type);

            Log.d(TAG, "cv.toString():" + cv.toString());
            if (getCount(data.day) <= 0) {
                long result = mWriteDB.insert(mTableName, "", cv);
                // 添加成功后返回行号，失败后返回-1
                if (result == -1) {
                    // 失败
                    return false;
                }
            } else {
                update(data);
            }
        }
        return true;
    }

    public void update(ScheduleArrange data) {
        String update_sql;
        update_sql = String.format("update %s set month='%s', day='%s', hour='%s', minute='%s', " +
                        "title='%s', content='%s', update_time='%s', alarm_type='%d' where ",
                mTableName, data.month, data.day, data.hour, data.minute,
                data.title, data.content, data.update_time, data.alarm_type);
        if (data.xuhao > 0) {
            update_sql = String.format("%s _id=%d;", update_sql, data.xuhao);
        } else {
            update_sql = String.format("%s day='%s';", update_sql, data.day);
        }
        Log.d(TAG, "update_sql:" + update_sql);
        mWriteDB.execSQL(update_sql);
    }

    protected List<?> queryInfo(String sql) {
        Log.d(TAG, "begin moveToFirst:" + sql);
        List<ScheduleArrange> data_list = new ArrayList<ScheduleArrange>();
        Cursor cursor = mReadDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            ScheduleArrange data_info = new ScheduleArrange();
            data_info.xuhao = cursor.getInt(0);
            data_info.month = cursor.getString(1);
            data_info.day = cursor.getString(2);
            data_info.hour = cursor.getString(3);
            data_info.minute = cursor.getString(4);
            data_info.title = cursor.getString(5);
            data_info.content = cursor.getString(6);
            data_info.update_time = cursor.getString(7);
            data_info.alarm_type = cursor.getInt(8);

            data_list.add(data_info);
            data_info = new ScheduleArrange();
        }
        Log.d(TAG, "end query_info");
        cursor.close();
        return data_list;
    }

    public int getCount(String day) {
        String sql = String.format("%s day='%s';", mSelectSQL, day);
        return queryCount(sql);
    }

    public List<?> queryInfoByDay(String day) {
        String sql = String.format("%s day='%s';", mSelectSQL, day);
        return queryInfo(sql);
    }

    public List<?> queryInfoByMonth(String month) {
        String sql = String.format("%s month='%s';", mSelectSQL, month);
        return queryInfo(sql);
    }

    public List<?> queryInfoByDayRange(String begin_day, String end_day) {
        String sql = String.format("%s day>='%s' and day<='%s';", mSelectSQL, begin_day, end_day);
        return queryInfo(sql);
    }

}
