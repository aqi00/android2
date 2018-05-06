package com.example.mixture.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

// 接受输入文本的对话框
public class InputDialogFragment extends DialogFragment {
    private static final String TAG = "InputDialogFragment";
    private String mSSID;
    private int mType;
    private String mMessage;
    private InputCallbacks mCallbacks;
    private EditText et_input;

    public static InputDialogFragment newInstance(String ssid, int type, String message) {
        InputDialogFragment fragment = new InputDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ssid", ssid);
        bundle.putInt("type", type);
        bundle.putString("message", message);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSSID = getArguments().getString("ssid");
        mType = getArguments().getInt("type");
        mMessage = getArguments().getString("message");
        mCallbacks = (InputCallbacks) activity;
    }

    public interface InputCallbacks {
        void onInput(String SSID, String password, int type);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout ll_root = new LinearLayout(getActivity());
        ll_root.setOrientation(LinearLayout.VERTICAL);
        ll_root.setLayoutParams(rootParams);

        TextView tv_message = new TextView(getActivity());
        tv_message.setText(mMessage);
        ll_root.addView(tv_message);
        et_input = new EditText(getActivity());
        ll_root.addView(et_input);

        Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(ll_root);
        //builder.setMessage(mMessage);
        builder.setPositiveButton("确  定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick ssid=" + mSSID + ",password=" + et_input.getText().toString() + ",type=" + mType);
                        mCallbacks.onInput(mSSID, et_input.getText().toString(), mType);
                    }
                });
        return builder.create();
    }

}
