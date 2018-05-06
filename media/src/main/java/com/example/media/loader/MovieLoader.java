package com.example.media.loader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.media.bean.MediaInfo;

import java.util.ArrayList;

/**
 * Created by ouyangshen on 2018/1/6.
 */

public class MovieLoader {
    private static final String TAG = "MovieLoader";
    private static ArrayList<MediaInfo> movieList = new ArrayList<MediaInfo>(); // 影视队列
    private static MovieLoader mLoader; // 声明一个影视加载器对象
    private static ContentResolver mResolver; // 声明一个内容解析器对象
    private static Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI; // 视频库的Uri
    private static String[] mMediaColumn = new String[]{
            MediaStore.Video.Media._ID, // 编号
            MediaStore.Video.Media.TITLE, // 电影名
            MediaStore.Video.Media.ALBUM, // 专辑名
            MediaStore.Video.Media.DURATION, // 播放时长
            MediaStore.Video.Media.SIZE, // 文件大小
            MediaStore.Video.Media.ARTIST, // 扮演者
            MediaStore.Video.Media.DATA}; // 文件路径

    // 利用单例模式获取影视加载器的唯一实例
    public static MovieLoader getInstance(ContentResolver resolver) {
        if (mLoader == null) {
            mResolver = resolver;
            mLoader = new MovieLoader();
        }
        return mLoader;
    }

    // 影视加载器的构造函数，从系统的视频库中获取影视文件列表
    private MovieLoader() {
        // 通过内容解析器查询系统的视频库，并返回结果集的游标
        Cursor cursor = mResolver.query(mVideoUri, mMediaColumn, null, null, null);
        if (cursor == null) {
            return;
        }
        // 下面遍历结果集，并逐个添加到影视队列
        while (cursor.moveToNext()) {
            MediaInfo movie = new MediaInfo();
            movie.setId(cursor.getLong(0));
            movie.setTitle(cursor.getString(1));
            movie.setAlbum(cursor.getString(2));
            movie.setDuration(cursor.getInt(3));
            movie.setSize(cursor.getLong(4));
            movie.setArtist(cursor.getString(5));
            movie.setUrl(cursor.getString(6));
            Log.d(TAG, movie.getTitle() + " " + movie.getDuration() + " " + movie.getArtist());
            movieList.add(movie);
        }
        cursor.close(); // 关闭游标
    }

    // 获取影视队列
    public ArrayList<MediaInfo> getMovieList() {
        return movieList;
    }

}
