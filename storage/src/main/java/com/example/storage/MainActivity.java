package com.example.storage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.storage.util.PermissionUtil;

/**
 * Created by ouyangshen on 2017/10/1.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_share_write).setOnClickListener(this);
        findViewById(R.id.btn_share_read).setOnClickListener(this);
        findViewById(R.id.btn_login_share).setOnClickListener(this);
        findViewById(R.id.btn_sqlite_create).setOnClickListener(this);
        findViewById(R.id.btn_sqlite_write).setOnClickListener(this);
        findViewById(R.id.btn_sqlite_read).setOnClickListener(this);
        findViewById(R.id.btn_login_sqlite).setOnClickListener(this);
        findViewById(R.id.btn_file_basic).setOnClickListener(this);
        findViewById(R.id.btn_file_path).setOnClickListener(this);
        findViewById(R.id.btn_text_write).setOnClickListener(this);
        findViewById(R.id.btn_text_read).setOnClickListener(this);
        findViewById(R.id.btn_image_write).setOnClickListener(this);
        findViewById(R.id.btn_image_read).setOnClickListener(this);
        findViewById(R.id.btn_app_life).setOnClickListener(this);
        findViewById(R.id.btn_app_write).setOnClickListener(this);
        findViewById(R.id.btn_app_read).setOnClickListener(this);
        findViewById(R.id.btn_content_provider).setOnClickListener(this);
        findViewById(R.id.btn_content_resolver).setOnClickListener(this);
        findViewById(R.id.btn_content_observer).setOnClickListener(this);
        findViewById(R.id.btn_menu_option).setOnClickListener(this);
        findViewById(R.id.btn_menu_context).setOnClickListener(this);
        findViewById(R.id.btn_shopping_cart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_share_write) {
            Intent intent = new Intent(this, ShareWriteActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_share_read) {
            Intent intent = new Intent(this, ShareReadActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_login_share) {
            Intent intent = new Intent(this, LoginShareActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_sqlite_create) {
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_sqlite_write) {
            Intent intent = new Intent(this, SQLiteWriteActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_sqlite_read) {
            Intent intent = new Intent(this, SQLiteReadActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_login_sqlite) {
            Intent intent = new Intent(this, LoginSQLiteActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_file_basic) {
            Intent intent = new Intent(this, FileBasicActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_file_path) {
            Intent intent = new Intent(this, FilePathActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_text_write) {
            Intent intent = new Intent(this, TextWriteActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_text_read) {
            Intent intent = new Intent(this, TextReadActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_image_write) {
            Intent intent = new Intent(this, ImageWriteActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_image_read) {
            Intent intent = new Intent(this, ImageReadActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_app_life) {
            Intent intent = new Intent(this, ActJumpActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_app_write) {
            Intent intent = new Intent(this, AppWriteActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_app_read) {
            Intent intent = new Intent(this, AppReadActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_content_provider) {
            Intent intent = new Intent(this, ContentProviderActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_content_resolver) {
            if (PermissionUtil.checkMultiPermission(this, new String[] {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, R.id.btn_content_resolver % 65536)) {
                startActivity(new Intent(this, ContentResolverActivity.class));
            }
        } else if (v.getId() == R.id.btn_content_observer) {
            if (PermissionUtil.checkMultiPermission(this, new String[] {Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, R.id.btn_content_observer % 65536)) {
                startActivity(new Intent(this, ContentObserverActivity.class));
            }
        } else if (v.getId() == R.id.btn_menu_option) {
            Intent intent = new Intent(this, MenuOptionActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_menu_context) {
            Intent intent = new Intent(this, MenuContextActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_shopping_cart) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == R.id.btn_content_resolver % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, ContentResolverActivity.class));
            } else {
                Toast.makeText(this, "需要允许通讯录权限才能读写联系人噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_content_observer % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, ContentObserverActivity.class));
            } else {
                Toast.makeText(this, "需要允许短信权限才能校准流量噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
