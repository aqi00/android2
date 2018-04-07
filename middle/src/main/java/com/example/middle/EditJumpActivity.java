package com.example.middle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/9/24.
 */
public class EditJumpActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_jump);
        // 从布局文件中获取名叫et_username的用户名编辑框
        EditText et_username = findViewById(R.id.et_username);
        // 从布局文件中获取名叫et_password的密码编辑框
        EditText et_password = findViewById(R.id.et_password);
        Button btn_login = findViewById(R.id.btn_login);
        // 给用户名编辑框添加文本变化监听器
        et_username.addTextChangedListener(new JumpTextWatcher(et_username, et_password));
        // 给密码编辑框添加文本变化监听器
        et_password.addTextChangedListener(new JumpTextWatcher(et_password, btn_login));
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            Toast.makeText(this, "这个登录按钮啥事也没做", Toast.LENGTH_SHORT).show();
        }
    }

    // 定义一个编辑框监听器，在输入回车符时自动跳到下一个控件
    private class JumpTextWatcher implements TextWatcher {
        private EditText mThisView;  // 声明当前的编辑框对象
        private View mNextView; // 声明下一个视图对象

        public JumpTextWatcher(EditText vThis, View vNext) {
            super();
            mThisView = vThis;
            if (vNext != null) {
                mNextView = vNext;
            }
        }

        // 在编辑框的输入文本变化前触发
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        // 在编辑框的输入文本变化时触发
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        // 在编辑框的输入文本变化后触发
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            // 发现输入回车符或换行符
            if (str.contains("\r") || str.contains("\n")) {
                // 去掉回车符和换行符
                mThisView.setText(str.replace("\r", "").replace("\n", ""));
                if (mNextView != null) {
                    // 让下一个视图获得焦点，即将光标移到下个视图
                    mNextView.requestFocus();
                    // 如果下一个视图是编辑框，则将光标自动移到编辑框的文本末尾
                    if (mNextView instanceof EditText) {
                        EditText et = (EditText) mNextView;
                        // 让光标自动移到编辑框内部的文本末尾
                        // 方式一：直接调用EditText的setSelection方法
                        et.setSelection(et.getText().length());
                        // 方式二：调用Selection类的setSelection方法
                        //Editable edit = et.getText();
                        //Selection.setSelection(edit, edit.length());
                    }
                }
            }
        }
    }
}
