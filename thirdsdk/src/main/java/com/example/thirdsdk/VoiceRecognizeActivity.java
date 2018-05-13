package com.example.thirdsdk;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.thirdsdk.util.FucUtil;
import com.example.thirdsdk.util.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class VoiceRecognizeActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = VoiceRecognizeActivity.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mRecognize;
    // 语音听写UI
    private RecognizerDialog mRecognizeDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mRecognizeResults = new LinkedHashMap<String, String>();
    private EditText et_recognize_text;
    private SharedPreferences mSharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognize);
        et_recognize_text = findViewById(R.id.et_recognize_text);
        findViewById(R.id.btn_recognize_start).setOnClickListener(this);
        findViewById(R.id.btn_recognize_stop).setOnClickListener(this);
        findViewById(R.id.btn_recognize_cancel).setOnClickListener(this);
        findViewById(R.id.btn_recognize_stream).setOnClickListener(this);
        findViewById(R.id.btn_recognize_setting).setOnClickListener(this);
        mSharedPreferences = getSharedPreferences(VoiceSettingsActivity.PREFER_NAME, Activity.MODE_PRIVATE);
        // 初始化识别无UI识别对象，使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mRecognize = SpeechRecognizer.createRecognizer(this, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请将assets下文件拷贝到项目中
        mRecognizeDialog = new RecognizerDialog(this, mInitListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mRecognize.cancel();
        mRecognize.destroy();
    }

    @Override
    public void onClick(View v) {
        int ret; // 函数调用返回值
        if (v.getId() == R.id.btn_recognize_setting) { // 进入参数设置页面
            Intent intent = new Intent(this, VoiceSettingsActivity.class);
            intent.putExtra("type", VoiceSettingsActivity.XF_RECOGNIZE);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_recognize_start) { // 开始听写。如何判断一次听写结束：OnResult isLast=true 或者 onError
            et_recognize_text.setText(null); // 清空显示内容
            mRecognizeResults.clear();
            // 设置参数
            resetParam();
            boolean isShowDialog = mSharedPreferences.getBoolean("show_dialog", true);
            if (isShowDialog) {
                // 显示听写对话框
                mRecognizeDialog.setListener(mRecognizeDialogListener);
                mRecognizeDialog.show();
                showTip("请开始说话………");
            } else {
                // 不显示听写对话框
                ret = mRecognize.startListening(mRecognizeListener);
                if (ret != ErrorCode.SUCCESS) {
                    showTip("听写失败,错误码：" + ret);
                } else {
                    showTip("请开始说话…");
                }
            }
        } else if (v.getId() == R.id.btn_recognize_stop) {  // 停止听写
            mRecognize.stopListening();
            showTip("停止听写");
        } else if (v.getId() == R.id.btn_recognize_cancel) {  // 取消听写
            mRecognize.cancel();
            showTip("取消听写");
        } else if (v.getId() == R.id.btn_recognize_stream) {  // 音频流识别
            et_recognize_text.setText(null);// 清空显示内容
            mRecognizeResults.clear();
            // 设置参数
            resetParam();
            // 设置音频来源为外部文件
            mRecognize.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
            // 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
            // mRecognize.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
            // mRecognize.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
            ret = mRecognize.startListening(mRecognizeListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("识别失败,错误码：" + ret);
            } else {
                byte[] audioData = FucUtil.readAudioFile(this, "retcognize_est.wav");
                if (null != audioData) {
                    showTip("开始音频流识别");
                    // 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），位长16bit，单声道的wav或者pcm
                    // 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
                    // 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别
                    mRecognize.writeAudio(audioData, 0, audioData.length);
                    mRecognize.stopListening();
                } else {
                    mRecognize.cancel();
                    showTip("读取音频流失败");
                }
            }
        }
    }

    // 初始化监听器
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    // 听写监听器
    private RecognizerListener mRecognizeListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);
            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        mRecognizeResults.put(sn, text);
        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mRecognizeResults.keySet()) {
            resultBuffer.append(mRecognizeResults.get(key));
        }
        et_recognize_text.setText(resultBuffer.toString());
        et_recognize_text.setSelection(et_recognize_text.length());
    }

    // 听写UI监听器
    private RecognizerDialogListener mRecognizeDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        // 识别回调错误
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }
    };

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    //参数设置
    public void resetParam() {
        // 清空参数
        mRecognize.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎。TYPE_LOCAL表示本地，TYPE_CLOUD表示云端，TYPE_MIX 表示混合
        mRecognize.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mRecognize.setParameter(SpeechConstant.RESULT_TYPE, "json");
        String language = mSharedPreferences.getString("recognize_language_preference", "mandarin");
        if (language.equals("en_us")) { // 设置语言
            mRecognize.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            mRecognize.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mRecognize.setParameter(SpeechConstant.ACCENT, language);
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mRecognize.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("recognize_vadbos_preference", "4000"));
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mRecognize.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("recognize_vadeos_preference", "1000"));
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mRecognize.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("recognize_punc_preference", "1"));
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mRecognize.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mRecognize.setParameter(SpeechConstant.ASR_AUDIO_PATH, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/msc/recognize.wav");
    }

}
