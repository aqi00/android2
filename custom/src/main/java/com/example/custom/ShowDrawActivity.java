package com.example.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.custom.widget.DrawRelativeLayout;

/**
 * Created by ouyangshen on 2017/10/14.
 */
public class ShowDrawActivity extends AppCompatActivity {
    private DrawRelativeLayout drl_content; // 声明一个绘画布局对象
    private Button btn_center;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_draw);
        // 从布局文件中获取名叫drl_content的绘画布局
        drl_content = findViewById(R.id.drl_content);
        btn_center = findViewById(R.id.btn_center);
        initTypeSpinner();
    }

    // 初始化绘图方式的下拉框
    private void initTypeSpinner() {
        ArrayAdapter<String> drawAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, descArray);
        Spinner sp_draw = findViewById(R.id.sp_draw);
        sp_draw.setPrompt("请选择绘图方式");
        sp_draw.setAdapter(drawAdapter);
        sp_draw.setOnItemSelectedListener(new DrawSelectedListener());
        sp_draw.setSelection(0);
    }

    private String[] descArray = {"不画图", "画矩形", "画圆角矩形", "画圆圈", "画椭圆",
            "onDraw画叉叉", "dispatchDraw画叉叉"};
    private int[] typeArray = {0, 1, 2, 3, 4, 5, 6};

    class DrawSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int type = typeArray[arg2];
            if (type == 5 || type == 6) {
                btn_center.setVisibility(View.VISIBLE);
            } else {
                btn_center.setVisibility(View.GONE);
            }
            // 设置绘图布局的绘制类型
            drl_content.setDrawType(type);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
