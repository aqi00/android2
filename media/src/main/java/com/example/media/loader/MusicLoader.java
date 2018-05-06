package com.example.media.loader;

import java.util.ArrayList;

import com.example.media.bean.MediaInfo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class MusicLoader {
    private static final String TAG = "MusicLoader";
    private static ArrayList<MediaInfo> musicList = new ArrayList<MediaInfo>(); // 音乐队列
    private static MusicLoader mLoader; // 声明一个音乐加载器对象
    private static ContentResolver mResolver; // 声明一个内容解析器对象
    private static Uri mAudioUri = Audio.Media.EXTERNAL_CONTENT_URI; // 音频库的Uri
    private static String[] mMediaColumn = new String[]{
            Audio.Media._ID, // 编号
            Audio.Media.TITLE, // 乐曲名
            Audio.Media.ALBUM, // 专辑名
            Audio.Media.DURATION, // 播放时长
            Audio.Media.SIZE, // 文件大小
            Audio.Media.ARTIST, // 演唱者
            Audio.Media.DATA}; // 文件路径

    // 利用单例模式获取音乐加载器的唯一实例
    public static MusicLoader getInstance(ContentResolver resolver) {
        if (mLoader == null) {
            mResolver = resolver;
            mLoader = new MusicLoader();
        }
        return mLoader;
    }

    // 音乐加载器的构造函数，从系统的音频库中获取音乐文件列表
    private MusicLoader() {
        // 通过内容解析器查询系统的音频库，并返回结果集的游标
        Cursor cursor = mResolver.query(mAudioUri, mMediaColumn, null, null, null);
        if (cursor == null) {
            return;
        }
        // 下面遍历结果集，并逐个添加到音乐队列
        while (cursor.moveToNext()) {
            MediaInfo music = new MediaInfo();
            music.setId(cursor.getLong(0));
            music.setTitle(cursor.getString(1));
            music.setAlbum(cursor.getString(2));
            music.setDuration(cursor.getInt(3));
            music.setSize(cursor.getLong(4));
            music.setArtist(cursor.getString(5));
            music.setUrl(cursor.getString(6));
            Log.d(TAG, music.getTitle() + " " + music.getDuration());
            musicList.add(music);
        }
        cursor.close(); // 关闭游标
    }

    // 获取音乐队列
    public ArrayList<MediaInfo> getMusicList() {
        return musicList;
    }

}
