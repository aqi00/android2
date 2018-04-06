package com.example.group;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TabHost;

/**
 * Created by ouyangshen on 2017/10/21.
 */
public class TabHostActivity extends TabActivity implements OnClickListener {
    private static final String TAG = "TabHostActivity";
    private Bundle mBundle = new Bundle(); // 声明一个包裹对象
    private TabHost tab_host; // 声明一个标签栏对象
    private LinearLayout ll_first, ll_second, ll_third;
    private String FIRST_TAG = "first"; // 第一个标签的标识串
    private String SECOND_TAG = "second"; // 第二个标签的标识串
    private String THIRD_TAG = "third"; // 第三个标签的标识串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        mBundle.putString("tag", TAG); // 往包裹中存入名叫tag的标记串
        ll_first = findViewById(R.id.ll_first); // 获取第一个标签的线性布局
        ll_second = findViewById(R.id.ll_second); // 获取第二个标签的线性布局
        ll_third = findViewById(R.id.ll_third); // 获取第三个标签的线性布局
        ll_first.setOnClickListener(this); // 给第一个标签注册点击监听器
        ll_second.setOnClickListener(this); // 给第二个标签注册点击监听器
        ll_third.setOnClickListener(this); // 给第三个标签注册点击监听器
        // 获取系统自带的标签栏，其实就是id为“@android:id/tabhost”的控件
        tab_host = getTabHost();
        // 往标签栏添加第一个标签，其中内容视图展示TabFirstActivity
        tab_host.addTab(getNewTab(FIRST_TAG, R.string.menu_first,
                R.drawable.tab_first_selector, TabFirstActivity.class));
        // 往标签栏添加第二个标签，其中内容视图展示TabSecondActivity
        tab_host.addTab(getNewTab(SECOND_TAG, R.string.menu_second,
                R.drawable.tab_second_selector, TabSecondActivity.class));
        // 往标签栏添加第三个标签，其中内容视图展示TabThirdActivity
        tab_host.addTab(getNewTab(THIRD_TAG, R.string.menu_third,
                R.drawable.tab_third_selector, TabThirdActivity.class));
        changeContainerView(ll_first); // 默认显示第一个标签的内容视图
    }

    // 根据定制参数获得新的标签规格
    private TabHost.TabSpec getNewTab(String spec, int label, int icon, Class<?> cls) {
        // 创建一个意图，并存入指定包裹
        Intent intent = new Intent(this, cls).putExtras(mBundle);
        // 生成并返回新的标签规格（包括内容意图、标签文字和标签图标）
        return tab_host.newTabSpec(spec).setContent(intent)
                .setIndicator(getString(label), getResources().getDrawable(icon));
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
            tab_host.setCurrentTabByTag(FIRST_TAG); // 设置当前标签为第一个标签
        } else if (v == ll_second) {
            tab_host.setCurrentTabByTag(SECOND_TAG); // 设置当前标签为第二个标签
        } else if (v == ll_third) {
            tab_host.setCurrentTabByTag(THIRD_TAG); // 设置当前标签为第三个标签
        }
    }
}
