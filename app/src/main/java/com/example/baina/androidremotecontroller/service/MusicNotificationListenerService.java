package com.example.baina.androidremotecontroller.service;

import android.content.Intent;
import android.media.AudioManager;
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
    private RemoteController.OnClientUpdateListener mExternalClientUpdateListener;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setExternalClientUpdateListener(RemoteController.OnClientUpdateListener externalClientUpdateListener) {
        mExternalClientUpdateListener = externalClientUpdateListener;
    }

    public void registerRemoteController() {
        mRemoteController = new RemoteController(this, this);
        boolean registered;
        try {
            registered = ((AudioManager) getSystemService(AUDIO_SERVICE)).registerRemoteController(mRemoteController);
        } catch (NullPointerException | SecurityException e) {
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
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e(TAG, "onNotificationRemoved...");
    }

    @Override
    public void onClientChange(boolean clearing) {
        if (mExternalClientUpdateListener != null)
            mExternalClientUpdateListener.onClientChange(clearing);
    }

    @Override
    public void onClientPlaybackStateUpdate(int state) {
        if (mExternalClientUpdateListener != null)
            mExternalClientUpdateListener.onClientPlaybackStateUpdate(state);
    }

    @Override
    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        if (mExternalClientUpdateListener != null)
            mExternalClientUpdateListener.onClientPlaybackStateUpdate(state, stateChangeTimeMs, currentPosMs, speed);
    }

    @Override
    public void onClientTransportControlUpdate(int transportControlFlags) {
        if (mExternalClientUpdateListener != null)
            mExternalClientUpdateListener.onClientTransportControlUpdate(transportControlFlags);
    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
        if (mExternalClientUpdateListener != null)
            mExternalClientUpdateListener.onClientMetadataUpdate(metadataEditor);
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

    public class RCBinder extends Binder {
        public MusicNotificationListenerService getService() {
            return MusicNotificationListenerService.this;
        }
    }
}
