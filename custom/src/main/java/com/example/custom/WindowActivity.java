package com.example.custom;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class WindowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 请求窗口的特征。其中Window.FEATURE_NO_TITLE指的是去掉窗口顶部的导航栏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // 设置窗口的内容视图
        getWindow().setContentView(R.layout.activity_window);
        // 设置窗口的布局参数（如宽度和高度）
//        getWindow().setLayout(400, 400);
        // 设置窗口的背景图片
//        getWindow().setBackgroundDrawableResource(R.drawable.icon_header);
        // 从窗口中获取名叫tv_info的文本视图
        TextView tv_info = getWindow().findViewById(R.id.tv_info);
        tv_info.setText("我在直接操作窗口啦");
    }

}
