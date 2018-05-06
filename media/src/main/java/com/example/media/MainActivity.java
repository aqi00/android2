package com.example.media;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.media.util.PermissionUtil;

/**
 * Created by ouyangshen on 2017/12/4.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_gallery).setOnClickListener(this);
        findViewById(R.id.btn_recycler_view).setOnClickListener(this);
        findViewById(R.id.btn_image_switcher).setOnClickListener(this);
        findViewById(R.id.btn_card_view).setOnClickListener(this);
        findViewById(R.id.btn_palette).setOnClickListener(this);
        findViewById(R.id.btn_ring_tone).setOnClickListener(this);
        findViewById(R.id.btn_sound_pool).setOnClickListener(this);
        findViewById(R.id.btn_audio_track).setOnClickListener(this);
        findViewById(R.id.btn_video_view).setOnClickListener(this);
        findViewById(R.id.btn_video_controller).setOnClickListener(this);
        findViewById(R.id.btn_media_controller).setOnClickListener(this);
        findViewById(R.id.btn_custom_controller).setOnClickListener(this);
        findViewById(R.id.btn_split_screen).setOnClickListener(this);
        findViewById(R.id.btn_pic_in_pic).setOnClickListener(this);
        findViewById(R.id.btn_float_window).setOnClickListener(this);
        findViewById(R.id.btn_screen_capture).setOnClickListener(this);
        findViewById(R.id.btn_screen_record).setOnClickListener(this);
        findViewById(R.id.btn_orientation).setOnClickListener(this);
        findViewById(R.id.btn_movie_player).setOnClickListener(this);
        findViewById(R.id.btn_spannable).setOnClickListener(this);
        findViewById(R.id.btn_music_player).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_gallery) {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_recycler_view) {
            Intent intent = new Intent(this, RecyclerViewActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_image_switcher) {
            Intent intent = new Intent(this, ImageSwitcherActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_card_view) {
            Intent intent = new Intent(this, CardViewActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_palette) {
            Intent intent = new Intent(this, PaletteActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_ring_tone) {
            Intent intent = new Intent(this, RingtoneActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_sound_pool) {
            Intent intent = new Intent(this, SoundPoolActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_audio_track) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.RECORD_AUDIO, R.id.btn_audio_track % 4096)) {
                PermissionUtil.goActivity(this, AudioTrackActivity.class);
            }
        } else if (v.getId() == R.id.btn_video_view) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_video_view % 4096)) {
                PermissionUtil.goActivity(this, VideoViewActivity.class);
            }
        } else if (v.getId() == R.id.btn_video_controller) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_video_controller % 4096)) {
                PermissionUtil.goActivity(this, VideoControllerActivity.class);
            }
        } else if (v.getId() == R.id.btn_media_controller) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_media_controller % 4096)) {
                PermissionUtil.goActivity(this, MediaControllerActivity.class);
            }
        } else if (v.getId() == R.id.btn_custom_controller) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_custom_controller % 4096)) {
                PermissionUtil.goActivity(this, CustomControllerActivity.class);
            }
        } else if (v.getId() == R.id.btn_split_screen) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Toast.makeText(this, "分屏功能需要Android7.0或以上版本", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_split_screen % 4096)) {
                PermissionUtil.goActivity(this, SplitScreenActivity.class);
            }
        } else if (v.getId() == R.id.btn_pic_in_pic) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Toast.makeText(this, "画中画功能需要Android8.0或以上版本", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_pic_in_pic % 4096)) {
                PermissionUtil.goActivity(this, PicInPicActivity.class);
            }
        } else if (v.getId() == R.id.btn_float_window) {
            Intent intent = new Intent(this, FloatWindowActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_screen_capture) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(this, "截屏功能需要Android5.0或以上版本", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_screen_capture % 4096)) {
                PermissionUtil.goActivity(this, ScreenCaptureActivity.class);
            }
        } else if (v.getId() == R.id.btn_screen_record) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(this, "录屏功能需要Android5.0或以上版本", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_screen_record % 4096)) {
                PermissionUtil.goActivity(this, ScreenRecordActivity.class);
            }
        } else if (v.getId() == R.id.btn_orientation) {
            Intent intent = new Intent(this, OrientationActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_movie_player) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_movie_player % 4096)) {
                PermissionUtil.goActivity(this, MoviePlayerActivity.class);
            }
        } else if (v.getId() == R.id.btn_spannable) {
            Intent intent = new Intent(this, SpannableActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_music_player) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Toast.makeText(this, "歌词滚动的暂停与恢复功能需要Android4.4或以上版本", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_music_player % 4096)) {
                PermissionUtil.goActivity(this, MusicPlayerActivity.class);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == R.id.btn_audio_track % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, AudioTrackActivity.class);
            } else {
                Toast.makeText(this, "需要允许录音权限才能录音噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_video_view % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, VideoViewActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能观看视频噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_video_controller % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, VideoControllerActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能观看视频噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_media_controller % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, MediaControllerActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能观看视频噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_custom_controller % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, CustomControllerActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能观看视频噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_split_screen % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, SplitScreenActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能演示分屏噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_pic_in_pic % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, PicInPicActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能演示画中画噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_screen_capture % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, ScreenCaptureActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能截屏噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_screen_record % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, ScreenRecordActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能录屏噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_movie_player % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, MoviePlayerActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能播放影视噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_music_player % 4096) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.goActivity(this, MusicPlayerActivity.class);
            } else {
                Toast.makeText(this, "需要允许SD卡权限才能播放音乐噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
