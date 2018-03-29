package com.example.junior;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/9/14.
 */
public class ClickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);
        // 从布局文件中获取名叫btn_click的按钮控件
        Button btn_click = findViewById(R.id.btn_click);
        // 给btn_click设置点击监听器，一旦用户点击按钮，就触发监听器的onClick方法
        btn_click.setOnClickListener(new MyOnClickListener());
        // 给btn_click设置长按监听器，一旦用户长按按钮，就触发监听器的onLongClick方法
        btn_click.setOnLongClickListener(new MyOnLongClickListener());
    }

    // 定义一个点击监听器，它实现了接口View.OnClickListener
    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) { // 点击事件的处理方法
            if (v.getId() == R.id.btn_click) { // 判断是否为btn_click被点击
                Toast.makeText(ClickActivity.this, "您点击了控件：" + ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 定义一个长按监听器，它实现了接口View.OnLongClickListener
    class MyOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) { // 长按事件的处理方法
            if (v.getId() == R.id.btn_click) { // 判断是否为btn_click被长按
                Toast.makeText(ClickActivity.this, "您长按了控件：" + ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
}
