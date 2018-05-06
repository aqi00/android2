package com.example.mixture;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/11.
 */
@SuppressLint("SetTextI18n")
public class JniCpuActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_cpu_jni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni_cpu);
        TextView tv_cpu_build = findViewById(R.id.tv_cpu_build);
        tv_cpu_build.setText("Build类获得的CPU指令集为" + Build.CPU_ABI);
        tv_cpu_jni = findViewById(R.id.tv_cpu_jni);
        findViewById(R.id.btn_cpu).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cpu) {
            // 调用JNI方法cpuFromJNI获得CPU信息
            String desc = cpuFromJNI(1, 0.5f, 99.9, true);
            tv_cpu_jni.setText(desc);
        }
    }

    // 声明cpuFromJNI是来自于JNI的原生方法
    public native String cpuFromJNI(int i1, float f1, double d1, boolean b1);

    // 在加载当前类时就去加载jni_mix.so，加载动作发生在页面启动之前
    static {
        System.loadLibrary("jni_mix");
    }

}
