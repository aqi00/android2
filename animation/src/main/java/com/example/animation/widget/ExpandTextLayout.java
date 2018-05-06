package com.example.animation.widget;

import com.example.animation.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandTextLayout extends LinearLayout implements OnClickListener {
    private Context mContext; // 声明一个上下文对象
    private TextView tv_content; // 声明一个文本视图对象
    private int mNormalLines = 3; // 正常的行数
    private boolean isSelected = false; // 是否选中

    public ExpandTextLayout(Context context) {
        this(context, null);
    }

    public ExpandTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 从布局文件text_expand.xml中获得展示内容
        LayoutInflater.from(mContext).inflate(R.layout.text_expand, this, true);
    }

    // 在布局展示完毕后调用，因为getLineHeight方法要等渲染完成后才能得知具体高度
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 从布局文件中获取名叫ll_content的线性布局
        LinearLayout ll_content = findViewById(R.id.ll_content);
        ll_content.setOnClickListener(this);
        // 从布局文件中获取名叫tv_content的文本视图
        tv_content = findViewById(R.id.tv_content);
        // 设置文本视图的行高为n行文字那么高
        tv_content.setHeight(tv_content.getLineHeight() * mNormalLines);
    }

    // 设置文本内容
    public void setText(String content) {
        tv_content.setText(content);
    }

    // 设置文本的资源编号
    public void setText(int id) {
        setText(mContext.getResources().getString(id));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_content) {
            isSelected = !isSelected;
            // 清除文本视图的动画
            tv_content.clearAnimation();
            final int deltaValue;
            // 获得文本视图当前的高度
            final int startValue = tv_content.getHeight();
            if (isSelected) { // 变成选中，则显示展开后的所有文字
                deltaValue = tv_content.getLineHeight() * tv_content.getLineCount() - startValue;
            } else { // 变成未选中，则显示收缩后的正常行数
                deltaValue = tv_content.getLineHeight() * mNormalLines - startValue;
            }
            // 创建一个文本展开/收缩动画
            Animation animation = new Animation() {
                // 在动画变换过程中调用
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    // 随着时间流逝，重新设置文本视图的行高
                    tv_content.setHeight((int) (startValue + deltaValue * interpolatedTime));
                }
            };
            // 设置动画的持续时间为500毫秒
            animation.setDuration(500);
            // 开始文本视图的动画展示
            tv_content.startAnimation(animation);
        }
    }

}
