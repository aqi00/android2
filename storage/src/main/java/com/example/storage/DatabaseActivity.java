package com.example.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2018/4/22.
 */

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_database;
    private String mDatabaseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        tv_database = findViewById(R.id.tv_database);
        findViewById(R.id.btn_database_create).setOnClickListener(this);
        findViewById(R.id.btn_database_delete).setOnClickListener(this);
        // 生成一个测试数据库的完整路径
        mDatabaseName = getFilesDir() + "/test.db";
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_database_create) {
            // 创建或打开数据库。数据库如果不存在就创建它，如果存在就打开它
            SQLiteDatabase db = openOrCreateDatabase(mDatabaseName, Context.MODE_PRIVATE, null);
            String desc = String.format("数据库%s创建%s", mDatabaseName, (db!=null)?"成功":"失败");
            tv_database.setText(desc);
        } else if (v.getId() == R.id.btn_database_delete) {
            // 删除数据库
            boolean result = deleteDatabase(mDatabaseName);
            String desc = String.format("数据库%s删除%s", mDatabaseName, result?"成功":"失败");
            tv_database.setText(desc);
        }
    }
}
