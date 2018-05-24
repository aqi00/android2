package com.example.thirdsdk;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.maps2d.model.TextOptions;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.example.thirdsdk.util.DateUtil;
import com.example.thirdsdk.util.MapGaodeUtil;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class MapGaodeActivity extends AppCompatActivity implements OnClickListener,
        OnMapClickListener, OnPoiSearchListener, InputtipsListener {
    private static final String TAG = "MapGaodeActivity";
    private TextView tv_scope_desc, tv_loc_position;
    private int search_method;
    private String[] searchArray = {"城市中搜索", "在周边搜索"};
    private int SEARCH_CITY = 0;
    private int SEARCH_NEARBY = 1;
    private boolean isPaused = true;

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
        setContentView(R.layout.activity_map_gaode);
        tv_scope_desc = findViewById(R.id.tv_scope_desc);
        tv_loc_position = findViewById(R.id.tv_loc_position);
        setMethodSpinner(this, R.id.sp_poi_method, SEARCH_CITY);
        initLocation(savedInstanceState);
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
            mLocClient.onDestroy();
            mLocClient = null;
        }
        // 关闭定位图层
        if (mMapLayer != null) {
            mMapLayer.setMyLocationEnabled(false);
        }
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private double mLatitude;
    private double mLongitude;

    // 以下主要是POI搜索用到的代码
    private PoiSearch mPoiSearch = null;
    private AutoCompleteTextView et_searchkey = null;
    private EditText et_city = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;

    private void initMap() {
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
                // 使用建议搜索服务获取建议列表，结果在onGetInputtips中更新
                InputtipsQuery inputquery = new InputtipsQuery(cs.toString(), city);
                Inputtips inputTips = new Inputtips(MapGaodeActivity.this, inputquery);
                inputTips.setInputtipsListener(MapGaodeActivity.this);
                inputTips.requestInputtipsAsyn();
            }
        });
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode != 1000) {
            Toast.makeText(this, "推荐文字错误代码是" + rCode, Toast.LENGTH_LONG).show();
        } else {
            sugAdapter.clear();
            for (Tip info : tipList) {
                if (info.getName() != null) {
                    sugAdapter.add(info.getName());
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
            PoiSearch.Query query = new PoiSearch.Query(keyword, null, city);
            query.setPageSize(10);
            query.setPageNum(load_Index);
            mPoiSearch = new PoiSearch(this, query);
            mPoiSearch.setOnPoiSearchListener(this);
            mPoiSearch.searchPOIAsyn();
        } else if (search_method == SEARCH_NEARBY) {
            LatLonPoint position = new LatLonPoint(mLatitude, mLongitude);
            int radius = Integer.parseInt(et_city.getText().toString());
            PoiSearch.Query query = new PoiSearch.Query(keyword, null, "福州");
            query.setPageSize(10);
            query.setPageNum(load_Index);
            mPoiSearch = new PoiSearch(this, query);
            SearchBound bound = new SearchBound(position, radius);
            mPoiSearch.setBound(bound);
            mPoiSearch.setOnPoiSearchListener(this);
            mPoiSearch.searchPOIAsyn();
        }
    }

    public void goToNextPage(View v) {
        load_Index++;
        searchButtonProcess(null);
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode != 1000) {
            Toast.makeText(this, "POI错误代码是" + rCode, Toast.LENGTH_LONG).show();
        } else if (result == null || result.getQuery() == null) {
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
        } else {
            mMapLayer.clear();
            List<PoiItem> poiList = result.getPois();
            // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
            List<SuggestionCity> suggestionCities = result.getSearchSuggestionCitys();
            if (poiList != null && poiList.size() > 0) {
                PoiOverlay poiOverlay = new PoiOverlay(mMapLayer, poiList);
                // 从地图上移除原POI信息
                poiOverlay.removeFromMap();
                // 往地图添加新POI信息
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
                // 给POI添加监听器。在点击POI时提示POI信息
                mMapLayer.setOnMarkerClickListener(new OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            } else if (suggestionCities != null && suggestionCities.size() > 0) {
                String infomation = "推荐城市\n";
                for (int i = 0; i < suggestionCities.size(); i++) {
                    SuggestionCity city = suggestionCities.get(i);
                    infomation += "城市名称:" + city.getCityName() + "城市区号:"
                            + city.getCityCode() + "城市编码:" + city.getAdCode() + "\n";
                }
                Toast.makeText(this, infomation, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "结果记录数为0", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem paramPoiItem, int paramInt) {}

    // 以下主要是定位用到的代码
    private MapView mMapView; // 声明一个地图视图对象
    private AMap mMapLayer; // 声明一个地图图层对象
    private AMapLocationClient mLocClient; // 声明一个定位客户端对象
    private boolean isFirstLoc = true; // 是否首次定位

    // 初始化地图定位
    private void initLocation(Bundle savedInstanceState) {
        // 从布局文件中获取名叫amapView的地图视图
        mMapView = findViewById(R.id.amapView);
        // 执行地图视图的创建操作
        mMapView.onCreate(savedInstanceState);
        // 先隐藏地图，待定位到当前城市时再显示
        mMapView.setVisibility(View.INVISIBLE);
        if (mMapLayer == null) {
            mMapLayer = mMapView.getMap(); // 从地图视图中获取地图图层
        }
        mMapLayer.setOnMapClickListener(this); // 给地图图层设置地图点击监听器
        mMapLayer.setMyLocationEnabled(true); // 开启定位图层
        mLocClient = new AMapLocationClient(this.getApplicationContext()); // 创建一个定位客户端
        mLocClient.setLocationListener(new MyLocationListenner()); // 设置定位监听器
        AMapLocationClientOption option = new AMapLocationClientOption(); // 创建定位参数对象
        option.setLocationMode(AMapLocationMode.Battery_Saving); // 设置省电的定位模式
        option.setNeedAddress(true); // 设置true才能获得详细的地址信息
        mLocClient.setLocationOption(option); // 给定位客户端设置定位参数
        mLocClient.startLocation(); // 命令定位客户端开始定位
        // 获取最近一次的位置
        // mLocClient.getLastKnownLocation();
    }

    // 定义一个定位监听器
    public class MyLocationListenner implements AMapLocationListener {

        // 在接收到定位消息时触发
        public void onLocationChanged(AMapLocation location) {
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
                    location.getStreetNum(), location.getAddress(),
                    DateUtil.formatDate(location.getTime()));
            tv_loc_position.setText(position);
            if (isFirstLoc) { // 首次定位
                isFirstLoc = false;
                LatLng ll = new LatLng(mLatitude, mLongitude); // 创建一个经纬度对象
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
                mMapLayer.moveCamera(update); // 设置地图图层的地理位置与缩放比例
                mMapView.setVisibility(View.VISIBLE); // 定位到当前城市时再显示图层
                // 画当前位置
                BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_locate);
                MarkerOptions ooMarker = new MarkerOptions().draggable(false)
                        .visible(true).icon(bitmapDesc).position(ll);
                mMapLayer.addMarker(ooMarker);
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
        if (isPolygon && posArray.size() > 1 && MapGaodeUtil.isInsidePolygon(pos, posArray)) {
            Log.d(TAG, "isInsidePolygon");
            LatLng centerPos = MapGaodeUtil.getCenterPos(posArray);
            TextOptions ooText = new TextOptions().backgroundColor(0x00ffffff)
                    .fontSize(26).fontColor(textColor).text("标题")// .rotate(-30)
                    .position(centerPos);
            mMapLayer.addText(ooText);
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
            int distance = (int) Math.round(MapGaodeUtil.getShortDistance(
                    thisPos.longitude, thisPos.latitude, firstPos.longitude,
                    firstPos.latitude));
            // 多次点击起点，要忽略之
            if (posArray.size() == 1 && distance <= 0) {
                return;
            } else if (posArray.size() > 1) {
                LatLng lastPos = posArray.get(posArray.size() - 1);
                int lastDistance = (int) Math.round(MapGaodeUtil.getShortDistance(
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
            Log.d(TAG, "distance=" + distance + ", radius=" + radius
                    + ", is_first=" + is_first);

            // 画直线
            LatLng lastPos = posArray.get(posArray.size() - 1);
            List<LatLng> points = new ArrayList<LatLng>();
            points.add(lastPos);
            points.add(thisPos);
            PolylineOptions ooPolyline = new PolylineOptions().width(2)
                    .color(lineColor).addAll(points);
            mMapLayer.addPolyline(ooPolyline);

            // 下面计算两点之间距离
            distance = (int) Math.round(MapGaodeUtil.getShortDistance(
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
            TextOptions ooText = new TextOptions().backgroundColor(0x00ffffff)
                    .fontSize(24).fontColor(textColor).text(disText)// .rotate(-30)
                    .position(llText);
            mMapLayer.addText(ooText);
        }
        if (!is_first) {
//			// 画圆圈
//			CircleOptions ooCircle = new CircleOptions().fillColor(lineColor)
//					.center(thisPos).strokeWidth(2).strokeColor(0xAAFF0000)
//					.radius(radius);
//			mMapLayer.addCircle(ooCircle);
            // 画图片标记
            BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_geo);
            MarkerOptions ooMarker = new MarkerOptions().draggable(false)
                    .visible(true).icon(bitmapDesc).position(thisPos);
            mMapLayer.addMarker(ooMarker);
            mMapLayer.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng markPos = marker.getPosition();
                    addDot(markPos);
                    marker.showInfoWindow();
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
            PolygonOptions ooPolygon = new PolygonOptions().addAll(posArray)
                    .strokeColor(0xFF00FF00).strokeWidth(1)
                    .fillColor(polygonColor);
            mMapLayer.addPolygon(ooPolygon);
            isPolygon = true;

            // 下面计算多边形的面积
            LatLng centerPos = MapGaodeUtil.getCenterPos(posArray);
            double area = Math.round(MapGaodeUtil.getArea(posArray));
            String areaText;
            if (area > 1000000) {
                areaText = Math.round(area * 100 / 1000000) / 100d + "平方公里";
            } else {
                areaText = (int) area + "平方米";
            }
            TextOptions ooText = new TextOptions().backgroundColor(0x00ffffff)
                    .fontSize(26).fontColor(textColor).text(areaText)// .rotate(-30)
                    .position(centerPos);
            mMapLayer.addText(ooText);
        }
        posArray.add(thisPos);
    }

    @Override
    public void onMapClick(LatLng arg0) {
        addDot(arg0);
    }

}
