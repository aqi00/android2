package com.example.thirdsdk;

import com.example.thirdsdk.fragment.ComposeSettingsFragment;
import com.example.thirdsdk.fragment.RecognizeSettingsFragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class VoiceSettingsActivity extends AppCompatActivity {
    public static final String PREFER_NAME = "com.example.thirdsdk";
    public static final int XF_RECOGNIZE = 0;
    public static final int XF_COMPOSE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra("type", XF_RECOGNIZE);
        if (type == XF_RECOGNIZE) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new RecognizeSettingsFragment())
                    .commit();
        } else if (type == XF_COMPOSE) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new ComposeSettingsFragment())
                    .commit();
        }
    }
}
