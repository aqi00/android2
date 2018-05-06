package com.example.media;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
public class SpannableActivity extends AppCompatActivity {
    private TextView tv_spannable; // 声明一个用于展示可变字符串的文本视图对象
    private String mText = "为人民服务"; // 原始字符串
    private String mKey = "人民"; // 关键字
    private int mBeginPos, mEndPos; // 起始位置和结束位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spannable);
        tv_spannable = findViewById(R.id.tv_spannable);
        tv_spannable.setText(mText);
        mBeginPos = mText.indexOf(mKey); // 获取关键字在源字符串中的起始位置
        mEndPos = mBeginPos + mKey.length(); // 获取关键字在源字符串中的结束位置
        initSpannableSpinner();
    }

    // 初始化可变样式的下拉框
    private void initSpannableSpinner() {
        ArrayAdapter<String> spannableAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, spannableArray);
        Spinner sp_spannable = findViewById(R.id.sp_spannable);
        sp_spannable.setPrompt("请选择可变字符串样式");
        sp_spannable.setAdapter(spannableAdapter);
        sp_spannable.setOnItemSelectedListener(new SpannableSelectedListener());
        sp_spannable.setSelection(0);
    }

    private String[] spannableArray = {
            "增大字号", "加粗字体", "前景红色", "背景绿色", "下划线", "表情图片"
    };
    class SpannableSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // 创建一个可变字符串
            SpannableString spanText = new SpannableString(mText);
            if (arg2 == 0) { // 增大字号
                spanText.setSpan(new RelativeSizeSpan(1.5f), mBeginPos, mEndPos,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 1) { // 加粗字体
                spanText.setSpan(new StyleSpan(Typeface.BOLD), mBeginPos, mEndPos,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 2) { // 前景红色
                spanText.setSpan(new ForegroundColorSpan(Color.RED), mBeginPos,
                        mEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 3) { // 背景绿色
                spanText.setSpan(new BackgroundColorSpan(Color.GREEN), mBeginPos,
                        mEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 4) { // 下划线
                spanText.setSpan(new UnderlineSpan(), mBeginPos, mEndPos,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (arg2 == 5) { // 表情图片
                spanText.setSpan(new ImageSpan(SpannableActivity.this, R.drawable.people),
                        mBeginPos, mEndPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv_spannable.setText(spanText);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
