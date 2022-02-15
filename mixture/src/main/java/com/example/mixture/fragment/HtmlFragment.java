package com.example.mixture.fragment;

import com.example.mixture.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HtmlFragment extends Fragment {
    private static final String TAG = "HtmlFragment";
    protected View mView;
    protected Context mContext;
    private String htmlPath;

    public static HtmlFragment newInstance(String htmlPath) {
        HtmlFragment fragment = new HtmlFragment();
        Bundle bundle = new Bundle();
        bundle.putString("htmlPath", htmlPath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        if (getArguments() != null) {
            htmlPath = getArguments().getString("htmlPath");
        }
        mView = inflater.inflate(R.layout.fragment_html, container, false);
        // 从布局文件中获取名叫wv_content的网页视图
        WebView wv_content = mView.findViewById(R.id.wv_content);
        // 设置是否允许访问文件，如WebView访问存储卡的文件。
        // 不过assets与res文件不受此限制，仍然可以通过“file:///android_asset”和“file:///android_res”访问
        // Android11开始该值默认为false，故Android11必须设置为true才能访问存储卡的网页
        wv_content.getSettings().setAllowFileAccess(true);
        // 命令网页视图加载指定路径的网页
        wv_content.loadUrl("file:///" + htmlPath);
        return mView;
    }

}
