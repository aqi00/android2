package com.example.performance.bean;

import java.util.ArrayList;

import com.example.performance.R;

public class Planet {
    public int image;
    public String name;
    public String desc;

    public Planet(int image, String name, String desc) {
        this.image = image;
        this.name = name;
        this.desc = desc;
    }

    private static int[] iconArray = {R.drawable.shuixing, R.drawable.jinxing, R.drawable.diqiu,
            R.drawable.huoxing, R.drawable.muxing, R.drawable.tuxing};
    private static String[] nameArray = {"水星", "金星", "地球", "火星", "木星", "土星"};
    private static String[] descArray = {
            "水星是太阳系八大行星最内侧也是最小的一颗行星，也是离太阳最近的行星",
            "金星是太阳系八大行星之一，排行第二，距离太阳0.725天文单位",
            "地球是太阳系八大行星之一，排行第三，也是太阳系中直径、质量和密度最大的类地行星，距离太阳1.5亿公里",
            "火星是太阳系八大行星之一，排行第四，属于类地行星，直径约为地球的53%",
            "木星是太阳系八大行星中体积最大、自转最快的行星，排行第五。它的质量为太阳的千分之一，但为太阳系中其它七大行星质量总和的2.5倍",
            "土星为太阳系八大行星之一，排行第六，体积仅次于木星"
    };

    public static ArrayList<Planet> getDefaultList() {
        ArrayList<Planet> planetList = new ArrayList<Planet>();
        for (int i = 0; i < iconArray.length; i++) {
            planetList.add(new Planet(iconArray[i], nameArray[i], descArray[i]));
        }
        return planetList;
    }
}
