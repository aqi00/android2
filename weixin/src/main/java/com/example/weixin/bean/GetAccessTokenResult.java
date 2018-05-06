package com.example.weixin.bean;

import org.json.JSONObject;

import android.util.Log;

public class GetAccessTokenResult {
    private static final String TAG = "GetAccessTokenResult";
    public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
    public String accessToken;
    public int expiresIn;
    public int errCode;
    public String errMsg;

    public void parseFrom(String content) {
        if (content == null || content.length() <= 0) {
            Log.e(TAG, "parseFrom fail, content is null");
            localRetCode = LocalRetCode.ERR_JSON;
            return;
        }

        try {
            JSONObject json = new JSONObject(content);
            if (json.has("access_token")) { // success case
                accessToken = json.getString("access_token");
                expiresIn = json.getInt("expires_in");
                localRetCode = LocalRetCode.ERR_OK;
            } else {
                errCode = json.getInt("errcode");
                errMsg = json.getString("errmsg");
                localRetCode = LocalRetCode.ERR_JSON;
            }
        } catch (Exception e) {
            localRetCode = LocalRetCode.ERR_JSON;
        }
    }
}
