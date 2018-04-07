package com.example.senior;

import java.util.ArrayList;

import com.example.senior.adapter.PlanetListAdapter;
import com.example.senior.bean.Planet;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class ListViewActivity extends AppCompatActivity {
    private final static String TAG = "ListViewActivity";
    private ListView lv_planet; // 声明一个列表视图对象
    private Drawable drawable;  // 声明一个图形对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ArrayList<Planet> planetList = Planet.getDefaultList();
        // 构建一个行星队列的列表适配器
        PlanetListAdapter adapter = new PlanetListAdapter(this, planetList);
        // 从布局视图中获取名叫lv_planet的列表视图
        lv_planet = findViewById(R.id.lv_planet);
        // 给lv_planet设置行星列表适配器
        lv_planet.setAdapter(adapter);
        // 给lv_planet设置列表项的点击监听器
        lv_planet.setOnItemClickListener(adapter);
        // 给lv_planet设置列表项的长按监听器
        lv_planet.setOnItemLongClickListener(adapter);
        // 从资源文件中获取分隔线的图形对象
        drawable = getResources().getDrawable(R.drawable.divider_red2);
        // 初始化分隔线下拉框
        initDividerSpinner();
    }

    // 初始化分隔线显示方式的下拉框
    private void initDividerSpinner() {
        ArrayAdapter<String> dividerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, dividerArray);
        Spinner sp_list = findViewById(R.id.sp_list);
        sp_list.setPrompt("请选择分隔线显示方式");
        sp_list.setAdapter(dividerAdapter);
        sp_list.setOnItemSelectedListener(new DividerSelectedListener());
        sp_list.setSelection(0);
    }

    private String[] dividerArray = {
            "不显示分隔线(分隔线高度为0)",
            "不显示分隔线(分隔线为null)",
            "只显示内部分隔线(先设置分隔线高度)",
            "只显示内部分隔线(后设置分隔线高度)",
            "显示底部分隔线(高度是wrap_content)",
            "显示底部分隔线(高度是match_parent)",
            "显示顶部分隔线(别瞎折腾了，显示不了)",
            "显示全部分隔线(看我用padding大法)"
    };
    class DividerSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int dividerHeight = 5;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lv_planet.setDivider(drawable);  // 设置lv_planet的分隔线
            lv_planet.setDividerHeight(dividerHeight);  // 设置lv_planet的分隔线高度
            lv_planet.setPadding(0, 0, 0, 0);  // 设置lv_planet的四周空白
            lv_planet.setBackgroundColor(Color.TRANSPARENT);  // 设置lv_planet的背景颜色
            if (arg2 == 0) {  // 不显示分隔线(分隔线高度为0)
                lv_planet.setDividerHeight(0);
            } else if (arg2 == 1) {  // 不显示分隔线(分隔线为null)
                lv_planet.setDivider(null);
                lv_planet.setDividerHeight(dividerHeight);
            } else if (arg2 == 2) {  // 只显示内部分隔线(先设置分隔线高度)
                lv_planet.setDividerHeight(dividerHeight);
                lv_planet.setDivider(drawable);
            } else if (arg2 == 3) {  // 只显示内部分隔线(后设置分隔线高度)
                lv_planet.setDivider(drawable);
                lv_planet.setDividerHeight(dividerHeight);
            } else if (arg2 == 4) {  // 显示底部分隔线(高度是wrap_content)
                lv_planet.setFooterDividersEnabled(true);
            } else if (arg2 == 5) {  // 显示底部分隔线(高度是match_parent)
                params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
                lv_planet.setFooterDividersEnabled(true);
            } else if (arg2 == 6) {  // 显示顶部分隔线(别瞎折腾了，显示不了)
                params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
                lv_planet.setFooterDividersEnabled(true);
                lv_planet.setHeaderDividersEnabled(true);
            } else if (arg2 == 7) {  // 显示全部分隔线(看我用padding大法)
                lv_planet.setDivider(null);
                lv_planet.setDividerHeight(dividerHeight);
                lv_planet.setPadding(0, dividerHeight, 0, dividerHeight);
                lv_planet.setBackgroundDrawable(drawable);
            }
            lv_planet.setLayoutParams(params);  // 设置lv_planet的布局参数
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
