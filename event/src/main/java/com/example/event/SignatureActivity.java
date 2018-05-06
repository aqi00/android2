package com.example.event;

import com.aqi00.lib.dialog.FileSaveFragment;
import com.aqi00.lib.dialog.FileSaveFragment.FileSaveCallbacks;
import com.example.event.util.BitmapUtil;
import com.example.event.util.DateUtil;
import com.example.event.widget.SignatureView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/23.
 */
public class SignatureActivity extends AppCompatActivity implements
        OnClickListener, FileSaveCallbacks {
    private final static String TAG = "SignatureActivity";
    private SignatureView view_signature; // 声明一个签名视图对象
    private ImageView iv_signature_new; // 声明一个图像视图对象
    private Bitmap mBitmap; // 声明一个位图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        // 从布局文件中获取名叫view_signature的签名视图
        view_signature = findViewById(R.id.view_signature);
        // 从布局文件中获取名叫iv_signature_new的图像视图
        iv_signature_new = findViewById(R.id.iv_signature_new);
        findViewById(R.id.btn_begin_signature).setOnClickListener(this);
        findViewById(R.id.btn_end_signature).setOnClickListener(this);
        findViewById(R.id.btn_reset_signature).setOnClickListener(this);
        findViewById(R.id.btn_revoke_signature).setOnClickListener(this);
        findViewById(R.id.btn_save_signature).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_signature) { // 点击了保存签名按钮
            if (mBitmap == null) {
                Toast.makeText(this, "请先开始然后结束签名", Toast.LENGTH_LONG).show();
                return;
            }
            // 打开文件保存对话框
            FileSaveFragment.show(this, "jpg");
        } else if (v.getId() == R.id.btn_begin_signature) { // 点击了开始签名按钮
            // 开启签名视图view_signature的绘图缓存
            view_signature.setDrawingCacheEnabled(true);
        } else if (v.getId() == R.id.btn_reset_signature) { // 点击了重置按钮
            // 清空签名视图
            view_signature.clear();
        } else if (v.getId() == R.id.btn_revoke_signature) { // 点击了回退按钮
            // 回退签名视图的最近一笔绘画
            view_signature.revoke();
        } else if (v.getId() == R.id.btn_end_signature) { // 点击了结束签名按钮
            if (!view_signature.isDrawingCacheEnabled()) { // 签名视图的绘图缓存不可用
                Toast.makeText(this, "请先开始签名", Toast.LENGTH_LONG).show();
            } else { // 签名视图的绘图缓存当前可用
                // 从签名视图view_signature的绘图缓存中获取位图对象
                Bitmap bitmap = view_signature.getDrawingCache();
                // 生成图片文件的保存路径
                String tempPath = BitmapUtil.getCachePath(this) + DateUtil.getNowDateTime() + ".jpg";
                // 把位图数据保存为图片文件
                BitmapUtil.saveBitmap(tempPath, bitmap, "jpg", 80);
                // 从指定路径的图片文件中获取位图数据
                mBitmap = BitmapFactory.decodeFile(tempPath);
                // 设置图像视图的位图对象
                iv_signature_new.setImageBitmap(mBitmap);
                // 延迟100毫秒后启动绘图缓存的重置任务
                new Handler().postDelayed(mResetCache, 100);
            }
        }
    }

    // 定义一个绘图缓存的重置任务
    private Runnable mResetCache = new Runnable() {
        @Override
        public void run() {
            // 关闭签名视图view_signature的绘图缓存
            view_signature.setDrawingCacheEnabled(false);
            // 开启签名视图view_signature的绘图缓存
            view_signature.setDrawingCacheEnabled(true);
        }
    };

    // 在判断文件能否保存时触发
    public boolean onCanSave(String absolutePath, String fileName) {
        return true;
    }

    // 点击文件保存对话框的确定按钮后触发
    public void onConfirmSave(String absolutePath, String fileName) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        Log.d(TAG, "path="+path);
        // 把位图数据保存为图片文件
        BitmapUtil.saveBitmap(path, mBitmap, "jpg", 80);
        Toast.makeText(this, "成功保存图片文件：" + path, Toast.LENGTH_LONG).show();
    }

}
