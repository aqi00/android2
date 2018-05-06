package com.example.media;

import java.util.Map;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.media.adapter.MediaListAdapter;
import com.example.media.bean.MediaInfo;
import com.example.media.loader.MusicLoader;
import com.example.media.widget.AudioController;
import com.example.media.widget.VolumeDialog;
import com.example.media.widget.VolumeDialog.VolumeAdjustListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2017/12/4.
 */
@SuppressLint("SetTextI18n")
public class MusicPlayerActivity extends AppCompatActivity implements
        OnClickListener, OnItemClickListener, FileSelectCallbacks, VolumeAdjustListener {
    private static final String TAG = "MusicPlayerActivity";
    private AudioController ac_play; // 声明一个音频控制条对象
    private AudioManager mAudioMgr; // 声明一个音频管理器对象
    private VolumeDialog dialog; // 声明一个音量对话框对象
    private MainApplication app; // 声明一个全局应用对象
    private MusicLoader loader; // 声明一个音乐加载器对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        findViewById(R.id.btn_open).setOnClickListener(this);
        initMusicList(); // 初始化音乐列表
        // 从布局文件中获取名叫ac_play的音频控制条
        ac_play = findViewById(R.id.ac_play);
        // 从系统服务中获取音频管理器
        mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获取全局应用的唯一实例
        app = MainApplication.getInstance();
    }

    // 初始化音乐列表
    private void initMusicList() {
        // 从布局文件中获取名叫lv_music的列表视图
        ListView lv_music = findViewById(R.id.lv_music);
        // 获得音乐加载器的唯一实例
        loader = MusicLoader.getInstance(getContentResolver());
        // 构建一个音乐信息的列表适配器
        MediaListAdapter adapter = new MediaListAdapter(this, loader.getMusicList());
        // 给lv_music设置音乐列表适配器
        lv_music.setAdapter(adapter);
        // 给lv_music设置单项点击监听器
        lv_music.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initController(); // 初始化音频控制条
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 移除所有的处理器任务
        mHandler.removeCallbacksAndMessages(null);
    }

    // 初始化音频控制条
    private void initController() {
        TextView tv_song = findViewById(R.id.tv_song);
        if (app.mSong != null) {
            tv_song.setText(app.mSong + "正在播放");
        } else {
            tv_song.setText("当前暂无歌曲播放");
        }
        // 延迟100毫秒后启动控制条刷新任务
        mHandler.postDelayed(mRefreshCtrl, 100);
    }

    // 定义一个控制条刷新任务
    private Runnable mRefreshCtrl = new Runnable() {
        @Override
        public void run() {
            // 设置音频控制条的播放进度
            ac_play.setCurrentTime(app.mMediaPlayer.getCurrentPosition(), 0);
            if (app.mMediaPlayer.getCurrentPosition() >= app.mMediaPlayer.getDuration()) { // 播放结束
                // 重置音频控制条的播放进度
                ac_play.setCurrentTime(0, 0);
            }
            // 延迟500毫秒后再次启动控制条刷新任务
            mHandler.postDelayed(this, 500);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) {
            String[] audioExs = new String[]{"mp3", "wav", "mid", "ogg", "amr", "acc", "pcm"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, audioExs, null);
        }
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        Log.d(TAG, "onConfirmSelect absolutePath=" + absolutePath + ". fileName=" + fileName);
        // 拼接文件的完整路径
        String file_path = absolutePath + "/" + fileName;
        // 创建一个媒体信息实例
        MediaInfo music = new MediaInfo(fileName, "未知", file_path);
        gotoPlay(music); // 跳转到音乐播放页面
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gotoPlay(loader.getMusicList().get(position));  // 跳转到音乐播放页面
    }

    // 跳转到音乐播放页面
    private void gotoPlay(MediaInfo media) {
        // 以下携带媒体信息跳转到音乐播放详情页面
        Intent intent = new Intent(this, MusicDetailActivity.class);
        intent.putExtra("music", media);
        startActivity(intent);
    }

    // 在发生物理按键事件时触发
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { // 按下了音量+键
            // 显示音量对话框，并将音量调大一级
            showVolumeDialog(AudioManager.ADJUST_RAISE);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // 按下了音量-键
            // 显示音量对话框，并将音量调小一级
            showVolumeDialog(AudioManager.ADJUST_LOWER);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) { // 按下了返回键
            finish(); // 关闭当前页面
        }
        return false;
    }

    // 显示音量对话框，同时在指定方向调节音量
    private void showVolumeDialog(int direction) {
        if (dialog == null || !dialog.isShowing()) {
            // 创建一个音量对话框
            dialog = new VolumeDialog(this);
            // 设置音量对话框的音量调节监听器
            dialog.setVolumeAdjustListener(this);
            dialog.show(); // 显示音量对话框
        }
        // 调节音量大小
        dialog.adjustVolume(direction, true);
        onVolumeAdjust(mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    // 在调节音量时触发
    public void onVolumeAdjust(int volume) {}

}
