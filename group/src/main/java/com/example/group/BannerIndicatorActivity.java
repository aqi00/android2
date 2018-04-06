package com.example.group;

import com.example.group.constant.ImageList;
import com.example.group.util.Utils;
import com.example.group.widget.BannerIndicator;
import com.example.group.widget.BannerIndicator.BannerClickListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * Created by ouyangshen on 2017/10/21.
 */
@SuppressLint("DefaultLocale")
public class BannerIndicatorActivity extends AppCompatActivity implements BannerClickListener {
    private static final String TAG = "BannerIndicatorActivity";
    private TextView tv_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_indicator);
        tv_pager = findViewById(R.id.tv_pager);
        // 从布局文件中获取名叫banner_indicator的横幅指示器
        BannerIndicator banner = findViewById(R.id.banner_indicator);
        LayoutParams params = (LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        // 设置横幅指示器的布局参数
        banner.setLayoutParams(params);
        // 设置横幅指示器的广告图片队列
        banner.setImage(ImageList.getDefault());
        // 设置横幅指示器的广告点击监听器
        banner.setOnBannerListener(this);
    }

    // 一旦点击了广告图，就回调监听器的onBannerClick方法
    public void onBannerClick(int position) {
        String desc = String.format("您点击了第%d张图片", position + 1);
        tv_pager.setText(desc);
    }

}
