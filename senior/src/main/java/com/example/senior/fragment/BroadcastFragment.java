package com.example.senior.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.example.senior.R;

public class BroadcastFragment extends Fragment {
    private static final String TAG = "BroadcastFragment";
    // 声明一个广播事件的标识串
    public final static String EVENT = "com.example.senior.fragment.BroadcastFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private int mPosition; // 位置序号
    private int mImageId; // 图片的资源编号
    private String mDesc; // 商品的文字描述
    private int mColorSeq = 0; // 背景颜色的序号
    private Spinner sp_bg;
    private boolean bFirst = true; // 是否是首次打开

    // 获取该碎片的一个实例
    public static BroadcastFragment newInstance(int position, int image_id, String desc) {
        BroadcastFragment fragment = new BroadcastFragment(); // 创建该碎片的一个实例
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putInt("position", position); // 往包裹存入位置序号
        bundle.putInt("image_id", image_id); // 往包裹存入图片的资源编号
        bundle.putString("desc", desc); // 往包裹存入商品的文字描述
        fragment.setArguments(bundle); // 把包裹塞给碎片
        return fragment; // 返回碎片实例
    }

    // 创建碎片视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        if (getArguments() != null) { // 如果碎片携带有包裹，则打开包裹获取参数信息
            mPosition = getArguments().getInt("position", 0);
            mImageId = getArguments().getInt("image_id", 0);
            mDesc = getArguments().getString("desc");
        }
        // 根据布局文件fragment_broadcast.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_broadcast, container, false);
        ImageView iv_pic = mView.findViewById(R.id.iv_pic);
        TextView tv_desc = mView.findViewById(R.id.tv_desc);
        iv_pic.setImageResource(mImageId);
        tv_desc.setText(mDesc);
        return mView; // 返回该碎片的视图对象
    }

    // 初始化页面背景色的下拉框
    private void initSpinner() {
        ArrayAdapter<String> dividerAdapter = new ArrayAdapter<String>(mContext,
                R.layout.item_select, mColorNameArray);
        sp_bg = mView.findViewById(R.id.sp_bg);
        sp_bg.setPrompt("请选择页面背景色");
        sp_bg.setAdapter(dividerAdapter);
        sp_bg.setSelection(mColorSeq);
        sp_bg.setOnItemSelectedListener(new ColorSelectedListener());
    }

    // 声明一个颜色名称数组
    private String[] mColorNameArray = {"红色", "黄色", "绿色", "青色", "蓝色"};
    // 声明一个颜色类型数组
    private int[] mColorIdArray = {Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE};
    // 定义一个与下拉框配套的颜色选择监听器
    class ColorSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (!bFirst || mColorSeq != arg2) { // 如果要改变背景色
                mColorSeq = arg2;
                // 创建一个广播事件的意图
                Intent intent = new Intent(BroadcastFragment.EVENT);
                intent.putExtra("seq", arg2);
                intent.putExtra("color", mColorIdArray[arg2]);
                // 通过本地的广播管理器来发送广播
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
            bFirst = false;
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onStart() {
        super.onStart();
        initSpinner();
        // 创建一个背景色变更的广播接收器
        bgChangeReceiver = new BgChangeReceiver();
        // 创建一个意图过滤器，只处理指定事件来源的广播
        IntentFilter filter = new IntentFilter(BroadcastFragment.EVENT);
        // 注册广播接收器，注册之后才能正常接收广播
        LocalBroadcastManager.getInstance(mContext).registerReceiver(bgChangeReceiver, filter);
    }

    @Override
    public void onStop() {
        // 注销广播接收器，注销之后就不再接收广播
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(bgChangeReceiver);
        super.onStop();
    }

    // 声明一个背景色变更的广播接收器
    private BgChangeReceiver bgChangeReceiver;
    // 定义一个广播接收器，用于处理背景色变更事件
    private class BgChangeReceiver extends BroadcastReceiver {

        // 一旦接收到背景色变更的广播，马上触发接收器的onReceive方法
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                // 从广播消息中取出最新的颜色序号
                mColorSeq = intent.getIntExtra("seq", 0);
                // 设置下拉框默认显示该序号项
                sp_bg.setSelection(mColorSeq);
            }
        }
    }

}
