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
        // 命令网页视图加载指定路径的网页
        wv_content.loadUrl("file:///" + htmlPath);
        return mView;
    }

}
