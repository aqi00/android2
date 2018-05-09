package com.example.group;

import com.example.group.adapter.LifeRecyclerAdapter;
import com.example.group.bean.LifeItem;
import com.example.group.util.Utils;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ouyangshen on 2017/9/3.
 */
public class ScrollAlipayActivity extends AppCompatActivity implements OnOffsetChangedListener {
    private final static String TAG = "ScrollAlipayActivity";
    private View tl_expand, tl_collapse; // 分别声明伸展时候与收缩时候的工具栏视图
    private View v_expand_mask, v_collapse_mask, v_pay_mask; // 分别声明三个遮罩视图
    private int mMaskColor; // 遮罩颜色

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_alipay);
        // 获取默认的蓝色遮罩颜色
        mMaskColor = getResources().getColor(R.color.blue_dark);
        // 从布局文件中获取名叫rv_content的循环视图
        RecyclerView rv_content = findViewById(R.id.rv_content);
        // 设置循环视图的布局管理器（四列的网格布局管理器）
        rv_content.setLayoutManager(new GridLayoutManager(this, 4));
        // 给rv_content设置生活频道网格适配器
        rv_content.setAdapter(new LifeRecyclerAdapter(this, LifeItem.getDefault()));
        // 从布局文件中获取名叫abl_bar的应用栏布局
        AppBarLayout abl_bar = findViewById(R.id.abl_bar);
        // 从布局文件中获取伸展之后的工具栏视图
        tl_expand = findViewById(R.id.tl_expand);
        // 从布局文件中获取收缩之后的工具栏视图
        tl_collapse = findViewById(R.id.tl_collapse);
        // 从布局文件中获取伸展之后的工具栏遮罩视图
        v_expand_mask = findViewById(R.id.v_expand_mask);
        // 从布局文件中获取收缩之后的工具栏遮罩视图
        v_collapse_mask = findViewById(R.id.v_collapse_mask);
        // 从布局文件中获取生活频道的遮罩视图
        v_pay_mask = findViewById(R.id.v_pay_mask);
        // 给abl_bar注册一个位置偏移的监听器
        abl_bar.addOnOffsetChangedListener(this);
    }

    // 每当应用栏向上滚动或者向下滚动，就会触发位置偏移监听器的onOffsetChanged方法
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int offset = Math.abs(verticalOffset);
        // 获取应用栏的整个滑动范围，以此计算当前的位移比例
        int total = appBarLayout.getTotalScrollRange();
        int alphaIn = Utils.px2dip(this, offset) * 2;
        int alphaOut = (200 - alphaIn) < 0 ? 0 : 200 - alphaIn;
        // 计算淡入时候的遮罩透明度
        int maskColorIn = Color.argb(alphaIn, Color.red(mMaskColor),
                Color.green(mMaskColor), Color.blue(mMaskColor));
        // 工具栏下方的生活频道布局要加速淡入或者淡出
        int maskColorInDouble = Color.argb(alphaIn * 2, Color.red(mMaskColor),
                Color.green(mMaskColor), Color.blue(mMaskColor));
        // 计算淡出时候的遮罩透明度
        int maskColorOut = Color.argb(alphaOut * 3, Color.red(mMaskColor),
                Color.green(mMaskColor), Color.blue(mMaskColor));
        if (offset <= total * 0.45) { // 偏移量小于一半，则显示伸展时候的工具栏
            tl_expand.setVisibility(View.VISIBLE);
            tl_collapse.setVisibility(View.GONE);
            v_expand_mask.setBackgroundColor(maskColorInDouble);
        } else { // 偏移量大于一半，则显示收缩时候的工具栏
            tl_expand.setVisibility(View.GONE);
            tl_collapse.setVisibility(View.VISIBLE);
            v_collapse_mask.setBackgroundColor(maskColorOut);
        }
        // 设置life_pay.xml即生活频道视图的遮罩颜色
        v_pay_mask.setBackgroundColor(maskColorIn);
    }
}
