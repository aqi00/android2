package com.example.mixture.bean;

public class WifiConnect {
    public String SSID;
    public int level;
    public boolean status;
    public int type;
    public int networkId;

    public WifiConnect() {
        SSID = "";
        level = 0;
        status = false;
        type = 0;
        networkId = -1;
    }

}
