package com.example.mixture;

import com.example.mixture.service.ImportDeviceService;
import com.example.mixture.util.PermissionUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/11.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_assets_text).setOnClickListener(this);
        findViewById(R.id.btn_assets_image).setOnClickListener(this);
        findViewById(R.id.btn_web_local).setOnClickListener(this);
        findViewById(R.id.btn_web_span).setOnClickListener(this);
        findViewById(R.id.btn_web_browser).setOnClickListener(this);
        findViewById(R.id.btn_web_script).setOnClickListener(this);
        findViewById(R.id.btn_jni_cpu).setOnClickListener(this);
        findViewById(R.id.btn_jni_secret).setOnClickListener(this);
        findViewById(R.id.btn_wifi_info).setOnClickListener(this);
        findViewById(R.id.btn_wifi_connect).setOnClickListener(this);
        findViewById(R.id.btn_wifi_ap).setOnClickListener(this);
        findViewById(R.id.btn_bluetooth_trans).setOnClickListener(this);
        findViewById(R.id.btn_netbios).setOnClickListener(this);
        findViewById(R.id.btn_wifi_share).setOnClickListener(this);
        findViewById(R.id.btn_pdf_render).setOnClickListener(this);
        findViewById(R.id.btn_ebook_reader).setOnClickListener(this);
        mHandler.postDelayed(mImportService, 100);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_assets_text) {
            Intent intent = new Intent(this, AssetsTextActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_assets_image) {
            Intent intent = new Intent(this, AssetsImageActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_web_local) {
            Intent intent = new Intent(this, WebLocalActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_web_span) {
            Intent intent = new Intent(this, WebSpanActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_web_browser) {
            Intent intent = new Intent(this, WebBrowserActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_web_script) {
            Intent intent = new Intent(this, WebScriptActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_jni_cpu) {
            Intent intent = new Intent(this, JniCpuActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_jni_secret) {
            Intent intent = new Intent(this, JniSecretActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_wifi_info) {
            Intent intent = new Intent(this, WifiInfoActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_wifi_connect) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android6.0之后查看WIFI需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_wifi_connect % 4096)) {
                    PermissionUtil.goActivity(this, WifiConnectActivity.class);
                }
            } else {
                PermissionUtil.goActivity(this, WifiConnectActivity.class);
            }
        } else if (v.getId() == R.id.btn_wifi_ap) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android8.0之后开关热点需要定位权限。Android9.0之后读取序列号需要号码权限
                if (PermissionUtil.checkMultiPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, R.id.btn_wifi_connect % 4096)) {
                    PermissionUtil.goActivity(this, WifiApActivity.class);
                }
            } else {
                PermissionUtil.goActivity(this, WifiApActivity.class);
            }
        } else if (v.getId() == R.id.btn_bluetooth_trans) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_bluetooth_trans % 4096)) {
                    PermissionUtil.goActivity(this, BluetoothTransActivity.class);
                }
            } else {
                PermissionUtil.goActivity(this, BluetoothTransActivity.class);
            }
        } else if (v.getId() == R.id.btn_netbios) {
            Intent intent = new Intent(this, NetbiosActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_wifi_share) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Toast.makeText(this, "Android8.0及以上版本不允许普通应用开关热点", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, WifiShareActivity.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.btn_pdf_render) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(this, "PDF渲染器需要Android5.0及以上版本", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_pdf_render % 4096)) {
                PermissionUtil.goActivity(this, PdfRenderActivity.class);
            }
        } else if (v.getId() == R.id.btn_ebook_reader) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_ebook_reader % 4096)) {
                PermissionUtil.goActivity(this, EbookReaderActivity.class);
            }
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mImportService = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(MainActivity.this, ImportDeviceService.class);
            startService(intent);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == R.id.btn_wifi_connect % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, WifiConnectActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能查看WIFI噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_wifi_ap % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, WifiApActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能开关热点噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_bluetooth_trans % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, BluetoothTransActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用蓝牙噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_pdf_render % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, PdfRenderActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能阅读PDF文件噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_ebook_reader % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, EbookReaderActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能阅读电子书噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
