package com.example.network.bean;

import java.util.ArrayList;

public class FriendGroup {
    public String title;
    public ArrayList<Friend> friend_list;

    public FriendGroup() {
        this.title = "";
        this.friend_list = new ArrayList<Friend>();
    }

    public FriendGroup(String title, ArrayList<Friend> friend_list) {
        this.title = title;
        this.friend_list = friend_list;
    }

}
