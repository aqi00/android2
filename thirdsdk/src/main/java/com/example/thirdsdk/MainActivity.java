package com.example.thirdsdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.thirdsdk.util.PermissionUtil;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_map_baidu).setOnClickListener(this);
        findViewById(R.id.btn_map_gaode).setOnClickListener(this);
        findViewById(R.id.btn_share_qq).setOnClickListener(this);
        findViewById(R.id.btn_share_wx).setOnClickListener(this);
        findViewById(R.id.btn_alipay).setOnClickListener(this);
        findViewById(R.id.btn_tts_language).setOnClickListener(this);
        findViewById(R.id.btn_tts_read).setOnClickListener(this);
        findViewById(R.id.btn_voice_recognize).setOnClickListener(this);
        findViewById(R.id.btn_voice_compose).setOnClickListener(this);
        findViewById(R.id.btn_rating_bar).setOnClickListener(this);
        findViewById(R.id.btn_take_tax).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_map_baidu) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_map_baidu % 4096)) {
                PermissionUtil.goActivity(this, MapBaiduActivity.class);
            }
        } else if (v.getId() == R.id.btn_map_gaode) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_map_gaode % 4096)) {
                PermissionUtil.goActivity(this, MapGaodeActivity.class);
            }
        } else if (v.getId() == R.id.btn_share_qq) {
            Intent intent = new Intent(this, ShareQQActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_share_wx) {
            Toast.makeText(this, "微信分享请移步weixin模块", Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.btn_alipay) {
            Intent intent = new Intent(this, AlipayActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_wxpay) {
            Toast.makeText(this, "微信支付请移步weixin模块", Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.btn_tts_language) {
            Intent intent = new Intent(this, TtsLanguageActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_tts_read) {
            Intent intent = new Intent(this, TtsReadActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_voice_recognize) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.RECORD_AUDIO, R.id.btn_voice_recognize % 4096)) {
                PermissionUtil.goActivity(this, VoiceRecognizeActivity.class);
            }
        } else if (v.getId() == R.id.btn_voice_compose) {
            Intent intent = new Intent(this, VoiceComposeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_rating_bar) {
            Intent intent = new Intent(this, RatingBarActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_take_tax) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, R.id.btn_take_tax % 4096)) {
                PermissionUtil.goActivity(this, TakeTaxActivity.class);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == R.id.btn_map_baidu % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, MapBaiduActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用百度地图噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_map_gaode % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, MapGaodeActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用高德地图噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_voice_recognize % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, VoiceRecognizeActivity.class);
            } else {
                Toast.makeText(this, "需要允许录音权限才能识别语音噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_take_tax % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, TakeTaxActivity.class);
            } else {
                Toast.makeText(this, "需要允许定位权限才能打车噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
