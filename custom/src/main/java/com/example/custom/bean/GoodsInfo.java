package com.example.custom.bean;

import java.util.ArrayList;

import com.example.custom.R;

public class GoodsInfo {
    public long rowid;
    public int xuhao;
    public String name;
    public String desc;
    public float price;
    public String thumb_path;
    public String pic_path;
    public int thumb;
    public int pic;

    public GoodsInfo() {
        rowid = 0L;
        xuhao = 0;
        name = "";
        desc = "";
        price = 0;
        thumb_path = "";
        pic_path = "";
        thumb = 0;
        pic = 0;
    }

    private static String[] mNameArray = {
            "iphone7", "Mate8", "小米5", "vivo X6S", "OPPO R9plus", "魅族Pro6"
    };
    private static String[] mDescArray = {
            "Apple iPhone 7 128GB 玫瑰金色 移动联通电信4G手机",
            "华为 HUAWEI Mate8 3GB+32GB版 移动联通4G手机（月光银）",
            "小米手机5 全网通 高配版 3GB内存 64GB 白色",
            "vivo X6S 金色 全网通4G 双卡双待 4GB+64GB",
            "OPPO R9plus 4GB+64GB内存版 金色 全网通4G手机 双卡双待",
            "魅族Pro6全网通公开版 4+32GB 银白色 移动联通电信4G手机 双卡双待"
    };
    private static float[] mPriceArray = {5888, 2499, 1799, 2298, 2499, 2199};
    private static int[] mThumbArray = {
            R.drawable.iphone_s, R.drawable.huawei_s, R.drawable.xiaomi_s,
            R.drawable.vivo_s, R.drawable.oppo_9p_s, R.drawable.meizu_s
    };
    private static int[] mPicArray = {
            R.drawable.iphone, R.drawable.huawei, R.drawable.xiaomi,
            R.drawable.vivo, R.drawable.oppo_9p, R.drawable.meizu
    };

    public static ArrayList<GoodsInfo> getDefaultList() {
        ArrayList<GoodsInfo> goodsList = new ArrayList<GoodsInfo>();
        for (int i = 0; i < mNameArray.length; i++) {
            GoodsInfo info = new GoodsInfo();
            info.name = mNameArray[i];
            info.desc = mDescArray[i];
            info.price = mPriceArray[i];
            info.thumb = mThumbArray[i];
            info.pic = mPicArray[i];
            goodsList.add(info);
        }
        return goodsList;
    }

}
