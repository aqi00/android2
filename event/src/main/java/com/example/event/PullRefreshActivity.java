package com.example.event;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.event.constant.ImageList;
import com.example.event.util.StatusBarUtil;
import com.example.event.util.Utils;
import com.example.event.widget.BannerFlipper;
import com.example.event.widget.PullDownRefreshLayout;

/**
 * Created by ouyangshen on 2018/1/4.
 */
@SuppressLint("DefaultLocale")
public class PullRefreshActivity extends AppCompatActivity implements
        BannerFlipper.BannerClickListener, PullDownRefreshLayout.PullRefreshListener {
    private static final String TAG = "PullRefreshActivity";
    private PullDownRefreshLayout pdrl_main; // 声明一个下拉刷新布局对象
    private TextView tv_flipper;
    private LinearLayout ll_title;
    private ImageView iv_scan;
    private ImageView iv_msg;
    private boolean isDragging = false; // 是否正在拖动
    private ProgressDialog mDialog; // 声明一个进度对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_refresh);
        // 从布局文件中获取名叫pdrl_main的下拉刷新布局
        pdrl_main = findViewById(R.id.pdrl_main);
        // 给pdrl_main设置刷新监听器
        pdrl_main.setOnRefreshListener(this);
        tv_flipper = findViewById(R.id.tv_flipper);
        ll_title = findViewById(R.id.ll_title);
        iv_scan = findViewById(R.id.iv_scan);
        iv_msg = findViewById(R.id.iv_msg);
        // 从布局文件中获取名叫banner_flipper的横幅飞掠器
        BannerFlipper banner = findViewById(R.id.banner_flipper);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        // 设置横幅飞掠器的布局参数
        banner.setLayoutParams(params);
        // 设置横幅飞掠器的图片队列
        banner.setImage(ImageList.getDefault());
        // 设置横幅飞掠器的图片点击监听器
        banner.setOnBannerListener(this);
        // 添加悬浮状态栏效果
        floatStatusBar();
    }

    private void floatStatusBar() {
        // 让App页面扩展到状态栏区域
        StatusBarUtil.fullScreen(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) ll_title.getLayoutParams();
            // 标题栏在上方留出一段距离，看起来仍在状态栏下方
            titleParams.topMargin = StatusBarUtil.getStatusBarHeight(this);
            ll_title.setLayoutParams(titleParams);
        }
    }

    // 在图片点击时触发
    public void onBannerClick(int position) {
        String desc = String.format("您点击了第%d张图片", position + 1);
        tv_flipper.setText(desc);
    }

    // 开始页面刷新
    private void beginRefresh() {
        if (mDialog == null || !mDialog.isShowing()) {
            // 显示进度对话框
            mDialog = ProgressDialog.show(this, "请稍等", "正在努力刷新页面");
            // 延迟1秒后启动刷新结束任务
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    endRefresh();
                }
            }, 1000);
        }
    }

    // 结束页面刷新
    private void endRefresh() {
        if (isDragging) {
            mDialog.dismiss(); // 关闭进度对话框
            pdrl_main.finishRefresh();
            isDragging = false;
        }
    }

    // 在下拉刷新时触发
    public void pullRefresh() {
        isDragging = true;
        beginRefresh();
    }

    // 在往上拉动时触发
    public void pullUp() {
        ll_title.setBackgroundResource(R.color.white);
        ll_title.setVisibility(View.VISIBLE);
        iv_scan.setImageResource(R.drawable.icon_scan_gray);
        iv_msg.setImageResource(R.drawable.icon_msg_gray);
        // 上拉页面，把状态栏背景改为灰色
        StatusBarUtil.setStatusBarColor(this, Color.DKGRAY);
    }

    // 在往下拉动时触发
    public void pullDown() {
        ll_title.setBackgroundResource(R.color.transparent);
        ll_title.setVisibility(View.VISIBLE);
        iv_scan.setImageResource(R.drawable.icon_scan_white);
        iv_msg.setImageResource(R.drawable.icon_msg_white);
        // 下拉到顶了，把状态栏背景改为透明
        StatusBarUtil.setStatusBarColor(this, Color.TRANSPARENT);
    }

    @Override
    public void hideTitle() {
        ll_title.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showTitle() {
        ll_title.setVisibility(View.VISIBLE);
    }

}
