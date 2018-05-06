package com.example.thirdsdk;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.thirdsdk.bean.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ouyangshen on 2018/5/2.
 */
public class TtsReadActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "TtsReadActivity";
    private TextToSpeech mSpeech; // 声明一个文字转语音对象
    private EditText et_tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_read);
        et_tts = findViewById(R.id.et_tts);
        findViewById(R.id.btn_read).setOnClickListener(this);
        // 创建一个文字转语音对象，初始化结果在监听器TTSListener中返回
        mSpeech = new TextToSpeech(TtsReadActivity.this, new TTSListener());
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
                initLanguageSpinner(); // 初始化语言下拉框
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

    private class EngineSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            recycleSpeech(); // 回收文字转语音对象
            // 创建指定语音引擎的文字转语音对象
            mSpeech = new TextToSpeech(TtsReadActivity.this, new TTSListener(),
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
    private String[] mValidLanguageArray; // 当前引擎支持的语言名称数组
    private Locale[] mValidLocaleArray; // 当前引擎支持的语言类型数组
    private String mTextEN = "hello world. This is a TTS demo.";
    private String mTextCN = "离离原上草，一岁一枯荣。野火烧不尽，春风吹又生。";

    // 初始化语言下拉框
    private void initLanguageSpinner() {
        ArrayList<Language> languageList = new ArrayList<Language>();
        // 下面遍历语言数组，从中挑选出当前引擎所支持的语言队列
        for (int i=0; i<mLanguageArray.length; i++) {
            // 设置朗读语言。通过检查函数的返回值，判断引擎是否支持该语言
            int result = mSpeech.setLanguage(mLocaleArray[i]);
            Log.d(TAG, "language="+mLanguageArray[i]+",result="+result);
            if (result != TextToSpeech.LANG_MISSING_DATA
                    && result != TextToSpeech.LANG_NOT_SUPPORTED) { // 语言可用
                Language language = new Language(mLanguageArray[i], mLocaleArray[i]);
                languageList.add(language);
            }
        }
        int length = languageList.size();
        Log.d(TAG, "length="+length);
        mValidLanguageArray = new String[length];
        mValidLocaleArray = new Locale[length];
        for(int i=0; i<length; i++) {
            mValidLanguageArray[i] = languageList.get(i).name;
            mValidLocaleArray[i] = languageList.get(i).locale;
        }
        // 下面初始化语言下拉框
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, mValidLanguageArray);
        languageAdapter.setDropDownViewResource(R.layout.item_select);
        Spinner sp = findViewById(R.id.sp_language);
        sp.setPrompt("请选择朗读语言");
        sp.setAdapter(languageAdapter);
        sp.setOnItemSelectedListener(new LanguageSelectedListener());
        sp.setSelection(0);
    }

    private class LanguageSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (mValidLocaleArray[arg2]==Locale.CHINA) { // 汉语
                et_tts.setText(mTextCN);
            } else { // 其它语言
                et_tts.setText(mTextEN);
            }
            // 设置选中的朗读语言
            mSpeech.setLanguage(mValidLocaleArray[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_read) {
            String content = et_tts.getText().toString();
            // 开始朗读指定文本
            int result = mSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
            String desc = String.format("朗读%s", result==TextToSpeech.SUCCESS?"成功":"失败");
            Toast.makeText(TtsReadActivity.this, desc, Toast.LENGTH_SHORT).show();
        }
    }

}
