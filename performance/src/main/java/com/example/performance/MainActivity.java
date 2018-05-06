package com.example.performance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by ouyangshen on 2017/12/27.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_include_one).setOnClickListener(this);
        findViewById(R.id.btn_include_two).setOnClickListener(this);
        findViewById(R.id.btn_screen_suitable).setOnClickListener(this);
        findViewById(R.id.btn_window_style).setOnClickListener(this);
        findViewById(R.id.btn_remove_task).setOnClickListener(this);
        findViewById(R.id.btn_logout_service).setOnClickListener(this);
        findViewById(R.id.btn_refer_strong).setOnClickListener(this);
        findViewById(R.id.btn_refer_weak).setOnClickListener(this);
        findViewById(R.id.btn_thread_pool).setOnClickListener(this);
        findViewById(R.id.btn_schedule_pool).setOnClickListener(this);
        findViewById(R.id.btn_battery_info).setOnClickListener(this);
        findViewById(R.id.btn_power_saving).setOnClickListener(this);
        findViewById(R.id.btn_alarm_idle).setOnClickListener(this);
        findViewById(R.id.btn_lru_cache).setOnClickListener(this);
        findViewById(R.id.btn_image_cache).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_include_one) {
            Intent intent = new Intent(this, IncludeOneActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_include_two) {
            Intent intent = new Intent(this, IncludeTwoActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_screen_suitable) {
            Intent intent = new Intent(this, ScreenSuitableActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_window_style) {
            Intent intent = new Intent(this, WindowStyleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_remove_task) {
            Intent intent = new Intent(this, RemoveTaskActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_logout_service) {
            Intent intent = new Intent(this, LogoutServiceActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_refer_strong) {
            Intent intent = new Intent(this, ReferStrongActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_refer_weak) {
            Intent intent = new Intent(this, ReferWeakActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_thread_pool) {
            Intent intent = new Intent(this, ThreadPoolActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_schedule_pool) {
            Intent intent = new Intent(this, SchedulePoolActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_battery_info) {
            Intent intent = new Intent(this, BatteryInfoActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_power_saving) {
            Intent intent = new Intent(this, PowerSavingActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_alarm_idle) {
            Intent intent = new Intent(this, AlarmIdleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_lru_cache) {
            Intent intent = new Intent(this, LruCacheActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_image_cache) {
            Intent intent = new Intent(this, ImageCacheActivity.class);
            startActivity(intent);
        }
    }

}
