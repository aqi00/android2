package com.example.storage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.storage.util.DateUtil;

/**
 * Created by ouyangshen on 2017/10/1.
 */
public class AppWriteActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_name;
    private EditText et_age;
    private EditText et_height;
    private EditText et_weight;
    private boolean bMarried = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_write);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        findViewById(R.id.btn_save).setOnClickListener(this);
        initTypeSpinner();
    }

    // 初始化婚姻状况的下拉框
    private void initTypeSpinner() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, typeArray);
        typeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp_married = findViewById(R.id.sp_married);
        sp_married.setPrompt("请选择婚姻状况");
        sp_married.setAdapter(typeAdapter);
        sp_married.setSelection(0);
        sp_married.setOnItemSelectedListener(new TypeSelectedListener());
    }

    private String[] typeArray = {"未婚", "已婚"};
    class TypeSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            bMarried = (arg2 == 0) ? false : true;
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            String name = et_name.getText().toString();
            String age = et_age.getText().toString();
            String height = et_height.getText().toString();
            String weight = et_weight.getText().toString();
            if (TextUtils.isEmpty(name)) {
                showToast("请先填写姓名");
                return;
            } else if (TextUtils.isEmpty(age)) {
                showToast("请先填写年龄");
                return;
            } else if (TextUtils.isEmpty(height)) {
                showToast("请先填写身高");
                return;
            } else if (TextUtils.isEmpty(weight)) {
                showToast("请先填写体重");
                return;
            }
            // 获取当前应用的Application实例
            MainApplication app = MainApplication.getInstance();
            // 以下直接修改Application实例中保存的映射全局变量
            app.mInfoMap.put("name", name);
            app.mInfoMap.put("age", age);
            app.mInfoMap.put("height", height);
            app.mInfoMap.put("weight", weight);
            app.mInfoMap.put("married", typeArray[!bMarried ? 0 : 1]);
            app.mInfoMap.put("update_time", DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss"));
            showToast("数据已写入全局内存");
        }
    }

    private void showToast(String desc) {
        Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
    }

}
