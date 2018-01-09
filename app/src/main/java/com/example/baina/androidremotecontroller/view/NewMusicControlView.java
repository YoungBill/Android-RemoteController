package com.example.baina.androidremotecontroller.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baina.androidremotecontroller.R;
import com.example.baina.androidremotecontroller.model.Music;
import com.example.baina.androidremotecontroller.service.MusicNotificationListenerService;
import com.example.baina.androidremotecontroller.utils.Constants;
import com.example.baina.androidremotecontroller.utils.SharedPreferenceUtil;

import java.util.Set;

/**
 * Created by baina on 18-1-5.
 * 音乐控制view，高度集成
 */

public class NewMusicControlView extends RelativeLayout implements SharedPreferences.OnSharedPreferenceChangeListener {

    //STATE_XX是音乐控制中心对应的三种状态
    //状态１，没有选择播放器
    private static final int STATE_NOMUSICPLAYER = 0x001;
    //状态２，已经选择播放器，但是没有音乐播放信息
    private static final int STATE_NOMUSICDATA = 0x002;
    //状态３，已经选择播放器，并且有音乐播放信息
    private static final int STATE_MUSICDATA = 0x003;

    private static final String TAG = NewMusicControlView.class.getSimpleName();

    private int mMusicControlState;
    private Context mContext;
    private ImageView mMusicCoverIv;
    private TextView mMusicPlayerTv;
    private TextView mMusicTitleTv;
    private TextView mMusicArtistTv;
    private ImageButton mPlayPauseIb;
    private ImageButton mPreviousIb;
    private ImageButton mNextIb;
    private RemoteController.OnClientUpdateListener mOnClientUpdateListener;
    private ServiceConnection mServiceConnection;
    private MusicNotificationListenerService mNotificationListenerService;
    private SharedPreferences mPreferences;

    public NewMusicControlView(Context context) {
        super(context);
    }

    public NewMusicControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate");
        onCreate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
        //绑定通知相关service
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicNotificationListenerService.RCBinder rcBinder = (MusicNotificationListenerService.RCBinder) service;
                mNotificationListenerService = rcBinder.getService();
                mNotificationListenerService.registerRemoteController();
                mNotificationListenerService.setExternalClientUpdateListener(mOnClientUpdateListener);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        mContext.bindService(new Intent(mContext, MusicNotificationListenerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        mPreferences = mContext.getSharedPreferences(Constants.MUSICPLAYER, Context.MODE_PRIVATE);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        onRestoreInstanceState();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
        //解绑通知相关service
//        mContext.unbindService(mServiceConnection);
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.MUSICPLAYER)) {
            onRestoreInstanceState();
        }
    }

    private void onCreate() {
        mContext = getContext();
        LayoutInflater.from(mContext).inflate(R.layout.layout_musiccontrol, this);
        mMusicCoverIv = findViewById(R.id.musicCoverIv);
        mMusicPlayerTv = findViewById(R.id.musicPlayerTv);
        mMusicTitleTv = findViewById(R.id.musicTitleTv);
        mMusicArtistTv = findViewById(R.id.musicArtistTv);
        mPlayPauseIb = findViewById(R.id.playPauseIb);
        mPreviousIb = findViewById(R.id.previousIb);
        mNextIb = findViewById(R.id.nextIb);
        //初始化点击事件
        initListener();
    }

    private void initListener() {
        mMusicCoverIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMusicControlState) {
                    case STATE_NOMUSICPLAYER:
                        selectMusicPlayer();
                        break;
                    case STATE_NOMUSICDATA:
                    case STATE_MUSICDATA:
                        String appPkg = SharedPreferenceUtil.getKeyString(Constants.MUSICPLAYER, null);
                        startApp(appPkg);
                        break;
                }
            }
        });
        mPreviousIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMusicControlState) {
                    case MusicControlView.STATE_MUSICDATA:
                        if (mNotificationListenerService != null) {
                            mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                        }
                        break;
                }
            }
        });
        mPlayPauseIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMusicControlState) {
                    case STATE_NOMUSICPLAYER:
                        selectMusicPlayer();
                        break;
                    case STATE_NOMUSICDATA:
                        String appPkg = SharedPreferenceUtil.getKeyString(Constants.MUSICPLAYER, null);
                        startApp(appPkg);
                        break;
                    case STATE_MUSICDATA:
                        if (mNotificationListenerService != null) {
                            //可能存在，正在播放音乐，并且此时本地有音乐信息，但是音乐app被用户杀死的情况，此时应该启动app
                            if (!mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)) {
                                String appPkg1 = SharedPreferenceUtil.getKeyString(Constants.MUSICPLAYER, null);
                                startApp(appPkg1);
                            }
                        }
                        break;
                }
            }
        });
        mNextIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMusicControlState) {
                    case STATE_MUSICDATA:
                        if (mNotificationListenerService != null) {
                            mNotificationListenerService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                        }
                        break;
                }
            }
        });
        mOnClientUpdateListener = new RemoteController.OnClientUpdateListener() {
            @Override
            public void onClientChange(boolean clearing) {

            }

            @Override
            public void onClientPlaybackStateUpdate(int state) {
                onPlaybackStateUpdate(state);
            }

            @Override
            public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
                onPlaybackStateUpdate(state);
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
                Log.d(TAG, "artist:" + artist + "album:" + album + "title:" + title + "duration:" + duration);
                Music music = new Music();
                music.setCover(bitmap);
                music.setTitle(title);
                music.setArtist(artist);
                onMetadataUpdate(music);
            }
        };
    }

    //初始化MusicControlView状态
    private void onRestoreInstanceState() {
        String appPkg = SharedPreferenceUtil.getKeyString(Constants.MUSICPLAYER, null);
        if (TextUtils.isEmpty(appPkg)) {
            onNoMusicPlayer();
        } else {
            ApplicationInfo info = null;
            try {
                info = mContext.getPackageManager().getApplicationInfo(appPkg, 0);
                onNoMusicData(info);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //没有选择播放器
    private void onNoMusicPlayer() {
        mMusicPlayerTv.setVisibility(VISIBLE);
        mMusicPlayerTv.setText("choose a music player");
        mMusicTitleTv.setVisibility(GONE);
        mMusicArtistTv.setVisibility(GONE);
        mMusicControlState = STATE_NOMUSICPLAYER;
    }

    //没有歌曲信息，此时只会显示播放器名称
    private void onNoMusicData(ApplicationInfo applicationInfo) {
        //这里放这句判断，主要是RemoteControllerActivity在onResume的时候，有个状态操作，如果此时有歌曲信息，则不走后面的逻辑
        if (mMusicControlState == STATE_MUSICDATA)
            return;
        mMusicCoverIv.setImageDrawable(applicationInfo.loadIcon(mContext.getPackageManager()));
        mMusicPlayerTv.setVisibility(VISIBLE);
        mMusicPlayerTv.setText(applicationInfo.loadLabel(mContext.getPackageManager()));
        mMusicTitleTv.setVisibility(GONE);
        mMusicArtistTv.setVisibility(GONE);
        mMusicControlState = STATE_NOMUSICDATA;
    }

    //更新音乐信息
    private void onMetadataUpdate(Music music) {
        if (mMusicCoverIv == null || mMusicPlayerTv == null || mMusicTitleTv == null || mMusicArtistTv == null)
            return;
        mMusicCoverIv.setImageBitmap(music.getCover());
        mMusicPlayerTv.setVisibility(GONE);
        mMusicTitleTv.setVisibility(VISIBLE);
        mMusicArtistTv.setVisibility(VISIBLE);
        mMusicTitleTv.setText(music.getTitle());
        mMusicArtistTv.setText(music.getArtist());
        mMusicControlState = STATE_MUSICDATA;
    }

    //更新播放/暂停按钮状态
    private void onPlaybackStateUpdate(int state) {
        if (mPlayPauseIb == null)
            return;
        mMusicControlState = STATE_MUSICDATA;
        switch (state) {
            case 2:
                //paused
                mPlayPauseIb.setBackgroundResource(R.mipmap.play);
                break;
            case 3:
                //playing
                mPlayPauseIb.setBackgroundResource(R.mipmap.pause);
                break;

        }
    }

    private void selectMusicPlayer() {
        if (!isNotificationListenerServiceEnabled(mContext)) {
            mContext.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            Toast.makeText(mContext, R.string.grant_notifications_access, Toast.LENGTH_SHORT).show();
        } else {
            mContext.startActivity(new Intent("com.baina.allsupportaudioapp"));
        }
    }

    private void startApp(String appPkg) {
        if (!isNotificationListenerServiceEnabled(mContext)) {
            mContext.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            Toast.makeText(mContext, R.string.grant_notifications_access, Toast.LENGTH_SHORT).show();
        } else {
            try {
                Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(appPkg);
                mContext.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.fail_startup_app, Toast.LENGTH_LONG).show();
            }
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
