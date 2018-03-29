package com.example.junior;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_px).setOnClickListener(this);
        findViewById(R.id.btn_color).setOnClickListener(this);
        findViewById(R.id.btn_screen).setOnClickListener(this);
        findViewById(R.id.btn_margin).setOnClickListener(this);
        findViewById(R.id.btn_gravity).setOnClickListener(this);
        findViewById(R.id.btn_scroll).setOnClickListener(this);
        findViewById(R.id.btn_marquee).setOnClickListener(this);
        findViewById(R.id.btn_bbs).setOnClickListener(this);
        findViewById(R.id.btn_click).setOnClickListener(this);
        findViewById(R.id.btn_scale).setOnClickListener(this);
        findViewById(R.id.btn_capture).setOnClickListener(this);
        findViewById(R.id.btn_icon).setOnClickListener(this);
        findViewById(R.id.btn_state).setOnClickListener(this);
        findViewById(R.id.btn_shape).setOnClickListener(this);
        findViewById(R.id.btn_nine).setOnClickListener(this);
        findViewById(R.id.btn_calculator).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_px) {
            Intent intent = new Intent(this, PxActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_color) {
            Intent intent = new Intent(this, ColorActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_screen) {
            Intent intent = new Intent(this, ScreenActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_margin) {
            Intent intent = new Intent(this, MarginActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_gravity) {
            Intent intent = new Intent(this, GravityActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_scroll) {
            Intent intent = new Intent(this, ScrollActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_marquee) {
            Intent intent = new Intent(this, MarqueeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_bbs) {
            Intent intent = new Intent(this, BbsActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_click) {
            Intent intent = new Intent(this, ClickActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_scale) {
            Intent intent = new Intent(this, ScaleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_capture) {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_icon) {
            Intent intent = new Intent(this, IconActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_state) {
            Intent intent = new Intent(this, StateActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_shape) {
            Intent intent = new Intent(this, ShapeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_nine) {
            Intent intent = new Intent(this, NineActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_calculator) {
            Intent intent = new Intent(this, CalculatorActivity.class);
            startActivity(intent);
        }
    }

}
