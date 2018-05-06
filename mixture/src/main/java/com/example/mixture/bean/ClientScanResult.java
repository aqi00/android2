package com.example.mixture.bean;

public class ClientScanResult {
    private String IpAddr; // 设备的IP地址
    private String HWAddr; // 设备的MAC地址
    private String Device; // 设备名称
    private String HostName; // 主机名称
    private boolean IsReachable; // 能否连通

    public ClientScanResult(String ipAddr, String hWAddr, String device, boolean isReachable, String hostName) {
        super();
        IpAddr = ipAddr;
        HWAddr = hWAddr;
        Device = device;
        IsReachable = isReachable;
        HostName = hostName;
    }

    public String getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    public String getHWAddr() {
        return HWAddr;
    }

    public void setHWAddr(String hWAddr) {
        HWAddr = hWAddr;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public String getHostName() {
        return HostName;
    }

    public void setHostName(String hostName) {
        HostName = hostName;
    }

    public void setReachable(boolean isReachable) {
        this.IsReachable = isReachable;
    }

    public boolean isReachable() {
        return IsReachable;
    }
}
