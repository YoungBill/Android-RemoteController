package com.example.baina.androidremotecontroller.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.baina.androidremotecontroller.model.Music;
import com.example.baina.androidremotecontroller.service.MusicNotificationListenerService;
import com.example.baina.androidremotecontroller.R;
import com.example.baina.androidremotecontroller.view.MusicControlView;

/**
 * Created by baina on 18-1-3.
 * 远程获取第三方音乐信息及控制第三方音乐
 */
public class RemoteControllerActivity extends Activity implements RemoteController.OnClientUpdateListener {

    private static final String TAG = RemoteControllerActivity.class.getSimpleName();

    private MusicNotificationListenerService mNotificationListenerService;
    private MusicControlView mMusicControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remotecontroller);
        mMusicControlView = findViewById(R.id.controlView);
        mMusicControlView.onCreate(new MusicControlView.OnMusicControlClickListener() {
            @Override
            public void OnClickPlayPause() {
                if (mNotificationListenerService != null) {
                    mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                }
            }

            @Override
            public void OnClickPrevious() {
                if (mNotificationListenerService != null) {
                    mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                }
            }

            @Override
            public void OnClickNext() {
                if (mNotificationListenerService != null) {
                    mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取通知相关权限
        String string = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!string.contains(MusicNotificationListenerService.class.getName())) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            Toast.makeText(RemoteControllerActivity.this, "请授予通知使用权限", Toast.LENGTH_SHORT).show();
            return;
        }
        bindService(new Intent(RemoteControllerActivity.this, MusicNotificationListenerService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicNotificationListenerService.RCBinder rcBinder = (MusicNotificationListenerService.RCBinder) service;
                mNotificationListenerService = rcBinder.getService();
                mNotificationListenerService.setExternalClientUpdateListener(RemoteControllerActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClientChange(boolean clearing) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int state) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        mMusicControlView.onClientPlaybackStateUpdate(state);
    }

    @Override
    public void onClientTransportControlUpdate(int transportControlFlags) {

    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
        String artist = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST, "null");
        String album = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUM, "null");
        String title = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, "null");
        Long duration = metadataEditor.getLong(MediaMetadataRetriever.METADATA_KEY_DURATION, -1);
        Bitmap defaultCover = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass);
        Bitmap bitmap = metadataEditor.getBitmap(RemoteController.MetadataEditor.BITMAP_KEY_ARTWORK, defaultCover);
//        setCoverImage(bitmap);
//
//        setContentString(artist);
//
//        setTitleString(title);
        Log.e(TAG, "artist:" + artist

                + "album:" + album

                + "title:" + title

                + "duration:" + duration);
        Music music = new Music();
        music.setCover(bitmap);
        music.setTitle(title);
        music.setArtist(artist);
        mMusicControlView.onClientMetadataUpdate(music);
    }
}
