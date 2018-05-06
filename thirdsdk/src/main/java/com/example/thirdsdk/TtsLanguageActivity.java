package com.example.thirdsdk;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.thirdsdk.adapter.LanguageListAdapter;
import com.example.thirdsdk.bean.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ouyangshen on 2018/5/2.
 */
public class TtsLanguageActivity extends AppCompatActivity {
    private final static String TAG = "TtsLanguageActivity";
    private TextToSpeech mSpeech; // 声明一个文字转语音对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_language);
        // 创建一个文字转语音对象，初始化结果在监听器TTSListener中返回
        mSpeech = new TextToSpeech(TtsLanguageActivity.this, new TTSListener());
    }

    private List<TextToSpeech.EngineInfo> mEngineList; // 语音引擎队列
    // 定义一个文字转语音的初始化监听器
    private class TTSListener implements TextToSpeech.OnInitListener {
        // 在初始化完成时触发
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) { // 初始化成功
                if (mEngineList == null) { // 首次初始化
                    // 获取系统支持的所有语音引擎
                    mEngineList = mSpeech.getEngines();
                    initEngineSpinner(); // 初始化语音引擎下拉框
                }
                initLanguageList(); // 初始化语言列表
            }
        }
    }

    // 初始化语音引擎下拉框
    private void initEngineSpinner() {
        String[] engineArray = new String[mEngineList.size()];
        for(int i=0; i<mEngineList.size(); i++) {
            engineArray[i] = mEngineList.get(i).label;
        }
        ArrayAdapter<String> engineAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, engineArray);
        engineAdapter.setDropDownViewResource(R.layout.item_select);
        Spinner sp = findViewById(R.id.sp_engine);
        sp.setPrompt("请选择语音引擎");
        sp.setAdapter(engineAdapter);
        sp.setOnItemSelectedListener(new EngineSelectedListener());
        sp.setSelection(0);
    }

    private class EngineSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            recycleSpeech(); // 回收文字转语音对象
            // 创建指定语音引擎的文字转语音对象
            mSpeech = new TextToSpeech(TtsLanguageActivity.this, new TTSListener(),
                    mEngineList.get(arg2).name);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 回收文字转语音对象
    private void recycleSpeech() {
        if (mSpeech != null) {
            mSpeech.stop(); // 停止文字转语音
            mSpeech.shutdown(); // 关闭文字转语音
            mSpeech = null;
        }
    }

    @Override
    protected void onDestroy() {
        recycleSpeech(); // 回收文字转语音对象
        super.onDestroy();
    }

    private String[] mLanguageArray = {"英语", "法语", "德语", "意大利语", "汉语普通话" };
    private Locale[] mLocaleArray = {
            Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN, Locale.CHINA };

    // 初始化语言列表
    private void initLanguageList() {
        ArrayList<Language> languageList = new ArrayList<Language>();
        // 下面遍历语言数组，从中挑选出当前引擎所支持的语言队列
        for (int i=0; i<mLanguageArray.length; i++) {
            String desc = "正常使用";
            // 设置朗读语言
            int result = mSpeech.setLanguage(mLocaleArray[i]);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                desc = "缺少数据";
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                desc = "暂不支持";
            }
            Language language = new Language(mLanguageArray[i], desc);
            languageList.add(language);
        }
        // 下面把该引擎对各语言的支持情况展示到列表视图上
        ListView lv_language = findViewById(R.id.lv_language);
        LanguageListAdapter adapter = new LanguageListAdapter(this, languageList);
        lv_language.setAdapter(adapter);
    }

}
