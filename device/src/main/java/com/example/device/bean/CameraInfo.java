package com.example.device.bean;

import java.util.List;

import android.hardware.Camera;

public class CameraInfo {
    public String camera_type;
    public String flash_mode;
    public String focus_mode;
    public String scene_mode;
    public String color_effect;
    public String white_balance;
    public int max_zoom;
    public int zoom;
    public List<Camera.Size> resolutionList;
}
