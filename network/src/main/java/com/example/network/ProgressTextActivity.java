package com.example.network;

import com.example.network.widget.TextProgressBar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/11/11.
 */
public class ProgressTextActivity extends AppCompatActivity {
    private TextProgressBar tpb_progress; // 声明一个文本进度条对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_bar);
        // 从布局文件中获取名叫tpb_progress的文本进度条
        tpb_progress = findViewById(R.id.tpb_progress);
        initProgressSpinner();
    }

    // 初始化进度值下拉框
    private void initProgressSpinner() {
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
            // 设置文本进度条的当前进度
            tpb_progress.setProgress(progress);
            // 设置文本进度条的进度描述文本
            tpb_progress.setProgressText("当前处理进度为" + progress + "%");
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
