package com.example.custom;

import com.example.custom.util.MeasureUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/14.
 */
@SuppressLint("DefaultLocale")
public class MeasureTextActivity extends AppCompatActivity {
    private TextView tv_desc, tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_text);
        tv_desc = findViewById(R.id.tv_desc);
        tv_text = findViewById(R.id.tv_text);
        initSizeSpinner();
    }

    // 初始化文字大小的下拉框
    private void initSizeSpinner() {
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, descArray);
        Spinner sp_size = findViewById(R.id.sp_size);
        sp_size.setPrompt("请选择文字大小");
        sp_size.setAdapter(sizeAdapter);
        sp_size.setOnItemSelectedListener(new SizeSelectedListener());
        sp_size.setSelection(0);
    }

    private String[] descArray = {"12sp", "15sp", "17sp", "20sp", "22sp", "25sp", "27sp", "30sp"};
    private int[] sizeArray = {12, 15, 17, 20, 22, 25, 27, 30};

    class SizeSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String text = tv_text.getText().toString();
            int textSize = sizeArray[arg2];
            tv_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            // 计算获取指定文本的宽度（其实就是长度）
            int width = (int) MeasureUtil.getTextWidth(text, textSize);
            // 计算获取指定文本的高度
            int height = (int) MeasureUtil.getTextHeight(text, textSize);
            String desc = String.format("下面文字的宽度是%d ,高度是%d", width, height);
            tv_desc.setText(desc);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }
}
