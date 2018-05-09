package com.example.custom;

import com.example.custom.util.MeasureUtil;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class PullRefreshActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "PullRefreshActivity";
    private LinearLayout ll_header;
    private Button btn_pull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_refresh);
        ll_header = findViewById(R.id.ll_header);
        btn_pull = findViewById(R.id.btn_pull);
        ll_header.setVisibility(View.GONE);
        btn_pull.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 计算获取线性布局的实际高度
        int height = (int) MeasureUtil.getRealHeight(ll_header);
        if (v.getId() == R.id.btn_pull) {
            if (!isStarted) { // 不在刷新，则开始下拉刷新
                mOffset = -height;
                btn_pull.setEnabled(false);
                // 立即开始下拉刷新任务
                mHandler.post(mRefresh);
            } else { // 已在刷新，则停止下拉刷新
                btn_pull.setText("开始下拉");
                ll_header.setVisibility(View.GONE);
            }
            isStarted = !isStarted;
        }
    }

    private boolean isStarted = false; // 是否开始刷新
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private int mOffset = 0; // 刷新过程中的下拉偏移
    // 定义一个下拉刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (mOffset <= 0) { // 尚未下拉到位
                // 通过设置视图上方的间隔，达到布局缩进的效果
                ll_header.setPadding(0, mOffset, 0, 0);
                ll_header.setVisibility(View.VISIBLE);
                mOffset += 8;
                // 延迟八十毫秒后重复刷新任务
                mHandler.postDelayed(this, 80);
            } else { // 已经下拉到顶了
                btn_pull.setText("恢复页面");
                btn_pull.setEnabled(true);
            }
        }
    };

}
