package com.example.device.bean;

public class Satellite {
    public int seq;
    public String nation;
    public String name;
    public int signal;
    public int elevation;
    public int azimuth;
    public String time;

    public Satellite() {
        seq = -1;
        nation = "";
        name = "";
        signal = -1;
        elevation = -1;
        azimuth = -1;
        time = "";
    }
}
