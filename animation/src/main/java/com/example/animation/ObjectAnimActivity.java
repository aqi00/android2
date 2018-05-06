package com.example.animation;

import android.animation.RectEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/27.
 */
public class ObjectAnimActivity extends AppCompatActivity {
    private ImageView iv_object_anim; // 声明一个图像视图对象
    private ObjectAnimator alphaAnim, translateAnim, scaleAnim, rotateAnim; // 分别声明四个属性动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_anim);
        // 从布局文件中获取名叫iv_object_anim的图像视图
        iv_object_anim = findViewById(R.id.iv_object_anim);
        initAnimator(); // 初始化属性动画
        initObjectSpinner();
    }

    // 初始化属性动画
    private void initAnimator() {
        // 构造一个在透明度上变化的属性动画
        alphaAnim = ObjectAnimator.ofFloat(iv_object_anim, "alpha", 1f, 0.1f, 1f);
        // 构造一个在横轴上平移的属性动画
        translateAnim = ObjectAnimator.ofFloat(iv_object_anim, "translationX", 0f, -200f, 0f, 200f, 0f);
        // 构造一个在纵轴上缩放的属性动画
        scaleAnim = ObjectAnimator.ofFloat(iv_object_anim, "scaleY", 1f, 0.5f, 1f);
        // 构造一个围绕中心点旋转的属性动画
        rotateAnim = ObjectAnimator.ofFloat(iv_object_anim, "rotation", 0f, 360f, 0f);
    }

    // 初始化动画类型下拉框
    private void initObjectSpinner() {
        ArrayAdapter<String> objectAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, objectArray);
        Spinner sp_object = findViewById(R.id.sp_object);
        sp_object.setPrompt("请选择属性动画类型");
        sp_object.setAdapter(objectAdapter);
        sp_object.setOnItemSelectedListener(new ObjectSelectedListener());
        sp_object.setSelection(0);
    }

    private String[] objectArray = {"灰度动画", "平移动画", "缩放动画", "旋转动画", "裁剪动画"};
    class ObjectSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            showAnimation(arg2); // 显示指定类型的属性动画
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    // 显示指定类型的属性动画
    private void showAnimation(int type) {
        ObjectAnimator anim = null;
        if (type == 0) { // 灰度动画
            anim = alphaAnim;
        } else if (type == 1) { // 平移动画
            anim = translateAnim;
        } else if (type == 2) { // 缩放动画
            anim = scaleAnim;
        } else if (type == 3) { // 旋转动画
            anim = rotateAnim;
        } else if (type == 4) { // 裁剪动画
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Toast.makeText(this, "矩形估值器需要Android4.3及以上版本",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            int width = iv_object_anim.getWidth();
            int height = iv_object_anim.getHeight();
            // 构造一个从四周向中间裁剪的属性动画
            ObjectAnimator clipAnim = ObjectAnimator.ofObject(iv_object_anim, "clipBounds",
                    new RectEvaluator(), new Rect(0, 0, width, height),
                    new Rect(width / 3, height / 3, width / 3 * 2, height / 3 * 2),
                    new Rect(0, 0, width, height));
            anim = clipAnim;
        }
        if (anim != null) {
            anim.setDuration(3000); // 设置动画的播放时长
            anim.start(); // 开始播放属性动画
        }
    }

}
