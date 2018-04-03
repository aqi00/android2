package com.example.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class NotifyProgressActivity extends AppCompatActivity implements OnClickListener {
    private ProgressBar pb_progress; // 声明一个进度条对象
    private EditText et_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_progress);
        // 从布局文件中获取名叫pb_progress的进度条
        pb_progress = findViewById(R.id.pb_progress);
        et_progress = findViewById(R.id.et_progress);
        findViewById(R.id.btn_progress).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_progress) {
            int progress = Integer.parseInt(et_progress.getText().toString());
            // 设置进度条的当前进度
            pb_progress.setProgress(progress);
        }
    }

}
