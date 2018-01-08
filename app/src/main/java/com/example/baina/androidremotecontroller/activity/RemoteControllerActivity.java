package com.example.baina.androidremotecontroller.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.baina.androidremotecontroller.model.Music;
import com.example.baina.androidremotecontroller.service.MusicNotificationListenerService;
import com.example.baina.androidremotecontroller.R;
import com.example.baina.androidremotecontroller.utils.Constants;
import com.example.baina.androidremotecontroller.utils.SharedPreferenceUtil;
import com.example.baina.androidremotecontroller.view.MusicControlView;

import java.util.Set;

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
            public void OnClickMusicCover(int musicControlState) {
                switch (musicControlState) {
                    case MusicControlView.STATE_NOMUSICPLAYER:
                        startActivity(new Intent("com.baina.allsupportaudioapp"));
                        break;
                    case MusicControlView.STATE_NOMUSICDATA:
                    case MusicControlView.STATE_MUSICDATA:
                        String appPkg = SharedPreferenceUtil.getKeyString(Constants.MUSICPLAYER, null);
                        startApp(appPkg);
                        break;
                }
            }

            @Override
            public void OnClickPlayPause(int musicControlState) {
                switch (musicControlState) {
                    case MusicControlView.STATE_NOMUSICPLAYER:
                        startActivity(new Intent("com.baina.allsupportaudioapp"));
                        break;
                    case MusicControlView.STATE_NOMUSICDATA:
                        String appPkg = SharedPreferenceUtil.getKeyString(Constants.MUSICPLAYER, null);
                        startApp(appPkg);
                        break;
                    case MusicControlView.STATE_MUSICDATA:
                        if (mNotificationListenerService != null) {
                            mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                        }
                        break;
                }
            }

            @Override
            public void OnClickPrevious(int musicControlState) {
                switch (musicControlState) {
                    case MusicControlView.STATE_MUSICDATA:
                        if (mNotificationListenerService != null) {
                            mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                        }
                        break;
                }
            }

            @Override
            public void OnClickNext(int musicControlState) {
                switch (musicControlState) {
                    case MusicControlView.STATE_MUSICDATA:
                        if (mNotificationListenerService != null) {
                            mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取通知相关权限
        if (!isNotificationListenerServiceEnabled(this)) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            Toast.makeText(RemoteControllerActivity.this, "请授予通知使用权限", Toast.LENGTH_SHORT).show();
        } else {
            bindService(new Intent(RemoteControllerActivity.this, MusicNotificationListenerService.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    MusicNotificationListenerService.RCBinder rcBinder = (MusicNotificationListenerService.RCBinder) service;
                    mNotificationListenerService = rcBinder.getService();
                    mNotificationListenerService.registerRemoteController();
                    mNotificationListenerService.setExternalClientUpdateListener(RemoteControllerActivity.this);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, Context.BIND_AUTO_CREATE);
        }
        //初始化MusicControlView相关
        String appPkg = SharedPreferenceUtil.getKeyString(Constants.MUSICPLAYER, null);
        if (TextUtils.isEmpty(appPkg)) {
            mMusicControlView.onNoMusicPlayer();
        } else {
            ApplicationInfo info = null;
            try {
                info = getPackageManager().getApplicationInfo(appPkg, 0);
                mMusicControlView.onNoMusicData(info);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
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
        Log.e(TAG, "artist:" + artist + "album:" + album + "title:" + title + "duration:" + duration);
        Music music = new Music();
        music.setCover(bitmap);
        music.setTitle(title);
        music.setArtist(artist);
        mMusicControlView.onClientMetadataUpdate(music);
    }

    private void startApp(String appPkg) {
        try {
            Intent intent = this.getPackageManager().getLaunchIntentForPackage(appPkg);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "应用未安装，启动失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 是否已经授予通知相关权限
     *
     * @param context，上下文对象
     * @return
     */
    private boolean isNotificationListenerServiceEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }
}

