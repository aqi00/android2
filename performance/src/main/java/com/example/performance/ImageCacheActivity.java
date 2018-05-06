package com.example.performance;

import com.example.performance.adapter.ImageListAdapter;
import com.example.performance.cache.ImageCache;
import com.example.performance.cache.ImageCacheConfig;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/27.
 */
public class ImageCacheActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "ImageCacheActivity";
    private TextView tv_cache;
    private ImageView iv_cache;
    private ListView lv_cache; // 声明一个用于展示网络图片的列表视图对象
    private ImageCache mCache; // 声明一个图片缓存对象
    // 这个是单个图像视图随机展示的图片数组。其中前六张图片可以正常访问，后两张图片不能正常访问
    private String[] mPlaceImg = {
            "http://b258.photo.store.qq.com/psb?/V11ZojBI312o2K/XM9Hdo68BzvH6ZObpYKkjmlD41ALDfuM8YhJ*SeaVmE!/b/dCxrz5koFgAA&bo=VQOAAgAAAAABB*Q!&rf=viewer_4",
            "http://b247.photo.store.qq.com/psb?/V11ZojBI312o2K/63aY8a4M5quhi.78*krOo7k3Gu3cknuclBJHS3g1fpc!/b/dDXWPZMlBgAA&bo=VQOAAgAAAAABB*Q!&rf=viewer_4",
            "http://b249.photo.store.qq.com/psb?/V11ZojBI312o2K/JMmveEpaF8rq1MD3fl7j84t1Py2DYodwY29TAbt0dMo!/b/dPYKbJQdBwAA&bo=VQOAAgAAAAABB*Q!&rf=viewer_4",
            "http://b258.photo.store.qq.com/psb?/V11ZojBI312o2K/OxVcL48FuqzvB1mDPhrHv4g0M6O8ymi6I3T4tQV8q*A!/b/dMfKypk.FgAA&bo=VQOAAgAAAAABB*Q!&rf=viewer_4",
            "http://b258.photo.store.qq.com/psb?/V11ZojBI312o2K/amX.gon15XsNqLGe6iU5luhsNbnnLd.m3YfgU*Dbfeo!/b/dI930pn0FQAA&bo=VQOAAgAAAAABB*Q!&rf=viewer_4",
            "http://b247.photo.store.qq.com/psb?/V11ZojBI312o2K/KEutY1ETW*G06pMEZ6iOJ6ydZxfVmTiCRzZCVyErM3s!/b/dEPKPZNJBQAA&bo=VQOAAgAAAAABB*Q!&rf=viewer_4",
            "http://b258.photo.store.qq.com/psb?/viewer_4",
            "http://b247.photo.store.qq.com/psb?/viewer_5",
    };
    // 这个是列表视图统一展示的图片数组
    private String[] mListImg = {
            "http://b86.photo.store.qq.com/psb?/V11ZojBI3Em2xO/XeY2fKiZrmNHT1V4kA16giHVzucf0pVfR7e6L*0QABE!/b/YSNZUDPQWAAAYpO.SzO.WAAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b85.photo.store.qq.com/psb?/V11ZojBI3Em2xO/UywnWltcpgsSUNjNOeYnTCl1vMrwlh6Eyu7m7gLn7RA!/b/YaEyszJjhwAAYjEuszKIiAAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b85.photo.store.qq.com/psb?/V11ZojBI3Em2xO/EtfxWhkZu23WJMBsUMBOWH9KojgEHl4*22hHvO*600Y!/b/YV.frjLShgAAYseUrjJdhwAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b83.photo.store.qq.com/psb?/V11ZojBI3WKzYh/FhqskU8042PufcDiAs6SBqFkus6.ALpHsx.zZfaRb*g!/b/Yf73gTEhiAAAYrwzjjFXiAAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b83.photo.store.qq.com/psb?/V11ZojBI3WKzYh/x1E0Ae5EdsPyybgCr7o4WFAC.mGVdJAvgiKaVi0f0GM!/b/YYeijDFRhwAAYroMhTEfiAAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b85.photo.store.qq.com/psb?/V11ZojBI25zdM8/Ufh7jH55F7FFSrNzzaiHdbNRRIL*w2Ll8GmswZAoxjU!/b/YbxEuTKJjwAAYnVCuTI.jgAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b84.photo.store.qq.com/psb?/V11ZojBI25zdM8/VIZq51p4QCv6IjWVjOWc7ZRZSXmFUOF7fPv4KNTYrj0!/b/Yaz*FTLNjQAAYo21IzLYjgAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b88.photo.store.qq.com/psb?/V11ZojBI25zdM8/RIjap1R4S7kAdAyWv1XL0lhIu16YJU7g31iKAih4clk!/b/YWbzfzT0KgAAYoSEgTRxKgAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b86.photo.store.qq.com/psb?/V11ZojBI1eIahe/kbMM0zUUeB0eq4E1l1lnO5gtb8wLiRuncZSEkIDjAFI!/b/YcA7SjMkVwAAYhEeRDPYUQAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
            "http://b89.photo.store.qq.com/psb?/V11ZojBI1eIahe/11pvTKE7QfPi4z4klFXlQzfmkvBRomqzdNQfhF.axOU!/b/YfafGzWNHwAAYpyuHjXSHgAA&bo=ngL2AQAAAAABBEg!&rf=viewer_4",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cache);
        tv_cache = findViewById(R.id.tv_cache);
        iv_cache = findViewById(R.id.iv_cache);
        lv_cache = findViewById(R.id.lv_cache);
        findViewById(R.id.btn_cache_holder).setOnClickListener(this);
        findViewById(R.id.btn_cache_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cache_holder) { // 点击了占位加载按钮
            iv_cache.setVisibility(View.VISIBLE);
            lv_cache.setVisibility(View.GONE);
            int pos = (int) (Math.random() * 100 % mPlaceImg.length);
            String file = mPlaceImg[pos];
            tv_cache.setText("月坛公园的玫瑰花盛开啦");
            // 创建一个新的图片缓存配置
            ImageCacheConfig config = new ImageCacheConfig.Builder()
                    .setBeginImage(R.drawable.load_default) // 设置加载开始前的图片资源编号
                    .setErrorImage(R.drawable.load_error) // 设置加载失败后的图片资源编号
                    .setCacheStyle(ImageCacheConfig.LRU) // 设置图片缓存的排队算法
                    .setFadeDuration(2000).build(); // 设置淡入动画的播放时长
            // 初始化图片缓存的配置，并给图像视图加载网络图片
            mCache.initConfig(config).show(file, iv_cache);
        } else if (v.getId() == R.id.btn_cache_list) { // 点击了列表加载按钮
            iv_cache.setVisibility(View.GONE);
            lv_cache.setVisibility(View.VISIBLE);
            tv_cache.setText("欢迎来到风景秀美的鼓浪屿");
            // 通过列表适配器加载网络图片，具体的缓存处理参见该列表的适配器代码
            ImageListAdapter adapter = new ImageListAdapter(this, mListImg);
            lv_cache.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart() {
        // 获取图片缓存对象的唯一实例
        mCache = ImageCache.getInstance(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // 清空图片缓存
        mCache.clear();
        super.onStop();
    }

}
