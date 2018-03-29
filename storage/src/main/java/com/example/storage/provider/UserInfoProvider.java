package com.example.storage.provider;

import com.example.storage.database.UserDBHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class UserInfoProvider extends ContentProvider {
    private final static String TAG = "UserInfoProvider";
    private UserDBHelper userDB; // 声明一个用户数据库的帮助器对象
    public static final int USER_INFO = 1; // Uri匹配时的代号
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static { // 往Uri匹配器中添加指定的数据路径
        uriMatcher.addURI(UserInfoContent.AUTHORITIES, "/user", USER_INFO);
    }

    // 根据指定条件删除数据
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        if (uriMatcher.match(uri) == USER_INFO) {
            // 获取SQLite数据库的写连接
            SQLiteDatabase db = userDB.getWritableDatabase();
            // 执行SQLite的删除操作，返回删除记录的数目
            count = db.delete(UserInfoContent.TABLE_NAME, selection, selectionArgs);
            db.close(); // 关闭SQLite数据库连接
        }
        return count;
    }

    // 插入数据
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newUri = uri;
        if (uriMatcher.match(uri) == USER_INFO) {
            // 获取SQLite数据库的写连接
            SQLiteDatabase db = userDB.getWritableDatabase();
            // 向指定的表插入数据，返回记录的行号
            long rowId = db.insert(UserInfoContent.TABLE_NAME, null, values);
            if (rowId > 0) { // 判断插入是否执行成功
                // 如果添加成功，利用新记录的行号生成新的地址
                newUri = ContentUris.withAppendedId(UserInfoContent.CONTENT_URI, rowId);
                // 通知监听器，数据已经改变
                getContext().getContentResolver().notifyChange(newUri, null);
            }
            db.close(); // 关闭SQLite数据库连接
        }
        return uri;
    }

    // 创建ContentProvider时调用，可在此获取具体的数据库帮助器实例
    @Override
    public boolean onCreate() {
        userDB = UserDBHelper.getInstance(getContext(), 1);
        return false;
    }

    // 根据指定条件查询数据库
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        if (uriMatcher.match(uri) == USER_INFO) {
            // 获取SQLite数据库的读连接
            SQLiteDatabase db = userDB.getReadableDatabase();
            // 执行SQLite的查询操作
            cursor = db.query(UserInfoContent.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder);
            // 设置内容解析器的监听
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    // 获取Uri数据的访问类型，暂未实现
    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // 更新数据，暂未实现
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
