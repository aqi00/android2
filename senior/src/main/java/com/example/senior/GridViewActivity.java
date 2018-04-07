package com.example.senior;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.senior.adapter.PlanetGridAdapter;
import com.example.senior.bean.Planet;
import com.example.senior.util.Utils;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class GridViewActivity extends AppCompatActivity {
    private final static String TAG = "GridViewActivity";
    private GridView gv_planet; // 声明一个网格视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        ArrayList<Planet> planetList = Planet.getDefaultList();
        // 构建一个行星队列的网格适配器
        PlanetGridAdapter adapter = new PlanetGridAdapter(this, planetList);
        // 从布局视图中获取名叫gv_planet的网格视图
        gv_planet = findViewById(R.id.gv_planet);
        // 给gv_planet设置行星网格适配器
        gv_planet.setAdapter(adapter);
        // 给gv_planet设置网格项的点击监听器
        gv_planet.setOnItemClickListener(adapter);
        // 给gv_planet设置网格项的长按监听器
        gv_planet.setOnItemLongClickListener(adapter);
        // 初始化分隔线下拉框
        initDividerSpinner();
    }

    // 初始化分隔线显示方式的下拉框
    private void initDividerSpinner() {
        ArrayAdapter<String> dividerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, dividerArray);
        Spinner sp_grid = findViewById(R.id.sp_grid);
        sp_grid.setPrompt("请选择分隔线显示方式");
        sp_grid.setAdapter(dividerAdapter);
        sp_grid.setOnItemSelectedListener(new DividerSelectedListener());
        sp_grid.setSelection(0);
    }

    private String[] dividerArray = {
            "不显示分隔线",
            "只显示内部分隔线(NO_STRETCH)",
            "只显示内部分隔线(COLUMN_WIDTH)",
            "只显示内部分隔线(STRETCH_SPACING)",
            "只显示内部分隔线(SPACING_UNIFORM)",
            "显示全部分隔线(看我用padding大法)"
    };
    class DividerSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int dividerPad = Utils.dip2px(GridViewActivity.this, 2); // 定义间隔宽度为2dp
            gv_planet.setBackgroundColor(Color.RED);  // 设置gv_planet的背景颜色
            gv_planet.setHorizontalSpacing(dividerPad);  // 设置gv_planet的水平方向空白
            gv_planet.setVerticalSpacing(dividerPad);  // 设置gv_planet的垂直方向空白
            gv_planet.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);  // 设置gv_planet的拉伸模式
            gv_planet.setColumnWidth(250);  // 设置gv_planet的每列宽度为250
            gv_planet.setPadding(0, 0, 0, 0);  // 设置gv_planet的四周空白
            if (arg2 == 0) {  // 不显示分隔线
                gv_planet.setBackgroundColor(Color.WHITE);
                gv_planet.setHorizontalSpacing(0);
                gv_planet.setVerticalSpacing(0);
            } else if (arg2 == 1) {  // 只显示内部分隔线(NO_STRETCH)
                gv_planet.setStretchMode(GridView.NO_STRETCH);
            } else if (arg2 == 2) {  // 只显示内部分隔线(COLUMN_WIDTH)
                gv_planet.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            } else if (arg2 == 3) {  // 只显示内部分隔线(STRETCH_SPACING)
                gv_planet.setStretchMode(GridView.STRETCH_SPACING);
            } else if (arg2 == 4) {  // 只显示内部分隔线(SPACING_UNIFORM)
                gv_planet.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
            } else if (arg2 == 5) {  // 显示全部分隔线（使用padding）
                gv_planet.setPadding(dividerPad, dividerPad, dividerPad, dividerPad);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
