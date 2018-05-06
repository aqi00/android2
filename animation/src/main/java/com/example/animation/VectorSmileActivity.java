package com.example.animation;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class VectorSmileActivity extends AppCompatActivity implements OnClickListener {
    private ImageView iv_vector_smile; // 声明一个图像视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector_smile);
        // 从布局文件中获取名叫iv_vector_smile的图像视图
        iv_vector_smile = findViewById(R.id.iv_vector_smile);
        findViewById(R.id.btn_vector_smile).setOnClickListener(this);
        findViewById(R.id.btn_eye_smile).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_vector_smile) {
            // 播放简单笑脸的矢量动画
            startVectorAnim(R.drawable.animated_vector_smile);
        } else if (v.getId() == R.id.btn_eye_smile) {
            // 播放眯眼笑脸的矢量动画
            startVectorAnim(R.drawable.animated_vector_smile_eye);
        }
    }

    // 开始播放矢量动画
    private void startVectorAnim(int drawableId) {
        iv_vector_smile.setImageResource(drawableId);
        // 将图形转换为具备动画特征的类型，然后再进行播放
        ((Animatable) iv_vector_smile.getDrawable()).start();
    }

}
