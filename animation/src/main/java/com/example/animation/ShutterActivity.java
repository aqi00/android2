package com.example.animation;

import com.example.animation.widget.ShutterView;

import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class ShutterActivity extends AppCompatActivity {
    private ShutterView sv_shutter; // 声明一个百叶窗视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutter);
        // 从布局文件中获取名叫sv_shutter的百叶窗视图
        sv_shutter = findViewById(R.id.sv_shutter);
        // 设置百叶窗视图的位图对象
        sv_shutter.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bdg03));
        initShutterSpinner();
    }

    // 初始化动画类型下拉框
    private void initShutterSpinner() {
        ArrayAdapter<String> shutterAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, shutterArray);
        Spinner sp_shutter = findViewById(R.id.sp_shutter);
        sp_shutter.setPrompt("请选择百叶窗动画类型");
        sp_shutter.setAdapter(shutterAdapter);
        sp_shutter.setOnItemSelectedListener(new ShutterSelectedListener());
        sp_shutter.setSelection(0);
    }

    private String[] shutterArray = {"水平五叶", "水平十叶", "水平二十叶",
            "垂直五叶", "垂直十叶", "垂直二十叶"};
    class ShutterSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // 设置百叶窗的方向
            sv_shutter.setOriention((arg2 < 3) ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
            if (arg2 == 0 || arg2 == 3) {
                sv_shutter.setLeafCount(5); // 设置百叶窗的叶片数量
            } else if (arg2 == 1 || arg2 == 4) {
                sv_shutter.setLeafCount(10); // 设置百叶窗的叶片数量
            } else if (arg2 == 2 || arg2 == 5) {
                sv_shutter.setLeafCount(20); // 设置百叶窗的叶片数量
            }
            // 构造一个按比率逐步展开的属性动画
            ObjectAnimator anim = ObjectAnimator.ofInt(sv_shutter, "ratio", 0, 100);
            anim.setDuration(3000); // 设置动画的播放时长
            anim.start(); // 开始播放属性动画
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
