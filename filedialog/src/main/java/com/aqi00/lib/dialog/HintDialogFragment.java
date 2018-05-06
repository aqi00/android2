package com.aqi00.lib.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HintDialogFragment extends DialogFragment {
    private static final String TAG = "HintDialogFragment";
    private LinearLayout mRoot;
    private String mMessage;

    public static HintDialogFragment newInstance(String message) {
        Log.d(TAG, "begin show HintDialogFragment: " + message);
        HintDialogFragment frag = new HintDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMessage = getArguments().getString("message");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout.LayoutParams mRootLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);
        mRoot = new LinearLayout(getActivity());
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setLayoutParams(mRootLayout);

        Builder popupBuilder = new AlertDialog.Builder(getActivity());
        popupBuilder.setView(mRoot);
        popupBuilder.setMessage(mMessage);
        popupBuilder.setPositiveButton("确  定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        return popupBuilder.create();
    }

    public static void popup(Context context, String tips) {
        HintDialogFragment fsf = HintDialogFragment.newInstance(tips);
        fsf.show(((Activity) context).getFragmentManager(), "");
    }

    public static void popupDisconnect(Context context) {
        String tips = "无法联网，请检查网络连接是否正常";
        HintDialogFragment fsf = HintDialogFragment.newInstance(tips);
        fsf.show(((Activity) context).getFragmentManager(), "");
    }

}
