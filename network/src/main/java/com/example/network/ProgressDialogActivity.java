package com.example.network;

import com.example.network.util.DateUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint(value={"HandlerLeak","SetTextI18n"})
public class ProgressDialogActivity extends AppCompatActivity {
    private final static String TAG = "ProgressDialogActivity";
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private TextView tv_result;
    private String mStyleDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_dialog);
        tv_result = findViewById(R.id.tv_result);
        initStyleSpinner();
    }

    // 初始化对话框样式的下拉框
    private void initStyleSpinner() {
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, descArray);
        Spinner sp_style = findViewById(R.id.sp_style);
        sp_style.setPrompt("请选择对话框样式");
        sp_style.setAdapter(styleAdapter);
        sp_style.setOnItemSelectedListener(new StyleSelectedListener());
        sp_style.setSelection(0);
    }

    private String[] descArray = {"圆圈进度", "水平进度条"};
    private int[] styleArray = {ProgressDialog.STYLE_SPINNER,
            ProgressDialog.STYLE_HORIZONTAL};

    class StyleSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (mDialog == null || !mDialog.isShowing()) { // 进度框未弹出
                mStyleDesc = descArray[arg2];
                int style = styleArray[arg2];
                if (style == ProgressDialog.STYLE_SPINNER) { // 圆圈进度框
                    // 弹出带有提示文字的圆圈进度对话框
                    mDialog = ProgressDialog.show(ProgressDialogActivity.this,
                            "请稍候", "正在努力加载页面");
                    // 延迟1500毫秒后启动关闭对话框的任务
                    mHandler.postDelayed(mCloseDialog, 1500);
                } else { // 水平进度框
                    // 创建一个进度对话框
                    mDialog = new ProgressDialog(ProgressDialogActivity.this);
                    mDialog.setTitle("请稍候"); // 设置进度对话框的标题文本
                    mDialog.setMessage("正在努力加载页面"); // 设置进度对话框的内容文本
                    mDialog.setMax(100); // 设置进度对话框的最大进度
                    mDialog.setProgressStyle(style); // 设置进度对话框的样式
                    mDialog.show(); // 显示进度对话框
                    new RefreshThread().start(); // 启动进度刷新线程
                }
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 定义一个关闭对话框的任务
    private Runnable mCloseDialog = new Runnable() {
        @Override
        public void run() {
            if (mDialog.isShowing()) { // 对话框仍在显示
                mDialog.dismiss(); // 关闭对话框
                tv_result.setText(DateUtil.getNowTime() + " " + mStyleDesc + "加载完成");
            }
        }
    };

    // 定义一个进度刷新线程
    private class RefreshThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                Message message = Message.obtain(); // 获得一个默认的消息对象
                message.what = 0; // 消息类型
                message.arg1 = i * 10;  // 消息数值
                mHandler.sendMessage(message); // 往处理器发送消息对象
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mHandler.sendEmptyMessage(1); // 往处理器发送类型为1的空消息
        }
    }

    // 创建一个处理器对象
    private Handler mHandler = new Handler() {
        // 在收到消息时触发
        public void handleMessage(Message msg) {
            if (msg.what == 0) { // 该类型表示刷新进度
                mDialog.setProgress(msg.arg1); // 设置进度对话框上的当前进度
            } else if (msg.what == 1) { // 该类型表示关闭对话框
                post(mCloseDialog); // 立即启动关闭对话框的任务
            }
        }
    };

}
