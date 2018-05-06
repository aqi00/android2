package com.aqi00.lib.util;

import com.aqi00.lib.R;

import android.app.Activity;
import android.util.DisplayMetrics;

public class FileResource {

    public int width = 0;
    public int height = 0;
    public int dialog_height = 0;
    public int resid_Icon = 0;
    public int resid_Directory = 0;
    public int resid_UpDirectory = 0;
    public int resid_File = 0;

    public FileResource(Activity act) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        width = mDisplayMetrics.widthPixels;
        height = mDisplayMetrics.heightPixels;

        if (height < 600) {
            if (height < 500) {
                dialog_height = 250;
            } else {
                dialog_height = 300;
            }
            resid_Icon = R.drawable.filedialog_root_m;
            resid_Directory = R.drawable.filedialog_folder_m;
            resid_UpDirectory = R.drawable.filedialog_folder_up_m;
            resid_File = R.drawable.filedialog_jpgfile_m;
        } else {
            dialog_height = 600;
            resid_Icon = R.drawable.filedialog_root_l;
            resid_Directory = R.drawable.filedialog_folder_l;
            resid_UpDirectory = R.drawable.filedialog_folder_up_l;
            resid_File = R.drawable.filedialog_jpgfile_l;
        }
    }

}
