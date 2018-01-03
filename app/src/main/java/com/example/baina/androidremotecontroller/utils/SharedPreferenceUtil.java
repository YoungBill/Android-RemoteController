package com.example.baina.androidremotecontroller.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.baina.androidremotecontroller.RemoteControllerApplication;

public class SharedPreferenceUtil {

    private static Context mContext = RemoteControllerApplication.getInstance();

    public static void putKeyInt(String key, int putValue) {
        SharedPreferences settingPre = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        settingPre.edit().putInt(key, putValue).commit();
    }

    public static int getKeyInt(String key, int defaultValue) {
        SharedPreferences intPreference = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        return intPreference.getInt(key, defaultValue);
    }


    public static void putKeyLong(String key, long putValue) {
        SharedPreferences settingPre = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        settingPre.edit().putLong(key, putValue).commit();

    }

    public static long getKeyLong(String key, long defaultValue) {
        SharedPreferences longPreference = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        return longPreference.getLong(key, defaultValue);
    }

    public static void putKeyBoolean(String key, boolean putValue) {
        SharedPreferences settingPre = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        settingPre.edit().putBoolean(key, putValue).commit();
    }

    public static boolean getKeyBoolean(String key, boolean defaultValue) {
        SharedPreferences booleanPreference = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        return booleanPreference.getBoolean(key, defaultValue);
    }

    public static void putKeyString(String key, String putValue) {
        SharedPreferences settingPre = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        settingPre.edit().putString(key, putValue).commit();
    }

    public static String getKeyString(String key, String defaultValue) {
        SharedPreferences stringPreference = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
        return stringPreference.getString(key, defaultValue);
    }
}
