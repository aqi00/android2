package com.example.mixture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/11.
 */
public class JniSecretActivity extends AppCompatActivity implements OnClickListener {
    private EditText et_origin; // 声明一个用于输入原始字符串的编辑框对象
    private EditText et_encrypt; // 声明一个用于输入加密字符串的编辑框对象
    private TextView tv_decrypt;
    private String mKey = "123456789abcdef"; // 该算法要求密钥串的长度为16位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni_secret);
        et_origin = findViewById(R.id.et_origin);
        et_encrypt = findViewById(R.id.et_encrypt);
        tv_decrypt = findViewById(R.id.tv_decrypt);
        findViewById(R.id.btn_encrypt).setOnClickListener(this);
        findViewById(R.id.btn_decrypt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_encrypt) { // 点击了加密按钮
            // 调用JNI方法encryptFromJNI获得加密后的字符串
            String des = encryptFromJNI(et_origin.getText().toString(), mKey);
            et_encrypt.setText(des);
        } else if (v.getId() == R.id.btn_decrypt) { // 点击了解密按钮
            // 调用JNI方法decryptFromJNI获得解密后的字符串
            String raw = decryptFromJNI(et_encrypt.getText().toString(), mKey);
            tv_decrypt.setText(raw);
        }
    }

    // 声明encryptFromJNI是来自于JNI的原生方法
    public native String encryptFromJNI(String raw, String key);

    // 声明decryptFromJNI是来自于JNI的原生方法
    public native String decryptFromJNI(String des, String key);

    // 在加载当前类时就去加载jni_mix.so，加载动作发生在页面启动之前
    static {
        System.loadLibrary("jni_mix");
    }

}
