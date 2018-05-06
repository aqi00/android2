package com.example.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.media.service.CaptureService;
import com.example.media.service.RecordService;
import com.example.media.util.AuthorityUtil;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "ScreenCaptureActivity";
    private MediaProjectionManager mMpMgr; // 声明一个媒体投影管理器对象
    private int REQUEST_MEDIA_PROJECTION = 1; // 媒体投影授权的请求代码
    private Intent mResultIntent = null; // 结果意图
    private int mResultCode = 0; // 结果代码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_capture);
        findViewById(R.id.btn_start_capture).setOnClickListener(this);
        // 从系统服务中获取媒体投影管理器
        mMpMgr = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        // 从全局变量中获取结果意图
        mResultIntent = MainApplication.getInstance().getResultIntent();
        // 从全局变量中获取结果代码
        mResultCode = MainApplication.getInstance().getResultCode();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_capture) {
            // 停止录屏服务
            stopService(new Intent(this, RecordService.class));
            startCapture(); // 开始截图操作
        }
    }

    // 开始截图操作
    private void startCapture() {
        if (mResultIntent != null && mResultCode != 0) { // 不是首次截图或录屏
            // 启动截图服务
            startService(new Intent(this, CaptureService.class));
        } else { // 是首次截图或录屏
            // 在YunOS上报错“android.content.ActivityNotFoundException: Unable to find explicit activity class {com.android.systemui/com.android.systemui.media.MediaProjectionPermissionActivity}; have you declared this activity in your AndroidManifest.xml?”
            // 即使添加了权限定义与Activity声明，也仍然报错
            // 怀疑是该操作系统为了安全把这个组件删除了
            try {
                // 弹出授权截图的对话框
                startActivityForResult(mMpMgr.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "当前系统不支持截屏功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 从授权截图的对话框返回时触发
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK) { // 允许授权
                // AppOpsManager.OP_SYSTEM_ALERT_WINDOW是隐藏变量（值为24），不能直接引用
                if (!AuthorityUtil.checkOp(this, 24)) { // 未开启悬浮窗权限
                    Toast.makeText(this, "截屏功能需要开启悬浮窗权限", Toast.LENGTH_SHORT).show();
                    // 跳到悬浮窗权限的设置页面
                    AuthorityUtil.requestAlertWindowPermission(this);
                } else { // 已开启悬浮窗权限
                    mResultCode = resultCode;
                    mResultIntent = data;
                    // 下面把结果代码、结果意图等等信息保存到全局变量中
                    MainApplication.getInstance().setResultCode(resultCode);
                    MainApplication.getInstance().setResultIntent(data);
                    MainApplication.getInstance().setMpMgr(mMpMgr);
                    // 启动录屏服务
                    startService(new Intent(this, CaptureService.class));
                }
            }
        }
    }

}
