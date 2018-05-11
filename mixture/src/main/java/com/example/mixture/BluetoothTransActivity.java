package com.example.mixture;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.example.mixture.adapter.BlueListAdapter;
import com.example.mixture.bean.BlueDevice;
import com.example.mixture.task.BlueAcceptTask;
import com.example.mixture.task.BlueAcceptTask.BlueAcceptListener;
import com.example.mixture.task.BlueConnectTask;
import com.example.mixture.task.BlueConnectTask.BlueConnectListener;
import com.example.mixture.task.BlueReceiveTask;
import com.example.mixture.util.BluetoothUtil;
import com.example.mixture.widget.InputDialogFragment;
import com.example.mixture.widget.InputDialogFragment.InputCallbacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ouyangshen on 2017/12/11.
 */
@SuppressLint(value={"SetTextI18n","HandlerLeak"})
public class BluetoothTransActivity extends AppCompatActivity implements
        OnItemClickListener, OnCheckedChangeListener,
        BlueConnectListener, InputCallbacks, BlueAcceptListener {
    private static final String TAG = "BluetoothTransActivity";
    private CheckBox ck_bluetooth;
    private TextView tv_discovery;
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private BluetoothAdapter mBluetooth; // 声明一个蓝牙适配器对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象
    private ArrayList<BlueDevice> mDeviceList = new ArrayList<BlueDevice>(); // 蓝牙设备队列
    private int mOpenCode = 1; // 是否允许扫描蓝牙设备的选择对话框返回结果代码
    private HashMap<String, Integer> mMapState = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_trans);
        initBluetooth(); // 初始化蓝牙适配器
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        tv_discovery = findViewById(R.id.tv_discovery);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
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
            BluetoothManager bm = (BluetoothManager)
                    getSystemService(Context.BLUETOOTH_SERVICE);
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
            if (mMapState.containsKey(device.getAddress())) {
                mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), mMapState.get(device.getAddress())));
            } else {
                mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), device.getBondState()));
            }
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
            if (BluetoothUtil.getBlueToothStatus(BluetoothTransActivity.this)) {
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
                // 下面这行代码为服务端需要，客户端不需要
                mHandler.postDelayed(mAccept, 1000);
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                cancelDiscovery(); // 取消蓝牙设备的搜索
                BluetoothUtil.setBlueToothStatus(this, false); // 关闭蓝牙功能
                initBlueDevice(); // 初始化蓝牙设备列表
            }
        }
    }

    // 定义一个连接侦听任务
    private Runnable mAccept = new Runnable() {
        @Override
        public void run() {
            if (mBluetooth.getState() == BluetoothAdapter.STATE_ON) { // 已连接
                // 创建一个蓝牙设备侦听线程
                BlueAcceptTask acceptTask = new BlueAcceptTask(true);
                acceptTask.setBlueAcceptListener(BluetoothTransActivity.this);
                acceptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else { // 未连接
                // 延迟1秒后重新启动连接侦听任务
                mHandler.postDelayed(this, 1000);
            }
        }
    };

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
        // 需要过滤多个动作，则调用IntentFilter对象的addAction添加新动作
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // 注册蓝牙设备搜索的广播接收器
        registerReceiver(discoveryReceiver, discoveryFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery(); // 取消蓝牙设备的搜索
        // 注销蓝牙设备搜索的广播接收器
        unregisterReceiver(discoveryReceiver);
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
                if (item.state != BlueListAdapter.CONNECTED) {
                    item.state = state;
                    mDeviceList.set(i, item);
                    mMapState.put(item.address, state);
                }
                break;
            }
        }
        if (i >= mDeviceList.size()) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), state));
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //cancelDiscovery();
        BlueDevice item = mDeviceList.get(position);
        // 根据设备地址获得远端的蓝牙设备对象
        BluetoothDevice device = mBluetooth.getRemoteDevice(item.address);
        Log.d(TAG, "getBondState="+device.getBondState()+", item.state="+item.state);
        if (device.getBondState() == BluetoothDevice.BOND_NONE) { // 尚未配对
            BluetoothUtil.createBond(device); // 创建配对信息
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                item.state != BlueListAdapter.CONNECTED) { // 已经配对但尚未连接
            tv_discovery.setText("开始连接");
            // 创建一个蓝牙设备连接线程
            BlueConnectTask connectTask = new BlueConnectTask(item.address);
            connectTask.setBlueConnectListener(this);
            connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device);
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                item.state == BlueListAdapter.CONNECTED) { // 已经配对且已经连接
            tv_discovery.setText("正在发送消息");
            // 弹出消息输入对话框
            InputDialogFragment dialog = InputDialogFragment.newInstance(
                    "", 0, "请输入要发送的消息");
            String fragTag = getResources().getString(R.string.app_name);
            dialog.show(getFragmentManager(), fragTag);
        }
    }

    // 向对方发送消息
    @Override
    public void onInput(String title, String message, int type) {
        Log.d(TAG, "onInput message=" + message);
        Log.d(TAG, "mBlueSocket is " + (mBlueSocket == null ? "null" : "not null"));
        Log.d(TAG, "mBlueSocket is " + (mBlueSocket.isConnected() ? "connected" : "disconnected"));
        if (!mBlueSocket.isConnected()) {
            try {
                mBlueSocket.connect();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "蓝牙连接失败", Toast.LENGTH_SHORT).show();
            }
        }
        // 往蓝牙设备套接字中写入消息数据
        BluetoothUtil.writeOutputStream(mBlueSocket, message);
    }

    private BluetoothSocket mBlueSocket; // 声明一个蓝牙设备的套接字对象

    // 客户端主动连接
    @Override
    public void onBlueConnect(String address, BluetoothSocket socket) {
        mBlueSocket = socket;
        tv_discovery.setText("连接成功");
        refreshDevice(mBlueSocket.getRemoteDevice(), BlueListAdapter.CONNECTED);
    }

    // 服务端侦听到连接
    @Override
    public void onBlueAccept(BluetoothSocket socket) {
        Log.d(TAG, "onBlueAccept socket is " + (socket == null ? "null" : "not null"));
        if (socket != null) {
            mBlueSocket = socket;
            refreshDevice(mBlueSocket.getRemoteDevice(), BlueListAdapter.CONNECTED);
            // 创建一个蓝牙消息的接收线程
            BlueReceiveTask receive = new BlueReceiveTask(mBlueSocket, mHandler);
            receive.start();
        }
    }

    // 收到消息接收线程读到的消息
    private Handler mHandler = new Handler() {
        // 在收到消息时触发
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                byte[] readBuf = (byte[]) msg.obj;
                // 把字节数据转换为字符串
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, "handleMessage readMessage=" + readMessage);
                // 弹出收到消息的提醒对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothTransActivity.this);
                builder.setTitle("我收到消息啦").setMessage(readMessage);
                builder.setPositiveButton("确定", null);
                builder.create().show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBlueSocket != null) {
            try {
                mBlueSocket.close(); // 关闭蓝牙设备的套接字
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
