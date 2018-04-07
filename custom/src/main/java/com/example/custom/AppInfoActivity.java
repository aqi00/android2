package com.example.custom;

import java.util.ArrayList;

import com.example.custom.adapter.AppInfoAdapter;
import com.example.custom.bean.AppInfo;
import com.example.custom.util.AppUtil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class AppInfoActivity extends AppCompatActivity {
    private final static String TAG = "AppInfoActivity";
    private ListView lv_appinfo; // 声明一个列表视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        // 从布局文件中获取名叫lv_appinfo的列表视图
        lv_appinfo = findViewById(R.id.lv_appinfo);
        initTypeSpinner();
    }

    // 初始化应用类型的下拉框
    private void initTypeSpinner() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, typeArray);
        Spinner sp_list = findViewById(R.id.sp_type);
        sp_list.setPrompt("请选择应用类型");
        sp_list.setAdapter(typeAdapter);
        sp_list.setOnItemSelectedListener(new TypeSelectedListener());
        sp_list.setSelection(0);
    }

    private String[] typeArray = {"所有应用", "联网应用"};
    class TypeSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // 获取已安装的应用信息队列
            ArrayList<AppInfo> appinfoList = AppUtil.getAppInfo(AppInfoActivity.this, arg2);
            // 构建一个应用信息的列表适配器
            AppInfoAdapter adapter = new AppInfoAdapter(AppInfoActivity.this, appinfoList);
            // 给lv_appinfo设置应用信息列表适配器
            lv_appinfo.setAdapter(adapter);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
