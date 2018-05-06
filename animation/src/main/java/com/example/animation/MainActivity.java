package com.example.animation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_frame_anim).setOnClickListener(this);
        findViewById(R.id.btn_gif).setOnClickListener(this);
        findViewById(R.id.btn_fade_anim).setOnClickListener(this);
        findViewById(R.id.btn_tween_anim).setOnClickListener(this);
        findViewById(R.id.btn_swing_anim).setOnClickListener(this);
        findViewById(R.id.btn_anim_set).setOnClickListener(this);
        findViewById(R.id.btn_banner_anim).setOnClickListener(this);
        findViewById(R.id.btn_object_anim).setOnClickListener(this);
        findViewById(R.id.btn_object_group).setOnClickListener(this);
        findViewById(R.id.btn_interpolator).setOnClickListener(this);
        findViewById(R.id.btn_vector_drawable).setOnClickListener(this);
        findViewById(R.id.btn_vector_smile).setOnClickListener(this);
        findViewById(R.id.btn_vector_hook).setOnClickListener(this);
        findViewById(R.id.btn_pay_success).setOnClickListener(this);
        findViewById(R.id.btn_pie).setOnClickListener(this);
        findViewById(R.id.btn_expand).setOnClickListener(this);
        findViewById(R.id.btn_scroller).setOnClickListener(this);
        findViewById(R.id.btn_shutter).setOnClickListener(this);
        findViewById(R.id.btn_mosaic).setOnClickListener(this);
        findViewById(R.id.btn_yingji).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_frame_anim) {
            Intent intent = new Intent(this, FrameAnimActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_gif) {
            Intent intent = new Intent(this, GifActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_fade_anim) {
            Intent intent = new Intent(this, FadeAnimActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_tween_anim) {
            Intent intent = new Intent(this, TweenAnimActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_swing_anim) {
            Intent intent = new Intent(this, SwingAnimActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_anim_set) {
            Intent intent = new Intent(this, AnimSetActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_banner_anim) {
            Intent intent = new Intent(this, BannerAnimActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_object_anim) {
            Intent intent = new Intent(this, ObjectAnimActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_object_group) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(this, ObjectGroupActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "属性动画的暂停与恢复功能需要Android4.4或以上版本", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_interpolator) {
            Intent intent = new Intent(this, InterpolatorActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_vector_drawable) {
            Intent intent = new Intent(this, VectorDrawableActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_vector_smile) {
            Intent intent = new Intent(this, VectorSmileActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_vector_hook) {
            Intent intent = new Intent(this, VectorHookActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_pay_success) {
            Intent intent = new Intent(this, PaySuccessActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_pie) {
            Intent intent = new Intent(this, PieActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_expand) {
            Intent intent = new Intent(this, ExpandActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_scroller) {
            Intent intent = new Intent(this, ScrollerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_shutter) {
            Intent intent = new Intent(this, ShutterActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_mosaic) {
            Intent intent = new Intent(this, MosaicActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_yingji) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Intent intent = new Intent(this, YingjiActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "播放动感影集需要Android4.3或以上版本", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
