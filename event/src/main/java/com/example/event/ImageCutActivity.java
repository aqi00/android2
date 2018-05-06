package com.example.event;

import java.util.Map;

import com.aqi00.lib.dialog.FileSaveFragment;
import com.aqi00.lib.dialog.FileSaveFragment.FileSaveCallbacks;
import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.event.util.BitmapUtil;
import com.example.event.widget.CropImageView;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/23.
 */
public class ImageCutActivity extends AppCompatActivity implements
        OnClickListener, FileSelectCallbacks, FileSaveCallbacks {
    private View v_shade; // 声明一个阴影视图对象
    private CropImageView civ_over; // 声明一个裁剪图像视图对象
    private ImageView iv_old; // 声明一个原始图片的图像视图对象
    private ImageView iv_new; // 声明一个最新图片的图像视图对象
    private Bitmap mBitmap = null; // 声明一个位图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cut);
        findViewById(R.id.btn_open_image).setOnClickListener(this);
        findViewById(R.id.btn_save_image).setOnClickListener(this);
        findViewById(R.id.btn_cut_begin).setOnClickListener(this);
        findViewById(R.id.btn_cut_end).setOnClickListener(this);
        // 从布局文件中获取名叫v_shade的阴影视图
        v_shade = findViewById(R.id.v_shade);
        // 从布局文件中获取名叫civ_over的裁剪图像视图
        civ_over = findViewById(R.id.civ_over);
        // 从布局文件中获取名叫iv_old的图像视图
        iv_old = findViewById(R.id.iv_old);
        // 从布局文件中获取名叫iv_new的图像视图
        iv_new = findViewById(R.id.iv_new);
        // 开启图像视图iv_old的绘图缓存
        iv_old.setDrawingCacheEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open_image) { // 点击了打开文件按钮
            // 打开文件选择对话框
            FileSelectFragment.show(this, new String[]{"jpg", "png"}, null);
        } else if (v.getId() == R.id.btn_save_image) { // 点击了保存文件按钮
            if (mBitmap == null) {
                Toast.makeText(this, "请先打开并裁剪图片文件", Toast.LENGTH_LONG).show();
                return;
            }
            // 打开文件保存对话框
            FileSaveFragment.show(this, "jpg");
        } else if (v.getId() == R.id.btn_cut_begin) { // 点击了开始裁剪按钮
            v_shade.setVisibility(View.VISIBLE);
            civ_over.setVisibility(View.VISIBLE);
            // 从图像视图iv_old的绘图缓存中获取位图对象
            Bitmap bitmap = iv_old.getDrawingCache();
            int left = bitmap.getWidth() / 4;
            int top = bitmap.getHeight() / 4;
            // 设置裁剪图像视图的原始位图
            civ_over.setOrigBitmap(bitmap);
            // 设置裁剪图像视图的位图边界
            civ_over.setBitmapRect(new Rect(left, top, left * 2, top * 2));
        } else if (v.getId() == R.id.btn_cut_end) { // 点击了结束裁剪按钮
            v_shade.setVisibility(View.GONE);
            civ_over.setVisibility(View.GONE);
            // 获取裁剪图像视图处理后的位图
            mBitmap = civ_over.getCropBitmap();
            // 设置图像视图iv_new的位图对象
            iv_new.setImageBitmap(mBitmap);
        }
    }

    // 在判断文件能否保存时触发
    public boolean onCanSave(String absolutePath, String fileName) {
        return true;
    }

    // 点击文件保存对话框的确定按钮后触发
    public void onConfirmSave(String absolutePath, String fileName) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        // 把位图数据保存为图片文件
        BitmapUtil.saveBitmap(path, mBitmap, "jpg", 80);
        Toast.makeText(this, "成功保存图片文件：" + path, Toast.LENGTH_LONG).show();
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        // 把要打开的图片文件显示在图像视图上面
        iv_old.setImageURI(Uri.parse(path));
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

}
