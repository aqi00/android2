package com.example.network;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.network.util.PermissionUtil;

/**
 * Created by ouyangshen on 2017/11/11.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_message).setOnClickListener(this);
        findViewById(R.id.btn_progress_dialog).setOnClickListener(this);
        findViewById(R.id.btn_progress_text).setOnClickListener(this);
        findViewById(R.id.btn_progress_circle).setOnClickListener(this);
        findViewById(R.id.btn_async_task).setOnClickListener(this);
        findViewById(R.id.btn_intent_service).setOnClickListener(this);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_json_parse).setOnClickListener(this);
        findViewById(R.id.btn_json_convert).setOnClickListener(this);
        findViewById(R.id.btn_http_request).setOnClickListener(this);
        findViewById(R.id.btn_http_image).setOnClickListener(this);
        findViewById(R.id.btn_download_apk).setOnClickListener(this);
        findViewById(R.id.btn_download_image).setOnClickListener(this);
        findViewById(R.id.btn_file_save).setOnClickListener(this);
        findViewById(R.id.btn_file_select).setOnClickListener(this);
        findViewById(R.id.btn_upload_http).setOnClickListener(this);
        findViewById(R.id.btn_net_address).setOnClickListener(this);
        findViewById(R.id.btn_socket).setOnClickListener(this);
        findViewById(R.id.btn_apk_info).setOnClickListener(this);
        findViewById(R.id.btn_app_store).setOnClickListener(this);
        findViewById(R.id.btn_fold_list).setOnClickListener(this);
        findViewById(R.id.btn_qqchat).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_message) {
            Intent intent = new Intent(this, MessageActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_progress_dialog) {
            Intent intent = new Intent(this, ProgressDialogActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_progress_text) {
            Intent intent = new Intent(this, ProgressTextActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_progress_circle) {
            Intent intent = new Intent(this, ProgressCircleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_async_task) {
            Intent intent = new Intent(this, AsyncTaskActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_intent_service) {
            Intent intent = new Intent(this, IntentServiceActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_connect) {
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_json_parse) {
            Intent intent = new Intent(this, JsonParseActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_json_convert) {
            Intent intent = new Intent(this, JsonConvertActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_http_request) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_http_request % 4096)) {
                PermissionUtil.goActivity(this, HttpRequestActivity.class);
            }
        } else if (v.getId() == R.id.btn_http_image) {
            Intent intent = new Intent(this, HttpImageActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_download_apk) {
            Intent intent = new Intent(this, DownloadApkActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_download_image) {
            Intent intent = new Intent(this, DownloadImageActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_file_save) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_file_save % 4096)) {
                PermissionUtil.goActivity(this, FileSaveActivity.class);
            }
        } else if (v.getId() == R.id.btn_file_select) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_file_select % 4096)) {
                PermissionUtil.goActivity(this, FileSelectActivity.class);
            }
        } else if (v.getId() == R.id.btn_upload_http) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_upload_http % 4096)) {
                PermissionUtil.goActivity(this, UploadHttpActivity.class);
            }
        } else if (v.getId() == R.id.btn_net_address) {
            Intent intent = new Intent(this, NetAddressActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_socket) {
            Intent intent = new Intent(this, SocketActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_apk_info) {
            Intent intent = new Intent(this, ApkInfoActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_app_store) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_app_store % 4096)) {
                PermissionUtil.goActivity(this, AppStoreActivity.class);
            }
        } else if (v.getId() == R.id.btn_fold_list) {
            Intent intent = new Intent(this, FoldListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_qqchat) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_qqchat % 4096)) {
                PermissionUtil.goActivity(this, QQLoginActivity.class);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == R.id.btn_http_request % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, HttpRequestActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能开始定位噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_file_save % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, FileSaveActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能保存文件噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_file_select % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, FileSelectActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能打开文件噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_upload_http % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, UploadHttpActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能上传文件噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_app_store % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, AppStoreActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能升级应用噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_qqchat % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, QQLoginActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能开始聊天噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
