package com.example.device.bean;

public class BlueDevice {
    public String name; // 蓝牙设备的名称
    public String address; // 蓝牙设备的MAC地址
    public int state; // 蓝牙设备的绑定状态

    public BlueDevice(String name, String address, int state) {
        this.name = name;
        this.address = address;
        this.state = state;
    }
}
