package com.example.network;

import java.util.ArrayList;

import com.example.network.adapter.FriendExpandAdapter;
import com.example.network.bean.Friend;
import com.example.network.bean.FriendGroup;
import com.example.network.bean.FriendResp;
import com.example.network.task.QueryFriendTask;
import com.example.network.task.QueryFriendTask.OnQueryFriendListener;
import com.example.network.thread.ClientThread;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint("StaticFieldLeak")
public class QQContactActivity extends AppCompatActivity implements
        OnClickListener, OnQueryFriendListener {
    private final static String TAG = "QQContactActivity";
    private static Context mContext;
    private static ExpandableListView elv_friend; // 声明一个可折叠列表视图对象
    private static ArrayList<FriendGroup> mGroupList = new ArrayList<FriendGroup>(); // 好友分组队列
    private static FriendGroup mGroupOnline = new FriendGroup(); // 在线好友分组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_contact);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置工具栏的标题文本
        tl_head.setTitle(getResources().getString(R.string.menu_second));
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
        tl_head.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mContext = getApplicationContext();
        mGroupOnline.title = "在线好友";
        // 从布局文件中获取名叫elv_friend的可折叠列表视图
        elv_friend = findViewById(R.id.elv_friend);
        findViewById(R.id.btn_refresh).setOnClickListener(this);
        // 创建好友查询线程
        QueryFriendTask queryTask = new QueryFriendTask();
        // 设置好友查询监听器
        queryTask.setOnQueryFriendListener(this);
        // 把好友查询线程加入到处理队列
        queryTask.execute();
    }

    // 在好友查询结束时触发
    public void onQueryFriend(String resp) {
        try {
            // 下面手工解析json串
//            JSONObject obj = new JSONObject(resp);
//            JSONArray groupArray = obj.getJSONArray("group_list");
//            for (int i = 0; i < groupArray.length(); i++) {
//                JSONObject groupObj = groupArray.getJSONObject(i);
//                FriendGroup group = new FriendGroup();
//                group.title = groupObj.getString("title");
//                JSONArray friendArray = groupObj.getJSONArray("friend_list");
//                for (int j = 0; j < friendArray.length(); j++) {
//                    JSONObject friendObj = friendArray.getJSONObject(j);
//                    Friend friend = new Friend("", friendObj.getString("nick_name"), "");
//                    group.friend_list.add(friend);
//                }
//                mGroupList.add(group);
//            }
            // 下面利用gson库自动解析json串
            FriendResp packageResp = new Gson().fromJson(resp, FriendResp.class);
            mGroupList = packageResp.group_list;
            showAllFriend(); // 显示所有好友分组
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "获取全部好友列表出错：" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        // 延迟500毫秒后启动分组刷新任务
        mHandler.postDelayed(mRefresh, 500);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 向后端服务器发送注销请求
        MainApplication.getInstance().sendAction(ClientThread.LOGOUT, "", "");
        super.onDestroy();
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个分组刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 向后端服务器发送获取在线好友的请求
            MainApplication.getInstance().sendAction(ClientThread.GETLIST, "", "");
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_refresh) {
            mHandler.post(mRefresh); // 立即启动分组刷新任务
        }
    }

    // 定义一个得到在线好友的广播接收器
    public static class GetListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.d(TAG, "onReceive");
                // 从意图中解包得到在线好友的消息内容
                String content = intent.getStringExtra(ClientThread.CONTENT);
                if (mContext != null && content != null && content.length() > 0) {
                    showFriendOnline(content); // 显示在线好友列表
                }
            }
        }
    }

    // 显示在线好友列表
    private static void showFriendOnline(String content) {
        int pos = content.indexOf(ClientThread.SPLIT_LINE);
        String head = content.substring(0, pos); // 消息头部
        String body = content.substring(pos + 1); // 消息主体
        String[] splitArray = head.split(ClientThread.SPLIT_ITEM);
        if (splitArray[0].equals(ClientThread.GETLIST)) { // 是获取好友列表
            String[] bodyArray = body.split("\\|"); // 每条好友记录之间以竖线分隔
            ArrayList<Friend> friendList = new ArrayList<Friend>();
            for (String oneBody : bodyArray) {
                String[] itemArray = oneBody.split(ClientThread.SPLIT_ITEM);
                if (oneBody.length() > 0 && itemArray.length >= 3) {
                    // itemArray数组内容依次为：设备编号、好友昵称、登录时间
                    friendList.add(new Friend(itemArray[0], itemArray[1], itemArray[2]));
                }
            }
            mGroupOnline.friend_list = friendList;
            showAllFriend(); // 显示所有好友分组
        } else { // 不是获取好友列表
            String hint = String.format("%s\n%s", splitArray[0], body);
            Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
        }
    }

    // 显示所有好友分组
    private static void showAllFriend() {
        ArrayList<FriendGroup> all_group = new ArrayList<FriendGroup>();
        all_group.add(mGroupOnline); // 先往好友队列添加在线好友
        all_group.addAll(mGroupList); // 再往好友队列添加各好友分组
        // 构建一个好友分组的可折叠列表适配器
        FriendExpandAdapter adapter = new FriendExpandAdapter(mContext, all_group);
        // 给elv_friend设置好友分组可折叠列表适配器
        elv_friend.setAdapter(adapter);
        // 给elv_friend设置孙子项的点击监听器
        elv_friend.setOnChildClickListener(adapter);
        // 给elv_friend设置分组的点击监听器
        elv_friend.setOnGroupClickListener(adapter);
        // 默认展开第一个好友分组，即在线好友分组
        elv_friend.expandGroup(0);
    }

    // 适配Android9.0开始
    @Override
    public void onStart() {
        super.onStart();
        // 从Android9.0开始，系统不再支持静态广播，应用广播只能通过动态注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 创建一个好友列表的广播接收器
            listReceiver = new GetListReceiver();
            // 注册广播接收器，注册之后才能正常接收广播
            registerReceiver(listReceiver, new IntentFilter(ClientThread.ACTION_GET_LIST));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 注销广播接收器，注销之后就不再接收广播
            unregisterReceiver(listReceiver);
        }
    }

    // 声明一个好友列表的广播接收器
    private GetListReceiver listReceiver;
    // 适配Android9.0结束

}
