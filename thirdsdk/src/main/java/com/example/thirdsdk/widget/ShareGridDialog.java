package com.example.thirdsdk.widget;

import java.util.ArrayList;

import com.example.thirdsdk.R;
import com.example.thirdsdk.adapter.ShareGridAdapter;
import com.example.thirdsdk.bean.ShareChanels;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;

@SuppressLint("HandlerLeak")
public class ShareGridDialog implements OnClickListener {
    private final static String TAG = "ShareGridDialog";
    private Dialog dialog;
    private View view;
    private Context mContext;
    private GridView gv_share_channel;

    private String mUrl;
    private String mTitle;
    private String mContent;
    private String mImgUrl;
    private ArrayList<ShareChanels> mChannelList;

    public ShareGridDialog(final Context context, ArrayList<ShareChanels> channelList) {
        mContext = context;
        mChannelList = channelList;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
        dialog = new Dialog(context, R.style.dialog_layout_bottom);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        gv_share_channel = view.findViewById(R.id.gv_share_channel);
        view.findViewById(R.id.tv_share_cancel).setOnClickListener(this);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                dismiss();
            } else if (msg.what == 9) {
                Log.d(TAG, (String) msg.obj);
            }
        }
    };

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        mImgUrl = imgUrl;
    }

    public void show() {
        ShareGridAdapter shareAdapter = new ShareGridAdapter(mContext, mHandler, mUrl,
                mTitle, mContent, mImgUrl, mChannelList);
        gv_share_channel.setAdapter(shareAdapter);
        gv_share_channel.setOnItemClickListener(shareAdapter);
        dialog.getWindow().setContentView(view);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_share_cancel) {
            dismiss();
        }
    }
}
