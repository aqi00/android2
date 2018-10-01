package com.example.network;

import java.util.HashMap;
import java.util.Map;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.network.task.UploadHttpTask;
import com.example.network.task.UploadHttpTask.OnUploadHttpListener;
import com.example.network.thread.ClientThread;
import com.example.network.util.DateUtil;
import com.example.network.util.Utils;
import com.example.network.widget.TextProgressCircle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint(value={"SetTextI18n","StaticFieldLeak","HardwareIds","RtlHardcoded"})
public class ChatMainActivity extends AppCompatActivity implements
        OnClickListener, FileSelectCallbacks, OnUploadHttpListener {
    private static final String TAG = "ChatMainActivity";
    private static Context mContext;
    private static Activity mActivity;
    private TextView tv_other;
    private EditText et_input;
    private static TextView tv_show;
    private static ScrollView sv_chat; // 声明一个滚动视图对象
    private static LinearLayout ll_show; // 声明一个聊天窗口的线性布局对象
    private String mOtherId; // 对方的设备编号
    private static int dip_margin; // 每条聊天记录的四周空白距离
    private static int TYPE_PHOTO = 0; // 图片消息
    private static int TYPE_SOUND = 1; // 音频消息
    private static Handler mHandler = new Handler(); // 声明一个处理器对象
    private static int MEDIA_WIDTH = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        mContext = getApplicationContext();
        mActivity = this;
        tv_other = findViewById(R.id.tv_other);
        et_input = findViewById(R.id.et_input);
        tv_show = findViewById(R.id.tv_show);
        // 从布局文件中获取名叫sv_chat的滚动视图
        sv_chat = findViewById(R.id.sv_chat);
        // 从布局文件中获取名叫ll_show的线性布局
        ll_show = findViewById(R.id.ll_show);
        findViewById(R.id.ib_photo).setOnClickListener(this);
        findViewById(R.id.ib_sound).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        dip_margin = Utils.dip2px(mContext, 5);
        // 获取前一个页面传来的包裹
        Bundle bundle = getIntent().getExtras();
        // 从包裹中获取名叫otherId的对方设备编号
        mOtherId = bundle.getString("otherId", "");
        // 从包裹中获取名叫otherName的对方昵称
        String desc = String.format("与%s聊天", bundle.getString("otherName", ""));
        tv_other.setText(desc);
        // 从系统服务中获取下载管理器
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_photo) { // 点击了发送图片按钮
            // 声明一个图片文件的扩展名数组
            String[] photoExt = new String[]{"jpg", "png"};
            HashMap<String, Object> map_param = new HashMap<String, Object>();
            map_param.put("type", TYPE_PHOTO);
            // 打开文件选择对话框
            FileSelectFragment.show(this, photoExt, map_param);
        } else if (v.getId() == R.id.ib_sound) { // 点击了发送音频按钮
            // 声明一个音频文件的扩展名数组
            String[] soundExt = new String[]{"amr", "aac", "mp3", "wav", "mid", "ogg"};
            HashMap<String, Object> map_param = new HashMap<String, Object>();
            map_param.put("type", TYPE_SOUND);
            // 打开文件选择对话框
            FileSelectFragment.show(this, soundExt, map_param);
        } else if (v.getId() == R.id.btn_send) { // 点击了发送文本按钮
            String body = et_input.getText().toString();
            // 拼接文本消息的消息内容
            String append = String.format("%s %s\n%s",
                    MainApplication.getInstance().getNickName(),
                    DateUtil.formatTime(DateUtil.getNowDateTime("")), body);
            // 在聊天窗口中添加文本消息
            appendMsg(Build.SERIAL, append);
            // 往后端服务器发送文本消息的请求
            MainApplication.getInstance().sendAction(ClientThread.SENDMSG, mOtherId, body);
            et_input.setText("");
        }
    }

    // 在聊天窗口中添加文本消息
    private static void appendMsg(String deviceId, String append) {
        // 我方消息靠右对齐，对方消息靠左对齐
        int gravity = deviceId.equals(Build.SERIAL) ? Gravity.RIGHT : Gravity.LEFT;
        // 我方消息背景色为蓝色，对方消息背景色为红色
        int bg_color = deviceId.equals(Build.SERIAL) ? 0xffccccff : 0xffffcccc;
        // 以下初始化单条聊天消息的线性布局
        LinearLayout ll_append = new LinearLayout(mContext);
        LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ll_params.setMargins(dip_margin, dip_margin, dip_margin, dip_margin);
        ll_append.setLayoutParams(ll_params);
        ll_append.setGravity(gravity);
        ll_append.setOrientation(LinearLayout.VERTICAL);
        // 以下初始化消息内容的文本视图
        TextView tv_append = getTextView(tv_show.getText().toString() + append, gravity);
        tv_append.setBackgroundColor(bg_color);
        // 把消息内容的文本视图添加到线性布局上
        ll_append.addView(tv_append);
        // 把单条消息的线性布局添加到聊天窗口上
        ll_show.addView(ll_append);
        // 延迟100毫秒后启动聊天窗口的滚动任务
        mHandler.postDelayed(mScroll, 100);
    }

    // 获得一个消息内容的文本视图模板
    private static TextView getTextView(String content, int gravity) {
        TextView tv = new TextView(mContext);
        tv.setText(content);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tv.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(tv_params);
        tv.setGravity(gravity);
        return tv;
    }

    // 定义一个聊天窗口的滚动任务
    private static Runnable mScroll = new Runnable() {
        @Override
        public void run() {
            // 让滚动视图滚动到底部
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    private String mUploadUrl = ClientThread.REQUEST_URL + "/uploadServlet"; // 文件上传地址
    private String mFileName; // 文件名称
    private static String mFilePath; // 文件所在目录
    private int mType; // 文件类型。TYPE_PHOTO表示图片文件，TYPE_SOUND表示音频文件

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        mFileName = fileName;
        // 拼接文件的完整路径
        mFilePath = String.format("%s/%s", absolutePath, fileName);
        Log.d(TAG, "select path=" + mFilePath);
        mType = (int) map_param.get("type");
        // 创建文件上传线程
        UploadHttpTask uploadTask = new UploadHttpTask();
        // 设置文件上传监听器
        uploadTask.setOnUploadHttpListener(this);
        // 把文件上传线程加入到处理队列
        uploadTask.execute(mUploadUrl, mFilePath);
//        String url = "http://img1.gtimg.com/fj/pics/hv1/229/130/1835/119354254.png";
//        MainApplication.getInstance().sendAction(ClientThread.SENDPHOTO, mOtherId, url);
//        String url = "http://mp3.haoduoge.com/s/2016-11-14/1479089459.mp3";
//        MainApplication.getInstance().sendAction(ClientThread.SENDSOUND, mOtherId, url);
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    // 在文件上传结束后触发
    public void onUploadFinish(String result) {
        Log.d(TAG, "upload result=" + result);
        if (result.equals("SUCC")) { // 上传成功
            // 拼接该文件的下载地址
            String downloadUrl = mUploadUrl.substring(0, mUploadUrl.lastIndexOf("/") + 1) + mFileName;
            // 拼接多媒体消息的消息标题
            String title = String.format("%s %s",
                    MainApplication.getInstance().getNickName(),
                    DateUtil.formatTime(DateUtil.getNowDateTime("")));
            if (mType == TYPE_PHOTO) { // 图片消息
                // 在聊天窗口中添加多媒体消息
                showMedia(ClientThread.RECVPHOTO, Build.SERIAL, title, mFilePath);
                // 向后端服务器发送图片消息的请求
                MainApplication.getInstance().sendAction(ClientThread.SENDPHOTO, mOtherId, downloadUrl);
            } else if (mType == TYPE_SOUND) { // 音频消息
                // 在聊天窗口中添加多媒体消息
                showMedia(ClientThread.RECVSOUND, Build.SERIAL, title, mFilePath);
                // 向后端服务器发送音频消息的请求
                MainApplication.getInstance().sendAction(ClientThread.SENDSOUND, mOtherId, downloadUrl);
            }
        } else { // 上传失败
            Toast.makeText(this, "上传失败：" + result, Toast.LENGTH_SHORT).show();
        }
    }

    // 定义一个收到消息的广播接收器
    public static class RecvMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.d(TAG, "onReceive");
                // 从广播意图中解包得到收到的消息内容
                String content = intent.getStringExtra(ClientThread.CONTENT);
                if (mContext != null && content != null && content.length() > 0) { // 接收成功
                    int pos = content.indexOf(ClientThread.SPLIT_LINE);
                    String head = content.substring(0, pos);  // 消息头部
                    String body = content.substring(pos + 1); // 消息主体
                    String[] splitArray = head.split(ClientThread.SPLIT_ITEM);
                    String action = splitArray[0];
                    if (action.equals(ClientThread.RECVMSG)) { // 文本消息
                        // 拼接文本消息的消息内容
                        String append = String.format("%s %s\n%s",
                                splitArray[2], DateUtil.formatTime(splitArray[3]), body);
                        // 在聊天窗口中添加文本消息
                        appendMsg(splitArray[1], append);
                    } else if (action.equals(ClientThread.RECVPHOTO) // 图片消息或者音频消息
                            || action.equals(ClientThread.RECVSOUND)) {
                        // 拼接多媒体消息的消息标题
                        String title = String.format("%s %s", splitArray[2],
                                DateUtil.formatTime(splitArray[3]));
                        // 在聊天窗口中添加多媒体消息
                        showMedia(action, splitArray[1], title, body);
                    } else { // 接收失败
                        String hint = String.format("%s\n%s", splitArray[0], body);
                        Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private static int mBeginViewId = 0x7F24FFF0;
    private static DownloadManager mDownloadManager; // 声明一个下载管理器对象
    private static long mDownloadId = 0; // 当前任务的下载编号

    // 在聊天窗口中添加多媒体消息
    private static void showMedia(String action, String deviceId, String title, String url) {
        Log.d(TAG, "showMedia action=" + action + ", url=" + url);
        boolean isLocalPath = !url.contains("http://");
        // 我方消息靠右对齐，对方消息靠左对齐
        int gravity = deviceId.equals(Build.SERIAL) ? Gravity.RIGHT : Gravity.LEFT;
        gravity = gravity | Gravity.CENTER_VERTICAL; // 并且垂直居中
        // 我方消息背景色为蓝色，对方消息背景色为红色
        int bg_color = deviceId.equals(Build.SERIAL) ? 0xffccccff : 0xffffcccc;
        int type = TYPE_PHOTO;
        // 以下初始化单条聊天消息的线性布局
        LinearLayout ll_append = new LinearLayout(mContext);
        LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ll_params.setMargins(dip_margin, dip_margin, dip_margin, dip_margin);
        ll_append.setLayoutParams(ll_params);
        ll_append.setGravity(gravity);
        ll_append.setOrientation(LinearLayout.VERTICAL);
        // 以下初始化消息内容的线性布局
        LinearLayout ll_content = new LinearLayout(mContext);
        LinearLayout.LayoutParams content_params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ll_content.setLayoutParams(content_params);
        ll_content.setBackgroundColor(bg_color);
        ll_content.setGravity(gravity);
        ll_content.setOrientation(LinearLayout.VERTICAL);
        // 以下初始化消息标题的文本视图
        TextView tv_title = getTextView(title, gravity);
        // 把消息标题的文本视图添加到线性布局上
        ll_content.addView(tv_title);
        // 以下初始化多媒体消息的相对布局
        RelativeLayout rl_content = new RelativeLayout(mContext);
        rl_content.setLayoutParams(content_params);
        rl_content.setId(mBeginViewId++);
        // 以下初始化图片消息的图像视图
        ImageView iv_append = new ImageView(mContext);
        LinearLayout.LayoutParams iv_params = new LinearLayout.LayoutParams(
                Utils.dip2px(mContext, MEDIA_WIDTH), LayoutParams.WRAP_CONTENT);
        iv_append.setId(mBeginViewId++);
        iv_append.setLayoutParams(iv_params);
        iv_append.setScaleType(ScaleType.FIT_CENTER);
        if (action.equals(ClientThread.RECVPHOTO)) { // 图片消息
            iv_append.setImageResource(R.drawable.default_photo);
        } else if (action.equals(ClientThread.RECVSOUND)) { // 音频消息
            iv_append.setImageResource(R.drawable.default_sound);
            type = TYPE_SOUND;
        }
        if (isLocalPath && action.equals(ClientThread.RECVPHOTO)) { // 已经接收到对方发来的图片
            // 把指定路径的图片显示在图像视图上面
            iv_append.setImageURI(Uri.parse(mFilePath));
            // 重新设置图像视图的高度
            iv_append.setLayoutParams(getImageParam(mFilePath, 0));
        }
        // 把图片消息的图像视图添加到相对布局上
        rl_content.addView(iv_append);
        // 创建一个文本进度圈
        TextProgressCircle tpc_progress = new TextProgressCircle(mContext);
        if (!isLocalPath) { // 不是本地文件
            RelativeLayout.LayoutParams tpc_params = new RelativeLayout.LayoutParams(
                    Utils.dip2px(mContext, MEDIA_WIDTH), Utils.dip2px(mContext, MEDIA_WIDTH));
            tpc_params.addRule(RelativeLayout.CENTER_IN_PARENT, rl_content.getId());
            tpc_progress.setId(mBeginViewId++);
            tpc_progress.setLayoutParams(tpc_params);
            tpc_progress.setBackgroundColor(0x99ffffff);
            tpc_progress.setVisibility(View.GONE);
            // 把文本进度圈添加到相对布局上
            rl_content.addView(tpc_progress);
        }
        // 把多媒体消息的相对布局添加到线性布局上
        ll_content.addView(rl_content);
        // 以下初始化消息详情的文本视图
        TextView tv_detail = getTextView("", gravity);
        tv_detail.setId(mBeginViewId++);
        // 把消息详情的文本视图添加到线性布局上
        ll_content.addView(tv_detail);
        // 把消息内容的线性布局添加到线性布局ll_append上
        ll_append.addView(ll_content);
        // 把单条消息的线性布局添加到聊天窗口上
        ll_show.addView(ll_append);
        // 延迟100毫秒后启动聊天窗口的滚动任务
        mHandler.postDelayed(mScroll, 100);
        if (!isLocalPath) { // 不是本地文件
            downloadFile(url); // 开始下载多媒体文件
            // 创建一个下载进度的刷新任务
            RefreshRunnable refresh = new RefreshRunnable(new int[]{
                    type, rl_content.getId(), iv_append.getId(), tpc_progress.getId(), tv_detail.getId()});
            // 延迟100毫秒后启动下载进度的刷新任务
            mHandler.postDelayed(refresh, 100);
        }
    }

    // 获取图像视图实际的布局参数
    private static ViewGroup.LayoutParams getImageParam(String imagePath, int type) {
        // 从指定路径的图片文件获取位图对象
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        // 重新计算图片消息的高度
        int height = bitmap.getHeight()*MEDIA_WIDTH/bitmap.getWidth();
        if (type == 0) {
            LinearLayout.LayoutParams iv_params = new LinearLayout.LayoutParams(
                    Utils.dip2px(mContext, MEDIA_WIDTH), Utils.dip2px(mContext, height));
            return iv_params;
        } else {
            RelativeLayout.LayoutParams iv_params = new RelativeLayout.LayoutParams(
                    Utils.dip2px(mContext, MEDIA_WIDTH), Utils.dip2px(mContext, height));
            return iv_params;
        }
    }

    // 下载对方发来的多媒体文件（包括图片和音频）
    private static void downloadFile(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        // 根据多媒体的下载地址构建一个Uri对象
        Uri uri = Uri.parse(url);
        // 创建一个下载请求对象，指定从哪个网络地址下载文件
        Request down = new Request(uri);
        // 设置允许下载的网络类型
        down.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        // 设置不在通知栏显示
        down.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
        // 设置不在系统下载页面显示
        down.setVisibleInDownloadsUi(false);
        // 设置下载文件在本地的保存路径
        down.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DCIM, filename);
        // 把下载请求对象加入到下载管理器的下载队列中
        mDownloadId = mDownloadManager.enqueue(down);
    }

    // 定义一个下载进度的刷新任务
    private static class RefreshRunnable implements Runnable {
        private String mMediaPath;  // 多媒体文件的保存路径
        private int mType; // 多媒体文件的文件类型
        private RelativeLayout rl_content; // 存放多媒体消息的相对布局
        private ImageView iv_append; // 该图像视图用于展示消息图片
        private TextProgressCircle tpc_progress; // 声明一个文本进度圈对象
        private TextView tv_detail; // 该文本视图用于展示消息详情。图片消息则展示图片大小，音频消息则展示播放时长

        public RefreshRunnable(int[] resIds) {
            mType = resIds[0];
            rl_content = mActivity.findViewById(resIds[1]);
            iv_append = mActivity.findViewById(resIds[2]);
            tpc_progress = mActivity.findViewById(resIds[3]);
            tv_detail = mActivity.findViewById(resIds[4]);
        }

        @Override
        public void run() {
            boolean isFinished = false;
            // 创建一个下载查询对象，按照下载编号进行过滤
            Query down_query = new Query();
            // 设置下载查询对象的编号过滤器
            down_query.setFilterById(mDownloadId);
            // 向下载管理器发起查询操作，并返回查询结果集的游标
            Cursor cursor = mDownloadManager.query(down_query);
            while (cursor.moveToNext()) {
                int uriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                int totalSizeIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int nowSizeIdx = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                // 根据总大小和已下载大小，计算当前的下载进度
                int progress = (int) (100 * cursor.getLong(nowSizeIdx) / cursor.getLong(totalSizeIdx));
                if (cursor.getString(uriIdx) == null) {
                    break;
                }
                // 显示文本进度圈
                tpc_progress.setVisibility(View.VISIBLE);
                // 设置文本进度圈的当前进度
                tpc_progress.setProgress(progress, 30);
                // 获取媒体文件的真实路径
                String fileUri = cursor.getString(uriIdx);
                mMediaPath = Uri.parse(fileUri).getPath();
                if (progress == 100) { // 下载完毕
                    isFinished = true;
                }
            }
            cursor.close(); // 关闭数据库游标
            if (!isFinished) { // 未下载完成
                // 延迟100毫秒后再次启动下载进度的刷新任务
                mHandler.postDelayed(this, 100);
            } else { // 已下载完成
                mHandler.postDelayed(mScroll, 100);
                // 隐藏文本进度圈
                tpc_progress.setVisibility(View.GONE);
                if (mType == TYPE_PHOTO) { // 图片文件
                    // 把指定路径的图片显示在图像视图上面
                    iv_append.setImageURI(Uri.parse(mMediaPath));
                    // 重新设置图像视图的高度
                    iv_append.setLayoutParams(getImageParam(mMediaPath, 1));
                    // 显示图片文件的文件大小
                    tv_detail.setText(Utils.getFileSize(mMediaPath));
                } else if (mType == TYPE_SOUND) { // 音频文件
                    // 创建一个媒体播放器
                    final MediaPlayer player = new MediaPlayer();
                    // 设置音频流的类型为音乐
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        // 设置媒体数据的文件路径
                        player.setDataSource(mMediaPath);
                        // 媒体播放器准备就绪
                        player.prepare();
                        // 显示音频文件的播放时长
                        tv_detail.setText((player.getDuration() / 1000) + "秒");
                        // 设置音频消息的点击事件。一旦点击它就开始播放声音
                        rl_content.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 媒体播放器开始播放
                                player.start();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 适配Android9.0开始
    @Override
    public void onStart() {
        super.onStart();
        // 从Android9.0开始，系统不再支持静态广播，应用广播只能通过动态注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 创建一个消息到达的广播接收器
            msgReceiver = new RecvMsgReceiver();
            // 注册广播接收器，注册之后才能正常接收广播
            registerReceiver(msgReceiver, new IntentFilter(ClientThread.ACTION_RECV_MSG));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 注销广播接收器，注销之后就不再接收广播
            unregisterReceiver(msgReceiver);
        }
    }

    // 声明一个消息到达的广播接收器
    private RecvMsgReceiver msgReceiver;
    // 适配Android9.0结束

}
