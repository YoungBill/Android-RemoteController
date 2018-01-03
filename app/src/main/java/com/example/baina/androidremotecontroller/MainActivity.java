package com.example.baina.androidremotecontroller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.provider.Settings;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<AppInfo> mAppInfoList;
    private SupportMusicPlayerAdapter mAdapter;
    private ListView mAppListView;
    private MusicNotificationListenerService mNotificationListenerService;
    private boolean mIsPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppListView = findViewById(R.id.appListView);
        mAppInfoList = new ArrayList<>();
        //获取通知相关权限
        String string = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!string.contains(MusicNotificationListenerService.class.getName())) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(MainActivity.this, MusicNotificationListenerService.class), new ServiceConnection() {
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
            //展示所有支持音频相关app
            case R.id.chooseBt:
                //耳机控制播放器的intent action
                Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                List<ResolveInfo> infoList = getPackageManager().queryBroadcastReceivers(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                if (infoList.size() > 0) {
                    for (ResolveInfo resolveInfo : infoList) {
                        AppInfo appInfo = new AppInfo();
                        //set Icon
                        appInfo.setAppIcon(resolveInfo.loadIcon(getPackageManager()));
                        //set Application Name
                        appInfo.setAppLabel(resolveInfo.loadLabel(getPackageManager()).toString());
                        //set Package Name
                        appInfo.setAppPkg(resolveInfo.activityInfo.packageName);
                        mAppInfoList.add(appInfo);
                    }
                    mAdapter = new SupportMusicPlayerAdapter(MainActivity.this, mAppInfoList);
                    mAppListView.setAdapter(mAdapter);
                }
                break;
            //播放/暂停
            case R.id.playPauseBt:
                mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                break;
            //上一曲
            case R.id.previousBt:
                mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                break;
            //下一曲
            case R.id.nextBt:
                mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                break;
        }
    }
}
