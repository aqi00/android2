package com.example.storage;

import com.example.storage.util.DateUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/1.
 */
public class MenuContextActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_context);
        tv_context = findViewById(R.id.tv_context);
        findViewById(R.id.btn_context).setOnClickListener(this);
        setRandomTime();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_context) {
            // 点击按钮而非长按打开上下文菜单，此时要先注册，然后显式打开，最后再注销
            registerForContextMenu(v); // 给按钮btn_context注册上下文菜单
            openContextMenu(v); // 显式打开上下文菜单
            unregisterForContextMenu(v); // 给按钮btn_context注销上下文菜单
        }
    }

    // 在页面恢复时调用
    protected void onResume() {
        // 给文本视图tv_context注册上下文菜单。
        // 注册之后，只要长按该控件，App就会自动打开上下文菜单
        registerForContextMenu(tv_context);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 给文本视图tv_context注销上下文菜单
        unregisterForContextMenu(tv_context);
        super.onPause();
    }

    // 在上下文菜单的菜单界面创建时调用
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // 从menu_option.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_option, menu);
    }

    // 在上下文菜单的菜单项选中时调用
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId(); // 获取菜单项的编号
        if (id == R.id.menu_change_time) { // 点击了菜单项“改变时间”
            setRandomTime();
        } else if (id == R.id.menu_change_color) { // 点击了菜单项“改变颜色”
            tv_context.setTextColor(getRandomColor());
        } else if (id == R.id.menu_change_bg) { // 点击了菜单项“改变背景”
            tv_context.setBackgroundColor(getRandomColor());
        }
        return true;
    }

    private void setRandomTime() {
        String desc = DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss") + " 这里是菜单显示文本";
        tv_context.setText(desc);
    }

    private int[] mColorArray = {
            Color.BLACK, Color.WHITE, Color.RED, Color.YELLOW, Color.GREEN,
            Color.BLUE, Color.CYAN, Color.MAGENTA, Color.GRAY, Color.DKGRAY
    };

    // 获取随机的颜色值
    private int getRandomColor() {
        int random = (int) (Math.random() * 10 % 10);
        return mColorArray[random];
    }

}
