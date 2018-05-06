package com.example.mixture;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/11.
 */
@SuppressLint("SetTextI18n")
public class WebLocalActivity extends AppCompatActivity {
    private String mFilePath = "file:///android_asset/html/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_local);
        TextView tv_web_path = findViewById(R.id.tv_web_path);
        // 从布局文件中获取名叫wv_assets_web的网页视图
        WebView wv_assets_web = findViewById(R.id.wv_assets_web);
        tv_web_path.setText("下面网页来源于资产文件：" + mFilePath);
        // 命令网页视图加载指定路径的网页
        wv_assets_web.loadUrl(mFilePath);
        // 给网页视图设置默认的网页浏览客户端
        wv_assets_web.setWebViewClient(new WebViewClient());
    }
}
