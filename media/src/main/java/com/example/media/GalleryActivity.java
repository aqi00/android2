package com.example.media;

import com.example.media.adapter.GalleryAdapter;
import com.example.media.util.Utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
public class GalleryActivity extends AppCompatActivity implements OnItemClickListener {
    private ImageView iv_gallery; // 声明一个用于展示大图的图像视图
    private Gallery gl_gallery; // 声明一个画廊视图对象
    // 画廊需要的图片资源编号数组
    private int[] mImageRes = {
            R.drawable.scene1, R.drawable.scene2, R.drawable.scene3,
            R.drawable.scene4, R.drawable.scene5, R.drawable.scene6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        // 从布局文件中获取名叫iv_gallery的图像视图
        iv_gallery = findViewById(R.id.iv_gallery);
        // 给图像视图设置图片的资源编号
        iv_gallery.setImageResource(mImageRes[0]);
        initGallery(); // 初始化画廊视图
    }

    // 初始化画廊视图
    private void initGallery() {
        int dip_pad = Utils.dip2px(this, 20);
        // 从布局文件中获取名叫gl_gallery的画廊视图
        gl_gallery = findViewById(R.id.gl_gallery);
        // 设置画廊的上下间距
        gl_gallery.setPadding(0, dip_pad, 0, dip_pad);
        // 设置画廊视图各单项之间的空白距离
        gl_gallery.setSpacing(dip_pad);
        // 设置画廊视图未选中部分的透明度
        gl_gallery.setUnselectedAlpha(0.5f);
        // 给画廊视图设置画廊适配器
        gl_gallery.setAdapter(new GalleryAdapter(this, mImageRes));
        // 给画廊视图设置单项点击监听器
        gl_gallery.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 在图像视图上面展示大图
        iv_gallery.setImageResource(mImageRes[position]);
    }

}
