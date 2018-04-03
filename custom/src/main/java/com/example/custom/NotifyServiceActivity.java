package com.example.custom;

import com.example.custom.service.MusicService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class NotifyServiceActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_song;
    private Button btn_send_service;
    private boolean isPlaying = true; // 是否正在播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_service);
        et_song = findViewById(R.id.et_song);
        btn_send_service = findViewById(R.id.btn_send_service);
        btn_send_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_service) {
            // 创建一个通往音乐服务的意图
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("is_play", isPlaying);
            intent.putExtra("song", et_song.getText().toString());
            if (isPlaying) { // 正在播放
                // 启动音乐服务，使之开始播放
                startService(intent);
                btn_send_service.setText("停止播放音乐");
            } else { // 不在播放
                // 停止音乐服务，使之结束播放
                stopService(intent);
                btn_send_service.setText("开始播放音乐");
            }
            isPlaying = !isPlaying;
        }
    }

}
