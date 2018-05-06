package com.example.performance;

import java.util.Map;

import com.example.performance.util.DateUtil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/27.
 */
public class LruCacheActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_lru_cache;
    private LruCache<String, String> mLanguageLru; // 声明一个最近最少使用算法的缓存对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lru_cache);
        tv_lru_cache = findViewById(R.id.tv_lru_cache);
        findViewById(R.id.btn_android).setOnClickListener(this);
        findViewById(R.id.btn_ios).setOnClickListener(this);
        findViewById(R.id.btn_java).setOnClickListener(this);
        findViewById(R.id.btn_cpp).setOnClickListener(this);
        findViewById(R.id.btn_python).setOnClickListener(this);
        findViewById(R.id.btn_net).setOnClickListener(this);
        findViewById(R.id.btn_php).setOnClickListener(this);
        findViewById(R.id.btn_perl).setOnClickListener(this);
        // 创建一个大小为5的LRU缓存
        mLanguageLru = new LruCache<String, String>(5);
    }

    @Override
    public void onClick(View v) {
        String language = ((Button) v).getText().toString();
        // 往LRU缓存上添加一条新的语言记录，具体的排队操作由LruCache内部自动完成
        mLanguageLru.put(language, DateUtil.getNowTime());
        printLruCache(); // 打印LRU缓存中的数据
    }

    // 打印LRU缓存中的数据
    private void printLruCache() {
        String desc = "";
        // 获取LRU缓存在当前时刻下的快照映射
        Map<String, String> cache = mLanguageLru.snapshot();
        for (Map.Entry<String, String> item : cache.entrySet()) {
            desc = String.format("%s%s 最后一次更新时间为%s\n",
                    desc, item.getKey(), item.getValue());
        }
        tv_lru_cache.setText(desc);
    }

}
