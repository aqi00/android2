package com.example.performance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/27.
 */
@SuppressLint("StaticFieldLeak")
public class PowerSavingActivity extends AppCompatActivity {
    private static TextView tv_screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_saving);
        tv_screen = findViewById(R.id.tv_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_screen.setText(MainApplication.getInstance().getChangeDesc());
    }

}
