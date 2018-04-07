package com.example.storage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.storage.util.DateUtil;
import com.example.storage.util.FileUtil;

/**
 * Created by ouyangshen on 2017/10/1.
 */
@SuppressLint("SetTextI18n")
public class TextWriteActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_name;
    private EditText et_age;
    private EditText et_height;
    private EditText et_weight;
    private boolean bMarried = false;
    private String mPath;
    private TextView tv_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_write);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        tv_path = findViewById(R.id.tv_path);
        findViewById(R.id.btn_save).setOnClickListener(this);
        // 获取当前App的私有存储目录
        mPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
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

            String content = "";
            content = String.format("%s　姓名：%s\n", content, name);
            content = String.format("%s　年龄：%s\n", content, age);
            content = String.format("%s　身高：%scm\n", content, height);
            content = String.format("%s　体重：%skg\n", content, weight);
            content = String.format("%s　婚否：%s\n", content, typeArray[!bMarried ? 0 : 1]);
            content = String.format("%s　注册时间：%s\n", content, DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss"));
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String file_path = mPath + DateUtil.getNowDateTime("") + ".txt";
                // 把文本字符串保存为文本文件
                FileUtil.saveText(file_path, content);
                tv_path.setText("用户注册信息文件的保存路径为：\n" + file_path);
                showToast("数据已写入SD卡文件");
            } else {
                showToast("未发现已挂载的SD卡，请检查");
            }
        }
    }

    private void showToast(String desc) {
        Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
    }

}
