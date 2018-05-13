package com.example.thirdsdk;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class VoiceComposeActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = VoiceComposeActivity.class.getSimpleName();
    // 语音合成对象
    private SpeechSynthesizer mCompose;
    // 默认发音人
    private String voicer = "xiaoyan";
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;
    private EditText et_compose_text;
    private TextView tv_spinner;
    private SharedPreferences mSharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_compose);
        et_compose_text = findViewById(R.id.et_compose_text);
        tv_spinner = findViewById(R.id.tv_spinner);
        tv_spinner.setOnClickListener(this);
        findViewById(R.id.btn_compose_play).setOnClickListener(this);
        findViewById(R.id.btn_compose_cancel).setOnClickListener(this);
        findViewById(R.id.btn_compose_pause).setOnClickListener(this);
        findViewById(R.id.btn_compose_resume).setOnClickListener(this);
        findViewById(R.id.btn_compose_setting).setOnClickListener(this);
        mSharedPreferences = getSharedPreferences(VoiceSettingsActivity.PREFER_NAME, MODE_PRIVATE);

        // 初始化合成对象
        mCompose = SpeechSynthesizer.createSynthesizer(this, mComposeInitListener);
        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);
        tv_spinner.setText(mCloudVoicersEntries[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mCompose.stopSpeaking();
        mCompose.destroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_compose_setting) {
            Intent intent = new Intent(this, VoiceSettingsActivity.class);
            intent.putExtra("type", VoiceSettingsActivity.XF_COMPOSE);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_compose_play) {  // 开始合成
            // 收到onCompleted 回调时，合成结束、生成合成音频。合成的音频格式：只支持pcm格式
            String text = et_compose_text.getText().toString();
            // 设置参数
            setParam();
            int code = mCompose.startSpeaking(text, mComposeListener);
            if (code != ErrorCode.SUCCESS) {
                showTip("语音合成失败,错误码: " + code);
            }
			// 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
			// text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/compose.pcm";
//			int code = mCompose.synthesizeToUri(text, path, mComposeListener);
        } else if (v.getId() == R.id.btn_compose_cancel) {  // 取消合成
            mCompose.stopSpeaking();
        } else if (v.getId() == R.id.btn_compose_pause) {  // 暂停播放
            mCompose.pauseSpeaking();
        } else if (v.getId() == R.id.btn_compose_resume) {  // 继续播放
            mCompose.resumeSpeaking();
        } else if (v.getId() == R.id.tv_spinner) {  // 选择发音人
            showPresonSelectDialog();
        }
    }

    private int selectedNum = 0;

    //发音人选择
    private void showPresonSelectDialog() {
        new AlertDialog.Builder(this).setTitle("在线合成发音人选项")
                .setSingleChoiceItems(mCloudVoicersEntries, // 单选框有几项,各是什么名字
                        selectedNum, // 默认的选项
                        new DialogInterface.OnClickListener() { // 点击单选框后的处理
                            public void onClick(DialogInterface dialog, int which) { // 点击了哪一项
                                tv_spinner.setText(mCloudVoicersEntries[which]);
                                voicer = mCloudVoicersValue[which];
                                if ("catherine".equals(voicer) || "henry".equals(voicer) || "vimary".equals(voicer)
                                        || "Mariane".equals(voicer) || "Allabent".equals(voicer) || "Gabriela".equals(voicer) || "Abha".equals(voicer) || "XiaoYun".equals(voicer)) {
                                    et_compose_text.setText(R.string.compose_source_en);
                                } else {
                                    et_compose_text.setText(R.string.compose_source);
                                }
                                selectedNum = which;
                                dialog.dismiss();
                            }
                        }).show();
    }

    //初始化监听
    private InitListener mComposeInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    //合成回调监听
    private SynthesizerListener mComposeListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else {
                showTip(error.getPlainDescription(true));
            }
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

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    // 参数设置
    private void setParam() {
        // 清空参数
        mCompose.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        mCompose.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mCompose.setParameter(SpeechConstant.VOICE_NAME, voicer);
        // 设置合成语速
        mCompose.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
        // 设置合成音调
        mCompose.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
        // 设置合成音量
        mCompose.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        // 设置播放器音频流类型
        mCompose.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mCompose.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mCompose.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mCompose.setParameter(SpeechConstant.TTS_AUDIO_PATH, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/msc/compose.wav");
    }

}
