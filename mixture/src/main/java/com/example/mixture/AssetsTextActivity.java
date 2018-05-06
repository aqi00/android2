package com.example.mixture;

import com.example.mixture.util.AssetsUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/11.
 */
@SuppressLint("SetTextI18n")
public class AssetsTextActivity extends AppCompatActivity {
    private String mFilePath = "file/libai.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_text);
        TextView tv_text_path = findViewById(R.id.tv_text_path);
        TextView tv_assets_text = findViewById(R.id.tv_assets_text);
        tv_text_path.setText("下面文字来源于资产文件：" + mFilePath);
        // 从资产文件中读取出字符串文本
        String str = AssetsUtil.getTxtFromAssets(this, mFilePath);
        tv_assets_text.setText(str);
    }

}
