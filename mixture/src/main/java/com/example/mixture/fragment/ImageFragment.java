package com.example.mixture.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mixture.R;
import com.example.mixture.VudroidActivity;
import com.example.mixture.util.FileUtil;

import org.vudroid.core.DecodeService;

import java.io.File;

public class ImageFragment extends Fragment {
    private static final String TAG = "ImageFragment";
    protected View mView;
    protected Context mContext;
    private String mPath;
    private ImageView iv_content;
    private ProgressDialog mDialog; // 声明一个进度对话框对象

    public static ImageFragment newInstance(String path) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        if (getArguments() != null) {
            mPath = getArguments().getString("path");
        }
        Log.d(TAG, "path=" + mPath);
        mView = inflater.inflate(R.layout.fragment_image, container, false);
        iv_content = mView.findViewById(R.id.iv_content);
        if ((new File(mPath)).exists()) {
            iv_content.setImageBitmap(BitmapFactory.decodeFile(mPath));
        }
        return mView;
    }

    // 电子书可能有很多页，为了节约系统资源，只在打开某页时采取解析该页的图像数据
    // 碎片页在可见与不可见之间切换时调用
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 如果指定路径已经存在图片文件，则直接显示该图片，否则需从头解析该页的图片
        if (mContext != null && isVisibleToUser &&
                !(new File(mPath)).exists() && VudroidActivity.decodeService != null) {
            readImage(); // 读取该书页的图像
        }
    }

    // 存储卡上没有该页的图片，就要到电子书中解析出该页的图像
    private void readImage() {
        // 弹出进度对话框
        mDialog = ProgressDialog.show(mContext, "请稍候", "正在努力加载");
        String dir = mPath.substring(0, mPath.lastIndexOf("/"));
        final int index = Integer.parseInt(mPath.substring(mPath.lastIndexOf("/") + 1, mPath.lastIndexOf(".")));
        // 解析页面的操作是异步的，解析结果在监听器中回调通知
        VudroidActivity.decodeService.decodePage(dir, index, new DecodeService.DecodeCallback() {
            @Override
            public void decodeComplete(final Bitmap bitmap) {
                // 把位图对象保存成图片，下次直接读取存储卡上的图片文件
                FileUtil.saveBitmap(mPath, bitmap);
                // 解码监听器在分线程中运行，调用runOnUiThread方法表示回到主线程操作界面
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 把位图对象显示到ImageView控件
                        iv_content.setImageBitmap(bitmap);
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss(); // 关闭进度对话框
                        }
                    }
                });
            }
        }, 1, new RectF(0, 0, 1, 1));
    }

}
