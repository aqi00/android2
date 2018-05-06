package com.example.mixture;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/11.
 */
@SuppressLint(value={"SetTextI18n","SetJavaScriptEnabled"})
public class WebBrowserActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "WebBrowserActivity";
    private EditText et_web_url; // 声明一个用于输入网址的编辑框对象
    private WebView wv_web; // 声明一个网页视图对象
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private String mUrl; // 完整的网页地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);
        et_web_url = findViewById(R.id.et_web_url);
        et_web_url.setText("xw.qq.com/");
        // 从布局文件中获取名叫wv_web的网页视图
        wv_web = findViewById(R.id.wv_web);
        findViewById(R.id.btn_web_go).setOnClickListener(this);
        findViewById(R.id.ib_back).setOnClickListener(this);
        findViewById(R.id.ib_forward).setOnClickListener(this);
        findViewById(R.id.ib_refresh).setOnClickListener(this);
        findViewById(R.id.ib_close).setOnClickListener(this);
        initWebViewSettings(); // 初始化网页视图的网页设置
    }

    // 初始化网页视图的网页设置
    private void initWebViewSettings() {
        // 获取网页视图的网页设置
        WebSettings settings = wv_web.getSettings();
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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_web_go) { // 点击了快去按钮
            // 从系统服务中获取输入法管理器
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            // 关闭输入法软键盘
            imm.hideSoftInputFromWindow(et_web_url.getWindowToken(), 0);
            mUrl = "http://" + et_web_url.getText().toString();
            Log.d(TAG, "url=" + mUrl);
            // 命令网页视图加载指定路径的网页
            wv_web.loadUrl(mUrl);
            // 给网页视图设置自定义的网页浏览客户端
            wv_web.setWebViewClient(mWebViewClient);
            // 给网页视图设置自定义的网页交互客户端
            wv_web.setWebChromeClient(mWebChrome);
            // 给网页视图设置文件下载监听器
            wv_web.setDownloadListener(mDownloadListener);
        } else if (v.getId() == R.id.ib_back) { // 点击了后退图标
            if (wv_web.canGoBack()) { // 如果能够后退
                wv_web.goBack(); // 回到上一个网页
            } else {
                Toast.makeText(this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.ib_forward) { // 点击了前进图标
            if (wv_web.canGoForward()) { // 如果能够前进
                wv_web.goForward(); // 去往下一个网页
            } else {
                Toast.makeText(this, "已经是最前一页了", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.ib_refresh) { // 点击了刷新图标
            wv_web.reload(); // 命令网页视图重新加载网页
            //wv_web.stopLoading(); // 停止加载
        } else if (v.getId() == R.id.ib_close) { // 点击了关闭图标
            finish(); // 关闭当前页面
        }
    }

    // 在按下返回键时触发
    public void onBackPressed() {
        Log.d(TAG, "getUrl="+wv_web.getUrl());
        if (wv_web.canGoBack() && !wv_web.getUrl().equals(mUrl)) { // 还能返回到上一个网页
            wv_web.goBack(); // 回到上一个网页
        } else { // 已经是最早的网页，无路返回了
            finish(); // 关闭当前页面
        }
    }

    // 定义一个网页浏览客户端
    private WebViewClient mWebViewClient = new WebViewClient() {
        // 收到SSL错误时触发
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
        }

        // 页面开始加载时触发
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "onPageStarted:" + url);
            if (mDialog == null || !mDialog.isShowing()) {
                // 下面弹出提示网页正在加载的进度对话框
                mDialog = new ProgressDialog(WebBrowserActivity.this);
                mDialog.setTitle("稍等");
                mDialog.setMessage("页面加载中……");
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mDialog.show(); // 显示进度对话框
            }
        }

        // 页面加载结束时触发
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "onPageFinished:" + url);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss(); // 关闭进度对话框
            }
        }

        // 收到错误信息时触发
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d(TAG, "onReceivedError: url=" + failingUrl + ", errorCode=" + errorCode + ", description=" + description);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss(); // 关闭进度对话框
            }
            Toast.makeText(WebBrowserActivity.this,
                    "页面加载失败，请稍候再试", Toast.LENGTH_LONG).show();
        }

        // 发生网页跳转时触发
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url); // 在当前的网页视图内部跳转
            return true;
        }
    };

    // 定义一个网页交互客户端
    private WebChromeClient mWebChrome = new WebChromeClient() {
        // 页面加载进度发生变化时触发
        public void onProgressChanged(WebView view, int progress) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.setProgress(progress); // 更新进度对话框的加载进度
            }
        }

        // 网页请求定位权限时触发
        public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
            callback.invoke(origin, true, false); // 不弹窗就允许网页获得定位权限
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    };

    // 定义一个文件下载监听器
    private DownloadListener mDownloadListener = new DownloadListener() {
        // 在下载开始前触发
        public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                    String mimetype, long contentLength) {
            // 此处操作文件下载
        }
    };

}
