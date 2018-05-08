package com.example.thirdsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

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
        findViewById(R.id.btn_wxpay).setOnClickListener(this);
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
            Intent intent = new Intent(this, MapBaiduActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_map_gaode) {
            Intent intent = new Intent(this, MapGaodeActivity.class);
            startActivity(intent);
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
            Intent intent = new Intent(this, VoiceRecognizeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_voice_compose) {
            Intent intent = new Intent(this, VoiceComposeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_rating_bar) {
            Intent intent = new Intent(this, RatingBarActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_take_tax) {
            Intent intent = new Intent(this, TakeTaxActivity.class);
            startActivity(intent);
        }
    }

}
