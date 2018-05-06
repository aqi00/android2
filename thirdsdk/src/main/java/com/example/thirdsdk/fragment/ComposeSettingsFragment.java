package com.example.thirdsdk.fragment;

import com.example.thirdsdk.R;
import com.example.thirdsdk.VoiceSettingsActivity;
import com.example.thirdsdk.widget.SettingTextWatcher;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

// 语音合成设置界面
public class ComposeSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {
    private EditTextPreference mSpeedPreference;
    private EditTextPreference mPitchPreference;
    private EditTextPreference mVolumePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定保存文件名字
        getPreferenceManager().setSharedPreferencesName(VoiceSettingsActivity.PREFER_NAME);
        addPreferencesFromResource(R.xml.voice_compose_setting);
        mSpeedPreference = (EditTextPreference) findPreference("speed_preference");
        mSpeedPreference.getEditText().addTextChangedListener(
                new SettingTextWatcher(getActivity(), mSpeedPreference, 0, 200));

        mPitchPreference = (EditTextPreference) findPreference("pitch_preference");
        mPitchPreference.getEditText().addTextChangedListener(
                new SettingTextWatcher(getActivity(), mPitchPreference, 0, 100));

        mVolumePreference = (EditTextPreference) findPreference("volume_preference");
        mVolumePreference.getEditText().addTextChangedListener(
                new SettingTextWatcher(getActivity(), mVolumePreference, 0, 100));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

}