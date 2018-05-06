package com.example.mixture.bean;

public class MacDevice {
    public int xuhao;
    public String mac;
    public String device;

    public MacDevice() {
        xuhao = 0;
        mac = "";
        device = "";
    }

    public MacDevice(String mac, String device) {
        xuhao = 0;
        this.mac = mac;
        this.device = device;
    }
}
