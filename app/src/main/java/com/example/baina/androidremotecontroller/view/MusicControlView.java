package com.example.baina.androidremotecontroller.view;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.baina.androidremotecontroller.R;
import com.example.baina.androidremotecontroller.model.AppInfo;
import com.example.baina.androidremotecontroller.model.Music;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by baina on 18-1-3.
 * 音乐控制view
 */

public class MusicControlView extends RelativeLayout {

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

    public void onCreate() {
        mContext = getContext();
        LayoutInflater.from(mContext).inflate(R.layout.layout_musiccontrol, this);
        mMusicCoverIv = findViewById(R.id.musicCoverIv);
        mMusicPlayerTv = findViewById(R.id.musicPlayerTv);
        mMusicTitleTv = findViewById(R.id.musicTitleTv);
        mMusicArtistTv = findViewById(R.id.musicArtistTv);
        mPlayPauseIb = findViewById(R.id.playPauseIb);
        mPreviousIb = findViewById(R.id.previousIb);
        mNextIb = findViewById(R.id.nextIb);
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
        //初始化播放/暂停按钮状态信息
        // TODO: 18-1-3 这里可以改进，目前只能得到是否在播放的状态信息，未来希望改进，能拿到所有正在操作的音乐信息
        if (((AudioManager) mContext.getSystemService(AUDIO_SERVICE)).isMusicActive()) {
            mPlayPauseIb.setBackgroundResource(R.mipmap.pause);
        } else {
            mPlayPauseIb.setBackgroundResource(R.mipmap.play);
        }
    }

    private void initListener() {
        mPreviousIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlClickListener != null) {
                    mMusicControlClickListener.OnClickPrevious();
                }
            }
        });
        mPlayPauseIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlClickListener != null)
                    mMusicControlClickListener.OnClickPlayPause();
            }
        });
        mNextIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControlClickListener != null)
                    mMusicControlClickListener.OnClickNext();
            }
        });
    }

    //没有选择播放器
    public void onNoMusicPlayer() {
        mMusicPlayerTv.setVisibility(VISIBLE);
        mMusicPlayerTv.setText("choose a music player");
        mMusicTitleTv.setVisibility(GONE);
        mMusicArtistTv.setVisibility(GONE);
    }

    //没有歌曲信息，此时不会只会显示播放器名称
    public void onNoMusicData(AppInfo appInfo) {
        mMusicCoverIv.setBackground(appInfo.getAppIcon());
        mMusicPlayerTv.setVisibility(VISIBLE);
        mMusicPlayerTv.setText(appInfo.getAppLabel());
        mMusicTitleTv.setVisibility(GONE);
        mMusicArtistTv.setVisibility(GONE);
    }

    //更新音乐信息
    public void onClientMetadataUpdate(Music music) {
        mMusicCoverIv.setImageBitmap(music.getCover());
        mMusicPlayerTv.setVisibility(GONE);
        mMusicTitleTv.setVisibility(VISIBLE);
        mMusicArtistTv.setVisibility(VISIBLE);
        mMusicTitleTv.setText(music.getTitle());
        mMusicArtistTv.setText(music.getArtist());
    }

    //更新音乐信息
    public void onClientPlaybackStateUpdate(int state) {
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
        void OnClickPlayPause();

        void OnClickPrevious();

        void OnClickNext();
    }
}
