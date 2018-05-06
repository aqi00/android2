package com.example.media.bean;

public class LrcContent {
    private String lrcStr; // 这行歌词的文字内容
    private int lrcTime; // 这行歌词的开始时间

    public String getLrcStr() {
        return lrcStr;
    }

    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }

    public int getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }
}
