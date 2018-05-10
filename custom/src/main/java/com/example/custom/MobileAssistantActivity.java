package com.example.custom;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.custom.adapter.TrafficInfoAdapter;
import com.example.custom.bean.AppInfo;
import com.example.custom.service.TrafficService;
import com.example.custom.util.AppUtil;
import com.example.custom.util.DateUtil;
import com.example.custom.util.SharedUtil;
import com.example.custom.util.StringUtil;
import com.example.custom.widget.CircleAnimation;
import com.example.custom.widget.CustomDateDialog;
import com.example.custom.widget.CustomDateDialog.OnDateSetListener;
import com.example.custom.widget.NoScrollListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/14.
 */
@SuppressLint("DefaultLocale")
public class MobileAssistantActivity extends Activity implements
        OnClickListener, OnDateSetListener {
    private final static String TAG = "MobileAssistantActivity";
    private TextView tv_day;
    private RelativeLayout rl_month;
    private TextView tv_month_traffic;
    private RelativeLayout rl_day;
    private TextView tv_day_traffic;
    private NoScrollListView nslv_traffic; // 声明一个不滚动列表视图
    private int mDay; // 选择的日期
    private int mNowDay; // 今天的日期
    private long traffic_month = 0; // 月流量数据
    private long traffic_day = 0; // 日流量数据
    private int limit_month; // 月流量限额
    private int limit_day; // 日流量限额
    private int line_width = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_assistant);
        // 创建一个通往流量服务的意图
        Intent intent = new Intent(this, TrafficService.class);
        // 启动指定意图的服务
        startService(intent);
        initView();
    }

    // 初始化各视图对象
    private void initView() {
        tv_day = findViewById(R.id.tv_day);
        rl_month = findViewById(R.id.rl_month);
        tv_month_traffic = findViewById(R.id.tv_month_traffic);
        rl_day = findViewById(R.id.rl_day);
        tv_day_traffic = findViewById(R.id.tv_day_traffic);
        // 从布局文件中获取名叫nslv_traffic的不滚动列表视图
        nslv_traffic = findViewById(R.id.nslv_traffic);
        findViewById(R.id.iv_menu).setOnClickListener(this);
        findViewById(R.id.iv_refresh).setOnClickListener(this);
        // 从共享参数中读取月流量限额
        limit_month = SharedUtil.getIntance(this).readInt("limit_month", 1024);
        // 从共享参数中读取日流量限额
        limit_day = SharedUtil.getIntance(this).readInt("limit_day", 30);
        mNowDay = Integer.parseInt(DateUtil.getNowDateTime("yyyyMMdd"));
        mDay = mNowDay;
        String day = DateUtil.getNowDateTime("yyyy年MM月dd日");
        tv_day.setText(day);
        tv_day.setOnClickListener(this);
        // 延迟500毫秒后开始刷新日流量数据
        mHandler.postDelayed(mDayRefresh, 500);
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个日流量的刷新任务
    private Runnable mDayRefresh = new Runnable() {
        @Override
        public void run() {
            refreshTraffic(mDay);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_day) { // 点击了日期文本
            Calendar calendar = Calendar.getInstance();
            // 弹出自定义的日期选择对话框
            CustomDateDialog dialog = new CustomDateDialog(this);
            dialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), this);
            dialog.show();
        } else if (v.getId() == R.id.iv_menu) { // 点击了三点菜单图标
            // 跳转到流量限额配置页面
            Intent intent = new Intent(this, MobileConfigActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.iv_refresh) { // 点击了转圈刷新图标
            mDay = mNowDay;
            // 立即启动今天的流量刷新任务
            mHandler.post(mDayRefresh);
        }
    }

    @Override
    public void onDateSet(int year, int month, int day) {
         String date = String.format("%d年%d月%d日", year, month, day);
        tv_day.setText(date);
        mDay = year * 10000 + month * 100 + day;
        // 选择完日期，立即启动流量刷新任务
        mHandler.post(mDayRefresh);
    }

    // 刷新指定日期的流量数据
    private void refreshTraffic(int day) {
        String last_date = DateUtil.getAddDate("" + day, -1);
        // 查询数据库获得截止到昨日的应用流量
        ArrayList<AppInfo> lastArray = MainApplication.getInstance().mTrafficHelper.query("day=" + last_date);
        // 查询数据库获得截止到今日的应用流量
        ArrayList<AppInfo> thisArray = MainApplication.getInstance().mTrafficHelper.query("day=" + day);
        ArrayList<AppInfo> newArray = new ArrayList<AppInfo>();
        traffic_day = 0;
        // 截止到今日的应用流量减去截止到昨日的应用流量，二者之差便是今日的流量数据
        for (int i = 0; i < thisArray.size(); i++) {
            AppInfo item = thisArray.get(i);
            for (int j = 0; j < lastArray.size(); j++) {
                if (item.uid == lastArray.get(j).uid) {
                    item.traffic -= lastArray.get(j).traffic;
                    break;
                }
            }
            traffic_day += item.traffic;
            newArray.add(item);
        }
        // 给流量信息队列补充每个应用的图标
        ArrayList<AppInfo> fullArray = AppUtil.fillAppInfo(this, newArray);
        // 构建一个流量信息的列表适配器
        TrafficInfoAdapter adapter = new TrafficInfoAdapter(MobileAssistantActivity.this, fullArray);
        // 给nslv_traffic设置流量信息列表适配器
        nslv_traffic.setAdapter(adapter);
        showDayAnimation(); // 显示日流量动画
        showMonthAnimation(); // 显示月流量动画
    }

    // 显示日流量的圆弧动画
    private void showDayAnimation() {
        rl_day.removeAllViews();
        int diameter = Math.min(rl_day.getWidth(), rl_day.getHeight()) - line_width * 2;
        String desc = "今日已用流量" + StringUtil.formatData(traffic_day);
        // 创建日流量的圆弧动画
        CircleAnimation dayAnimation = new CircleAnimation(MobileAssistantActivity.this);
        // 设置日流量动画的四周边界
        dayAnimation.setRect((rl_day.getWidth() - diameter) / 2 + line_width,
                (rl_day.getHeight() - diameter) / 2 + line_width,
                (rl_day.getWidth() + diameter) / 2 - line_width,
                (rl_day.getHeight() + diameter) / 2 - line_width);
        float trafficM = traffic_day / 1024.0f / 1024.0f;
        if (trafficM > limit_day * 2) { // 超出两倍限额，则展示红色圆弧进度
            int end_angle = (int) ((trafficM > limit_day * 3) ? 360 : (trafficM - limit_day * 2) * 360 / limit_day);
            dayAnimation.setAngle(0, end_angle);
            dayAnimation.setFront(Color.RED, line_width, Style.STROKE);
            desc = String.format("%s\n超出限额%s", desc,
                    StringUtil.formatData(traffic_day - limit_day * 1024 * 1024));
        } else if (trafficM > limit_day) { // 超出一倍限额，则展示橙色圆弧进度
            int end_angle = (int) ((trafficM > limit_day * 2) ? 360 : (trafficM - limit_day) * 360 / limit_day);
            dayAnimation.setAngle(0, end_angle);
            dayAnimation.setFront(0xffff9900, line_width, Style.STROKE);
            desc = String.format("%s\n超出限额%s", desc,
                    StringUtil.formatData(traffic_day - limit_day * 1024 * 1024));
        } else { // 未超出限额，则展示绿色圆弧进度
            int end_angle = (int) (trafficM * 360 / limit_day);
            dayAnimation.setAngle(0, end_angle);
            dayAnimation.setFront(Color.GREEN, line_width, Style.STROKE);
            desc = String.format("%s\n剩余流量%s", desc,
                    StringUtil.formatData(limit_day * 1024 * 1024 - traffic_day));
        }
        rl_day.addView(dayAnimation);
        // 渲染日流量的圆弧动画
        dayAnimation.render();
        tv_day_traffic.setText(desc);
    }

    // 显示月流量的圆弧动画。未实现，读者可实践之
    private void showMonthAnimation() {
        rl_month.removeAllViews();
        int diameter = Math.min(rl_month.getWidth(), rl_month.getHeight()) - line_width * 2;
        tv_month_traffic.setText("本月已用流量待统计");
        // 创建月流量的圆弧动画
        CircleAnimation monthAnimation = new CircleAnimation(MobileAssistantActivity.this);
        // 设置月流量动画的四周边界
        monthAnimation.setRect((rl_month.getWidth() - diameter) / 2 + line_width,
                (rl_month.getHeight() - diameter) / 2 + line_width,
                (rl_month.getWidth() + diameter) / 2 - line_width,
                (rl_month.getHeight() + diameter) / 2 - line_width);
        monthAnimation.setAngle(0, 0);
        monthAnimation.setFront(Color.GREEN, line_width, Style.STROKE);
        rl_month.addView(monthAnimation);
        // 渲染月流量的圆弧动画
        monthAnimation.render();
    }

}
