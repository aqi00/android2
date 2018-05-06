package com.example.weixin;

import net.sourceforge.simcpux.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_share_wx).setOnClickListener(this);
        findViewById(R.id.btn_wxpay).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_share_wx) {
            Intent intent = new Intent(this, ShareWXActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_wxpay) {
            Intent intent = new Intent(this, WxpayActivity.class);
            startActivity(intent);
        }
    }
}
