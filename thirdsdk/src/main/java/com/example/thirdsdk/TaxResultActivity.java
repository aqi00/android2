package com.example.thirdsdk;

import com.example.thirdsdk.adapter.ShareGridAdapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * Created by ouyangshen on 2017/12/18.
 */
@SuppressLint("DefaultLocale")
public class TaxResultActivity extends AppCompatActivity implements
        OnClickListener, OnRatingBarChangeListener {
    private final static String TAG = "TaxResultActivity";
    private RatingBar rb_tax_score; // 声明一个评分条对象
    private Button btn_tax_score;
    private GridView gv_share_channel; // 声明一个用于展示分享渠道的网格视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_result);
        // 从布局文件中获取名叫rb_tax_score的评分条
        rb_tax_score = findViewById(R.id.rb_tax_score);
        btn_tax_score = findViewById(R.id.btn_tax_score);
        // 从布局文件中获取名叫gv_share_channel的网格视图
        gv_share_channel = findViewById(R.id.gv_share_channel);
        // 设置评分监听器
        rb_tax_score.setOnRatingBarChangeListener(this);
        btn_tax_score.setOnClickListener(this);
        initShareChannel(); // 初始化分享渠道
    }

    // 在评分发生变化时触发
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {}

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_tax_score) {
            // 下面两行获取评分条的评分星级，并提示用户
            String desc = String.format("您的评分为%d颗星，感谢您的评价", (int) rb_tax_score.getRating());
            Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
            btn_tax_score.setText("已评价");
            btn_tax_score.setTextColor(getResources().getColor(R.color.dark_grey));
            btn_tax_score.setEnabled(false);
            // 设置作为指示器，也就是不允许拖动星星
            rb_tax_score.setIsIndicator(true);
        }
    }

    private Handler mHandler = new Handler();

    // 初始化分享渠道
    private void initShareChannel() {
        String url = "http://blog.csdn.net/aqi00";
        String title = "我用咚咚打车啦";
        String content = "你也来打打车，方便快捷真省心。";
        String imgage_url = "http://avatar.csdn.net/C/1/5/1_aqi00.jpg";
        // 下面通过适配器在网格视图上展示几个分享渠道：QQ好友、QQ空间、腾讯微博
        ShareGridAdapter adapter = new ShareGridAdapter(this, mHandler, url,
                title, content, imgage_url, null);
        gv_share_channel.setAdapter(adapter);
        gv_share_channel.setOnItemClickListener(adapter);
    }

}
