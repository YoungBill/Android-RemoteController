package com.example.baina.androidremotecontroller.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by baina on 18-1-2.
 * 音乐通知相关服务
 */

public class MusicNotificationListenerService extends NotificationListenerService implements RemoteController.OnClientUpdateListener {


    private String TAG = MusicNotificationListenerService.class.getSimpleName();

    private RemoteController mRemoteController;
    private RCBinder mBinder = new RCBinder();
    private int mState;

    @Override
    public void onCreate() {
        super.onCreate();
        registerRemoteController();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerRemoteController() {
        mRemoteController = new RemoteController(this, this);
        boolean registered;
        try {
            registered = ((AudioManager) getSystemService(AUDIO_SERVICE)).registerRemoteController(mRemoteController);
        } catch (NullPointerException e) {
            registered = false;
        }
        if (registered) {
            try {
                mRemoteController.setArtworkConfiguration(100, 100);
                mRemoteController.setSynchronizationMode(RemoteController.POSITION_SYNCHRONIZATION_CHECK);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.e(TAG, "onNotificationPosted...");
        if (sbn.getPackageName().contains("music")) {
            Log.e(TAG, "音乐软件正在播放...");
            Log.e(TAG, sbn.getPackageName());
        }
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e(TAG, "onNotificationRemoved...");
    }

    @Override
    public void onClientChange(boolean clearing) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int state) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        mState = state;
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
    }

    public boolean sendMusicKeyEvent(int keyCode) {
        if (mRemoteController != null) {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            boolean down = mRemoteController.sendMediaKeyEvent(keyEvent);
            keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
            boolean up = mRemoteController.sendMediaKeyEvent(keyEvent);
            return down && up;
        }
        return false;
    }

    public int getState() {
        return mState;
    }

    public class RCBinder extends Binder {
        public MusicNotificationListenerService getService() {
            return MusicNotificationListenerService.this;
        }
    }
}
