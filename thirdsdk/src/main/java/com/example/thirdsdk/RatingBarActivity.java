package com.example.thirdsdk;

import com.example.thirdsdk.util.CacheUtil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class RatingBarActivity extends AppCompatActivity implements
        OnCheckedChangeListener, OnRatingBarChangeListener {
    private CheckBox ck_whole;
    private RatingBar rb_score; // 声明一个评分条对象
    private TextView tv_rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_bar);
        ck_whole = findViewById(R.id.ck_whole);
        tv_rating = findViewById(R.id.tv_rating);
        ck_whole.setOnCheckedChangeListener(this);
        initRatingBar(); // 初始化评分条
    }

    // 初始化评分条
    private void initRatingBar() {
        // 从布局文件中获取名叫rb_score的评分条
        rb_score = findViewById(R.id.rb_score);
        // 设置不作为指示器，也就是允许拖动星星
        rb_score.setIsIndicator(false);
        // 设置星星的个数
        rb_score.setNumStars(5);
        // 设置初始评价等级
        rb_score.setRating(3);
        // 设置每次增减的大小
        rb_score.setStepSize(1);
        // 设置评分监听器
        rb_score.setOnRatingBarChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 依据复选框的选中状态，设置评分条能否选择半颗星星
        rb_score.setStepSize(ck_whole.isChecked() ? 1 : rb_score.getNumStars() / 10.0f);
    }

    // 在评分发生变化时触发
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        String desc = String.format("当前选中的是%s颗星", CacheUtil.formatDecimal(rating, 1));
        tv_rating.setText(desc);
    }

}
