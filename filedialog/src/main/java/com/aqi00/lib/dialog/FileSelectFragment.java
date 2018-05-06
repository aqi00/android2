package com.aqi00.lib.dialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.aqi00.lib.R;
import com.aqi00.lib.util.DirUtil;
import com.aqi00.lib.util.FileResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileSelectFragment extends DialogFragment implements
        OnItemClickListener, OnItemLongClickListener {
    private static final String TAG = "FileSelectFragment";
    final String PARENT = "\u25C0";
    private FileSelectCallbacks mCallbacks;
    private ArrayList<File> mFileList;

    private TextView mSelectedPath;
    private TextView mSelectedFile;
    private LinearLayout mRoot;
    private ListView mDirView;

    private File mCurrDir;
    private File mCurrFile;
    private int resid_OK;
    private int resid_Cancel;
    private int resid_Title;
    private int resid_Icon;
    private int resid_Dir;
    private int resid_UpDir;
    private int resid_File;

    private int dialog_Height;
    private Map<String, Object> mMapParam;
    private Mode mSelectMode;
    private FilenameFilter mFileFilter;

    public enum Mode {
        DirectorySelector, FileSelector
    }

    public interface FileSelectCallbacks {
        void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param);
        boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param);
    }

    public static FilenameFilter FiletypeFilter(final ArrayList<String> fileExtensions) {
        FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File directory, String fileName) {
                File f = new File(String.format("%s/%s", directory.getAbsolutePath(), fileName));
                boolean matched = f.isDirectory();
                if (!matched) {
                    for (String s : fileExtensions) {
                        s = String.format(".{0,}\\%s$", s);
                        s = s.toUpperCase(Locale.getDefault());
                        fileName = fileName.toUpperCase(Locale.getDefault());
                        matched = fileName.matches(s);
                        if (matched) {
                            break;
                        }
                    }
                }
                return matched;
            }
        };
        return fileNameFilter;
    }

    public static FileSelectFragment newInstance(Mode mSelectMode,
                                                 int res_DialogHeight, int resid_OK,
                                                 int resid_Cancel, int resid_Title, int resid_Icon,
                                                 int resid_Directory, int resid_UpDirectory, int resid_File) {
        FileSelectFragment frag = new FileSelectFragment();
        Bundle args = new Bundle();
        args.putInt("mode", mSelectMode.ordinal());
        args.putInt("dialogHeight", res_DialogHeight);
        args.putInt("captionOK", resid_OK);
        args.putInt("captionCancel", resid_Cancel);
        args.putInt("popupTitle", resid_Title);
        args.putInt("iconPopup", resid_Icon);
        args.putInt("iconDirectory", resid_Directory);
        args.putInt("iconUpDirectory", resid_UpDirectory);
        args.putInt("iconFile", resid_File);
        frag.setArguments(args);
        return frag;
    }

    public void setFilter(FilenameFilter mFileFilter, Map<String, Object> map_param) {
        mMapParam = map_param;
        this.mFileFilter = mFileFilter;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof FileSelectCallbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }
        mCallbacks = (FileSelectCallbacks) activity;
        mFileList = new ArrayList<File>();
        dialog_Height = getArguments().getInt("dialogHeight");
        resid_OK = getArguments().getInt("captionOK");
        resid_Cancel = getArguments().getInt("captionCancel");
        resid_Title = getArguments().getInt("popupTitle");
        resid_Icon = getArguments().getInt("iconPopup");
        resid_File = getArguments().getInt("iconFile");
        resid_Dir = getArguments().getInt("iconDirectory");
        resid_UpDir = getArguments().getInt("iconUpDirectory");
        mSelectMode = Mode.values()[getArguments().getInt("mode")];
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
        mFileList = getDirectoryContent(mCurrDir);
        DirectoryDisplay displayFormat = new DirectoryDisplay(getActivity(), mFileList);

        LinearLayout.LayoutParams listViewLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dialog_Height, 0.0F);
        mDirView = new ListView(getActivity());
        mDirView.setLayoutParams(listViewLayout);
        mDirView.setAdapter(displayFormat);
        mDirView.setOnItemClickListener(this);
        mDirView.setOnItemLongClickListener(this);
        mRoot.addView(mDirView);
        View horizDivider = new View(getActivity());
        horizDivider.setBackgroundColor(Color.CYAN);
        mRoot.addView(horizDivider, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        LinearLayout nameArea = new LinearLayout(getActivity());
        nameArea.setOrientation(LinearLayout.HORIZONTAL);
        nameArea.setLayoutParams(mRootLayout);
        mRoot.addView(nameArea);

        mSelectedPath = new TextView(getActivity());
        mSelectedPath.setText(mCurrDir.getAbsolutePath() + "/");
        nameArea.addView(mSelectedPath);
        if (mSelectMode == Mode.FileSelector) {
            mSelectedFile = new TextView(getActivity());
            mSelectedFile.setGravity(Gravity.LEFT);
            mSelectedFile.setPadding(2, 0, 6, 0);
            nameArea.addView(mSelectedFile);
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
                    String filename = null;
                    if (mCurrFile != null) {
                        filename = mCurrFile.getName();
                    }
                    if (mCallbacks.isFileValid(absolutePath, filename, mMapParam)) {
                        dismiss();
                        mCallbacks.onConfirmSelect(absolutePath, filename, mMapParam);
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
        mCurrFile = null;
        String file_path = mCurrDir.getAbsolutePath();
        if (mCurrDir.getParent() != null) {
            file_path += "/";
        }
        mSelectedPath.setText(file_path);
        if (pos >= 0 || pos < mFileList.size()) {
            mCurrFile = mFileList.get(pos);
            String file_name = mCurrFile.getName();
            if (!mCurrFile.isDirectory() && !file_name.equals(PARENT)
                    && mSelectMode == Mode.FileSelector) {
                mSelectedFile.setText(mCurrFile.getName());
            }
        }

        File selected;
        if (pos >= 0 || pos < mFileList.size()) {
            selected = mFileList.get(pos);
            String name = selected.getName();
            if (selected.isDirectory() || name.equals(PARENT)) {
                if (name.equals(PARENT)) {
                    mCurrDir = mCurrDir.getParentFile();
                } else {
                    mCurrDir = selected;
                }
                mFileList = getDirectoryContent(mCurrDir);
                DirectoryDisplay displayFormatter = new DirectoryDisplay(getActivity(), mFileList);
                mDirView.setAdapter(displayFormatter);
                mCurrFile = null;
                String path = mCurrDir.getAbsolutePath();
                if (mCurrDir.getParent() != null) {
                    path += "/";
                }
                mSelectedPath.setText(path);
                if (mSelectMode == Mode.FileSelector) {
                    mSelectedFile.setText(null);
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
        File selected;
        if (pos >= 0 || pos < mFileList.size()) {
            selected = mFileList.get(pos);
            String name = selected.getName();
            if (selected.isDirectory() || name.equals(PARENT)) {
                if (name.equals(PARENT)) {
                    mCurrDir = mCurrDir.getParentFile();
                } else {
                    mCurrDir = selected;
                }
                mFileList = getDirectoryContent(mCurrDir);
                DirectoryDisplay displayFormatter = new DirectoryDisplay(getActivity(), mFileList);
                mDirView.setAdapter(displayFormatter);
                mCurrFile = null;
                String path = mCurrDir.getAbsolutePath();
                if (mCurrDir.getParent() != null) {
                    path += "/";
                }
                mSelectedPath.setText(path);
                if (mSelectMode == Mode.FileSelector) {
                    mSelectedFile.setText(null);
                }
            }
        }
        return false;
    }

    private ArrayList<File> getDirectoryContent(File directory) {
        ArrayList<File> displayedContent = new ArrayList<File>();
        File[] files;
        if (mFileFilter != null) {
            files = directory.listFiles(mFileFilter);
        } else {
            files = directory.listFiles();
        }
        if (directory.getParent() != null) {
            displayedContent.add(new File(PARENT));
        }
        if (files != null) {
            for (File f : files) {
                boolean canDisplay = true;
                if (mSelectMode == Mode.DirectorySelector && !f.isDirectory()) {
                    canDisplay = false;
                }
                canDisplay = (canDisplay && !f.isHidden());
                if (canDisplay) {
                    displayedContent.add(f);
                }
            }
        }
        return displayedContent;
    }

    private class DirectoryDisplay extends ArrayAdapter<File> {
        public DirectoryDisplay(Context context, List<File> displayContent) {
            super(context, android.R.layout.simple_list_item_1, displayContent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int iconID = resid_File;
            TextView textview = (TextView) super.getView(position, convertView, parent);
            if (mFileList.get(position) != null) {
                String name = mFileList.get(position).getName();
                textview.setText(name);
                if (mFileList.get(position).isDirectory()) {
                    iconID = resid_Dir;
                }
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

    public static void show(Context context, String[] extensions, Map<String, Object> param) {
        if (!DirUtil.isStorageAdmit(context)) {
            HintDialogFragment.popup(context, "请先给该应用开启手机存储读写权限");
            return;
        }
        Activity act = (Activity) context;
        FileResource fileRes = new FileResource(act);
        FileSelectFragment fsf = FileSelectFragment.newInstance(
                FileSelectFragment.Mode.FileSelector,
                fileRes.dialog_height, // 对话框高度
                R.string.btn_ok, R.string.btn_cancel,
                R.string.tag_title_OpenFile, fileRes.resid_Icon,
                fileRes.resid_Directory, fileRes.resid_UpDirectory,
                fileRes.resid_File);
        ArrayList<String> allowedExtensions = new ArrayList<String>();
        for (String extension : extensions) {
            allowedExtensions.add("." + extension);
            allowedExtensions.add("." + extension.toUpperCase(Locale.getDefault()));
        }
        fsf.setFilter(FileSelectFragment.FiletypeFilter(allowedExtensions), param);
        String fragTag = act.getResources().getString(R.string.tag_fragment_FileSelect);
        fsf.show(act.getFragmentManager(), fragTag);
    }

}
