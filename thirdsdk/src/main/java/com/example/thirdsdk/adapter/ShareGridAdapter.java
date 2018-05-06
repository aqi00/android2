package com.example.thirdsdk.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.thirdsdk.R;
import com.example.thirdsdk.bean.ShareChanels;
import com.example.thirdsdk.util.CacheUtil;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.t.Weibo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@SuppressLint("StaticFieldLeak")
public class ShareGridAdapter extends BaseAdapter implements OnItemClickListener {
    private final static String TAG = "ShareGridAdapter";
    private static Context mContext;
    private Handler mHandler;
    private String mUrl;
    private String mTitle;
    private static String mContent;
    private static String mImageUrl;
    private ArrayList<ShareChanels> mChannelList;

    private final String QQ_APPID = "100330589"; // 这里替换为开发者在QQ互联平台申请的应用id
    private static Tencent mTencent;
    private int QQ = 0; // QQ好友
    private int QZONE = 1; // QQ空间
    private int WEIBO = 2; // 腾讯微博
    private int[] mShareIcons = {R.drawable.logo_qq, R.drawable.logo_qzone,
            R.drawable.logo_tencentweibo};

    public ShareGridAdapter(final Context context, Handler handler, String url,
                            String title, String content, final String imageUrl,
                            ArrayList<ShareChanels> channelList) {
        mContext = context;
        mHandler = handler;
        mUrl = url;
        mTitle = title;
        mContent = content;
        if (imageUrl != null && Patterns.WEB_URL.matcher(imageUrl).matches()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mImageUrl = CacheUtil.getImagePath(imageUrl,
                                CacheUtil.getFileCache(context));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            mImageUrl = imageUrl;
        }

        if (channelList == null) {
            mChannelList = new ArrayList<ShareChanels>();
            mChannelList.add(new ShareChanels("QQ好友", QQ));
            mChannelList.add(new ShareChanels("QQ空间", QZONE));
            mChannelList.add(new ShareChanels("腾讯微博", WEIBO));
        } else {
            mChannelList = channelList;
        }
        // 创建一个QQ实例，用于QQ分享、QQ空间分享、腾讯微博分享
        mTencent = Tencent.createInstance(QQ_APPID, mContext);
    }

    @Override
    public int getCount() {
        return mChannelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChannelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_share, null);
            holder.tv_share_name = convertView.findViewById(R.id.tv_share_name);
            holder.iv_share_icon = convertView.findViewById(R.id.iv_share_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShareChanels item = mChannelList.get(position);
        holder.tv_share_name.setText(item.channelName);
        holder.tv_share_name.setPadding(0, 0, 0, 0);
        holder.iv_share_icon.setImageResource(mShareIcons[item.channelType]);
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_share_name;
        public ImageView iv_share_icon;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ShareChanels item = mChannelList.get(arg2);
        mHandler.sendEmptyMessageDelayed(0, 1500);
        if (item.channelType == QQ) { // 分享给QQ好友
            mShareListener = new ShareQQListener(mContext, item.channelName);
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mContent);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mUrl);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getPackageName());
            mTencent.shareToQQ((Activity) mContext, params, mShareListener);
        } else if (item.channelType == QZONE) { // 分享到QQ空间
            mShareListener = new ShareQQListener(mContext, item.channelName);
            ArrayList<String> urlList = new ArrayList<String>();
            urlList.add(mImageUrl);
            Log.d(TAG, "mImageUrl=" + mImageUrl);
            Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mTitle);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mContent);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urlList);
            Log.d(TAG, "begin shareToQzone");
            mTencent.shareToQzone((Activity) mContext, params, mShareListener);
            Log.d(TAG, "end shareToQzone");
        } else if (item.channelType == WEIBO) { // 分享到腾讯微博
            // 腾讯微博分享需要QQ登录授权
            mTencent.login((Activity) mContext, "all", mLoginListener);
        }
    }

    // 创建一个登录授权监听器
    public static IUiListener mLoginListener = new IUiListener() {

        // 在登录完成时触发
        public void onComplete(Object object) {
            Log.d(TAG, "登录完成:" + object.toString());
            try {
                JSONObject jsonObject = (JSONObject) object;
                String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                    mTencent.setAccessToken(token, expires);
                    mTencent.setOpenId(openId);
                    Weibo weibo = new Weibo(mContext, mTencent.getQQToken());
                    if (mImageUrl == null || mImageUrl.length() <= 0) {
                        weibo.sendText(mContent, new ShareQQListener(mContext, "微博文字"));
                    } else {
                        weibo.sendPicText(mContent, mImageUrl, new ShareQQListener(mContext, "微博图文"));
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "登录异常:" + e.getMessage());
            }
        }

        // 在失败完成时触发
        public void onError(UiError error) {
            Log.d(TAG, "登录失败:" + error.errorMessage);
        }

        // 在登录取消时触发
        public void onCancel() {
            Log.d(TAG, "登录取消");
        }
    };

    public static ShareQQListener mShareListener; // 声明一个QQ分享监听器对象
    // 定义一个QQ分享监听器
    private static class ShareQQListener implements IUiListener {
        private Context context;
        private String channelName; // 分享渠道名称

        public ShareQQListener(final Context context, final String channelName) {
            this.context = context;
            this.channelName = channelName;
        }

        // 在分享成功时触发
        public void onComplete(Object object) {
            Toast.makeText(context, channelName + "分享完成:" + object.toString(), Toast.LENGTH_LONG).show();
        }

        // 在分享失败时触发
        public void onError(UiError error) {
            Toast.makeText(context, channelName + "分享失败:" + error.errorMessage, Toast.LENGTH_LONG).show();
        }

        // 在分享取消时触发
        public void onCancel() {
            Toast.makeText(context, channelName + "分享取消", Toast.LENGTH_LONG).show();
        }
    }

}
