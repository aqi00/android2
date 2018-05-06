package com.example.event.constant;

import com.example.event.R;

import java.util.ArrayList;

/**
 * Created by ouyangshen on 2018/1/4.
 */

public class ImageList {

    public static ArrayList<Integer> getDefault() {
        ArrayList<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.banner_1);
        imageList.add(R.drawable.banner_2);
        imageList.add(R.drawable.banner_3);
        imageList.add(R.drawable.banner_4);
        imageList.add(R.drawable.banner_5);
        return imageList;
    }
}
