package com.baina.androidremotecontroller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baina.androidremotecontroller.R;

/**
 * 测试高度集成音乐控制view的Activity
 * Created by baina on 18-1-5.
 */

public class TestMusicControlViewActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testmusiccontrolview);
    }
}
