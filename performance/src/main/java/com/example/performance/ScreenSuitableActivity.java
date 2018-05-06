package com.example.performance;

import com.example.performance.adapter.PlanetAdapter;
import com.example.performance.bean.Planet;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewStub;
import android.widget.GridView;
import android.widget.ListView;

/**
 * Created by ouyangshen on 2017/12/27.
 */
public class ScreenSuitableActivity extends BaseActivity {
    private static final String TAG = "ScreenSuitableActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_suitable);
        setTitle("自适应布局演示页面");
        // 获取当前的屏幕配置
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
            showList(); // 显示列表
        } else { // 横屏
            showGrid(); // 显示网格
        }
    }

    // 以列表形式呈现六大行星
    private void showList() {
        // 从布局文件中获取名叫vs_list的占位视图
        ViewStub vs_list = findViewById(R.id.vs_list);
        vs_list.inflate(); // 展开占位视图
        // 下面通过列表视图展示行星信息
        ListView lv_hello = findViewById(R.id.lv_hello);
        PlanetAdapter adapter = new PlanetAdapter(this, R.layout.item_list,
                Planet.getDefaultList(), Color.WHITE);
        lv_hello.setAdapter(adapter);
    }

    // 以网格形式呈现六大行星
    private void showGrid() {
        // 从布局文件中获取名叫vs_grid的占位视图
        ViewStub vs_grid = findViewById(R.id.vs_grid);
        vs_grid.inflate(); // 展开占位视图
        // 下面通过网格视图展示行星信息
        GridView gv_hello = findViewById(R.id.gv_hello);
        PlanetAdapter adapter = new PlanetAdapter(this, R.layout.item_grid,
                Planet.getDefaultList(), Color.WHITE);
        gv_hello.setAdapter(adapter);
    }

}
