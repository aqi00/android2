package com.example.mixture;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.mixture.util.FileUtil;
import com.example.mixture.widget.ViewSlider;

public class PdfSliderActivity extends AppCompatActivity {
    private final static String TAG = "PdfSliderActivity";
    private ViewSlider vs_content; // 声明一个滑动视图对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private ProgressDialog mDialog; // 声明一个进度对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_slider);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
        tl_head.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 关闭当前页面
            }
        });
        TextView tv_title = findViewById(R.id.tv_title);
        // 从布局文件中获取名叫vs_content的滑动视图
        vs_content = findViewById(R.id.vs_content);
        // 从前一个页面传来的意图中获取名叫path的文件路径
        String path = getIntent().getStringExtra("path");
        // 从文件路径中获取文件名称
        tv_title.setText(FileUtil.getFileName(path));
        loadBook(); // 加载书籍内容
    }

    // 加载书籍内容
    private void loadBook() {
        // 从前一个页面传来的意图中获取名叫img_list的图片路径队列
        final ArrayList<String> imgArray = getIntent().getStringArrayListExtra("img_list");
        if (imgArray != null && imgArray.size() > 0) {
            // 弹出进度对话框
            mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
            // 延迟50毫秒后启动书页加载任务
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 给滑动视图设置图片路径队列
                    vs_content.setFilePath(imgArray);
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss(); // 关闭进度对话框
                    }
                }
            }, 100);
        }
    }

}
