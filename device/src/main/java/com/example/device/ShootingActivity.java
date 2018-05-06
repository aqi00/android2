package com.example.device;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.example.device.adapter.ShootingAdapter;

/**
 * Created by ouyangshen on 2017/11/4.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ShootingActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "ShootingActivity";
    private FrameLayout fl_content; // 声明一个框架布局对象
    private ImageView iv_photo; // 声明一个图像视图对象
    private GridView gv_shooting; // 声明一个网格视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting);
        // 从布局文件中获取名叫fl_content的框架布局
        fl_content = findViewById(R.id.fl_content);
        // 从布局文件中获取名叫iv_photo的图像视图
        iv_photo = findViewById(R.id.iv_photo);
        // 从布局文件中获取名叫gv_shooting的网格视图
        gv_shooting = findViewById(R.id.gv_shooting);
        findViewById(R.id.btn_catch_behind).setOnClickListener(this);
        findViewById(R.id.btn_catch_front).setOnClickListener(this);
    }

    // 处理camera2拍照页面的返回结果
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult. requestCode=" + requestCode + ", resultCode=" + resultCode);
        Bundle resp = intent.getExtras(); // 获取返回的包裹
        String is_null = resp.getString("is_null");
        if (!TextUtils.isEmpty(is_null) && !is_null.equals("yes")) { // 有发生拍照动作
            int type = resp.getInt("type");
            Log.d(TAG, "type=" + type);
            if (type == 0) { // 单拍。一次只拍一张
                iv_photo.setVisibility(View.VISIBLE);
                gv_shooting.setVisibility(View.GONE);
                String path = resp.getString("path");
                fillBitmap(BitmapFactory.decodeFile(path, null));
            } else if (type == 1) { // 连拍。一次连续拍了好几张
                iv_photo.setVisibility(View.GONE);
                gv_shooting.setVisibility(View.VISIBLE);
                ArrayList<String> pathList = resp.getStringArrayList("path_list");
                Log.d(TAG, "pathList.size()=" + pathList.size());
                // 通过网格视图展示连拍的数张照片
                ShootingAdapter adapter = new ShootingAdapter(this, pathList);
                gv_shooting.setAdapter(adapter);
            }
        }
    }

    // 以合适比例显示照片
    private void fillBitmap(Bitmap bitmap) {
        Log.d(TAG, "fillBitmap width=" + bitmap.getWidth() + ",height=" + bitmap.getHeight());
        // 位图的高度大于框架布局的高度，则按比例调整图像视图的宽高
        if (bitmap.getHeight() > fl_content.getMeasuredHeight()) {
            LayoutParams params = iv_photo.getLayoutParams();
            params.height = fl_content.getMeasuredHeight();
            params.width = bitmap.getWidth() * fl_content.getMeasuredHeight() / bitmap.getHeight();
            // 设置iv_photo的布局参数
            iv_photo.setLayoutParams(params);
        }
        // 设置iv_photo的拉伸类型为居中
        iv_photo.setScaleType(ScaleType.FIT_CENTER);
        // 设置iv_photo的位图对象
        iv_photo.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        // 从系统服务中获取相机管理器
        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String[] ids;
        try {
            // 获取摄像头的编号数组
            ids = cm.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return;
        }
        if (v.getId() == R.id.btn_catch_behind) { // 点击了后置摄像头拍照按钮
            if (checkCamera(ids, CameraCharacteristics.LENS_FACING_FRONT + "")) {
                // 前往camera2的拍照页面
                Intent intent = new Intent(this, TakeShootingActivity.class);
                // 类型为后置摄像头
                intent.putExtra("type", CameraCharacteristics.LENS_FACING_FRONT);
                // 需要处理拍照页面的返回结果
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(this, "当前设备不支持后置摄像头", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_catch_front) { // 点击了前置摄像头拍照按钮
            if (checkCamera(ids, CameraCharacteristics.LENS_FACING_BACK + "")) {
                // 前往camera2的拍照页面
                Intent intent = new Intent(this, TakeShootingActivity.class);
                // 类型为前置摄像头
                intent.putExtra("type", CameraCharacteristics.LENS_FACING_BACK);
                // 需要处理拍照页面的返回结果
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(this, "当前设备不支持前置摄像头", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 检查是否存在指定类型的摄像头
    private boolean checkCamera(String[] ids, String type) {
        boolean result = false;
        for (String id : ids) {
            if (id.equals(type)) {
                result = true;
                break;
            }
        }
        return result;
    }

}
