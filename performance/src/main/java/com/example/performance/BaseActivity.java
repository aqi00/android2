package com.example.performance;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/27.
 */
public class BaseActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onResume() {
        super.onResume();
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
        tl_head.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.iv_share).setOnClickListener(this);
    }

    // 设置页面标题
    protected void setTitle(String title) {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.iv_share) {
            Toast.makeText(this, "请先实现分享功能噢", Toast.LENGTH_LONG).show();
        }
    }
}
