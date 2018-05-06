package com.example.event;

import com.example.event.widget.PanoramaView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class GlPanoramaActivity extends AppCompatActivity {
    private final static String TAG = "GlPanoramaActivity";
    private PanoramaView pv_content; // 声明一个全景视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_panorama);
        // 从布局文件中获取名叫pv_content的全景视图
        pv_content = findViewById(R.id.pv_content);
        // 设置全景视图的全景图片
        pv_content.initRender(resArray[0]);
        initExampleSpinner();
    }

    // 初始化样例下拉框
    private void initExampleSpinner() {
        ArrayAdapter<String> exampleAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, exampleArray);
        Spinner sp_example = findViewById(R.id.sp_example);
        sp_example.setPrompt("请选择全景照片例子");
        sp_example.setAdapter(exampleAdapter);
        sp_example.setOnItemSelectedListener(new ExampleSelectedListener());
        sp_example.setSelection(0);
    }

    private String[] exampleArray = {"居家生活", "城市街景", "故宫风光", "海滨栈道"};
    private int[] resArray = {R.drawable.panorama01, R.drawable.panorama02,
            R.drawable.panorama03, R.drawable.panorama04};

    class ExampleSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
            pv_content.setDrawableId(resArray[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放全景视图的陀螺仪传感器
        pv_content.releaseSensor();
    }
}
