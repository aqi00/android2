package com.example.group;

import com.example.group.fragment.TabFirstFragment;
import com.example.group.fragment.TabSecondFragment;
import com.example.group.fragment.TabThirdFragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/21.
 */
public class TabFragmentActivity extends AppCompatActivity {
    private static final String TAG = "TabFragmentActivity";
    private FragmentTabHost tabHost; // 声明一个碎片标签栏对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_fragment);
        Bundle bundle = new Bundle(); // 创建一个包裹对象
        bundle.putString("tag", TAG); // 往包裹中存入名叫tag的标记
        // 从布局文件中获取名叫tabhost的碎片标签栏
        tabHost = findViewById(android.R.id.tabhost);
        // 把实际的内容框架安装到碎片标签栏
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        // 往标签栏添加第一个标签，其中内容视图展示TabFirstFragment
        tabHost.addTab(getTabView(R.string.menu_first, R.drawable.tab_first_selector),
                TabFirstFragment.class, bundle);
        // 往标签栏添加第二个标签，其中内容视图展示TabSecondFragment
        tabHost.addTab(getTabView(R.string.menu_second, R.drawable.tab_second_selector),
                TabSecondFragment.class, bundle);
        // 往标签栏添加第三个标签，其中内容视图展示TabThirdFragment
        tabHost.addTab(getTabView(R.string.menu_third, R.drawable.tab_third_selector),
                TabThirdFragment.class, bundle);
        // 不显示各标签之间的分隔线
        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
    }

    // 根据字符串和图标的资源编号，获得对应的标签规格
    private TabSpec getTabView(int textId, int imgId) {
        // 根据资源编号获得字符串对象
        String text = getResources().getString(textId);
        // 根据资源编号获得图形对象
        Drawable drawable = getResources().getDrawable(imgId);
        // 设置图形的四周边界。这里必须设置图片大小，否则无法显示图标
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        // 根据布局文件item_tabbar.xml生成标签按钮对象
        View item_tabbar = getLayoutInflater().inflate(R.layout.item_tabbar, null);
        TextView tv_item = item_tabbar.findViewById(R.id.tv_item_tabbar);
        tv_item.setText(text);
        // 在文字上方显示标签的图标
        tv_item.setCompoundDrawables(null, drawable, null, null);
        // 生成并返回该标签按钮对应的标签规格
        return tabHost.newTabSpec(text).setIndicator(item_tabbar);
    }
}
