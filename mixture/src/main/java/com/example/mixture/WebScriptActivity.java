package com.example.mixture;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebScriptActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "WebScriptActivity";
    private String mDemoPath = "file:///android_asset/javascript/demo.html";
    private WebView wv_js;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_script);
        wv_js = findViewById(R.id.wv_js);
        findViewById(R.id.btn_web_popup).setOnClickListener(this);
        findViewById(R.id.btn_js_string).setOnClickListener(this);
        initWebViewSettings();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        // 启动WebView的JavaScript交互功能
        wv_js.getSettings().setJavaScriptEnabled(true);
        // 把Client类实例化为js能够调用client，然后网页demo.html内部就能直接调用client的方法
        wv_js.addJavascriptInterface(new Client(), "client");
        // 设置WebView的浏览器对象，这里用默认的网页浏览客户端
        wv_js.setWebViewClient(new WebViewClient());
        // 内容的渲染需要WebChromeClient去实现，它还能解决js中alert不弹出的问题和其他内容渲染问题
        wv_js.setWebChromeClient(mWebChrome);
        // 试试看用默认的WebChromeClient会有什么样的弹窗
        //wv_js.setWebChromeClient(new WebChromeClient());
        wv_js.loadUrl(mDemoPath);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_web_popup) {
            wv_js.loadUrl("javascript:showMsgFromWeb()");
        } else if (v.getId() == R.id.btn_js_string) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // evaluateJavascript该方法为Android4.4以后引入，该方法用于获取js的返回串
                wv_js.evaluateJavascript("getMsgFromWeb()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WebScriptActivity.this);
                        builder.setTitle("来自安卓的对话框").setMessage(UnicodeToString(value));
                        builder.create().show();
                    }
                });
            } else {
                Toast.makeText(this, "Android4.4之后才支持该功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final class Client {

        // 如要返回值可把void改为String等等类型
        @JavascriptInterface
        public void showMsgFromAndroid(String msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WebScriptActivity.this);
            builder.setTitle("来自安卓的对话框").setMessage(msg);
            builder.create().show();
        }

        @JavascriptInterface
        public String getMsgFromAndroid(String msg) {
            return "这是Android返回的字符串：" + msg;
        }

    }

    // 将JS返回的字符串做转义处理
    public static String UnicodeToString(String str) {
        if (str != null && str.trim().length() > 0) {
            String un = str.trim();
            StringBuilder sb = new StringBuilder();
            int idx = un.indexOf("\\u");
            while (idx >= 0) {
                if (idx > 0) {
                    sb.append(un.substring(0, idx));
                }
                String hex = un.substring(idx + 2, idx + 2 + 4);
                sb.append((char) Integer.parseInt(hex, 16));
                un = un.substring(idx + 2 + 4);
                idx = un.indexOf("\\u");
            }
            sb.append(un);
            return sb.toString();
        }
        return "";
    }

    private WebChromeClient mWebChrome = new WebChromeClient() {
        private String mTitle = "";

        @Override
        public void onReceivedTitle(WebView view, String title) {
            mTitle = title; //获取当前网页的标题文本
        }

        // 重写onJsAlert方法，标题非空则为Android来源，为空则为网页来源
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            String title = mTitle;
            if (title == null || title.length() <= 0) {
                title = "来自网页的对话框";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(WebScriptActivity.this)
                    .setTitle(title).setMessage(message)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            });
            // setCancelable要设置为false，点击对话框外部时不让关闭对话框
            // 不然JsResult的confirm方法没有得到执行，网页上的其它控件就不可使用
            builder.setCancelable(false).create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            String title = mTitle;
            if (title == null || title.length() <= 0) {
                title = "来自网页的对话框";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(WebScriptActivity.this)
                    .setTitle(title).setMessage(message)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setNeutralButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            });
            builder.setCancelable(false).create().show();
            return true;
        }

    };

}
