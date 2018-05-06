package com.example.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
public class SeekbarActivity extends AppCompatActivity implements OnSeekBarChangeListener {
    private TextView tv_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekbar);
        tv_progress = findViewById(R.id.tv_progress);
        // 从布局文件中获取名叫sb_progress的拖动条
        SeekBar sb_progress = findViewById(R.id.sb_progress);
        // 给sb_progress设置拖动变更监听器
        sb_progress.setOnSeekBarChangeListener(this);
        // 设置拖动条的当前进度
        sb_progress.setProgress(50);
    }

    // 在进度变更时触发。第三个参数为true表示用户拖动，为false表示代码设置进度
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String desc = "当前进度为：" + seekBar.getProgress() + ", 最大进度为" + seekBar.getMax();
        tv_progress.setText(desc);
    }

    // 在开始拖动进度时触发
    public void onStartTrackingTouch(SeekBar seekBar) {}

    // 在停止拖动进度时触发
    public void onStopTrackingTouch(SeekBar seekBar) {}

}
