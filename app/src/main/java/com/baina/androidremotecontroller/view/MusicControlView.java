package com.baina.androidremotecontroller.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baina.androidremotecontroller.R;
import com.baina.androidremotecontroller.model.Music;

/**
 * Created by baina on 18-1-3.
 * 音乐控制view
 * 给调用者预留有接口
 */

public class MusicControlView extends RelativeLayout {

    //STATE_XX是音乐控制中心对应的三种状态
    //状态１，没有选择播放器
    public static final int STATE_NOMUSICPLAYER = 0x001;
    //状态２，已经选择播放器，但是没有音乐播放信息
    public static final int STATE_NOMUSICDATA = 0x002;
    //状态３，已经选择播放器，并且有音乐播放信息
    public static final int STATE_MUSICDATA = 0x003;

    private int mMusicControlState;
    private Context mContext;
    private ImageView mMusicCoverIv;
    private TextView mMusicPlayerTv;
    private TextView mMusicTitleTv;
    private TextView mMusicArtistTv;
    private ImageButton mPlayPauseIb;
    private ImageButton mPreviousIb;
    private ImageButton mNextIb;
    private OnMusicControlClickListener mMusicControlClickListener;

    public MusicControlView(Context context) {
        super(context);
    }

    public MusicControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onCreate(final OnMusicControlClickListener musicControlClickListener) {
        mContext = getContext();
        LayoutInflater.from(mContext).inflate(R.layout.layout_musiccontrol, this);
        mMusicCoverIv = findViewById(R.id.musicCoverIv);
        mMusicPlayerTv = findViewById(R.id.musicPlayerTv);
        mMusicTitleTv = findViewById(R.id.musicTitleTv);
        mMusicArtistTv = findViewById(R.id.musicArtistTv);
        mPlayPauseIb = findViewById(R.id.playPauseIb);
        mPreviousIb = findViewById(R.id.previousIb);
        mNextIb = findViewById(R.id.nextIb);
        mMusicControlClickListener = musicControlClickListener;
        //初始化点击事件
        initListener();
    }

    private void initListener() {
        mMusicCoverIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlClickListener != null) {
                    mMusicControlClickListener.OnClickMusicCover(mMusicControlState);
                }
            }
        });
        mPreviousIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlClickListener != null) {
                    mMusicControlClickListener.OnClickPrevious(mMusicControlState);
                }
            }
        });
        mPlayPauseIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlClickListener != null)
                    mMusicControlClickListener.OnClickPlayPause(mMusicControlState);
            }
        });
        mNextIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlClickListener != null)
                    mMusicControlClickListener.OnClickNext(mMusicControlState);
            }
        });
    }

    //没有选择播放器
    public void onNoMusicPlayer() {
        mMusicPlayerTv.setVisibility(VISIBLE);
        mMusicPlayerTv.setText("choose a music player");
        mMusicTitleTv.setVisibility(GONE);
        mMusicArtistTv.setVisibility(GONE);
        mMusicControlState = STATE_NOMUSICPLAYER;
    }

    //没有歌曲信息，此时只会显示播放器名称
    public void onNoMusicData(ApplicationInfo applicationInfo) {
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
    public void onClientMetadataUpdate(Music music) {
        mMusicCoverIv.setImageBitmap(music.getCover());
        mMusicPlayerTv.setVisibility(GONE);
        mMusicTitleTv.setVisibility(VISIBLE);
        mMusicArtistTv.setVisibility(VISIBLE);
        mMusicTitleTv.setText(music.getTitle());
        mMusicArtistTv.setText(music.getArtist());
        mMusicControlState = STATE_MUSICDATA;
    }

    //更新播放/暂停按钮状态
    public void onClientPlaybackStateUpdate(int state) {
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

    public interface OnMusicControlClickListener {

        //点击音乐封面
        void OnClickMusicCover(int musicControlState);

        //点击播放/暂停
        void OnClickPlayPause(int musicControlState);

        //点击上一曲
        void OnClickPrevious(int musicControlState);

        //点击下一曲
        void OnClickNext(int musicControlState);
    }
}
