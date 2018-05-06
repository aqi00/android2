package com.example.network;

import com.example.network.task.GetAddressTask;
import com.example.network.task.GetAddressTask.OnAddressListener;
import com.example.network.util.DateUtil;
import com.example.network.util.SwitchUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint(value={"SetTextI18n","DefaultLocale"})
public class HttpRequestActivity extends AppCompatActivity implements OnAddressListener {
    private final static String TAG = "HttpRequestActivity";
    private TextView tv_location;
    private String mLocation = "";
    private LocationManager mLocationMgr; // 声明一个定位管理器对象
    private Criteria mCriteria = new Criteria(); // 声明一个定位准则对象
    private Handler mHandler = new Handler(); // 声明一个处理器
    private boolean isLocationEnable = false; // 定位服务是否可用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_request);
        tv_location = findViewById(R.id.tv_location);
        SwitchUtil.checkGpsIsOpen(this, "需要打开定位功能才能查看定位结果信息");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.removeCallbacks(mRefresh); // 移除定位刷新任务
        initLocation();
        mHandler.postDelayed(mRefresh, 100); // 延迟100毫秒启动定位刷新任务
    }

    // 初始化定位服务
    private void initLocation() {
        // 从系统服务中获取定位管理器
        mLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 设置定位精确度。Criteria.ACCURACY_COARSE表示粗略，Criteria.ACCURACY_FIN表示精细
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否需要海拔信息
        mCriteria.setAltitudeRequired(true);
        // 设置是否需要方位信息
        mCriteria.setBearingRequired(true);
        // 设置是否允许运营商收费
        mCriteria.setCostAllowed(true);
        // 设置对电源的需求
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        // 获取定位管理器的最佳定位提供者
        String bestProvider = mLocationMgr.getBestProvider(mCriteria, true);
        if (mLocationMgr.isProviderEnabled(bestProvider)) { // 定位提供者当前可用
            tv_location.setText("正在获取" + bestProvider + "定位对象");
            mLocation = String.format("定位类型=%s", bestProvider);
            beginLocation(bestProvider);
            isLocationEnable = true;
        } else { // 定位提供者暂不可用
            tv_location.setText("\n" + bestProvider + "定位不可用");
            isLocationEnable = false;
        }
    }

    private String mAddress = ""; // 详细地址描述

    // 在找到详细地址后触发
    public void onFindAddress(String address) {
        mAddress = address;
    }

    // 设置定位结果文本
    private void setLocationText(Location location) {
        if (location != null) {
            String desc = String.format("%s\n定位对象信息如下： " +
                            "\n\t其中时间：%s" + "\n\t其中经度：%f，纬度：%f" +
                            "\n\t其中高度：%d米，精度：%d米" + "\n\t其中地址：%s",
                    mLocation, DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss"),
                    location.getLongitude(), location.getLatitude(),
                    Math.round(location.getAltitude()), Math.round(location.getAccuracy()), mAddress);
            Log.d(TAG, desc);
            tv_location.setText(desc);
            // 创建一个详细地址查询的线程
            GetAddressTask addressTask = new GetAddressTask();
            // 设置详细地址查询的监听器
            addressTask.setOnAddressListener(this);
            // 把详细地址查询线程加入到处理队列
            addressTask.execute(location);
        } else {
            tv_location.setText(mLocation + "\n暂未获取到定位对象");
        }
    }

    // 开始定位
    private void beginLocation(String method) {
        // 检查当前设备是否已经开启了定位功能
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请授予定位权限并开启定位功能", Toast.LENGTH_SHORT).show();
            return;
        }
        // 设置定位管理器的位置变更监听器
        mLocationMgr.requestLocationUpdates(method, 300, 0, mLocationListener);
        // 获取最后一次成功定位的位置信息
        Location location = mLocationMgr.getLastKnownLocation(method);
        setLocationText(location);
    }

    // 定义一个位置变更监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setLocationText(location);
        }

        @Override
        public void onProviderDisabled(String arg0) {}

        @Override
        public void onProviderEnabled(String arg0) {}

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    };

    // 定义一个刷新任务，若无法定位则每隔一秒就尝试定位
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (!isLocationEnable) {
                initLocation();
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (mLocationMgr != null) {
            // 移除定位管理器的位置变更监听器
            mLocationMgr.removeUpdates(mLocationListener);
        }
        super.onDestroy();
    }

}
