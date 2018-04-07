package com.example.storage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.storage.util.DateUtil;

/**
 * Created by ouyangshen on 2017/9/24.
 */
public class ActNextActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "ActNextActivity";
    private TextView tv_life; // 声明一个文本视图对象
    private String mStr = "";

    private void refreshLife(String desc) { // 刷新生命周期的日志信息
        Log.d(TAG, desc);
        mStr = String.format("%s    %s %s %s\n", mStr, DateUtil.getNowTimeDetail(), TAG, desc);
        tv_life.setText(mStr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 创建活动页面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_next);
        findViewById(R.id.btn_act_pre).setOnClickListener(this);
        // 从布局文件中获取名叫tv_life的文本视图
        tv_life = findViewById(R.id.tv_life);
        refreshLife("onCreate");
    }

    @Override
    protected void onStart() { // 开始活动页面
        refreshLife("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() { // 停止活动页面
        refreshLife("onStop");
        super.onStop();
    }

    @Override
    protected void onResume() { // 恢复活动页面
        refreshLife("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() { // 暂停活动页面
        refreshLife("onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() { // 重启活动页面
        refreshLife("onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() { // 销毁活动页面
        refreshLife("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_act_pre) {
            Intent intent = new Intent(); // 创建一个新意图
            intent.putExtra("life", mStr); // 把字符串参数塞给意图
            setResult(Activity.RESULT_OK, intent); // 携带意图返回前一个页面
            finish(); // 关闭当前页面
        }
    }

}
