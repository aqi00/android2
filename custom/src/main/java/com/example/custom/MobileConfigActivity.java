package com.example.custom;

import com.example.custom.util.SharedUtil;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/10/14.
 */
@SuppressLint(value={"SetTextI18n","DefaultLocale","StaticFieldLeak"})
public class MobileConfigActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "MobileConfigActivity";
    private static EditText et_config_month;
    private static EditText et_config_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_config);
        et_config_month = findViewById(R.id.et_config_month);
        et_config_day = findViewById(R.id.et_config_day);
        findViewById(R.id.btn_config_save).setOnClickListener(this);
        findViewById(R.id.btn_auto_adjust).setOnClickListener(this);
        // 从共享参数中获取每月的流量限额
        int limit_month = SharedUtil.getIntance(this).readInt("limit_month", 1024);
        // 从共享参数中获取每日的流量限额
        int limit_day = SharedUtil.getIntance(this).readInt("limit_day", 50);
        et_config_month.setText("" + limit_month);
        et_config_day.setText("" + limit_day);
        // 初始化短信的内容观察器
        initSmsObserver();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_config_save) {
            // 把流量限额保存到共享参数中
            saveFlowConfig(Integer.parseInt(et_config_month.getText().toString()),
                    Integer.parseInt(et_config_day.getText().toString()));
            Toast.makeText(this, "成功保存配置", Toast.LENGTH_SHORT).show();
            finish();
        } else if (v.getId() == R.id.btn_auto_adjust) {
            // 无需用户操作，自动发送短信
            sendSmsAuto(mCustomNumber, "18");
        }
    }

    // 把每月和每日的流量限额保存到共享参数中
    private static void saveFlowConfig(int limit_month, int limit_day) {
        SharedUtil.getIntance(MainApplication.getInstance()).writeInt("limit_month", limit_month);
        SharedUtil.getIntance(MainApplication.getInstance()).writeInt("limit_day", limit_day);
    }

    // 短信发送事件
    private String SENT_SMS_ACTION = "com.example.custom.SENT_SMS_ACTION";
    // 短信接收事件
    private String DELIVERED_SMS_ACTION = "com.example.custom.DELIVERED_SMS_ACTION";

    // 无需用户操作，由App自动发送短信
    public void sendSmsAuto(String phoneNumber, String message) {
        // 以下指定短信发送事件的详细信息
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentIntent.putExtra("phone", phoneNumber);
        sentIntent.putExtra("message", message);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 以下指定短信接收事件的详细信息
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        deliverIntent.putExtra("phone", phoneNumber);
        deliverIntent.putExtra("message", message);
        PendingIntent deliverPI = PendingIntent.getBroadcast(this, 1, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 获取默认的短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        // 开始发送短信内容。要确保打开发送短信的完全权限，不是那种还需提示的不完整权限
        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private SmsGetObserver mObserver; // 声明一个短信获取的观察器对象
    private static String mCustomNumber = "10086"; // 中国移动的客服号码
    private static Uri mSmsUri; // 声明一个系统短信提供器的Uri对象
    private static String[] mSmsColumn; // 声明一个短信记录的字段数组

    // 初始化短信观察器
    private void initSmsObserver() {
        //mSmsUri = Uri.parse("content://sms/inbox");
        //Android5.0之后似乎无法单独观察某个信箱，只能监控整个短信
        mSmsUri = Uri.parse("content://sms");
        mSmsColumn = new String[]{"address", "body", "date"};
        // 创建一个短信观察器对象
        mObserver = new SmsGetObserver(this, mHandler);
        // 给指定Uri注册内容观察器，一旦Uri内部发生数据变化，就触发观察器的onChange方法
        getContentResolver().registerContentObserver(mSmsUri, true, mObserver);
    }

    @Override
    protected void onDestroy() {
        // 注销内容观察器
        getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    // 定义一个短信获取的观察器
    private static class SmsGetObserver extends ContentObserver {
        private Context mContext; // 声明一个上下文对象
        public SmsGetObserver(Context context, Handler handler) {
            super(handler);
            mContext = context;
        }

        @Override
        public void onChange(boolean selfChange) { // 观察到短信的内容提供器发生变化
            String sender = "", content = "";
            // 构建一个查询短信的条件语句，这里使用移动号码测试，故而查找10086发来的短信
            String selection = String.format("address='%s' and date>%d",
                    mCustomNumber, System.currentTimeMillis() - 1000 * 60 * 60);
            // 通过内容解析器获取符合条件的结果集游标
            Cursor cursor = mContext.getContentResolver().query(
                    mSmsUri, mSmsColumn, selection, null, " date desc");
            // 循环取出游标所指向的所有短信记录
            while (cursor.moveToNext()) {
                sender = cursor.getString(0);
                content = cursor.getString(1);
                break;
            }
            cursor.close();
            //content = "您办理的套餐内含数据总流量为1GB176MB，已使用310MB，剩余890MB。";
            String totalFlow = "0";
            if (sender.equals(mCustomNumber)) {
                // 解析流量校准短信里面的总流量数值
                totalFlow = findFlow(content, "总流量为", "，");
            }
            String[] flows = totalFlow.split("GB");
            Log.d(TAG, "totalFlow="+totalFlow+", flows.length="+flows.length);
            int flowData = 0;
            if (totalFlow.contains("GB") && TextUtils.isDigitsOnly(flows[0])) {
                flowData += Integer.parseInt(flows[0]) * 1024;
            }
            if (flows.length>1 && TextUtils.isDigitsOnly(flows[1])) {
                flowData += Integer.parseInt(flows[1]);
            }
            if (et_config_month != null && flowData!=0) {
                et_config_month.setText("" + flowData);
                et_config_day.setText("" + flowData/30);
                // 把流量限额保存到共享参数中
                saveFlowConfig(flowData, flowData/30);
                Toast.makeText(MainApplication.getInstance(), "流量校准成功", Toast.LENGTH_SHORT).show();
            }
            super.onChange(selfChange);
        }
    }

    // 解析流量校准短信里面的流量数值
    private static String findFlow(String sms, String begin, String end) {
        int begin_pos = sms.indexOf(begin);
        if (begin_pos < 0) {
            return "未获取";
        }
        String sub_sms = sms.substring(begin_pos);
        int end_pos = sub_sms.indexOf(end);
        if (end_pos < 0) {
            return "未获取";
        }
        if (end.equals("，")) {
            return sub_sms.substring(begin.length(), end_pos);
        } else {
            return sub_sms.substring(begin.length(), end_pos + end.length());
        }
    }

}
