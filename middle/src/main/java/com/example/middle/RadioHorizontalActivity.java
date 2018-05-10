package com.example.middle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/9/24.
 */
public class RadioHorizontalActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private TextView tv_sex; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_horizontal);
        // 从布局文件中获取名叫tv_sex的文本视图
        tv_sex = findViewById(R.id.tv_sex);
        // 从布局文件中获取名叫rg_sex的单选组
        RadioGroup rg_sex = findViewById(R.id.rg_sex);
        // 给rg_sex设置单选监听器，一旦用户点击组内的单选按钮，就触发监听器的onCheckedChanged方法
        rg_sex.setOnCheckedChangeListener(this);
        // 给rg_sex设置单选监听器，一旦用户点击组内的单选按钮，就触发监听器的onCheckedChanged方法
        //rg_sex.setOnCheckedChangeListener(new RadioListener());
    }

    // 在用户点击组内的单选按钮时触发
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_male) {
            tv_sex.setText("哇哦，你是个帅气的男孩");
        } else if (checkedId == R.id.rb_female) {
            tv_sex.setText("哇哦，你是个漂亮的女孩");
        }
    }

    // 定义一个单选监听器，它实现了接口RadioGroup.OnCheckedChangeListener
    class RadioListener implements RadioGroup.OnCheckedChangeListener{
        // 在用户点击组内的单选按钮时触发
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Toast.makeText(RadioHorizontalActivity.this, "您选中了控件"+checkedId, Toast.LENGTH_LONG).show();
        }
    }
}
