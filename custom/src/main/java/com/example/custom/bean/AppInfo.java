package com.example.custom.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public int uid;
    public String label;
    public String package_name;
    public Drawable icon;
    public long traffic;

    public long rowid;
    public int xuhao;
    public int month;
    public int day;
    public String icon_path;

    public AppInfo() {
        uid = 0;
        label = "";
        package_name = "";
        icon = null;
        traffic = 0;

        rowid = 0;
        xuhao = 0;
        month = 0;
        day = 0;
        icon_path = "";
    }

}
