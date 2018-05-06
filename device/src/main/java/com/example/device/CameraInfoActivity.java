package com.example.device;

import java.util.ArrayList;

import com.example.device.adapter.CameraAdapter;
import com.example.device.bean.CameraInfo;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("DefaultLocale")
public class CameraInfoActivity extends AppCompatActivity {
    private final static String TAG = "CameraInfoActivity";
    private ListView lv_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_info);
        lv_camera = findViewById(R.id.lv_camera);
        checkCamera();
    }

    // 检查当前设备支持的摄像头信息
    private void checkCamera() {
        ArrayList<CameraInfo> cameraList = new ArrayList<CameraInfo>();
        // 获取摄像头的个数
        int cameraCount = Camera.getNumberOfCameras();
        Log.d(TAG, String.format("摄像头个数=%d", cameraCount));
        for (int i = 0; i < cameraCount; i++) {
            CameraInfo info = new CameraInfo();
            Camera camera = Camera.open(i); // 打开指定摄像头
            Parameters params = camera.getParameters(); // 获取该摄像头的参数
            info.camera_type = (i == 0) ? "前置" : "后置"; // 获取摄像头的类型
            info.flash_mode = params.getFlashMode(); // 获取摄像头的闪光模式
            info.focus_mode = params.getFocusMode(); // 获取摄像头的对焦模式
            info.scene_mode = params.getSceneMode(); // 获取摄像头的场景模式
            info.color_effect = params.getColorEffect(); // 获取摄像头的颜色效果
            info.white_balance = params.getWhiteBalance(); // 获取摄像头的白平衡
            info.max_zoom = params.getMaxZoom(); // 获取摄像头的最大缩放比例
            info.zoom = params.getZoom(); // 获取摄像头的当前缩放比例
            info.resolutionList = params.getSupportedPreviewSizes(); // 获取摄像头支持的预览分辨率
            camera.release(); // 释放摄像头
            cameraList.add(info);
        }
        CameraAdapter adapter = new CameraAdapter(this, cameraList);
        lv_camera.setAdapter(adapter);
    }

}
