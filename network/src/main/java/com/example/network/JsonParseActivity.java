package com.example.network;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
@SuppressLint("DefaultLocale")
public class JsonParseActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_json;
    private String mJsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_parse);
        tv_json = findViewById(R.id.tv_json);
        findViewById(R.id.btn_construct_json).setOnClickListener(this);
        findViewById(R.id.btn_parser_json).setOnClickListener(this);
        findViewById(R.id.btn_traverse_json).setOnClickListener(this);
        mJsonStr = getJsonStr();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_construct_json) {
            // 显示完整的json串
            tv_json.setText(mJsonStr);
        } else if (v.getId() == R.id.btn_parser_json) {
            // 显示json串解析后的各个参数值
            tv_json.setText(parserJson(mJsonStr));
        } else if (v.getId() == R.id.btn_traverse_json) {
            // 显示json串的遍历结果串
            tv_json.setText(traverseJson(mJsonStr));
        }
    }

    // 获取一个手动构造的json串
    private String getJsonStr() {
        String str = "";
        // 创建一个JSON对象
        JSONObject obj = new JSONObject();
        try {
            // 添加一个名叫name的字符串参数
            obj.put("name", "address");
            // 创建一个JSON数组对象
            JSONArray array = new JSONArray();
            for (int i = 0; i < 3; i++) {
                JSONObject item = new JSONObject();
                // 添加一个名叫item的字符串参数
                item.put("item", "第" + (i + 1) + "个元素");
                // 把item这个JSON对象加入到JSON数组
                array.put(item);
            }
            // 添加一个名叫list的数组参数
            obj.put("list", array);
            // 添加一个名叫count的整型参数
            obj.put("count", array.length());
            // 添加一个名叫desc的字符串参数
            obj.put("desc", "这是测试串");
            // 把JSON对象转换为json字符串
            str = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    // 解析json串内部的各个参数
    private String parserJson(String jsonStr) {
        String result = "";
        try {
            // 根据json串构建一个JSON对象
            JSONObject obj = new JSONObject(jsonStr);
            // 获得名叫name的字符串参数
            String name = obj.getString("name");
            // 获得名叫desc的字符串参数
            String desc = obj.getString("desc");
            // 获得名叫count的整型参数
            int count = obj.getInt("count");
            result = String.format("%sname=%s\n", result, name);
            result = String.format("%sdesc=%s\n", result, desc);
            result = String.format("%scount=%d\n", result, count);
            // 获得名叫list的数组参数
            JSONArray listArray = obj.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                // 获得数组中指定下标的JSON对象
                JSONObject list_item = listArray.getJSONObject(i);
                // 获得名叫item的字符串参数
                String item = list_item.getString("item");
                result = String.format("%s\titem=%s\n", result, item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 遍历json串保存的键值对信息
    private String traverseJson(String jsonStr) {
        String result = "";
        try {
            // 根据json串构建一个JSON对象
            JSONObject obj = new JSONObject(jsonStr);
            // 获得JSON对象内部的键名称迭代器
            Iterator<String> it = obj.keys();
            while (it.hasNext()) { // 遍历JSONObject
                String key = it.next(); // 获得下一个键的名称
                String value = obj.getString(key); // 获得与该键对应的值信息
                result = String.format("%skey=%s, value=%s\n", result, key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
