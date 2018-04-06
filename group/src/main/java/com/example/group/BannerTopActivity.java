package com.example.group;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.group.constant.ImageList;
import com.example.group.util.StatusBarUtil;
import com.example.group.util.Utils;
import com.example.group.widget.BannerPager;

/**
 * Created by ouyangshen on 2018/2/24.
 */
@SuppressLint("DefaultLocale")
public class BannerTopActivity extends AppCompatActivity implements
        OnClickListener, BannerPager.BannerClickListener {
    private static final String TAG = "BannerTopActivity";
    private Button btn_top;
    private boolean isOccupy = true; // 是否占据了状态栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_top);
        btn_top = findViewById(R.id.btn_top);
        btn_top.setOnClickListener(this);
        // 让当前页面全屏展示，也就是向上顶到状态栏
        StatusBarUtil.fullScreen(this);
        // 从布局文件中获取名叫banner_top的横幅轮播条
        BannerPager banner = findViewById(R.id.banner_top);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
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
        Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_top) {
            if (isOccupy) { // 已占据状态栏
                // 下挪页面内容，从而恢复状态栏
                StatusBarUtil.reset(this);
            } else { // 未占据状态栏
                // 上挪页面内容，使之占据状态栏
                StatusBarUtil.fullScreen(this);
            }
            isOccupy = !isOccupy;
            btn_top.setText(isOccupy ? "腾出状态栏" : "霸占状态栏");
        }
    }
}
