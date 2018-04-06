package com.example.group;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * Created by ouyangshen on 2017/10/21.
 */
public class DepartmentStoreActivity extends ActivityGroup implements OnClickListener {
    private static final String TAG = "DepartmentStoreActivity";
    private Bundle mBundle = new Bundle(); // 声明一个包裹对象
    private LinearLayout ll_container, ll_first, ll_second, ll_third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_store);
        // 从布局文件中获取名叫ll_container的线性布局，用于存放内容视图
        ll_container = findViewById(R.id.ll_container);
        ll_first = findViewById(R.id.ll_first); // 获取第一个标签的线性布局
        ll_second = findViewById(R.id.ll_second); // 获取第二个标签的线性布局
        ll_third = findViewById(R.id.ll_third); // 获取第三个标签的线性布局
        ll_first.setOnClickListener(this); // 给第一个标签注册点击监听器
        ll_second.setOnClickListener(this); // 给第二个标签注册点击监听器
        ll_third.setOnClickListener(this); // 给第三个标签注册点击监听器
        mBundle.putString("tag", TAG); // 往包裹中存入名叫tag的标记串
        changeContainerView(ll_first); // 默认显示第一个标签的内容视图
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_first || v.getId() == R.id.ll_second || v.getId() == R.id.ll_third) {
            changeContainerView(v); // 点击了哪个标签，就切换到该标签对应的内容视图
        }
    }

    // 内容视图改为展示指定的视图
    private void changeContainerView(View v) {
        ll_first.setSelected(false); // 取消选中第一个标签
        ll_second.setSelected(false); // 取消选中第二个标签
        ll_third.setSelected(false); // 取消选中第三个标签
        v.setSelected(true); // 选中指定标签
        if (v == ll_first) {
            // 切换到第一个活动页面DepartmentHomeActivity
            toActivity("first", DepartmentHomeActivity.class);
        } else if (v == ll_second) {
            // 切换到第二个活动页面DepartmentClassActivity
            toActivity("second", DepartmentClassActivity.class);
        } else if (v == ll_third) {
            // 切换到第三个活动页面DepartmentCartActivity
            toActivity("third", DepartmentCartActivity.class);
        }
    }

    // 把内容视图切换到对应的Activity活动页面
    private void toActivity(String label, Class<?> cls) {
        // 创建一个意图，并存入指定包裹
        Intent intent = new Intent(this, cls).putExtras(mBundle);
        // 移除内容框架下面的所有下级视图
        ll_container.removeAllViews();
        // 启动意图指向的活动，并获取该活动页面的顶层视图
        View v = getLocalActivityManager().startActivity(label, intent).getDecorView();
        // 设置内容视图的布局参数
        v.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        // 把活动页面的顶层视图（即内容视图）添加到内容框架上
        ll_container.addView(v);
    }

}
