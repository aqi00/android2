package com.example.thirdsdk.fragment;

import com.example.thirdsdk.R;
import com.example.thirdsdk.VoiceSettingsActivity;
import com.example.thirdsdk.widget.SettingTextWatcher;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

// 语音识别设置界面
public class RecognizeSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {
    private EditTextPreference mVadbosPreference;
    private EditTextPreference mVadeosPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(VoiceSettingsActivity.PREFER_NAME);
        addPreferencesFromResource(R.xml.voice_recognize_setting);

        mVadbosPreference = (EditTextPreference) findPreference("recognize_vadbos_preference");
        mVadbosPreference.getEditText().addTextChangedListener(
                new SettingTextWatcher(getActivity(), mVadbosPreference, 0, 10000));

        mVadeosPreference = (EditTextPreference) findPreference("recognize_vadeos_preference");
        mVadeosPreference.getEditText().addTextChangedListener(
                new SettingTextWatcher(getActivity(), mVadeosPreference, 0, 10000));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}
