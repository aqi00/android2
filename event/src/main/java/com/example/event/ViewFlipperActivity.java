package com.example.event;

import java.util.ArrayList;

import com.example.event.constant.ImageList;
import com.example.event.util.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

/**
 * Created by ouyangshen on 2017/11/23.
 */
public class ViewFlipperActivity extends AppCompatActivity implements OnClickListener {
    private Button btn_control_flipper;
    private RelativeLayout rl_content; // 声明一个相对布局对象
    private ViewFlipper vf_content; // 声明一个飞掠视图对象
    private RadioGroup rg_indicator; // 声明一个单选组对象
    private boolean isPlaying = true; // 是否正在播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flipper);
        btn_control_flipper = findViewById(R.id.btn_control_flipper);
        // 从布局文件中获取名叫rl_content的相对布局
        rl_content = findViewById(R.id.rl_content);
        // 从布局文件中获取名叫banner_flipper的飞掠视图
        vf_content = findViewById(R.id.vf_content);
        // 从布局文件中获取名叫rg_indicator的单选组
        rg_indicator = findViewById(R.id.rg_indicator);
        btn_control_flipper.setOnClickListener(this);
        findViewById(R.id.btn_pre_flipper).setOnClickListener(this);
        findViewById(R.id.btn_next_flipper).setOnClickListener(this);
        initFlipper(); // 初始化横幅飞掠器
    }

    // 初始化横幅飞掠器
    private void initFlipper() {
        LayoutParams params = (LayoutParams) rl_content.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        // 设置相对布局的布局参数
        rl_content.setLayoutParams(params);
        ArrayList<Integer> imageList = ImageList.getDefault();
        // 下面给每个图片都分配一个场景，并加入到飞掠视图
        for (Integer imageID : imageList) {
            ImageView iv_item = new ImageView(this);
            iv_item.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            iv_item.setScaleType(ImageView.ScaleType.FIT_XY);
            iv_item.setImageResource(imageID);
            // 往飞掠视图添加一个图像视图
            vf_content.addView(iv_item);
        }
        int dip_15 = Utils.dip2px(this, 15);
        // 下面给每个图片都分配一个指示圆点
        for (int i = 0; i < imageList.size(); i++) {
            RadioButton radio = new RadioButton(this);
            radio.setLayoutParams(new RadioGroup.LayoutParams(dip_15, dip_15));
            radio.setGravity(Gravity.CENTER);
            radio.setButtonDrawable(R.drawable.indicator_selector);
            // 往单选组添加一个指示圆点
            rg_indicator.addView(radio);
        }
        // 设置飞掠视图当前展示的场景。这里默认展示第一张
        vf_content.setDisplayedChild(0);
        // 让飞掠视图自动开始翻页
        vf_content.setAutoStart(true);
        // 延迟200毫秒后启动指示器刷新任务
        mHandler.postDelayed(mRefresh, 200);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pre_flipper) { // 点击了往前翻页按钮
            vf_content.showPrevious(); // 显示上一个场景
        } else if (v.getId() == R.id.btn_next_flipper) { // 点击了往后翻页按钮
            vf_content.showNext(); // 显示下一个场景
        } else if (v.getId() == R.id.btn_control_flipper) { // 点击了停止自动翻页按钮
            isPlaying = !isPlaying;
            if (isPlaying) { // 正在翻页
                vf_content.startFlipping(); // 开始自动翻页
                btn_control_flipper.setText("停止自动翻页");
            } else { // 不在翻页
                vf_content.stopFlipping(); // 停止自动翻页
                btn_control_flipper.setText("开始自动翻页");
            }
        }
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个指示器的刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 获得正在播放的场景位置
            int pos = vf_content.getDisplayedChild();
            // 根据场景位置，设置当前的高亮指示圆点
            ((RadioButton) rg_indicator.getChildAt(pos)).setChecked(true);
            // 延迟200毫秒后再次启动指示器刷新任务
            mHandler.postDelayed(this, 200);
        }
    };

}
