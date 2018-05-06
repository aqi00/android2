package com.example.event;

import java.util.Map;

import com.aqi00.lib.dialog.FileSaveFragment;
import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSaveFragment.FileSaveCallbacks;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.event.util.BitmapUtil;
import com.example.event.widget.BitmapView;
import com.example.event.widget.MeituView;
import com.example.event.widget.MeituView.ImageChangetListener;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/23.
 */
public class MeituActivity extends AppCompatActivity implements
        FileSelectCallbacks, FileSaveCallbacks, ImageChangetListener {
    private final static String TAG = "MeituActivity";
    private MeituView mv_content; // 声明一个美图视图对象
    private TextView tv_intro;
    private BitmapView bv_content; // 声明一个位图视图对象
    private Bitmap mBitmap = null; // 声明一个位图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meitu);
        // 从布局文件中获取名叫mv_content的美图视图
        mv_content = findViewById(R.id.mv_content);
        // 设置美图视图的图像变更监听器
        mv_content.setImageChangetListener(this);
        tv_intro = findViewById(R.id.tv_intro);
        // 从布局文件中获取名叫bv_content的位图视图
        bv_content = findViewById(R.id.bv_content);
        // 开启位图视图bv_content的绘图缓存
        bv_content.setDrawingCacheEnabled(true);
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
        tv_intro.setVisibility(View.GONE);
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        // 从指定路径的图片文件中获取位图数据
        Bitmap bitmap = BitmapUtil.openBitmap(path);
        // 设置位图视图的位图对象
        bv_content.setImageBitmap(bitmap);
        refreshImage(true);
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    // 刷新图像展示
    private void refreshImage(boolean is_first) {
        // 从位图视图bv_content的绘图缓存中获取位图对象
        Bitmap bitmap = bv_content.getDrawingCache();
        // 设置美图视图的原始位图
        mv_content.setOrigBitmap(bitmap);
        if (is_first) { // 首次打开
            int left = bitmap.getWidth() / 4;
            int top = bitmap.getHeight() / 4;
            // 设置美图视图的位图边界
            mv_content.setBitmapRect(new Rect(left, top, left * 2, top * 2));
        } else { // 非首次打开
            // 设置美图视图的位图边界
            mv_content.setBitmapRect(mv_content.getBitmapRect());
        }
    }

    // 在图片平移时触发
    public void onImageTraslate(int offsetX, int offsetY, boolean bReset) {
        // 设置位图视图的偏移距离
        bv_content.setOffset(offsetX, offsetY, bReset);
        refreshImage(false);
    }

    // 在图片缩放时触发
    public void onImageScale(float ratio) {
        // 设置位图视图的缩放比率
        bv_content.setScaleRatio(ratio, false);
        refreshImage(false);
    }

    // 在图片旋转时触发
    public void onImageRotate(int degree) {
        // 设置位图视图的旋转角度
        bv_content.setRotateDegree(degree, false);
        refreshImage(false);
    }

    // 在图片点击时触发
    public void onImageClick() {}

    // 在图片长按时触发
    public void onImageLongClick() {
        // 给美图视图注册上下文菜单
        registerForContextMenu(mv_content);
        // 为美图视图打开上下文菜单
        openContextMenu(mv_content);
        // 给美图视图注销上下文菜单
        unregisterForContextMenu(mv_content);
    }

    // 在创建上下文菜单时调用
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_meitu, menu);
    }

    // 在选中菜单项时调用
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_file_open) {
            // 打开文件选择对话框
            FileSelectFragment.show(this, new String[]{"jpg", "png"}, null);
        } else if (id == R.id.menu_file_save) {
            // 获取美图视图处理后的位图
            mBitmap = mv_content.getCropBitmap();
            // 打开文件保存对话框
            FileSaveFragment.show(this, "jpg");
        }
        return true;
    }
}
