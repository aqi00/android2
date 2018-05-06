package com.example.network;

import java.util.Map;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint("SetTextI18n")
public class FileSelectActivity extends AppCompatActivity implements
        OnClickListener, FileSelectCallbacks {
    private ImageView iv_image_select; // 声明一个图像视图对象
    private TextView tv_image_select; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle selectdInstanceState) {
        super.onCreate(selectdInstanceState);
        setContentView(R.layout.activity_file_select);
        iv_image_select = findViewById(R.id.iv_image_select);
        tv_image_select = findViewById(R.id.tv_image_select);
        findViewById(R.id.btn_image_select).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_image_select) {
            // 声明一个图片文件的扩展名数组
            String[] imgExt = new String[]{"jpg", "png"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, imgExt, null);
        }
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        // 把要打开的图片文件显示在图像视图上面
        iv_image_select.setImageURI(Uri.parse(path));
        // 把要打开的图片文件路径显示在文本视图上面
        tv_image_select.setText("打开图片的路径为：" + path);
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

}
