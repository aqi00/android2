package com.example.media;

import java.util.Map;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
public class VideoControllerActivity extends AppCompatActivity implements
        OnClickListener, FileSelectCallbacks {
    private static final String TAG = "VideoControllerActivity";
    private VideoView vv_content; // 声明一个视频视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_controller);
        findViewById(R.id.btn_open).setOnClickListener(this);
        // 从布局文件中获取名叫vv_content的视频视图
        vv_content = findViewById(R.id.vv_content);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) {
            String[] videoExs = new String[]{"mp4", "3gp", "mkv", "mov", "avi"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, videoExs, null);
        }
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        // 拼接文件的完整路径
        String file_path = absolutePath + "/" + fileName;
        Log.d(TAG, "file_path=" + file_path);
        // 设置视频视图的视频路径
        vv_content.setVideoPath(file_path);
        // 视频视图请求获得焦点
        vv_content.requestFocus();
        // 创建一个媒体控制条
        MediaController mc_play = new MediaController(this);
        // 给视频视图设置相关联的媒体控制条
        vv_content.setMediaController(mc_play);
        // 给媒体控制条设置相关联的视频视图
        mc_play.setMediaPlayer(vv_content);
        // 视频视图开始播放
        vv_content.start();
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

}
