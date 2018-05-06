package com.example.weixin.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.example.weixin.bean.GetPrepayIdResult;
import com.example.weixin.bean.LocalRetCode;
import com.example.weixin.bean.WechatConstants;
import com.example.weixin.util.MD5Util;
import com.example.weixin.util.WechatUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetPrepayIdTask extends AsyncTask<String, Void, GetPrepayIdResult> {
    private static final String TAG = "GetPrepayIdTask";
    private Context mContext;
    private ProgressDialog mDialog;
    private String mAccessToken;
    private String[] mGoodsInfo;

    public GetPrepayIdTask(Context context, String accessToken) {
        mContext = context;
        mAccessToken = accessToken;
    }

    @Override
    protected void onPreExecute() {
        mDialog = ProgressDialog.show(mContext, "提示", "正在获取预支付订单...");
    }

    @Override
    protected GetPrepayIdResult doInBackground(String... params) {
        mGoodsInfo = new String[]{params[0], params[1], params[2]};
        String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", mAccessToken);
        String entity = genProductArgs();
        Log.d(TAG, "doInBackground, url = " + url + ", entity = " + entity);

        GetPrepayIdResult result = new GetPrepayIdResult();
        byte[] buf = WechatUtil.httpPost(url, entity);
        if (buf == null || buf.length == 0) {
            result.localRetCode = LocalRetCode.ERR_HTTP;
            return result;
        }
        String content = new String(buf);
        Log.d(TAG, "doInBackground, response content = " + content);
        result.parseFrom(content);
        return result;
    }

    @Override
    protected void onPostExecute(GetPrepayIdResult result) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (result.localRetCode == LocalRetCode.ERR_OK) {
            Toast.makeText(mContext, "获取prepayid成功", Toast.LENGTH_LONG).show();
            payWithWechat(result);
        } else {
            Toast.makeText(mContext, "获取prepayid失败，原因" + result.localRetCode.name(), Toast.LENGTH_LONG).show();
        }
    }

    private IWXAPI mWeixinApi;

    // // 如果获取token和预付标识在服务器实现，只留下支付动作在客户端实现，那么下面要异步调用
    // private void payWithWechat() {
    // final String payInfo = "";
    //
    // Runnable payRunnable = new Runnable() {
    // @Override
    // public void run() {
    // sendWXPayReq(payInfo);
    // }
    // };
    //
    // Thread payThread = new Thread(payRunnable);
    // payThread.start();
    // }

    private String genPackage(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WechatConstants.PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成

        // 进行md5摘要前，params内容为原始内容，未经过url encode处理
        String packageSign = MD5Util.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5Util.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String getTraceId() {
        return "crestxu_" + genTimeStamp();
    }

    private String genOutTradNo() {
        Random random = new Random();
        return MD5Util.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long timeStamp;
    private String nonceStr, packageValue;

    private String genSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (; i < params.size() - 1; i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());

        String sha1 = WechatUtil.sha1(sb.toString());
        Log.d(TAG, "genSign, sha1 = " + sha1);
        return sha1;
    }

    private String genProductArgs() {
        JSONObject json = new JSONObject();

        try {
            json.put("appid", WechatConstants.APP_ID);
            String traceId = getTraceId(); // traceId
            // 由开发者自定义，可用于订单的查询与跟踪，建议根据支付用户信息生成此id
            json.put("traceid", traceId);
            nonceStr = genNonceStr();
            json.put("noncestr", nonceStr);

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("bank_type", "WX"));
            packageParams.add(new BasicNameValuePair("body", mGoodsInfo[0]));
            packageParams.add(new BasicNameValuePair("description", mGoodsInfo[1]));
            packageParams.add(new BasicNameValuePair("fee_type", "1"));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("partner", WechatConstants.PARTNER_ID));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));
            packageParams.add(new BasicNameValuePair("total_fee", ""
                    + (int) (Float.parseFloat(mGoodsInfo[2]) * 100)));
            packageValue = genPackage(packageParams);

            json.put("package", packageValue);
            timeStamp = genTimeStamp();
            json.put("timestamp", timeStamp);

            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", WechatConstants.APP_ID));
            signParams.add(new BasicNameValuePair("appkey", WechatConstants.APP_KEY));
            signParams.add(new BasicNameValuePair("noncestr", nonceStr));
            signParams.add(new BasicNameValuePair("package", packageValue));
            signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
            signParams.add(new BasicNameValuePair("traceid", traceId));
            json.put("app_signature", genSign(signParams));

            json.put("sign_method", "sha1");
        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

        return json.toString();
    }

    private void payWithWechat(GetPrepayIdResult result) {
        PayReq req = new PayReq();
        req.appId = WechatConstants.APP_ID;
        req.partnerId = WechatConstants.PARTNER_ID;
        req.prepayId = result.prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = String.valueOf(timeStamp);
        req.packageValue = "Sign=" + packageValue;

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("appkey", WechatConstants.APP_KEY));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genSign(signParams);

        Log.d(TAG, "WXAPIFactory.createWXAPI");
        mWeixinApi = WXAPIFactory.createWXAPI(mContext, WechatConstants.APP_ID);
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        Log.d(TAG, "mWeixinApi.sendReq");
        mWeixinApi.sendReq(req);
    }
}
