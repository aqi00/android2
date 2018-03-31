package com.example.senior;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_date_picker).setOnClickListener(this);
        findViewById(R.id.btn_time_picker).setOnClickListener(this);
        findViewById(R.id.btn_base_adapter).setOnClickListener(this);
        findViewById(R.id.btn_list_view).setOnClickListener(this);
        findViewById(R.id.btn_list_cart).setOnClickListener(this);
        findViewById(R.id.btn_grid_view).setOnClickListener(this);
        findViewById(R.id.btn_grid_channel).setOnClickListener(this);
        findViewById(R.id.btn_view_pager).setOnClickListener(this);
        findViewById(R.id.btn_title_strip).setOnClickListener(this);
        findViewById(R.id.btn_tab_strip).setOnClickListener(this);
        findViewById(R.id.btn_launch_simple).setOnClickListener(this);
        findViewById(R.id.btn_fragment_static).setOnClickListener(this);
        findViewById(R.id.btn_fragment_dynamic).setOnClickListener(this);
        findViewById(R.id.btn_launch_improve).setOnClickListener(this);
        findViewById(R.id.btn_broad_temp).setOnClickListener(this);
        findViewById(R.id.btn_broad_system).setOnClickListener(this);
        findViewById(R.id.btn_alarm).setOnClickListener(this);
        findViewById(R.id.btn_month_picker).setOnClickListener(this);
        findViewById(R.id.btn_calendar).setOnClickListener(this);
        findViewById(R.id.btn_vibrator).setOnClickListener(this);
        findViewById(R.id.btn_schedule).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_date_picker) {
            Intent intent = new Intent(this, DatePickerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_time_picker) {
            Intent intent = new Intent(this, TimePickerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_base_adapter) {
            Intent intent = new Intent(this, BaseAdapterActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_list_view) {
            Intent intent = new Intent(this, ListViewActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_list_cart) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_grid_view) {
            Intent intent = new Intent(this, GridViewActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_grid_channel) {
            Intent intent = new Intent(this, ShoppingChannelActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_view_pager) {
            Intent intent = new Intent(this, ViewPagerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_title_strip) {
            Intent intent = new Intent(this, PagerTitleStripActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_tab_strip) {
            Intent intent = new Intent(this, PagerTabStripActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_launch_simple) {
            Intent intent = new Intent(this, LaunchSimpleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_fragment_static) {
            Intent intent = new Intent(this, FragmentStaticActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_fragment_dynamic) {
            Intent intent = new Intent(this, FragmentDynamicActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_launch_improve) {
            Intent intent = new Intent(this, LaunchImproveActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_broad_temp) {
            Intent intent = new Intent(this, BroadTempActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_broad_system) {
            Intent intent = new Intent(this, BroadSystemActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_alarm) {
            Intent intent = new Intent(this, AlarmActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_month_picker) {
            Intent intent = new Intent(this, MonthPickerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_calendar) {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_vibrator) {
            Intent intent = new Intent(this, VibratorActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_schedule) {
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
        }
    }

}
