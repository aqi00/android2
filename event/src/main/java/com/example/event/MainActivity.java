package com.example.event;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.event.util.PermissionUtil;

/**
 * Created by ouyangshen on 2017/11/23.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_key_soft).setOnClickListener(this);
        findViewById(R.id.btn_key_hard).setOnClickListener(this);
        findViewById(R.id.btn_volume_set).setOnClickListener(this);
        findViewById(R.id.btn_gesture_detector).setOnClickListener(this);
        findViewById(R.id.btn_view_flipper).setOnClickListener(this);
        findViewById(R.id.btn_banner_flipper).setOnClickListener(this);
        findViewById(R.id.btn_event_dispatch).setOnClickListener(this);
        findViewById(R.id.btn_event_intercept).setOnClickListener(this);
        findViewById(R.id.btn_touch_single).setOnClickListener(this);
        findViewById(R.id.btn_touch_multiple).setOnClickListener(this);
        findViewById(R.id.btn_signature).setOnClickListener(this);
        findViewById(R.id.btn_custom_scroll).setOnClickListener(this);
        findViewById(R.id.btn_disallow_scroll).setOnClickListener(this);
        findViewById(R.id.btn_drawer_layout).setOnClickListener(this);
        findViewById(R.id.btn_pull_refresh).setOnClickListener(this);
        findViewById(R.id.btn_image_change).setOnClickListener(this);
        findViewById(R.id.btn_image_cut).setOnClickListener(this);
        findViewById(R.id.btn_meitu).setOnClickListener(this);
        findViewById(R.id.btn_gl_line).setOnClickListener(this);
        findViewById(R.id.btn_gl_globe).setOnClickListener(this);
        findViewById(R.id.btn_gl_panorama).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_key_soft) {
            Intent intent = new Intent(this, KeySoftActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_key_hard) {
            Intent intent = new Intent(this, KeyHardActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_volume_set) {
            Intent intent = new Intent(this, VolumeSetActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_gesture_detector) {
            Intent intent = new Intent(this, GestureDetectorActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_view_flipper) {
            Intent intent = new Intent(this, ViewFlipperActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_banner_flipper) {
            Intent intent = new Intent(this, BannerFlipperActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_event_dispatch) {
            Intent intent = new Intent(this, EventDispatchActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_event_intercept) {
            Intent intent = new Intent(this, EventInterceptActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_touch_single) {
            Intent intent = new Intent(this, TouchSingleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_touch_multiple) {
            Intent intent = new Intent(this, TouchMultipleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_signature) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_signature % 4096)) {
                PermissionUtil.goActivity(this, SignatureActivity.class);
            }
        } else if (v.getId() == R.id.btn_custom_scroll) {
            Intent intent = new Intent(this, CustomScrollActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_disallow_scroll) {
            Intent intent = new Intent(this, DisallowScrollActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_drawer_layout) {
            Intent intent = new Intent(this, DrawerLayoutActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_pull_refresh) {
            Intent intent = new Intent(this, PullRefreshActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_image_change) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_image_change % 4096)) {
                PermissionUtil.goActivity(this, ImageChangeActivity.class);
            }
        } else if (v.getId() == R.id.btn_image_cut) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_image_cut % 4096)) {
                PermissionUtil.goActivity(this, ImageCutActivity.class);
            }
        } else if (v.getId() == R.id.btn_meitu) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_meitu % 4096)) {
                PermissionUtil.goActivity(this, MeituActivity.class);
            }
        } else if (v.getId() == R.id.btn_gl_line) {
            Intent intent = new Intent(this, GlLineActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_gl_globe) {
            Intent intent = new Intent(this, GlGlobeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_gl_panorama) {
            Intent intent = new Intent(this, GlPanoramaActivity.class);
            startActivity(intent);
        }
    }

    private boolean needExit = false; // 是否需要退出App

    // 在按下返回键时触发
//    public void onBackPressed() {
//        if (needExit) {
//            finish(); // 关闭当前页面
//            return;
//        }
//        needExit = true;
//        Toast.makeText(this, "再按一次返回键退出!", Toast.LENGTH_SHORT).show();
//    }

    // 在发生物理按键动作时触发
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 按下返回键
            if (needExit) {
                finish(); // 关闭当前页面
            }
            needExit = true;
            Toast.makeText(this, "再按一次返回键退出!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == R.id.btn_signature % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, SignatureActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能保存签名文件噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_image_change % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, ImageChangeActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能处理图片文件噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_image_cut % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, ImageCutActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能处理图片文件噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_meitu % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, MeituActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能处理图片文件噢", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
