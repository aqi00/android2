package org.vudroid.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class ViewerPreferences {
    private SharedPreferences sharedPreferences;
    private static final String FULL_SCREEN = "FullScreen";

    public ViewerPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("ViewerPreferences", 0);
    }

    public void setFullScreen(boolean fullscreen) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FULL_SCREEN, fullscreen);
        editor.commit();
    }

    public boolean isFullScreen() {
        return sharedPreferences.getBoolean(FULL_SCREEN, false);
    }

    public void addRecent(Uri uri) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("recent:" + uri.toString(), uri.toString() + "\n" + System.currentTimeMillis());
        editor.commit();
    }

    public List<Uri> getRecent() {
        TreeMap<Long, Uri> treeMap = new TreeMap<Long, Uri>();
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("recent")) {
                String uriPlusDate = sharedPreferences.getString(key, null);
                String[] uriThenDate = uriPlusDate.split("\n");
                treeMap.put(Long.parseLong(uriThenDate.length > 1 ? uriThenDate[1] : "0"), Uri.parse(uriThenDate[0]));
            }
        }
        ArrayList<Uri> list = new ArrayList<Uri>(treeMap.values());
        Collections.reverse(list);
        return list;
    }
}
