package com.example.junior;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.junior.util.Utils;

/**
 * Created by ouyangshen on 2017/9/11.
 */
public class PxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_px);
        // 将10dp的尺寸大小转换为对应的px数值
        int dip_10 = Utils.dip2px(this, 10L);
        // 从布局文件中获取名叫tv_padding的文本视图
        TextView tv_padding = findViewById(R.id.tv_padding);
        // 设置该文本视图的内部文字与控件四周的间隔大小
        tv_padding.setPadding(dip_10, dip_10, dip_10, dip_10);
    }
}
