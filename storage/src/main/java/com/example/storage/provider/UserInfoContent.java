package com.example.storage.provider;

import com.example.storage.database.UserDBHelper;

import android.net.Uri;
import android.provider.BaseColumns;

public class UserInfoContent implements BaseColumns {
    // 这里的名称必须与AndroidManifest.xml里的android:authorities保持一致
    public static final String AUTHORITIES = "com.example.storage.provider.UserInfoProvider";
    // 表名
    public static final String TABLE_NAME = UserDBHelper.TABLE_NAME;
    // 访问该内容提供器的URI
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/user");
    //	// 该内容提供器返回的数据类型定义
//	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myprovider.user";
//	public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.myprovider.user";
    // 下面是该表的各个字段名称
    public static final String USER_NAME = "name";
    public static final String USER_AGE = "age";
    public static final String USER_HEIGHT = "height";
    public static final String USER_WEIGHT = "weight";
    public static final String USER_MARRIED = "married";
    // 默认的排序方法
    public static final String DEFAULT_SORT_ORDER = "_id desc";
}
