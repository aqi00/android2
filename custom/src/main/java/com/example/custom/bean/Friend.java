package com.example.custom.bean;

public class Friend {
    public String phone;
    public String relation;
    public String value;
    public boolean admit_circle;

    public Friend(String phone) {
        this.phone = phone;
        relation = "其他";
        value = "";
        admit_circle = true;
    }

}
