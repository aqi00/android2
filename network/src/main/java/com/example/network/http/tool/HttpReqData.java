package com.example.network.http.tool;

public class HttpReqData {
    public String url;
    public String cookie;
    public String referer;
    public String content_type;
    public String x_requested_with;
    public StringBuffer params;
    public String charset;
    public String boundary;

    public HttpReqData() {
        url = "";
        cookie = "";
        referer = "";
        content_type = "";
        x_requested_with = "";
        params = new StringBuffer();
        charset = "utf-8";
        boundary = "";
    }

    public HttpReqData(String url) {
        this();
        this.url = url;
    }

}
