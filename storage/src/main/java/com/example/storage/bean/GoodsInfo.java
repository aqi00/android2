package com.example.storage.bean;

public class GoodsInfo {
    public long rowid;
    public int xuhao;
    public String name;
    public String desc;
    public float price;
    public String thumb_path;
    public String pic_path;

    public GoodsInfo() {
        rowid = 0L;
        xuhao = 0;
        name = "";
        desc = "";
        price = 0;
        thumb_path = "";
        pic_path = "";
    }
}
