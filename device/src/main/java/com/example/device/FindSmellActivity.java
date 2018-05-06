package com.example.device;

import java.util.HashMap;
import java.util.Map;

import com.example.device.bean.Satellite;
import com.example.device.util.DateUtil;
import com.example.device.util.SwitchUtil;
import com.example.device.widget.CompassView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
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
 * Created by ouyangshen on 2017/11/4.
 */
@SuppressLint("DefaultLocale")
public class FindSmellActivity extends AppCompatActivity {
    private final static String TAG = "FindSmellActivity";
    private TextView tv_satellite;
    private CompassView cv_satellite; // 声明一个罗盘视图对象
    private Map<Integer, Satellite> mapSatellite = new HashMap<Integer, Satellite>();
    private LocationManager mLocationMgr; // 声明一个定位管理器对象
    private Criteria mCriteria = new Criteria(); // 声明一个定位准则对象
    private Handler mHandler = new Handler();
    private boolean isLocationEnable = false; // 定位服务是否可用
    private String mLocationType = ""; // 定位类型。是卫星定位还是网络定位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_smell);
        tv_satellite = findViewById(R.id.tv_satellite);
        // 从布局文件中获取名叫cv_satellite的罗盘视图
        cv_satellite = findViewById(R.id.cv_satellite);
        SwitchUtil.checkGpsIsOpen(this, "需要打开定位功能才能查看卫星导航信息");
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
        if (mLocationMgr.isProviderEnabled(bestProvider)) {  // 定位提供者当前可用
            mLocationType = bestProvider;
            beginLocation(bestProvider);
            isLocationEnable = true;
        } else { // 定位提供者暂不可用
            isLocationEnable = false;
        }
    }

    // 设置定位结果文本
    private void setLocationText(Location location) {
        if (location != null) {
            String desc = String.format("当前定位类型：%s，定位时间：%s" +
                            "\n经度：%f，纬度：%f\n高度：%d米，精度：%d米",
                    mLocationType, DateUtil.getNowTime(),
                    location.getLongitude(), location.getLatitude(),
                    Math.round(location.getAltitude()), Math.round(location.getAccuracy()));
            tv_satellite.setText(desc);
        } else {
            Log.d(TAG, "暂未获取到定位对象");
        }
    }

    // 开始定位
    private void beginLocation(String method) {
        // 检查当前设备是否已经开启了定位功能
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请授予定位权限并开启定位功能", Toast.LENGTH_SHORT).show();
            return;
        }
        // 设置定位管理器的位置变更监听器
        mLocationMgr.requestLocationUpdates(method, 300, 0, mLocationListener);
        // 获取最后一次成功定位的位置信息
        Location location = mLocationMgr.getLastKnownLocation(method);
        setLocationText(location);
        // 给定位管理器添加导航状态监听器
        mLocationMgr.addGpsStatusListener(mStatusListener);
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
            // 移除定位管理器的导航状态监听器
            mLocationMgr.removeGpsStatusListener(mStatusListener);
            // 移除定位管理器的位置变更监听器
            mLocationMgr.removeUpdates(mLocationListener);
        }
        super.onDestroy();
    }

    // 定义一个导航状态监听器
    private GpsStatus.Listener mStatusListener = new GpsStatus.Listener() {

        // 在卫星导航系统的状态变更时触发
        public void onGpsStatusChanged(int event) {
            // 获取卫星定位的状态信息
            GpsStatus gpsStatus = mLocationMgr.getGpsStatus(null);
            switch (event) {
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS: // 周期的报告卫星状态
                    // 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
                    Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                    for (GpsSatellite satellite : satellites) {
                        /*
                         * satellite.getElevation(); //卫星的仰角 (卫星的高度)
                         * satellite.getAzimuth(); //卫星的方位角
                         * satellite.getSnr(); //卫星的信噪比
                         * satellite.getPrn(); //卫星的伪随机码，可以认为就是卫星的编号
                         * satellite.hasAlmanac(); //卫星是否有年历表
                         * satellite.hasEphemeris(); //卫星是否有星历表
                         * satellite.usedInFix(); //卫星是否被用于近期的GPS修正计算
                         */
                        Satellite item = new Satellite();
                        item.seq = satellite.getPrn();
                        item.signal = Math.round(satellite.getSnr());
                        item.elevation = Math.round(satellite.getElevation());
                        item.azimuth = Math.round(satellite.getAzimuth());
                        item.time = DateUtil.getNowDateTime();
                        if (item.seq <= 64 || (item.seq >= 120 && item.seq <= 138)) {
                            item.nation = "美国";
                            item.name = "GPS";
                        } else if (item.seq >= 201 && item.seq <= 237) {
                            item.nation = "中国";
                            item.name = "北斗";
                        } else if (item.seq >= 65 && item.seq <= 89) {
                            item.nation = "俄罗斯";
                            item.name = "格洛纳斯";
                        } else {
                            Log.d(TAG, "Other seq="+item.seq+", signal="+item.signal+", elevation="+item.elevation+", azimuth="+item.azimuth);
                            item.nation = "其他";
                            item.name = "未知";
                        }
                        mapSatellite.put(item.seq, item);
                    }
                    cv_satellite.setSatelliteMap(mapSatellite);
                case GpsStatus.GPS_EVENT_FIRST_FIX: // 首次卫星定位
                case GpsStatus.GPS_EVENT_STARTED: // 卫星导航服务开始
                case GpsStatus.GPS_EVENT_STOPPED: // 卫星导航服务停止
                default:
                    break;
            }
        }
    };

}
