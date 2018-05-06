package com.example.event;

import java.util.Map;

import com.aqi00.lib.dialog.FileSaveFragment;
import com.aqi00.lib.dialog.FileSaveFragment.FileSaveCallbacks;
import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.event.util.BitmapUtil;
import com.example.event.widget.BitmapView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/11/23.
 */
public class ImageChangeActivity extends AppCompatActivity implements
        OnClickListener, FileSelectCallbacks, FileSaveCallbacks {
    private BitmapView bv_image; // 声明一个位图视图对象
    private Bitmap mBitmap = null; // 声明一个位图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_change);
        // 从布局文件中获取名叫bv_image的位图视图
        bv_image = findViewById(R.id.bv_image);
        findViewById(R.id.btn_open_image).setOnClickListener(this);
        findViewById(R.id.btn_save_image).setOnClickListener(this);
        initScaleSpinner();
        initRotateSpinner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开启位图视图bv_image的绘图缓存
        bv_image.setDrawingCacheEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 关闭位图视图bv_image的绘图缓存
        bv_image.setDrawingCacheEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open_image) {
            // 打开文件选择对话框
            FileSelectFragment.show(this, new String[]{"jpg", "png"}, null);
        } else if (v.getId() == R.id.btn_save_image) {
            if (mBitmap == null) {
                Toast.makeText(this, "请先打开图片文件", Toast.LENGTH_LONG).show();
                return;
            }
            // 打开文件保存对话框
            FileSaveFragment.show(this, "jpg");
        }
    }

    // 初始化缩放比率下拉框
    private void initScaleSpinner() {
        ArrayAdapter<String> scaleAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, scaleArray);
        Spinner sp_style = findViewById(R.id.sp_scale);
        sp_style.setPrompt("请选择缩放比率");
        sp_style.setAdapter(scaleAdapter);
        sp_style.setOnItemSelectedListener(new ScaleSelectedListener());
        sp_style.setSelection(3);
    }

    private String[] scaleArray = {"0.25", "0.5", "0.75", "1.0", "1.5", "2.0", "4.0"};
    class ScaleSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            bv_image.setScaleRatio(Float.parseFloat(scaleArray[arg2]), true);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化旋转角度下拉框
    private void initRotateSpinner() {
        ArrayAdapter<String> rotateAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, rotateArray);
        Spinner sp_style = findViewById(R.id.sp_rotate);
        sp_style.setPrompt("请选择旋转角度");
        sp_style.setAdapter(rotateAdapter);
        sp_style.setOnItemSelectedListener(new RotateSelectedListener());
        sp_style.setSelection(0);
    }

    private String[] rotateArray = {"0", "45", "90", "135", "180", "225", "270", "315"};
    class RotateSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            bv_image.setRotateDegree(Integer.parseInt(rotateArray[arg2]), true);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 在判断文件能否保存时触发
    public boolean onCanSave(String absolutePath, String fileName) {
        return true;
    }

    // 点击文件保存对话框的确定按钮后触发
    public void onConfirmSave(String absolutePath, String fileName) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        // 从位图视图bv_image的绘图缓存中获取位图对象
        Bitmap bitmap = bv_image.getDrawingCache();
        // 把位图数据保存为图片文件
        BitmapUtil.saveBitmap(path, bitmap, "jpg", 80);
        // 回收位图对象
        bitmap.recycle();
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        // 从指定路径的图片文件中获取位图数据
        mBitmap = BitmapFactory.decodeFile(path);
        // 设置位图视图的位图对象
        bv_image.setImageBitmap(mBitmap);
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

}
