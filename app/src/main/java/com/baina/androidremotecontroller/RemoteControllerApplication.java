package com.baina.androidremotecontroller;

import android.app.Application;
import android.content.Context;

/**
 *
 *
 * Created by baina on 18-1-3.
 */

public class RemoteControllerApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getInstance() {
        return mContext;
    }
}
