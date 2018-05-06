package com.example.thirdsdk;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.thirdsdk.task.AlipayTask;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class TakeTaxActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "TakeTaxActivity";
    private EditText et_departure;
    private EditText et_destination;
    private TextView tv_travel;
    private Button btn_travel;
    private int mStep = 0;
    private SpeechSynthesizer mCompose;
    private LatLng mUserPos;
    private LatLng mDriverPos;
    private BitmapDescriptor icon_car;
    private int mDelayTime = 10000;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_take_tax);
        et_departure = findViewById(R.id.et_departure);
        et_destination = findViewById(R.id.et_destination);
        tv_travel = findViewById(R.id.tv_travel);
        btn_travel = findViewById(R.id.btn_travel);
        btn_travel.setOnClickListener(this);
        initLocation();
        initVoiceSetting();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_travel) {
            if (mStep == 0) { // 开始打车
                btn_travel.setTextColor(getResources().getColor(R.color.dark_grey));
                btn_travel.setEnabled(false);
                speaking("等待司机接单");
                mHandler.postDelayed(mAccept, mDelayTime);
            } else if (mStep == 1) { // 支付车费
                String desc = String.format("从%s到%s的打车费", et_departure.getText().toString(), et_destination.getText().toString());
                new AlipayTask(this, 1).execute("打车费", desc, "0.01");
                isFinished = false;
            }
            mStep++;
        }
    }

    private Handler mHandler = new Handler();
    private double latitude_offset;
    private double longitude_offset;
    private Runnable mAccept = new Runnable() {
        @Override
        public void run() {
            double latitude = mUserPos.latitude + getRandomDecimal();
            double longitude = mUserPos.longitude + getRandomDecimal();
            Log.d(TAG, "latitude=" + latitude + ", longitude=" + longitude);
            latitude_offset = latitude - mUserPos.latitude;
            longitude_offset = longitude - mUserPos.longitude;
            mDriverPos = new LatLng(latitude, longitude);
            rereshCar(mDriverPos);
            speaking("司机马上过来");
            mHandler.postDelayed(mRefresh, mDelayTime / 100);
        }
    };

    private double getRandomDecimal() {
        return 0.05 - Math.random() * 200 % 10.0 / 100.0;
    }

    private Runnable mRefresh = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            if (i++ < 100) {
                double new_latitude = mUserPos.latitude + latitude_offset * (100 - i) / 100.0;
                double new_longitude = mUserPos.longitude + longitude_offset * (100 - i) / 100.0;
                rereshCar(new LatLng(new_latitude, new_longitude));
                mHandler.postDelayed(this, mDelayTime / 100);
            } else {
                speaking("快车已经到达，请上车");
                mHandler.postDelayed(mTravel, mDelayTime);
            }
        }
    };

    private Runnable mTravel = new Runnable() {
        private int i = 0;

        @Override
        public void run() {
            if (i++ < 100) {
                double new_latitude = mUserPos.latitude + latitude_offset * i / 100.0;
                double new_longitude = mUserPos.longitude + longitude_offset * i / 100.0;
                rereshCar(new LatLng(new_latitude, new_longitude));
                mHandler.postDelayed(this, mDelayTime / 100);
            } else {
                speaking("已经到达目的地，欢迎下次再来乘车");
                btn_travel.setTextColor(getResources().getColor(R.color.black));
                btn_travel.setEnabled(true);
                btn_travel.setText("支付车费");
                isFinished = true;
            }
        }
    };

    private void rereshCar(LatLng pos) {
        mMapLayer.clear();
        OverlayOptions ooCar = new MarkerOptions().draggable(false)
                .visible(true).icon(icon_car).position(pos);
        mMapLayer.addOverlay(ooCar);
    }

    private void initVoiceSetting() {
        mCompose = SpeechSynthesizer.createSynthesizer(this, mComposeInitListener);
        mCompose.setParameter(SpeechConstant.PARAMS, null);
        mCompose.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mCompose.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        SharedPreferences shared = getSharedPreferences(VoiceSettingsActivity.PREFER_NAME, MODE_PRIVATE);
        mCompose.setParameter(SpeechConstant.SPEED, shared.getString("speed_preference", "50"));
        mCompose.setParameter(SpeechConstant.PITCH, shared.getString("pitch_preference", "50"));
        mCompose.setParameter(SpeechConstant.VOLUME, shared.getString("volume_preference", "50"));
        mCompose.setParameter(SpeechConstant.STREAM_TYPE, shared.getString("stream_preference", "3"));
        mCompose.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFinished) {
            mStep = 0;
            mMapLayer.clear();
            tv_travel.setText("准备出发");
            btn_travel.setText("开始叫车");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompose.stopSpeaking();
        mCompose.destroy();
    }

    private void speaking(String text) {
        tv_travel.setText(text);
        int code = mCompose.startSpeaking(text, mComposeListener);
        if (code != ErrorCode.SUCCESS) {
            Toast.makeText(this, "语音合成失败,错误码: " + code, Toast.LENGTH_SHORT).show();
        }
    }

    // 初始化监听
    private InitListener mComposeInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(TakeTaxActivity.this, "语音初始化失败,错误码: " + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private SynthesizerListener mComposeListener = new SynthesizerListener() {
        @Override
        public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {}
        @Override
        public void onCompleted(SpeechError arg0) {}
        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
        @Override
        public void onSpeakBegin() {}
        @Override
        public void onSpeakPaused() {}
        @Override
        public void onSpeakProgress(int arg0, int arg1, int arg2) {}
        @Override
        public void onSpeakResumed() {}
    };

    // 以下主要是定位用到的代码
    private MapView mMapView;
    private BaiduMap mMapLayer;
    private LocationClient mLocClient;
    private boolean isFirstLoc = true; // 是否首次定位

    private void initLocation() {
        mMapView = findViewById(R.id.mv_dongdong);
        mMapView.setVisibility(View.INVISIBLE);
        mMapLayer = mMapView.getMap();
        mMapLayer.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new MyLocationListenner());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
        icon_car = BitmapDescriptorFactory.fromResource(R.drawable.car_small);
    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                Log.d(TAG, "location is null or mMapView is null");
                return;
            }
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG, "latitude=" + latitude + ", longitude=" + longitude);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100).latitude(latitude).longitude(longitude).build();
            mMapLayer.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                mUserPos = new LatLng(latitude, longitude);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(mUserPos, 15);
                mMapLayer.animateMapStatus(update);
                mMapView.setVisibility(View.VISIBLE);
            }
        }
    }

}
