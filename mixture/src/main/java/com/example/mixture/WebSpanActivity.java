package com.example.mixture;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/12/11.
 */
public class WebSpanActivity extends AppCompatActivity {
    private TextView tv_spannable; // 声明一个用于展示可变字符串的文本视图对象
    private WebView wv_spannable; // 声明一个网页视图对象
    private String mText = "为人民服务"; // 原始字符串
    private String mKey = "人民"; // 关键字
    private int mBeginPos, mEndPos; // 起始位置和结束位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_span);
        tv_spannable = findViewById(R.id.tv_spannable);
        // 从布局文件中获取名叫wv_spannable的网页视图
        wv_spannable = findViewById(R.id.wv_spannable);
        tv_spannable.setText(mText);
        mBeginPos = mText.indexOf(mKey); // 获取关键字在源字符串中的起始位置
        mEndPos = mBeginPos + mKey.length(); // 获取关键字在源字符串中的结束位置
        initSpannableSpinner();
        initWebViewSettings(); // 初始化网页视图的网页设置
    }

    // 初始化可变样式的下拉框
    private void initSpannableSpinner() {
        ArrayAdapter<String> spannableAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, spannableArray);
        Spinner sp_spannable = findViewById(R.id.sp_spannable);
        sp_spannable.setPrompt("请选择可变字符串样式");
        sp_spannable.setAdapter(spannableAdapter);
        sp_spannable.setOnItemSelectedListener(new SpannableSelectedListener());
        sp_spannable.setSelection(6);
    }

    private String[] spannableArray = {
            "增大字号", "加粗字体", "前景红色", "背景绿色", "下划线", "表情图片", "超链接"
    };
    class SpannableSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            wv_spannable.setVisibility(View.GONE);
            // 创建一个可变字符串
            SpannableString spanText = new SpannableString(mText);
            if (arg2 == 0) { // 增大字号
                spanText.setSpan(new RelativeSizeSpan(1.5f), mBeginPos, mEndPos,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 1) { // 加粗字体
                spanText.setSpan(new StyleSpan(Typeface.BOLD), mBeginPos, mEndPos,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 2) { // 前景红色
                spanText.setSpan(new ForegroundColorSpan(Color.RED), mBeginPos,
                        mEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 3) { // 背景绿色
                spanText.setSpan(new BackgroundColorSpan(Color.GREEN), mBeginPos,
                        mEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 4) { // 下划线
                spanText.setSpan(new UnderlineSpan(), mBeginPos, mEndPos,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 5) { // 表情图片
                spanText.setSpan(new ImageSpan(WebSpanActivity.this, R.drawable.people),
                        mBeginPos, mEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 6) { // 超链接
                showUrlSpan(); // 显示超链接的文字风格
                return;
            }
            tv_spannable.setText(spanText);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化网页视图的网页设置
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        // 获取网页视图的网页设置
        WebSettings settings = wv_spannable.getSettings();
        // 设置是否自动加载图片
        settings.setLoadsImagesAutomatically(true);
        // 设置默认的文本编码
        settings.setDefaultTextEncodingName("utf-8");
        // 设置是否支持Javascript
        settings.setJavaScriptEnabled(true);
        // 设置是否允许js自动打开新窗口（window.open()）
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        // 设置是否支持缩放
        settings.setSupportZoom(true);
        // 设置是否出现缩放工具
        settings.setBuiltInZoomControls(true);
        // 当容器超过页面大小时，是否放大页面大小到容器宽度
        settings.setUseWideViewPort(true);
        // 当页面超过容器大小时，是否缩小页面尺寸到页面宽度
        settings.setLoadWithOverviewMode(true);
        // 设置自适应屏幕。4.2.2及之前版本自适应时可能会出现表格错乱的情况
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        // 设置是否启用本地存储
        settings.setDomStorageEnabled(true);
        // 优先使用缓存
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置是否使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置是否启用app缓存
        settings.setAppCacheEnabled(true);
        // 设置app缓存文件的路径
        settings.setAppCachePath("");
        // 设置是否允许访问文件，如WebView访问sd卡的文件。
        // 不过assets与res文件不受此限制，仍然可以通过“file:///android_asset”和“file:///android_res”访问
        settings.setAllowFileAccess(true);
        // 设置是否启用数据库
        settings.setDatabaseEnabled(true);
    }

    // 显示超链接的文字风格
    private void showUrlSpan() {
        // 创建一个可变字符串
        SpannableString spanText = new SpannableString(mText);
        // 设置tv_spannable内部文本的移动方式为超链移动
        // 调用setMovementMethod方法之后，点击超链接才有反应
        tv_spannable.setMovementMethod(LinkMovementMethod.getInstance());
        // 从HTML标记中获取可变对象
        Spannable sp = (Spannable) Html.fromHtml("<a href=\"\">" + mKey + "</a>");
        CharSequence text = sp.toString();
        // 生成超链接的风格数组
        URLSpan[] urls = sp.getSpans(0, text.length(), URLSpan.class);
        for (URLSpan url : urls) {
            // 给可变字符串设置超链接风格
            MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
            spanText.setSpan(myURLSpan, mBeginPos, mEndPos,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv_spannable.setText(spanText);
    }

    // 定义一个超链接的风格，用于指定点击事件的逻辑处理
    private class MyURLSpan extends URLSpan {
        public MyURLSpan(String url) {
            super(url);
        }

        // 在点击超链文字时触发
        public void onClick(View widget) {
            wv_spannable.setVisibility(View.VISIBLE);
            // 命令网页视图加载指定路径的网页
            wv_spannable.loadUrl("http://blog.csdn.net/aqi00");
            // 网页视图请求获得焦点
            wv_spannable.requestFocus();
            // 给网页视图设置默认的网页浏览客户端
            wv_spannable.setWebViewClient(new WebViewClient());
        }
    }
}
