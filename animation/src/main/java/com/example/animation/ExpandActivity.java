package com.example.animation;

import com.example.animation.widget.ExpandTextLayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class ExpandActivity extends AppCompatActivity {
    private int[] newsArray = {R.string.news1, R.string.news2, R.string.news3, R.string.news4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand);
        // 从布局文件中获取名叫etl_content的展开文字布局
        ExpandTextLayout etl_content = findViewById(R.id.etl_content);
        int seq = (int) (Math.random() * 100 % 4);
        // 设置展开文字布局的文本内容
        etl_content.setText(newsArray[seq]);
    }
}
