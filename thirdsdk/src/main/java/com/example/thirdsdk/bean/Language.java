package com.example.thirdsdk.bean;

import java.util.Locale;

public class Language {
    public String name;
    public String desc;
    public Locale locale;

    public Language(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public Language(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

}
