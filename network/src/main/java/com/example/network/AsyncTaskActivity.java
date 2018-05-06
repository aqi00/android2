package com.example.network;

import com.example.network.task.ProgressAsyncTask;
import com.example.network.task.ProgressAsyncTask.OnProgressListener;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint(value={"SetTextI18n","DefaultLocale"})
public class AsyncTaskActivity extends AppCompatActivity implements OnProgressListener {
    private TextView tv_async;
    private ProgressBar pb_async; // 声明一个进度条对象
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    public int mShowStyle; // 显示风格
    public int BAR_HORIZONTAL = 1; // 水平条
    public int DIALOG_CIRCLE = 2; // 圆圈对话框
    public int DIALOG_HORIZONTAL = 3; // 水平对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        tv_async = findViewById(R.id.tv_async);
        // 从布局文件中获取名叫pb_async的进度条
        pb_async = findViewById(R.id.pb_async);
        initBookSpinner(); // 初始化书籍选择下拉框
    }

    // 初始化书籍选择下拉框
    private void initBookSpinner() {
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, bookArray);
        Spinner sp_style = findViewById(R.id.sp_style);
        sp_style.setPrompt("请选择要加载的小说");
        sp_style.setAdapter(styleAdapter);
        sp_style.setOnItemSelectedListener(new StyleSelectedListener());
        sp_style.setSelection(0);
    }

    private String[] bookArray = {"三国演义", "西游记", "红楼梦"};
    private int[] styleArray = {BAR_HORIZONTAL, DIALOG_CIRCLE, DIALOG_HORIZONTAL};
    class StyleSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            startTask(styleArray[arg2], bookArray[arg2]); // 启动书籍加载线程
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 启动书籍加载线程
    private void startTask(int style, String msg) {
        mShowStyle = style;
        // 创建一个书籍加载线程
        ProgressAsyncTask asyncTask = new ProgressAsyncTask(msg);
        // 设置书籍加载监听器
        asyncTask.setOnProgressListener(this);
        // 把书籍加载线程加入到处理队列
        asyncTask.execute(msg);
    }

    // 关闭对话框
    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) { // 对话框仍在显示
            mDialog.dismiss(); // 关闭对话框
        }
    }

    // 在线程处理结束时触发
    public void onFinish(String result) {
        String desc = String.format("您要阅读的《%s》已经加载完毕", result);
        tv_async.setText(desc);
        closeDialog(); // 关闭对话框
    }

    // 在线程处理取消时触发
    public void onCancel(String result) {
        String desc = String.format("您要阅读的《%s》已经取消加载", result);
        tv_async.setText(desc);
        closeDialog(); // 关闭对话框
    }

    // 在线程处理过程中更新进度时触发
    public void onUpdate(String request, int progress, int sub_progress) {
        String desc = String.format("%s当前加载进度为%d%%", request, progress);
        tv_async.setText(desc);
        if (mShowStyle == BAR_HORIZONTAL) { // 水平条
            pb_async.setProgress(progress); // 设置水平进度条的当前进度
            pb_async.setSecondaryProgress(sub_progress); // 设置水平进度条的次要进度
        } else if (mShowStyle == DIALOG_HORIZONTAL) { // 水平对话框
            mDialog.setProgress(progress); // 设置水平进度对话框的当前进度
            mDialog.setSecondaryProgress(sub_progress); // 设置水平进度对话框的次要进度
        }
    }

    // 在线程处理开始时触发
    public void onBegin(String request) {
        tv_async.setText(request + "开始加载");
        if (mDialog == null || !mDialog.isShowing()) {  // 进度框未弹出
            if (mShowStyle == DIALOG_CIRCLE) { // 圆圈对话框
                // 弹出带有提示文字的圆圈进度对话框
                mDialog = ProgressDialog.show(this, "稍等", request + "页面加载中……");
            } else if (mShowStyle == DIALOG_HORIZONTAL) { // 水平对话框
                mDialog = new ProgressDialog(this); // 创建一个进度对话框
                mDialog.setTitle("稍等"); // 设置进度对话框的标题文本
                mDialog.setMessage(request + "页面加载中……"); // 设置进度对话框的内容文本
                mDialog.setIcon(R.drawable.ic_search); // 设置进度对话框的图标
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 设置进度对话框的样式
                mDialog.show(); // 显示进度对话框
            }
        }
    }

}
