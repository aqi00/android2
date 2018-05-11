package com.example.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.device.adapter.BlueListAdapter;
import com.example.device.bean.BlueDevice;
import com.example.device.util.BluetoothUtil;
import com.example.device.widget.AudioPlayer;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by ouyangshen on 2018/2/7.
 */
@SuppressLint("SetTextI18n")
public class FindListenActivity extends AppCompatActivity implements
        OnCheckedChangeListener, OnItemClickListener {
    private static final String TAG = "FindListenActivity";
    private Context mContext;
    private CheckBox ck_bluetooth;
    private TextView tv_discovery;
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private AudioPlayer ap_music; // 声明一个音频播放器对象
    private BluetoothAdapter mBluetooth; // 声明一个蓝牙适配器对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象
    private ArrayList<BlueDevice> mDeviceList = new ArrayList<BlueDevice>(); // 蓝牙设备队列
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private int mOpenCode = 1; // 是否允许扫描蓝牙设备的选择对话框返回结果代码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_listen);
        initBluetooth(); // 初始化蓝牙适配器
        mContext = this;
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        tv_discovery = findViewById(R.id.tv_discovery);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
        // 从布局文件中获取名叫ap_music的音频播放器
        ap_music = findViewById(R.id.ap_music);
        ck_bluetooth.setOnCheckedChangeListener(this);
        if (BluetoothUtil.getBlueToothStatus(this)) {
            ck_bluetooth.setChecked(true);
        }
        initBlueDevice(); // 初始化蓝牙设备列表
    }

    // 初始化蓝牙适配器
    private void initBluetooth() {
        // Android从4.3开始增加支持BLE技术（即蓝牙4.0及以上版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 从系统服务中获取蓝牙管理器
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetooth = bm.getAdapter();
        } else {
            // 获取系统默认的蓝牙适配器
            mBluetooth = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetooth == null) {
            Toast.makeText(this, "本机未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 初始化蓝牙设备列表
    private void initBlueDevice() {
        mDeviceList.clear();
        // 获取已经配对的蓝牙设备集合
        Set<BluetoothDevice> bondedDevices = mBluetooth.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
        }
        if (mListAdapter == null) { // 首次打开页面，则创建一个新的蓝牙设备列表
            mListAdapter = new BlueListAdapter(this, mDeviceList);
            lv_bluetooth.setAdapter(mListAdapter);
            lv_bluetooth.setOnItemClickListener(this);
        } else { // 不是首次打开页面，则刷新蓝牙设备列表
            mListAdapter.notifyDataSetChanged();
        }
    }

    private Runnable mDiscoverable = new Runnable() {
        public void run() {
            // Android8.0要在已打开蓝牙功能时才会弹出下面的选择窗
            if (BluetoothUtil.getBlueToothStatus(FindListenActivity.this)) {
                // 弹出是否允许扫描蓝牙设备的选择对话框
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent, mOpenCode);
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bluetooth) {
            if (isChecked) { // 开启蓝牙功能
                ck_bluetooth.setText("蓝牙开");
                if (!BluetoothUtil.getBlueToothStatus(this)) {
                    BluetoothUtil.setBlueToothStatus(this, true); // 开启蓝牙功能
                }
                mHandler.post(mDiscoverable);
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                cancelDiscovery(); // 取消蓝牙设备的搜索
                BluetoothUtil.setBlueToothStatus(this, false); // 关闭蓝牙功能
                initBlueDevice(); // 初始化蓝牙设备列表
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == mOpenCode) { // 来自允许蓝牙扫描的对话框
            // 延迟50毫秒后启动蓝牙设备的刷新任务
            mHandler.postDelayed(mRefresh, 50);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "允许本地蓝牙被附近的其它蓝牙设备发现",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "不允许蓝牙被附近的其它蓝牙设备发现",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 定义一个刷新任务，每隔两秒刷新扫描到的蓝牙设备
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            beginDiscovery(); // 开始扫描周围的蓝牙设备
            // 延迟2秒后再次启动蓝牙设备的刷新任务
            mHandler.postDelayed(this, 2000);
        }
    };

    // 开始扫描周围的蓝牙设备
    private void beginDiscovery() {
        // 如果当前不是正在搜索，则开始新的搜索任务
        if (!mBluetooth.isDiscovering()) {
            initBlueDevice(); // 初始化蓝牙设备列表
            tv_discovery.setText("正在搜索蓝牙设备");
            mBluetooth.startDiscovery(); // 开始扫描周围的蓝牙设备
        }
    }

    // 取消蓝牙设备的搜索
    private void cancelDiscovery() {
        mHandler.removeCallbacks(mRefresh);
        tv_discovery.setText("取消搜索蓝牙设备");
        // 当前正在搜索，则取消搜索任务
        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery(); // 取消扫描周围的蓝牙设备
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mRefresh, 50);
        // 注册蓝牙设备搜索的广播接收器
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(discoveryReceiver, discoveryFilter);
        // 获取A2DP的蓝牙代理
        mBluetooth.getProfileProxy(this, serviceListener, BluetoothProfile.A2DP);
        // 创建一个意图过滤器
        IntentFilter a2dpFilter = new IntentFilter();
        // 指定A2DP的连接状态变更广播
        a2dpFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        // 指定A2DP的播放状态变更广播
        a2dpFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        // 注册A2DP连接管理的广播接收器
        registerReceiver(a2dpReceiver, a2dpFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery(); // 取消蓝牙设备的搜索
        // 注销蓝牙设备搜索的广播接收器
        unregisterReceiver(discoveryReceiver);
        // 注销A2DP连接管理的广播接收器
        unregisterReceiver(a2dpReceiver);
    }

    // 蓝牙设备的搜索结果通过广播返回
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action=" + action);
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { // 发现新的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "name=" + device.getName() + ", state=" + device.getBondState());
                refreshDevice(device, device.getBondState()); // 将发现的蓝牙设备加入到设备列表
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // 搜索完毕
                //mHandler.removeCallbacks(mRefresh); // 需要持续搜索就要注释这行
                tv_discovery.setText("蓝牙设备搜索完成");
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) { // 配对状态变更
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    tv_discovery.setText("正在配对" + device.getName());
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    tv_discovery.setText("完成配对" + device.getName());
                    mHandler.postDelayed(mRefresh, 50);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    tv_discovery.setText("取消配对" + device.getName());
                    refreshDevice(device, device.getBondState());
                }
            }
        }
    };

    // 刷新蓝牙设备列表
    private void refreshDevice(BluetoothDevice device, int state) {
        int i;
        for (i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.address.equals(device.getAddress())) {
                item.state = state;
                mDeviceList.set(i, item);
                break;
            }
        }
        if (i >= mDeviceList.size()) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
        }
        mListAdapter.notifyDataSetChanged();
    }

    private String mAddress;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAddress = mDeviceList.get(position).address;
        // 根据设备地址获得远端的蓝牙设备对象
        BluetoothDevice device = mBluetooth.getRemoteDevice(mAddress);
        if (device.getBondState() == BluetoothDevice.BOND_NONE) { // 尚未配对
            BluetoothUtil.connectA2dp(bluetoothA2dp, device); // 创建配对信息
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) { // 已经配对
            BluetoothUtil.removeBond(device); // 移除配对信息
        } else if (device.getBondState() == BlueListAdapter.CONNECTED) { // 已经建立A2DP连接
            BluetoothUtil.disconnectA2dp(bluetoothA2dp, device); // 断开A2DP连接
        }
    }

    private BluetoothA2dp bluetoothA2dp; // 声明一个蓝牙音频传输对象
    // 定义一个A2DP的服务监听器，类似于Service的绑定方式启停，
    // 也有onServiceConnected和onServiceDisconnected两个接口方法
    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {

        // 在服务断开连接时触发
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                Toast.makeText(mContext, "onServiceDisconnected", Toast.LENGTH_SHORT).show();
                // A2DP已连接，则释放A2DP的蓝牙代理
                bluetoothA2dp = null;
            }
        }

        // 在服务建立连接时触发
        public void onServiceConnected(int profile, final BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                Toast.makeText(mContext, "onServiceConnected", Toast.LENGTH_SHORT).show();
                // A2DP已连接，则设置A2DP的蓝牙代理
                bluetoothA2dp = (BluetoothA2dp) proxy;
            }
        }
    };

    // 定义一个A2DP连接的广播接收器
    private BroadcastReceiver a2dpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                // 侦听到A2DP的连接状态变更广播
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    BluetoothDevice device = mBluetooth.getRemoteDevice(mAddress);
                    int connectState = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE,
                            BluetoothA2dp.STATE_DISCONNECTED);
                    if (connectState == BluetoothA2dp.STATE_CONNECTED) {
                        // 收到连接上的广播，则更新设备状态为已连接
                        refreshDevice(device, BlueListAdapter.CONNECTED);
                        ap_music.initFromRaw(mContext, R.raw.mountain_and_water);
                        Toast.makeText(mContext, "已连上蓝牙音箱。快来播放音乐试试",
                                Toast.LENGTH_SHORT).show();
                    } else if (connectState == BluetoothA2dp.STATE_DISCONNECTED) {
                        // 收到断开连接的广播，则更新设备状态为已断开
                        refreshDevice(device, BluetoothDevice.BOND_NONE);
                        Toast.makeText(mContext, "已断开蓝牙音箱",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                // 侦听到A2DP的播放状态变更广播
                case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
                    int playState = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE,
                            BluetoothA2dp.STATE_NOT_PLAYING);
                    if (playState == BluetoothA2dp.STATE_PLAYING) {
                        Toast.makeText(mContext, "蓝牙音箱正在播放",
                                Toast.LENGTH_SHORT).show();
                    } else if (playState == BluetoothA2dp.STATE_NOT_PLAYING) {
                        Toast.makeText(mContext, "蓝牙音箱停止播放",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

}
