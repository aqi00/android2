package com.example.network.task;

import com.example.network.http.HttpRequestUtil;
import com.example.network.http.tool.HttpReqData;
import com.example.network.http.tool.HttpRespData;
import com.example.network.thread.ClientThread;

import android.os.AsyncTask;
import android.util.Log;

// 查询好友列表的线程
public class QueryFriendTask extends AsyncTask<Void, Void, String> {
    private final static String TAG = "QueryFriendTask";
    private String mQueryUrl = ClientThread.REQUEST_URL + "/queryFriend"; // 查询好友列表的服务地址

    public QueryFriendTask() {
        super();
    }

    // 线程正在后台处理
    protected String doInBackground(Void... params) {
        Log.d(TAG, "query url=" + mQueryUrl);
        // 创建一个HTTP请求对象
        HttpReqData req_data = new HttpReqData(mQueryUrl);
        // 发送HTTP请求信息，并获得HTTP应答对象
        HttpRespData resp_data = HttpRequestUtil.postData(req_data);
        // 返回串的样例：  {"title":"好友列表","group_list":[{"title":"亲戚","friend_list":[{"nick_name":"阿磊"},{"nick_name":"小月"},{"nick_name":"阿哥"},{"nick_name":"阿哥"},{"nick_name":"老吴"}]},{"title":"朋友","friend_list":[{"nick_name":"小红"},{"nick_name":"小白"},{"nick_name":"老吴"},{"nick_name":"小军"},{"nick_name":"阿毛"}]},{"title":"同学","friend_list":[{"nick_name":"小明"},{"nick_name":"老张"},{"nick_name":"老刘"},{"nick_name":"阿哥"},{"nick_name":"阿南"},{"nick_name":"大田"},{"nick_name":"阿英"},{"nick_name":"大牛"},{"nick_name":"老郑"},{"nick_name":"小燕"}]},{"title":"同事","friend_list":[{"nick_name":"阿磊"},{"nick_name":"老张"},{"nick_name":"小白"},{"nick_name":"大伟"},{"nick_name":"阿毛"},{"nick_name":"大麦"},{"nick_name":"老陈"},{"nick_name":"大田"}]},{"title":"客户","friend_list":[{"nick_name":"小丽"},{"nick_name":"大伟"},{"nick_name":"阿英"},{"nick_name":"阿毛"},{"nick_name":"阿紫"}]}]}
        Log.d(TAG, "result=" + resp_data.content);
        return resp_data.content; // 返回应答内容的json串
    }

    // 线程已经完成处理
    protected void onPostExecute(String resp) {
        // HTTP调用完毕，触发监听器的获得好友事件
        mListener.onQueryFriend(resp);
    }

    private OnQueryFriendListener mListener; // 声明一个查询好友的监听器对象
    // 设置查询好友的监听器
    public void setOnQueryFriendListener(OnQueryFriendListener listener) {
        mListener = listener;
    }

    // 定义一个查询好友的监听器接口
    public interface OnQueryFriendListener {
        void onQueryFriend(String resp);
    }

}
