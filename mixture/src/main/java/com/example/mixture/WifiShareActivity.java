package com.example.mixture;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.mixture.adapter.ClientListAdapter;
import com.example.mixture.bean.ClientScanResult;
import com.example.mixture.database.MacManager;
import com.example.mixture.task.GetClientListTask;
import com.example.mixture.task.GetClientListTask.GetClientListener;
import com.example.mixture.task.GetClientNameTask;
import com.example.mixture.task.GetClientNameTask.FindNameListener;
import com.example.mixture.util.WifiUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/11.
 */
@SuppressLint("DefaultLocale")
public class WifiShareActivity extends AppCompatActivity implements
        OnClickListener, OnCheckedChangeListener, GetClientListener, FindNameListener {
    private static final String TAG = "WifiShareActivity";
    private CheckBox ck_wifi_switch;
    private EditText et_wifi_name, et_wifi_password;
    private TextView tv_connect;
    private LinearLayout ll_client_title;
    private ListView lv_wifi_client; // 声明一个用于展示已连接设备的列表视图对象
    private WifiManager mWifiManager; // 声明一个无线网络管理器对象
    private WifiConfiguration mWifiConfig = new WifiConfiguration(); // 声明一个无线网络配置对象
    private int mDesType = WifiConfiguration.KeyMgmt.NONE; // 加密类型
    private ArrayList<ClientScanResult> mClientArray = new ArrayList<ClientScanResult>(); // 已连接设备的队列
    private HashMap<String, String> mapName = new HashMap<String, String>(); // IP与主机名的关系映射
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_share);
        ck_wifi_switch = findViewById(R.id.ck_wifi_switch);
        et_wifi_name = findViewById(R.id.et_wifi_name);
        et_wifi_password = findViewById(R.id.et_wifi_password);
        tv_connect = findViewById(R.id.tv_connect);
        ll_client_title = findViewById(R.id.ll_client_title);
        lv_wifi_client = findViewById(R.id.lv_wifi_client);
        findViewById(R.id.btn_wifi_save).setOnClickListener(this);
        et_wifi_name.setText(Build.SERIAL);
        et_wifi_password.setText("");
        ck_wifi_switch.setOnCheckedChangeListener(this);
        // 从系统服务中获取无线网络管理器
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        setWifiConfig(); // 创建初始的WIFI配置信息
        initDesSpinner();
        // 延迟50毫秒后启动已连接设备的扫描任务
        mHandler.postDelayed(mClientTask, 50);
    }

    // 初始化加密类型下拉框
    private void initDesSpinner() {
        Spinner sp_wifi_des = findViewById(R.id.sp_wifi_des);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, desNameArray);
        typeAdapter.setDropDownViewResource(R.layout.item_select);
        sp_wifi_des.setAdapter(typeAdapter);
        sp_wifi_des.setSelection(mDesType);
        sp_wifi_des.setPrompt("请选择加密类型");
        sp_wifi_des.setOnItemSelectedListener(new DesTypeSelectedListener());
        sp_wifi_des.setSelection(mDesType);
    }

    private String[] desNameArray = {"无", "WPA PSK", "WPA2 PSK"};
    private int[] desTypeArray = {WifiConfiguration.KeyMgmt.NONE, WifiConfiguration.KeyMgmt.WPA_PSK, 4};

    class DesTypeSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mDesType = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_wifi_save) {
            if (et_wifi_name.getText().length() < 4) {
                Toast.makeText(this, "WIFI名称长度需不小于四位", Toast.LENGTH_SHORT).show();
                return;
            } else if (mDesType != 0 && et_wifi_password.getText().length() < 8) {
                Toast.makeText(this, "WIFI密码长度需不小于八位", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "已保存本次WIFI设置", Toast.LENGTH_SHORT).show();
            // 只有当前已开启WIFI的，才需要断开并重连。当前未开启WIFI的，保存设置后不自动开WIFI
            int timeout = 0;
            if (ck_wifi_switch.isChecked()) {
                ck_wifi_switch.setChecked(false);
                timeout = 2000;
            }
            setWifiConfig(); // 设定WIFI配置信息
            // 延迟若干时间后启动WIFI热点的开启任务
            mHandler.postDelayed(mReOpenTask, timeout);
        }
    }

    // 通过用户名、密码、加密类型创建WIFI配置信息
    private void setWifiConfig() {
        mWifiConfig.allowedKeyManagement.clear();
        mWifiConfig.SSID = et_wifi_name.getText().toString();
        if (mDesType == WifiConfiguration.KeyMgmt.NONE) { // 无密码
            mWifiConfig.preSharedKey = "";
            mWifiConfig.wepKeys[0] = et_wifi_password.getText().toString();
            mWifiConfig.wepTxKeyIndex = 0;
        } else { // WPA加密或者WPA2加密
            mWifiConfig.allowedKeyManagement.set(desTypeArray[mDesType]);
            mWifiConfig.preSharedKey = et_wifi_password.getText().toString();
            mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            mWifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            mWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
    }

    // 定义一个WIFI热点的开启任务
    private Runnable mReOpenTask = new Runnable() {
        @Override
        public void run() {
            if (WifiUtil.getWifiApState(mWifiManager) == WifiUtil.WIFI_AP_STATE_DISABLED) {
                ck_wifi_switch.setChecked(true);
            } else {
                // 延迟2秒后再次启动WIFI热点的开启任务
                mHandler.postDelayed(this, 2000);
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_wifi_switch) {
            String result;
            if (isChecked) {
                setWifiConfig(); // 设定WIFI配置信息
                // 开启WIFI热点
                result = WifiUtil.setWifiApEnabled(mWifiManager, mWifiConfig, true);
            } else {
                // 关闭WIFI热点
                result = WifiUtil.setWifiApEnabled(mWifiManager, mWifiConfig, false);
            }
            Log.d(TAG, "onCheckedChanged: " + isChecked + ". " + result);
            if (!TextUtils.isEmpty(result)) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                ck_wifi_switch.setChecked(!isChecked);
            }
        }
    }

    // 定义一个已连接设备的扫描任务
    private Runnable mClientTask = new Runnable() {
        @Override
        public void run() {
            // 下面开启分线程扫描已经连上来的设备
            GetClientListTask getClientTask = new GetClientListTask();
            getClientTask.setGetClientListener(WifiShareActivity.this);
            getClientTask.execute();
            // 延迟3秒后再次启动已连接设备的扫描任务
            mHandler.postDelayed(this, 3000);
        }
    };

    // 在找到已连接设备后触发
    public void onGetClient(ArrayList<ClientScanResult> clientList) {
        mClientArray = clientList;
        Log.d(TAG, "mClientArray.size()=" + mClientArray.size());
        if (WifiUtil.getWifiApState(mWifiManager) != WifiUtil.WIFI_AP_STATE_ENABLING
                && WifiUtil.getWifiApState(mWifiManager) != WifiUtil.WIFI_AP_STATE_ENABLED) { // 未开启热点
            mClientArray.clear();
        } else if (mClientArray == null) {
            mClientArray = new ArrayList<ClientScanResult>();
        }
        if (mClientArray.size() <= 0) { // 无设备连接
            tv_connect.setText("当前没有设备连接");
            ll_client_title.setVisibility(View.GONE);
        } else { // 有设备连接
            String desc = String.format("当前已有%d台设备连接", mClientArray.size());
            tv_connect.setText(desc);
            ll_client_title.setVisibility(View.VISIBLE);
        }
        // 为每个设备匹配品牌名称与制造厂商
        for (ClientScanResult item : mClientArray) {
            String ipAddr = item.getIpAddr();
            // 根据设备的MAC地址到数据库中查找对应的品牌名称
            item.setDevice(MacManager.getInstance(this).getMacDevice(item.getHWAddr()));
            if (mapName.containsKey(ipAddr)) { // 已经找到该IP对应的主机名称
                item.setHostName(mapName.get(ipAddr));
            } else { // 尚未找到该IP对应的主机名称
                // 根据设备的品牌名称到数据库中查找对应的制造厂商
                item.setHostName(MacManager.getInstance(this).getDeviceName(item.getDevice()));
                String upperDevice = item.getDevice().toUpperCase();
                // 若是笔记本电脑，则依据NetBIOS协议获取该设备的主机名
                // 这里只处理几款主流的笔记本品牌，包括联想、惠普、戴尔、华硕、宏碁、东芝
                if (upperDevice.equals("INTEL") || upperDevice.equals("HEWLETT")
                        || upperDevice.equals("DELL") || upperDevice.equals("ASUS")
                        || upperDevice.equals("ACER") || upperDevice.equals("TOSHIBA")) {
                    Log.d(TAG, "new GetClientNameTask");
                    // 下面开启分线程根据设备的IP地址获取它的主机名称
                    GetClientNameTask getNameTask = new GetClientNameTask();
                    getNameTask.setFindNameListener(WifiShareActivity.this);
                    getNameTask.execute(ipAddr);
                }
            }
        }
        // 把已连接设备通过列表视图展现出来
        ClientListAdapter clientAdapter = new ClientListAdapter(this, mClientArray);
        lv_wifi_client.setAdapter(clientAdapter);
    }

    // 声明nameFromJNI是来自于JNI的原生方法
    public static native String nameFromJNI(String ip);

    // 在加载当前类时就去加载jni_mix.so，加载动作发生在页面启动之前
    static {
        System.loadLibrary("jni_mix");
    }

    // 在找到主机名称时触发
    public void onFindName(String info) {
        if (!TextUtils.isEmpty(info)) {
            String[] split = info.split("\\|");
            if (split.length > 1 && split[1].length() > 0) {
                // 添加到IP地址与主机名的关系映射
                mapName.put(split[0], split[1]);
            }
        }
    }

}
