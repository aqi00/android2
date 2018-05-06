package com.example.performance;

import com.example.performance.widget.CustomDialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/12/27.
 */
public class WindowStyleActivity extends AppCompatActivity {
    private int mStyle; // 风格样式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wnidow_style);
        initStyleSpinner();
    }

    // 初始化样式下拉框
    private void initStyleSpinner() {
        ArrayAdapter<String> backgroundAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, backgroundArray);
        Spinner sp_background = findViewById(R.id.sp_background);
        sp_background.setPrompt("请选择窗口背景风格样式");
        sp_background.setAdapter(backgroundAdapter);
        sp_background.setOnItemSelectedListener(new BackgroundSelectedListener());
        sp_background.setSelection(0);
    }

    private String[] backgroundArray = {
            "不显示对话框", "android:windowBackground风格",
            "android:background风格", "android:windowFrame风格"
    };
    private int[] styleArray = {0, R.style.CustomWindowBackground,
            R.style.CustomBackground, R.style.CustomFrame};

    class BackgroundSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 > 0) {
                mStyle = styleArray[arg2];
                // 延迟500毫秒后启动弹窗任务
                new Handler().postDelayed(mShowDialog, 500);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 定义一个弹窗任务，用于显示指定风格的对话框
    private Runnable mShowDialog = new Runnable() {
        @Override
        public void run() {
            CustomDialog dialog = new CustomDialog(WindowStyleActivity.this, mStyle);
            dialog.show(); // 显示自定义样式的对话框
        }
    };

}
