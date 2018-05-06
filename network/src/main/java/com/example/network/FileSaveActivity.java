package com.example.network;

import com.aqi00.lib.dialog.FileSaveFragment;
import com.aqi00.lib.dialog.FileSaveFragment.FileSaveCallbacks;
import com.example.network.util.BitmapUtil;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint("SetTextI18n")
public class FileSaveActivity extends AppCompatActivity implements
        OnClickListener, FileSaveCallbacks {
    private EditText et_image_save; // 声明一个编辑框对象
    private TextView tv_image_save; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_save);
        et_image_save = findViewById(R.id.et_image_save);
        tv_image_save = findViewById(R.id.tv_image_save);
        findViewById(R.id.btn_image_save).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开启编辑框et_image_save的绘图缓存
        et_image_save.setDrawingCacheEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 关闭编辑框et_image_save的绘图缓存
        et_image_save.setDrawingCacheEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_image_save) {
            // 打开文件保存对话框
            FileSaveFragment.show(this, "jpg");
        }
    }

    // 在判断文件能否保存时触发
    public boolean onCanSave(String absolutePath, String mFileName) {
        return true;
    }

    // 点击文件保存对话框的确定按钮后触发
    public void onConfirmSave(String absolutePath, String fileName) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        // 从编辑框et_image_save的绘图缓存中获取位图对象
        Bitmap bitmap = et_image_save.getDrawingCache();
        // 把位图数据保存为图片文件
        BitmapUtil.saveBitmap(path, bitmap, "jpg", 80);
        // 回收位图对象
        bitmap.recycle();
        // 把已保存的图片文件路径显示在文本视图上面
        tv_image_save.setText("截图的保存路径为：" + path);
    }

}
