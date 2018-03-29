package com.example.storage.bean;

public class UserInfo {
    public long rowid;
    public int xuhao;
    public String name;
    public int age;
    public long height;
    public float weight;
    public boolean married;
    public String update_time;
    public String phone;
    public String password;

    public UserInfo() {
        rowid = 0L;
        xuhao = 0;
        name = "";
        age = 0;
        height = 0L;
        weight = 0.0f;
        married = false;
        update_time = "";
        phone = "";
        password = "";
    }
}
