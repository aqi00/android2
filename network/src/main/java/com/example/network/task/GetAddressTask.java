package com.example.network.task;

import java.text.MessageFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.network.http.HttpRequestUtil;
import com.example.network.http.tool.HttpReqData;
import com.example.network.http.tool.HttpRespData;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

// 根据经纬度获取详细地址的线程
public class GetAddressTask extends AsyncTask<Location, Void, String> {
    private final static String TAG = "GetAddressTask";
    private String mAddressUrl = "http://maps.google.cn/maps/api/geocode/json?latlng={0},{1}&sensor=true&language=zh-CN";

    public GetAddressTask() {
        super();
    }

    // 线程正在后台处理
    protected String doInBackground(Location... params) {
        Location location = params[0];
        // 把经度和纬度代入到URL地址
        String url = MessageFormat.format(mAddressUrl, location.getLatitude(), location.getLongitude());
        // 创建一个HTTP请求对象
        HttpReqData req_data = new HttpReqData(url);
        // 发送HTTP请求信息，并获得HTTP应答对象
        HttpRespData resp_data = HttpRequestUtil.getData(req_data);
        Log.d(TAG, "return json = " + resp_data.content);
        String address = "未知";
        // 下面从json串中逐级解析formatted_address字段获得详细地址描述
        if (resp_data.err_msg.length() <= 0) {
            try {
                JSONObject obj = new JSONObject(resp_data.content);
                JSONArray resultArray = obj.getJSONArray("results");
                if (resultArray.length() > 0) {
                    JSONObject resultObj = resultArray.getJSONObject(0);
                    address = resultObj.getString("formatted_address");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "address = " + address);
        return address; // 返回HTTP应答内容中的详细地址
    }

    // 线程已经完成处理
    protected void onPostExecute(String address) {
        // HTTP调用完毕，触发监听器的找到地址事件
        mListener.onFindAddress(address);
    }

    private OnAddressListener mListener; // 声明一个查询详细地址的监听器对象
    // 设置查询详细地址的监听器
    public void setOnAddressListener(OnAddressListener listener) {
        mListener = listener;
    }

    // 定义一个查询详细地址的监听器接口
    public interface OnAddressListener {
        void onFindAddress(String address);
    }

}
