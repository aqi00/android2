package com.example.network;

import com.example.network.task.GetImageCodeTask;
import com.example.network.task.GetImageCodeTask.OnImageCodeListener;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
public class HttpImageActivity extends AppCompatActivity implements
        OnClickListener, OnImageCodeListener {
    private ImageView iv_image_code;
    private boolean isRunning = false; // 是否正在运行

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_image);
        iv_image_code = findViewById(R.id.iv_image_code);
        iv_image_code.setOnClickListener(this);
        getImageCode(); // 获取图片验证码
    }

    // 获取图片验证码
    private void getImageCode() {
        if (!isRunning) {
            isRunning = true;
            // 创建验证码获取线程
            GetImageCodeTask codeTask = new GetImageCodeTask();
            // 设置验证码获取监听器
            codeTask.setOnImageCodeListener(this);
            // 把验证码获取线程加入到处理队列
            codeTask.execute();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_image_code) {
            getImageCode(); // 获取图片验证码
        }
    }

    // 在得到验证码后触发
    public void onGetCode(String path) {
        // 把指定路径的验证码图片显示在图像视图上面
        iv_image_code.setImageURI(Uri.parse(path));
        isRunning = false;
    }

}
