package com.example.weixin.bean;

import org.json.JSONObject;

import android.util.Log;

public class GetPrepayIdResult {
    private static final String TAG = "GetPrepayIdResult";
    public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
    public String prepayId;
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
            if (json.has("prepayid")) { // success case
                prepayId = json.getString("prepayid");
                localRetCode = LocalRetCode.ERR_OK;
            } else {
                localRetCode = LocalRetCode.ERR_JSON;
            }
            errCode = json.getInt("errcode");
            errMsg = json.getString("errmsg");
        } catch (Exception e) {
            localRetCode = LocalRetCode.ERR_JSON;
        }
    }
}
