
package com.aqi00.lib.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.aqi00.lib.R;
import com.aqi00.lib.util.DirUtil;
import com.aqi00.lib.util.FileResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FileSaveFragment extends DialogFragment implements OnItemClickListener {
    private static final String TAG = "FileSaveFragment";
    private final String PARENT = "\u25C0";
    private FileSaveCallbacks mCallbacks;
    private ArrayList<File> mDirList;
    private String mDefaultExt;

    private TextView mCurrPath;
    private EditText mFileName;
    private LinearLayout mRoot;
    private ListView mDirView;

    private File mCurrDir;
    private int resid_OK;
    private int resid_Cancel;
    private int resid_Title;
    private int resid_EditHint;
    private int resid_Icon;

    private int dialog_Height;
    private int resid_Dir;
    private int resid_UpDir;
    private int resid_File;

    public static boolean FileExists(String absolutePath, String mFileName) {
        File checkFile = new File(absolutePath, mFileName);
        return checkFile.exists();
    }

    public static boolean IsAlphaNumeric(String mFileName) {
        mFileName = NameNoExtension(mFileName);
        return (!mFileName.matches(".*\\W{1,}.*"));
    }

    public static String Extension(String mFileName) {
        String extension = "";
        if (mFileName.contains(".")) {
            String[] tokens = mFileName.split("\\.(?=[^\\.]+$)");
            extension = tokens[1];
        }
        return extension;
    }

    public static String NameNoExtension(String mFileName) {
        if (mFileName.contains(".")) {
            String[] tokens = mFileName.split("\\.(?=[^\\.]+$)");
            mFileName = tokens[0];
        }
        return mFileName;
    }

    public interface FileSaveCallbacks {
        boolean onCanSave(String absolutePath, String fileName);
        void onConfirmSave(String absolutePath, String fileName);
    }

    public static FileSaveFragment newInstance(String mDefaultExt,
                                               int res_DialogHeight, int resid_OK,
                                               int resid_Cancel, int resid_Title,
                                               int resid_EditHint, int resid_Icon,
                                               int resid_Directory, int resid_UpDirectory, int resid_File) {
        FileSaveFragment frag = new FileSaveFragment();
        Bundle args = new Bundle();
        args.putString("extensionList", mDefaultExt);
        args.putInt("captionOK", resid_OK);
        args.putInt("captionCancel", resid_Cancel);
        args.putInt("popupTitle", resid_Title);
        args.putInt("editHint", resid_EditHint);
        args.putInt("popupIcon", resid_Icon);
        args.putInt("dialogHeight", res_DialogHeight);
        args.putInt("iconDirectory", resid_Directory);
        args.putInt("iconUpDirectory", resid_UpDirectory);
        args.putInt("iconFile", resid_File);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof FileSaveCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (FileSaveCallbacks) activity;
        mDirList = new ArrayList<File>();
        mDefaultExt = getArguments().getString("extensionList");
        resid_OK = getArguments().getInt("captionOK");
        resid_Cancel = getArguments().getInt("captionCancel");
        resid_Title = getArguments().getInt("popupTitle");
        resid_EditHint = getArguments().getInt("editHint");
        resid_Icon = getArguments().getInt("popupIcon");
        dialog_Height = getArguments().getInt("dialogHeight");
        resid_File = getArguments().getInt("iconFile");
        resid_Dir = getArguments().getInt("iconDirectory");
        resid_UpDir = getArguments().getInt("iconUpDirectory");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout.LayoutParams mRootLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);
        mRoot = new LinearLayout(getActivity());
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setLayoutParams(mRootLayout);

        mCurrDir = new File(Environment.getExternalStorageDirectory().toString() + "/Download/");
        mDirList = getSubDirectories(mCurrDir);
        DirectoryDisplay displayFormat = new DirectoryDisplay(getActivity(), mDirList);
        LinearLayout.LayoutParams listViewLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dialog_Height, 0.0F);
        mDirView = new ListView(getActivity());
        mDirView.setLayoutParams(listViewLayout);
        mDirView.setAdapter(displayFormat);
        mDirView.setOnItemClickListener(this);
        mRoot.addView(mDirView);
        View horizDivider = new View(getActivity());
        horizDivider.setBackgroundColor(Color.CYAN);
        mRoot.addView(horizDivider, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 2));
        LinearLayout nameArea = new LinearLayout(getActivity());
        nameArea.setOrientation(LinearLayout.HORIZONTAL);
        nameArea.setLayoutParams(mRootLayout);
        mRoot.addView(nameArea);

        mCurrPath = new TextView(getActivity());
        mCurrPath.setText(mCurrDir.getAbsolutePath() + "/");
        nameArea.addView(mCurrPath);
        LinearLayout.LayoutParams mFileNameLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1.0F);
        mFileName = new EditText(getActivity());
        mFileName.setHint(resid_EditHint);
        mFileName.setGravity(Gravity.LEFT);
        mFileName.setLayoutParams(mFileNameLayout);
        mFileName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        nameArea.addView(mFileName);
        if (mDefaultExt != null) {
            TextView defaultExt = new TextView(getActivity());
            defaultExt.setText(mDefaultExt);
            defaultExt.setGravity(Gravity.LEFT);
            defaultExt.setPadding(2, 0, 6, 0);
            nameArea.addView(defaultExt);
        }

        Builder popupBuilder = new AlertDialog.Builder(getActivity());
        popupBuilder.setView(mRoot);
        popupBuilder.setIcon(resid_Icon);
        popupBuilder.setTitle(resid_Title);
        popupBuilder.setPositiveButton(resid_OK,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        popupBuilder.setNegativeButton(resid_Cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        return popupBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String absolutePath = mCurrDir.getAbsolutePath();
                    String filename = mFileName.getText().toString() + mDefaultExt;
                    if (mCallbacks.onCanSave(absolutePath, filename)) {
                        dismiss();
                        mCallbacks.onConfirmSave(absolutePath, filename);
                    }
                }
            });
        }
    }

    private ArrayList<File> getSubDirectories(File directory) {
        ArrayList<File> directories = new ArrayList<File>();
        File[] files = directory.listFiles();
        if (directory.getParent() != null) {
            directories.add(new File(PARENT));
        }

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && !f.isHidden()) {
                    directories.add(f);
                }
            }
        }
        return directories;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View list, int pos, long id) {
        File selected;
        if (pos >= 0 || pos < mDirList.size()) {
            selected = mDirList.get(pos);
            String name = selected.getName();

            if (name.equals(PARENT)) {
                mCurrDir = mCurrDir.getParentFile();
            } else {
                mCurrDir = selected;
            }

            mDirList = getSubDirectories(mCurrDir);
            DirectoryDisplay displayFormatter = new DirectoryDisplay(getActivity(), mDirList);
            mDirView.setAdapter(displayFormatter);

            String path = mCurrDir.getAbsolutePath();
            if (mCurrDir.getParent() != null) {
                path += "/";
            }
            mCurrPath.setText(path);
        }
    }

    private class DirectoryDisplay extends ArrayAdapter<File> {
        public DirectoryDisplay(Context context, List<File> displayContent) {
            super(context, android.R.layout.simple_list_item_1, displayContent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int iconID = resid_File;
            TextView textview = (TextView) super.getView(position, convertView, parent);

            if (mDirList.get(position) != null) {
                textview.setText(mDirList.get(position).getName());
                if (mDirList.get(position).isDirectory()) {
                    iconID = resid_Dir;
                }
                String name = mDirList.get(position).getName();
                if (name.equals(PARENT)) {
                    iconID = resid_UpDir;
                }
                if (iconID > 0) {
                    Drawable icon = getActivity().getResources().getDrawable(iconID);
                    textview.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                }
            }
            return textview;
        }
    }

    public static void show(Context context, String extension) {
        if (!DirUtil.isStorageAdmit(context)) {
            HintDialogFragment.popup(context, "请先给该应用开启手机存储读写权限");
            return;
        }
        Activity act = (Activity) context;
        FileResource fileRes = new FileResource(act);
        FileSaveFragment fsf = FileSaveFragment.newInstance(
                "." + extension,
                fileRes.dialog_height, // 对话框高度
                R.string.btn_ok, R.string.btn_cancel,
                R.string.tag_title_SaveFile,
                R.string.tag_save_hint,
                fileRes.resid_Icon,
                fileRes.resid_Directory,
                fileRes.resid_UpDirectory,
                fileRes.resid_File);
        String fragTag = act.getResources().getString(R.string.tag_fragment_FileSave);
        fsf.show(act.getFragmentManager(), fragTag);
    }

}
