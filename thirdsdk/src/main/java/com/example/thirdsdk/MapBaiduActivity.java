package com.example.thirdsdk;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.thirdsdk.util.MapBaiduUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class MapBaiduActivity extends AppCompatActivity implements OnClickListener,
        OnMapClickListener, OnGetPoiSearchResultListener,
        OnGetSuggestionResultListener {
    private static final String TAG = "MapBaiduActivity";
    private TextView tv_scope_desc, tv_loc_position;
    private int search_method;
    private String[] searchArray = {"城市中搜索", "在周边搜索"};
    private int SEARCH_CITY = 0;
    private int SEARCH_NEARBY = 1;
    private boolean isPaused = false;

    private void setMethodSpinner(Context context, int spinner_id, int seq) {
        Spinner sp_poi_method = findViewById(spinner_id);
        ArrayAdapter<String> county_adapter;
        county_adapter = new ArrayAdapter<String>(context,
                R.layout.item_select, searchArray);
        county_adapter.setDropDownViewResource(R.layout.item_select);
        // setPrompt是设置弹出对话框的标题
        sp_poi_method.setPrompt("请选择POI搜索方式");
        sp_poi_method.setAdapter(county_adapter);
        sp_poi_method.setOnItemSelectedListener(new SpinnerSelectedListenerOrder());
        if (seq >= 0) {
            sp_poi_method.setSelection(seq, true);
        } else {
            sp_poi_method.setFocusable(false);
        }
    }

    class SpinnerSelectedListenerOrder implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            search_method = arg2;
            if (search_method == SEARCH_CITY) {
                tv_scope_desc.setText("市内找");
            } else if (search_method == SEARCH_NEARBY) {
                tv_scope_desc.setText("米内找");
            }
            et_city.setText("");
            et_searchkey.setText("");
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意该方法要在setContentView方法之前实现
        // 初始化百度地图SDK
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map_baidu);
        tv_scope_desc = findViewById(R.id.tv_scope_desc);
        tv_loc_position = findViewById(R.id.tv_loc_position);
        setMethodSpinner(this, R.id.sp_poi_method, SEARCH_CITY);
        initLocation();
        initMap();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_search) {
            searchButtonProcess(v);
        } else if (v.getId() == R.id.btn_next_data) {
            goToNextPage(v);
        } else if (v.getId() == R.id.btn_clear_data) {
            et_city.setText("");
            et_searchkey.setText("");
            // 清除所有图层
            mMapView.getMap().clear();
            posArray.clear();
            isPolygon = false;
        }
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        isPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        if (isPaused) {
            mMapView.onResume();
            isPaused = false;
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        if (null != mLocClient) {
            mLocClient.stop();
            mLocClient = null;
        }
        // 关闭定位图层
        if (mMapLayer != null) {
            mMapLayer.setMyLocationEnabled(false);
        }
        mMapView.onDestroy();
        mMapView = null;
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }

    private double mLatitude;
    private double mLongitude;

    // 以下主要是POI搜索用到的代码
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private AutoCompleteTextView et_searchkey = null;
    private EditText et_city = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;

    private void initMap() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        et_city = findViewById(R.id.et_city);
        et_searchkey = findViewById(R.id.et_searchkey);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_next_data).setOnClickListener(this);
        findViewById(R.id.btn_clear_data).setOnClickListener(this);
        sugAdapter = new ArrayAdapter<String>(this, R.layout.item_select);
        et_searchkey.setAdapter(sugAdapter);

        // 当输入关键字变化时，动态更新建议列表
        et_searchkey.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                String city = et_city.getText().toString();
                // 使用建议搜索服务获取建议列表，结果在onGetSuggestionResult中更新
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(city));
            }
        });
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res != null && res.getAllSuggestions() != null) {
            sugAdapter.clear();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info.key != null) {
                    sugAdapter.add(info.key);
                }
            }
            sugAdapter.notifyDataSetChanged();
        }
    }

    // 影响搜索按钮点击事件
    public void searchButtonProcess(View v) {
        Log.d(TAG, "editCity=" + et_city.getText().toString()
                + ", editSearchKey=" + et_searchkey.getText().toString()
                + ", load_Index=" + load_Index);
        String keyword = et_searchkey.getText().toString();
        if (search_method == SEARCH_CITY) {
            String city = et_city.getText().toString();
            mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city)
                    .keyword(keyword).pageNum(load_Index));
        } else if (search_method == SEARCH_NEARBY) {
            LatLng position = new LatLng(mLatitude, mLongitude);
            int radius = Integer.parseInt(et_city.getText().toString());
            mPoiSearch.searchNearby((new PoiNearbySearchOption())
                    .location(position).keyword(keyword).radius(radius)
                    .pageNum(load_Index));
        }
    }

    public void goToNextPage(View v) {
        load_Index++;
        searchButtonProcess(null);
    }

    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
        } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mMapLayer.clear();
            PoiOverlay overlay = new MyPoiOverlay(mMapLayer);
            mMapLayer.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
        } else if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city + ",";
            }
            strInfo += "找到结果";
            Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
        }
    }

    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG,
                    "name=" + result.getName() + ",address="
                            + result.getAddress() + ",detail_url="
                            + result.getDetailUrl() + ",shop_hours="
                            + result.getShopHours() + ",telephone="
                            + result.getTelephone() + ",price="
                            + result.getPrice() + ",type=" + result.getType()
                            + ",tag=" + result.getTag());
            Toast.makeText(this, result.getName() + ": " + result.getAddress(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult result) {}

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            return true;
        }
    }

    // 以下主要是定位用到的代码
    private MapView mMapView; // 声明一个地图视图对象
    private BaiduMap mMapLayer; // 声明一个地图图层对象
    private LocationClient mLocClient; // 声明一个定位客户端对象
    private boolean isFirstLoc = true; // 是否首次定位

    // 初始化地图定位
    private void initLocation() {
        // 从布局文件中获取名叫bmapView的地图视图
        mMapView = findViewById(R.id.bmapView);
        // 先隐藏地图，待定位到当前城市时再显示
        mMapView.setVisibility(View.INVISIBLE);
        mMapLayer = mMapView.getMap(); // 从地图视图中获取地图图层
        mMapLayer.setOnMapClickListener(this); // 给地图图层设置地图点击监听器
        mMapLayer.setMyLocationEnabled(true); // 开启定位图层
        mLocClient = new LocationClient(this); // 创建一个定位客户端
        mLocClient.registerLocationListener(new MyLocationListenner()); // 设置定位监听器
        LocationClientOption option = new LocationClientOption(); // 创建定位参数对象
        option.setOpenGps(true); // 打开GPS
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000); // 设置定位的时间间隔
        option.setIsNeedAddress(true); // 设置true才能获得详细的地址信息
        mLocClient.setLocOption(option); // 给定位客户端设置定位参数
        mLocClient.start(); // 命令定位客户端开始定位
        // 获取最近一次的位置
        // mLocClient.getLastKnownLocation();
    }

    // 定义一个定位监听器
    public class MyLocationListenner implements BDLocationListener {

        // 在接收到定位消息时触发
        public void onReceiveLocation(BDLocation location) {
            // 如果地图视图已经销毁，则不再处理新接收的位置
            if (location == null || mMapView == null) {
                Log.d(TAG, "location is null or mMapView is null");
                return;
            }
            mLatitude = location.getLatitude(); // 获得该位置的纬度
            mLongitude = location.getLongitude(); // 获得该位置的经度
            String position = String.format("当前位置：%s|%s|%s|%s|%s|%s|%s",
                    location.getProvince(), location.getCity(),
                    location.getDistrict(), location.getStreet(),
                    location.getStreetNumber(), location.getAddrStr(),
                    location.getTime());
            tv_loc_position.setText(position);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(mLatitude).longitude(mLongitude)
                    .build();
            mMapLayer.setMyLocationData(locData); // 给地图图层设置定位地点
            if (isFirstLoc) { // 首次定位
                isFirstLoc = false;
                LatLng ll = new LatLng(mLatitude, mLongitude); // 创建一个经纬度对象
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 14);
                mMapLayer.animateMapStatus(update); // 设置地图图层的地理位置与缩放比例
                mMapView.setVisibility(View.VISIBLE); // 定位到当前城市时再显示图层
            }
        }
    }

    // 下面是在地图上添加绘图操作
    private static int lineColor = 0x55FF0000;
    private static int arcColor = 0xbb00FFFF;
    private static int textColor = 0x990000FF;
    private static int polygonColor = 0x77FFFF00;
    private static int radius = 100;
    private ArrayList<LatLng> posArray = new ArrayList<LatLng>();
    boolean isPolygon = false;

    private void addDot(LatLng pos) {
        if (isPolygon && posArray.size() > 1 && MapBaiduUtil.isInsidePolygon(pos, posArray)) {
            Log.d(TAG, "isInsidePolygon");
            LatLng centerPos = MapBaiduUtil.getCenterPos(posArray);
            OverlayOptions ooText = new TextOptions().bgColor(0x00ffffff)
                    .fontSize(26).fontColor(textColor).text("标题")// .rotate(-30)
                    .position(centerPos);
            mMapLayer.addOverlay(ooText);
            return;
        }
        if (isPolygon) {
            Log.d(TAG, "isPolygon == true");
            posArray.clear();
            isPolygon = false;
        }
        boolean is_first = false;
        LatLng thisPos = pos;
        if (posArray.size() > 0) {
            LatLng firstPos = posArray.get(0);
            int distance = (int) Math.round(MapBaiduUtil.getShortDistance(
                    thisPos.longitude, thisPos.latitude, firstPos.longitude,
                    firstPos.latitude));
            // 多次点击起点，要忽略之
            if (posArray.size() == 1 && distance <= 0) {
                return;
            } else if (posArray.size() > 1) {
                LatLng lastPos = posArray.get(posArray.size() - 1);
                int lastDistance = (int) Math.round(MapBaiduUtil.getShortDistance(
                        thisPos.longitude, thisPos.latitude, lastPos.longitude,
                        lastPos.latitude));
                // 重复响应当前位置的点击，要忽略之
                if (lastDistance <= 0) {
                    return;
                }
            }
            if (distance < radius * 2) {
                thisPos = firstPos;
                is_first = true;
            }
            Log.d(TAG, "distance=" + distance + ", radius=" + radius + ", is_first=" + is_first);

            // 画直线
            LatLng lastPos = posArray.get(posArray.size() - 1);
            List<LatLng> points = new ArrayList<LatLng>();
            points.add(lastPos);
            points.add(thisPos);
            OverlayOptions ooPolyline = new PolylineOptions().width(2)
                    .color(lineColor).points(points);
            mMapLayer.addOverlay(ooPolyline);

            // 下面计算两点之间距离
            distance = (int) Math.round(MapBaiduUtil.getShortDistance(
                    thisPos.longitude, thisPos.latitude, lastPos.longitude,
                    lastPos.latitude));
            String disText;
            if (distance > 1000) {
                disText = Math.round(distance * 10 / 1000) / 10d + "公里";
            } else {
                disText = distance + "米";
            }
            LatLng llText = new LatLng(
                    (thisPos.latitude + lastPos.latitude) / 2,
                    (thisPos.longitude + lastPos.longitude) / 2);
            OverlayOptions ooText = new TextOptions().bgColor(0x00ffffff)
                    .fontSize(24).fontColor(textColor).text(disText)// .rotate(-30)
                    .position(llText);
            mMapLayer.addOverlay(ooText);
        }
        if (!is_first) {
//			// 画圆圈
//			OverlayOptions ooCircle = new CircleOptions().fillColor(lineColor)
//					.center(thisPos).stroke(new Stroke(2, 0xAAFF0000))
//					.radius(radius);
//			mMapLayer.addOverlay(ooCircle);
            // 画图片标记
            BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_geo);
            OverlayOptions ooMarker = new MarkerOptions().draggable(false)
                    .visible(true).icon(bitmapDesc).position(thisPos);
            mMapLayer.addOverlay(ooMarker);
            mMapLayer.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng markPos = marker.getPosition();
                    addDot(markPos);
                    return true;
                }
            });
        } else {
            Log.d(TAG, "posArray.size()=" + posArray.size());
            // 可能存在地图与标记同时响应点击事件的情况
            if (posArray.size() < 3) {
                posArray.clear();
                isPolygon = false;
                return;
            }
            // 画多边形
            OverlayOptions ooPolygon = new PolygonOptions().points(posArray)
                    .stroke(new Stroke(1, 0xFF00FF00)).fillColor(polygonColor);
            mMapLayer.addOverlay(ooPolygon);
            isPolygon = true;

            // 下面计算多边形的面积
            LatLng centerPos = MapBaiduUtil.getCenterPos(posArray);
            double area = Math.round(MapBaiduUtil.getArea(posArray));
            String areaText;
            if (area > 1000000) {
                areaText = Math.round(area * 100 / 1000000) / 100d + "平方公里";
            } else {
                areaText = (int) area + "平方米";
            }
            OverlayOptions ooText = new TextOptions().bgColor(0x00ffffff)
                    .fontSize(26).fontColor(textColor).text(areaText)// .rotate(-30)
                    .position(centerPos);
            mMapLayer.addOverlay(ooText);
        }
        posArray.add(thisPos);
        if (posArray.size() >= 3) {
            // 画弧线
            OverlayOptions ooArc = new ArcOptions()
                    .color(arcColor).width(2)
                    .points(posArray.get(posArray.size() - 1),
                            posArray.get(posArray.size() - 2),
                            posArray.get(posArray.size() - 3));
            mMapLayer.addOverlay(ooArc);
        }
    }

    @Override
    public void onMapClick(LatLng arg0) {
        addDot(arg0);
    }

    @Override
    public boolean onMapPoiClick(MapPoi arg0) {
        addDot(arg0.getPosition());
        return false;
    }

}
