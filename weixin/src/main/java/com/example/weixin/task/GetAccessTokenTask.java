package com.example.weixin.task;

import com.example.weixin.bean.GetAccessTokenResult;
import com.example.weixin.bean.LocalRetCode;
import com.example.weixin.bean.WechatConstants;
import com.example.weixin.util.WechatUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetAccessTokenTask extends AsyncTask<String, Void, GetAccessTokenResult> {
    private static final String TAG = "GetAccessTokenTask";
    private Context mContext;
    private ProgressDialog mDialog;
    private String[] mGoodsInfo;

    public GetAccessTokenTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mDialog = ProgressDialog.show(mContext, "提示", "正在获取access token...");
    }

    @Override
    protected GetAccessTokenResult doInBackground(String... params) {
        mGoodsInfo = new String[]{params[0], params[1], params[2]};
        GetAccessTokenResult result = new GetAccessTokenResult();
        String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                WechatConstants.APP_ID, WechatConstants.APP_SECRET);
        Log.d(TAG, "get access token, url = " + url);

        byte[] buf = WechatUtil.httpGet(url);
        if (buf == null || buf.length == 0) {
            result.localRetCode = LocalRetCode.ERR_HTTP;
            return result;
        }
        String content = new String(buf);
        result.parseFrom(content);
        return result;
    }

    @Override
    protected void onPostExecute(GetAccessTokenResult result) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        Log.d(TAG, "RetCode=" + result.localRetCode + ", errCode=" + result.errCode + ", errMsg=" + result.errMsg);
        if (result.localRetCode == LocalRetCode.ERR_OK) {
            Toast.makeText(mContext, "获取access token成功, accessToken = " + result.accessToken, Toast.LENGTH_LONG).show();
            GetPrepayIdTask getPrepayId = new GetPrepayIdTask(mContext, result.accessToken);
            getPrepayId.execute(mGoodsInfo[0], mGoodsInfo[1], mGoodsInfo[2]);
        } else {
            Toast.makeText(mContext, "获取access token失败，原因: " + result.localRetCode.name(), Toast.LENGTH_LONG).show();
        }
    }
}
