package com.example.animation;

import com.example.animation.constant.ImageList;
import com.example.animation.util.Utils;
import com.example.animation.widget.BannerFlipper;
import com.example.animation.widget.BannerFlipper.BannerClickListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * Created by ouyangshen on 2017/11/27.
 */
@SuppressLint("DefaultLocale")
public class BannerAnimActivity extends AppCompatActivity implements BannerClickListener {
    private static final String TAG = "BannerAnimActivity";
    private TextView tv_flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_anim);
        tv_flipper = findViewById(R.id.tv_flipper);
        // 从布局文件中获取名叫banner_flipper的横幅飞掠器
        BannerFlipper banner = findViewById(R.id.banner_flipper);
        LayoutParams params = (LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        // 设置横幅飞掠器的布局参数
        banner.setLayoutParams(params);
        // 设置横幅飞掠器的图片队列
        banner.setImage(ImageList.getDefault());
        // 设置横幅飞掠器的横幅点击监听器
        banner.setOnBannerListener(this);
    }

    // 在点击横幅图片时触发
    public void onBannerClick(int position) {
        String desc = String.format("您点击了第%d张图片", position + 1);
        tv_flipper.setText(desc);
    }

}
