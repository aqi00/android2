package com.example.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.custom.util.Utils;
import com.example.custom.widget.OffsetLayout;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class OnLayoutActivity extends AppCompatActivity {
    private OffsetLayout ol_content; // 声明一个偏移布局对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_layout);
        // 从布局文件中获取名叫ol_content的偏移布局
        ol_content = findViewById(R.id.ol_content);
        initOffsetSpinner();
    }

    // 初始化偏移大小的下拉框
    private void initOffsetSpinner() {
        ArrayAdapter<String> offsetAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, descArray);
        Spinner sp_offset = findViewById(R.id.sp_offset);
        sp_offset.setPrompt("请选择偏移大小");
        sp_offset.setAdapter(offsetAdapter);
        sp_offset.setOnItemSelectedListener(new OffsetSelectedListener());
        sp_offset.setSelection(0);
    }

    private String[] descArray = {"无偏移", "向左偏移50", "向右偏移50", "向上偏移50", "向下偏移50"};
    private int[] offsetArray = {0, -50, 50, -50, 50};

    class OffsetSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int offset = Utils.dip2px(OnLayoutActivity.this, offsetArray[arg2]);
            if (arg2 == 0 || arg2 == 1 || arg2 == 2) {
                // 设置偏移布局在水平方向上的偏移量
                ol_content.setOffsetHorizontal(offset);
            } else if (arg2 == 3 || arg2 == 4) {
                // 设置偏移布局在垂直方向上的偏移量
                ol_content.setOffsetVertical(offset);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
