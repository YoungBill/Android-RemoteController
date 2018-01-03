package com.example.baina.androidremotecontroller.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.baina.androidremotecontroller.service.MusicNotificationListenerService;
import com.example.baina.androidremotecontroller.R;

/**
 * Created by baina on 18-1-3.
 * 远程获取第三方音乐信息及控制第三方音乐
 */
public class RemoteControllerActivity extends Activity {

    private static final String TAG = RemoteControllerActivity.class.getSimpleName();

    private MusicNotificationListenerService mNotificationListenerService;
    private boolean mIsPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remotecontroller);
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
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    public void OnClick(View view) {
        switch (view.getId()) {
            //播放/暂停
            case R.id.playPauseBt:
                if (mNotificationListenerService != null)
                    mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                break;
            //上一曲
            case R.id.previousBt:
                if (mNotificationListenerService != null)
                    mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                break;
            //下一曲
            case R.id.nextBt:
                if (mNotificationListenerService != null)
                    mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                break;
        }
    }
}
