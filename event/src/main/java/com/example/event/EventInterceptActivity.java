package com.example.event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.event.util.DateUtil;
import com.example.event.widget.InterceptLayout;
import com.example.event.widget.InterceptLayout.InterceptListener;

public class EventInterceptActivity extends AppCompatActivity implements
        OnClickListener, InterceptListener {
    private TextView tv_intercept_no;
    private TextView tv_intercept_yes;
    private String desc_no = "";
    private String desc_yes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_intercept);
        tv_intercept_no = findViewById(R.id.tv_intercept_no);
        tv_intercept_yes = findViewById(R.id.tv_intercept_yes);
        // 从布局文件中获取名叫il_yes的拦截布局
        InterceptLayout il_yes = findViewById(R.id.il_yes);
        // 设置拦截布局的事件拦截监听器
        il_yes.setInterceptListener(this);
        findViewById(R.id.btn_intercept_no).setOnClickListener(this);
        findViewById(R.id.btn_intercept_yes).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_intercept_no) {
            desc_no = String.format("%s%s 您点击了按钮\n", desc_no, DateUtil.getNowTime());
            tv_intercept_no.setText(desc_no);
        } else if (v.getId() == R.id.btn_intercept_yes) {
            desc_yes = String.format("%s%s 您点击了按钮\n", desc_yes, DateUtil.getNowTime());
            tv_intercept_yes.setText(desc_yes);
        }
    }

    // 在拦截触摸事件时触发
    public void onIntercept() {
        desc_yes = String.format("%s%s 触摸动作被拦截，按钮点击不了了\n", desc_yes,
                DateUtil.getNowTime());
        tv_intercept_yes.setText(desc_yes);
    }

}
