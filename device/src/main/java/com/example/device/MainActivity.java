package com.example.device;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.device.util.PermissionUtil;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_turn_view).setOnClickListener(this);
        findViewById(R.id.btn_turn_surface).setOnClickListener(this);
        findViewById(R.id.btn_camera_info).setOnClickListener(this);
        findViewById(R.id.btn_photograph).setOnClickListener(this);
        findViewById(R.id.btn_turn_texture).setOnClickListener(this);
        findViewById(R.id.btn_shooting).setOnClickListener(this);
        findViewById(R.id.btn_seekbar).setOnClickListener(this);
        findViewById(R.id.btn_volume).setOnClickListener(this);
        findViewById(R.id.btn_audio).setOnClickListener(this);
        findViewById(R.id.btn_video).setOnClickListener(this);
        findViewById(R.id.btn_sensor).setOnClickListener(this);
        findViewById(R.id.btn_acceleration).setOnClickListener(this);
        findViewById(R.id.btn_light).setOnClickListener(this);
        findViewById(R.id.btn_direction).setOnClickListener(this);
        findViewById(R.id.btn_step).setOnClickListener(this);
        findViewById(R.id.btn_gyroscope).setOnClickListener(this);
        findViewById(R.id.btn_location_setting).setOnClickListener(this);
        findViewById(R.id.btn_location_begin).setOnClickListener(this);
        findViewById(R.id.btn_nfc).setOnClickListener(this);
        findViewById(R.id.btn_infrared).setOnClickListener(this);
        findViewById(R.id.btn_bluetooth).setOnClickListener(this);
        findViewById(R.id.btn_navigation).setOnClickListener(this);
        findViewById(R.id.btn_wechat).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_turn_view) {
            Intent intent = new Intent(this, TurnViewActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_turn_surface) {
            Intent intent = new Intent(this, TurnSurfaceActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_camera_info) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.CAMERA, R.id.btn_camera_info % 4096)) {
                PermissionUtil.goActivity(this, CameraInfoActivity.class);
            }
        } else if (v.getId() == R.id.btn_photograph) {
             if (PermissionUtil.checkPermission(this, Manifest.permission.CAMERA, R.id.btn_photograph % 4096)) {
                 PermissionUtil.goActivity(this, PhotographActivity.class);
             }
        } else if (v.getId() == R.id.btn_turn_texture) {
            Intent intent = new Intent(this, TurnTextureActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_shooting) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(MainActivity.this, "Andorid版本低于5.0无法使用camera2",
                        Toast.LENGTH_SHORT).show();
            } else if (PermissionUtil.checkPermission(this, Manifest.permission.CAMERA, R.id.btn_shooting % 4096)) {
                PermissionUtil.goActivity(this, ShootingActivity.class);
            }
        } else if (v.getId() == R.id.btn_seekbar) {
            Intent intent = new Intent(this, SeekbarActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_volume) {
            Intent intent = new Intent(this, VolumeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_audio) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.RECORD_AUDIO, R.id.btn_audio % 4096)) {
                PermissionUtil.goActivity(this, AudioActivity.class);
            }
        } else if (v.getId() == R.id.btn_video) {
            if (PermissionUtil.checkMultiPermission(this, new String[] {
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, R.id.btn_video % 4096)) {
                PermissionUtil.goActivity(this, VideoActivity.class);
            }
        } else if (v.getId() == R.id.btn_sensor) {
            Intent intent = new Intent(this, SensorActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_acceleration) {
            Intent intent = new Intent(this, AccelerationActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_light) {
            Intent intent = new Intent(this, LightActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_direction) {
            Intent intent = new Intent(this, DirectionActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_step) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Toast.makeText(MainActivity.this, "计步器需要Android4.4或以上版本",
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, StepActivity.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.btn_gyroscope) {
            Intent intent = new Intent(this, GyroscopeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_location_setting) {
            Intent intent = new Intent(this, LocationSettingActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_location_begin) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_location_begin % 4096)) {
                PermissionUtil.goActivity(this, LocationActivity.class);
            }
        } else if (v.getId() == R.id.btn_nfc) {
            Intent intent = new Intent(this, NfcActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_infrared) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Toast.makeText(MainActivity.this, "红外遥控需要Android4.4或以上版本",
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, InfraredActivity.class);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.btn_bluetooth) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_bluetooth % 4096)) {
                    PermissionUtil.goActivity(this, BluetoothActivity.class);
                }
            } else {
                PermissionUtil.goActivity(this, BluetoothActivity.class);
            }
        } else if (v.getId() == R.id.btn_navigation) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_navigation % 4096)) {
                PermissionUtil.goActivity(this, NavigationActivity.class);
            }
        } else if (v.getId() == R.id.btn_wechat) {
            Intent intent = new Intent(this, WeChatActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == R.id.btn_camera_info % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, CameraInfoActivity.class);
            } else {
                Toast.makeText(this, "需要允许相机权限才能查看相机信息噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_photograph % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, PhotographActivity.class);
            } else {
                Toast.makeText(this, "需要允许相机权限才能拍照噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_shooting % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, ShootingActivity.class);
            } else {
                Toast.makeText(this, "需要允许相机权限才能拍照噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_audio % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, AudioActivity.class);
            } else {
                Toast.makeText(this, "需要允许录音权限才能录音噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_video % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, VideoActivity.class);
            } else {
                Toast.makeText(this, "需要同时允许拍照和录音权限才能录像噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_location_begin % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, LocationActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能开始定位噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_bluetooth % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, BluetoothActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用蓝牙噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_navigation % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, NavigationActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能查看导航噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
