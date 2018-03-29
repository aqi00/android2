package com.example.junior;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/9/11.
 */
public class ColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        // 从布局文件中获取名叫tv_code_six的文本视图
        TextView tv_code_six = findViewById(R.id.tv_code_six);
        // 给文本视图tv_code_six设置背景为透明的绿色，透明就是看不到
        tv_code_six.setBackgroundColor(0x00ff00);
        // 从布局文件中获取名叫tv_code_eight的文本视图
        TextView tv_code_eight = findViewById(R.id.tv_code_eight);
        // 给文本视图tv_code_eight设置背景为不透明的绿色，即正常的绿色
        tv_code_eight.setBackgroundColor(0xff00ff00);
    }
}
