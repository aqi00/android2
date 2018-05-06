package com.example.media;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.media.adapter.MediaListAdapter;
import com.example.media.bean.MediaInfo;
import com.example.media.loader.MovieLoader;

import java.util.Map;

public class MoviePlayerActivity extends AppCompatActivity implements
        OnClickListener, OnItemClickListener, FileSelectCallbacks {
    private static final String TAG = "MoviePlayerActivity";
    private MovieLoader loader; // 声明一个影视加载器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);
        findViewById(R.id.btn_open).setOnClickListener(this);
        initMovieList(); // 初始化影视列表
    }

    // 初始化影视列表
    private void initMovieList() {
        // 从布局文件中获取名叫lv_movie的列表视图
        ListView lv_movie = findViewById(R.id.lv_movie);
        // 获得影视加载器的唯一实例
        loader = MovieLoader.getInstance(getContentResolver());
        // 构建一个影视信息的列表适配器
        MediaListAdapter adapter = new MediaListAdapter(this, loader.getMovieList());
        // 给lv_movie设置影视列表适配器
        lv_movie.setAdapter(adapter);
        // 给lv_movie设置单项点击监听器
        lv_movie.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) {
            String[] videoExs = new String[]{"mp4", "3gp", "mkv", "mov", "avi"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, videoExs, null);
        }
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        Log.d(TAG, "onConfirmSelect absolutePath=" + absolutePath + ". fileName=" + fileName);
        // 拼接文件的完整路径
        String file_path = absolutePath + "/" + fileName;
        // 创建一个媒体信息实例
        MediaInfo movie = new MediaInfo(fileName, "未知", file_path);
        gotoPlay(movie); // 跳转到电影播放页面
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gotoPlay(loader.getMovieList().get(position)); // 跳转到电影播放页面
    }

    // 跳转到电影播放页面
    private void gotoPlay(MediaInfo media) {
        // 以下携带媒体信息跳转到影视播放详情页面
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", media);
        startActivity(intent);
    }

}
