package com.example.network.task;

import com.example.network.MainApplication;
import com.example.network.http.HttpRequestUtil;
import com.example.network.http.tool.HttpReqData;
import com.example.network.http.tool.HttpRespData;
import com.example.network.util.BitmapUtil;
import com.example.network.util.DateUtil;

import android.os.AsyncTask;
import android.util.Log;

// 获取图片验证码的线程
public class GetImageCodeTask extends AsyncTask<Void, Void, String> {
    private final static String TAG = "GetImageCodeTask";
    // 请求图片验证码的服务地址
    private String mImageCodeUrl = "http://222.77.181.14/ValidateCode.aspx?r=";
    //private String mImageCodeUrl = "http://220.160.54.47:82/JSPORTLET/radomImage?x=";

    public GetImageCodeTask() {
        super();
    }

    // 线程正在后台处理
    protected String doInBackground(Void... params) {
        // 为验证码地址添加一个随机串（以当前时间模拟随机串）
        String url = mImageCodeUrl + DateUtil.getNowDateTime();
        Log.d(TAG, "image url=" + url);
        // 创建一个HTTP请求对象
        HttpReqData req_data = new HttpReqData(url);
        // 发送HTTP请求信息，并获得HTTP应答对象
        HttpRespData resp_data = HttpRequestUtil.getImage(req_data);
        // 拼接一个图片验证码的本地临时路径
        String path = BitmapUtil.getCachePath(MainApplication.getInstance()) + DateUtil.getNowDateTime() + ".jpg";
        // 把HTTP调用获得的位图数据保存为图片
        BitmapUtil.saveBitmap(path, resp_data.bitmap, "jpg", 80);
        Log.d(TAG, "image path=" + path);
        return path; // 返回验证码图片的本地路径
    }

    // 线程已经完成处理
    protected void onPostExecute(String path) {
        // HTTP调用完毕，触发监听器的得到验证码事件
        mListener.onGetCode(path);
    }

    private OnImageCodeListener mListener; // 声明一个获取图片验证码的监听器对象
    // 设置获取图片验证码的监听器
    public void setOnImageCodeListener(OnImageCodeListener listener) {
        mListener = listener;
    }

    // 定义一个获取图片验证码的监听器接口
    public interface OnImageCodeListener {
        void onGetCode(String path);
    }

}
