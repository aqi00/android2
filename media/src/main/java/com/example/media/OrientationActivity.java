package com.example.media;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.media.util.DateUtil;

public class OrientationActivity extends AppCompatActivity {
    private TextView tv_orientation;
    private String mDesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation);
        TextView tv_create = findViewById(R.id.tv_create);
        tv_orientation = findViewById(R.id.tv_orientation);
        String desc = String.format("%s %s", DateUtil.getNowTime(), "活动页面已创建，请旋转手机屏幕");
        tv_create.setText(desc);
    }

    // 在配置项变更时触发。比如屏幕方向发生变更等等
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) { // 判断当前的屏幕方向
            case Configuration.ORIENTATION_PORTRAIT: // 切换到竖屏
                mDesc = String.format("%s\n%s %s", mDesc,
                        DateUtil.getNowTime(), "当前屏幕为竖屏方向");
                tv_orientation.setText(mDesc);
                break;
            case Configuration.ORIENTATION_LANDSCAPE: // 切换到横屏
                mDesc = String.format("%s\n%s %s", mDesc,
                        DateUtil.getNowTime(), "当前屏幕为横屏方向");
                tv_orientation.setText(mDesc);
                break;
            default:
                break;
        }
    }
}
