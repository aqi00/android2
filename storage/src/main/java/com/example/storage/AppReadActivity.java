package com.example.storage;

import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/10/1.
 */
public class AppReadActivity extends AppCompatActivity {
	private TextView tv_app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_read);
		tv_app = findViewById(R.id.tv_app);
		readAppMemory();
	}

	// 读取全局内存中保存的变量信息
	private void readAppMemory() {
		String desc = "全局内存中保存的信息如下：";
		// 获取当前应用的Application实例
		MainApplication app = MainApplication.getInstance();
		// 获取Application实例中保存的映射全局变量
		Map<String, String> mapParam = app.mInfoMap;
		// 遍历映射全局变量内部的键值对信息
		for (Map.Entry<String, String> item_map : mapParam.entrySet()) {
			desc = String.format("%s\n　%s的取值为%s", 
					desc, item_map.getKey(), item_map.getValue());
		}
		if (mapParam.size() <= 0) {
			desc = "全局内存中保存的信息为空";
		}
		tv_app.setText(desc);
	}
	
}
