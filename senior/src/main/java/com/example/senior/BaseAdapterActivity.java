package com.example.senior;

import java.util.ArrayList;

import com.example.senior.adapter.PlanetListAdapter;
import com.example.senior.bean.Planet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/10/7.
 */
public class BaseAdapterActivity extends AppCompatActivity {
    private ArrayList<Planet> planetList; // 声明一个行星队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_adapter);
        initPlanetSpinner();
    }

    // 初始化行星列表的下拉框
    private void initPlanetSpinner() {
        // 获取默认的行星队列，即水星、金星、地球、火星、木星、土星
        planetList = Planet.getDefaultList();
        // 构建一个行星列表的适配器
        PlanetListAdapter adapter = new PlanetListAdapter(this, planetList);
        // 从布局文件中获取名叫sp_planet的下拉框
        Spinner sp = findViewById(R.id.sp_planet);
        // 设置下拉框的标题
        sp.setPrompt("请选择行星");
        // 设置下拉框的列表适配器
        sp.setAdapter(adapter);
        // 设置下拉框默认显示第一项
        sp.setSelection(0);
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    // 定义一个选择监听器，它实现了接口OnItemSelectedListener
    private class MySelectedListener implements OnItemSelectedListener {
        // 选择事件的处理方法，其中arg2代表选择项的序号
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Toast.makeText(BaseAdapterActivity.this, "您选择的是" + planetList.get(arg2).name, Toast.LENGTH_LONG).show();
        }

        // 未选择时的处理方法，通常无需关注
        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
