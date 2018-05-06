package com.example.network;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.network.widget.TextProgressCircle;

/**
 * Created by ouyangshen on 2017/11/11.
 */
public class ProgressCircleActivity extends AppCompatActivity {
    private TextProgressCircle tpc_progress; // 声明一个文本进度圈对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_circle);
        // 从布局文件中获取名叫tpc_progress的文本进度圈
        tpc_progress = findViewById(R.id.tpc_progress);
        initPorgressSpinner();
    }

    // 初始化进度值下拉框
    private void initPorgressSpinner() {
        ArrayAdapter<String> progressAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, progressArray);
        Spinner sp_progress = findViewById(R.id.sp_progress);
        sp_progress.setPrompt("请选择进度值");
        sp_progress.setAdapter(progressAdapter);
        sp_progress.setOnItemSelectedListener(new DividerSelectedListener());
        sp_progress.setSelection(0);
    }

    private String[] progressArray = {
            "0", "10", "20", "30", "40", "50",
            "60", "70", "80", "90", "100"
    };
    class DividerSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int progress = Integer.parseInt(progressArray[arg2]);
            // 设置文本进度圈的当前进度
            tpc_progress.setProgress(progress, -1);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
