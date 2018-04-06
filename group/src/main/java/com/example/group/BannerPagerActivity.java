package com.example.group;

import com.example.group.constant.ImageList;
import com.example.group.util.Utils;
import com.example.group.widget.BannerPager;
import com.example.group.widget.BannerPager.BannerClickListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * Created by ouyangshen on 2017/10/21.
 */
@SuppressLint("DefaultLocale")
public class BannerPagerActivity extends AppCompatActivity implements BannerClickListener {
    private static final String TAG = "BannerPagerActivity";
    private TextView tv_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_pager);
        tv_pager = findViewById(R.id.tv_pager);
        // 从布局文件中获取名叫banner_pager的横幅轮播条
        BannerPager banner = findViewById(R.id.banner_pager);
        // 获取横幅轮播条的布局参数
        LayoutParams params = (LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        // 设置横幅轮播条的布局参数
        banner.setLayoutParams(params);
        // 设置横幅轮播条的广告图片队列
        banner.setImage(ImageList.getDefault());
        // 设置横幅轮播条的广告点击监听器
        banner.setOnBannerListener(this);
        // 开始广告图片的轮播滚动
        banner.start();
    }

    // 一旦点击了广告图，就回调监听器的onBannerClick方法
    public void onBannerClick(int position) {
        String desc = String.format("您点击了第%d张图片", position + 1);
        tv_pager.setText(desc);
    }

}
