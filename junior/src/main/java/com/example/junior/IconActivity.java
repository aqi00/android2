package com.example.junior;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by ouyangshen on 2017/9/15.
 */
public class IconActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_icon; // 声明一个按钮对象
    private Drawable drawable; // 声明一个图形对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon);
        // 从布局文件中获取名叫btn_icon的按钮控件
        btn_icon = findViewById(R.id.btn_icon);
        // 从资源文件ic_launcher.png中获取图形对象
        drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        // 设置图形对象的矩形边界大小，注意必须设置图片大小，否则不会显示图片
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        // 下面通过四个按钮，分别演示左、上、右、下四个方向的图标效果
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_top).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.btn_bottom).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {  // 一旦监听到点击动作，就触发监听器的onClick方法
        if (v.getId() == R.id.btn_left) {
            // 设置按钮控件btn_icon内部文字左边的图标
            btn_icon.setCompoundDrawables(drawable, null, null, null);
        } else if (v.getId() == R.id.btn_top) {
            // 设置按钮控件btn_icon内部文字上方的图标
            btn_icon.setCompoundDrawables(null, drawable, null, null);
        } else if (v.getId() == R.id.btn_right) {
            // 设置按钮控件btn_icon内部文字右边的图标
            btn_icon.setCompoundDrawables(null, null, drawable, null);
        } else if (v.getId() == R.id.btn_bottom) {
            // 设置按钮控件btn_icon内部文字下方的图标
            btn_icon.setCompoundDrawables(null, null, null, drawable);
        }
    }
}
