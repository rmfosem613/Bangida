package com.bangida.bangidaapp.UtilsService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

// 로그인하면 token 값을 폰에 저장
public class SharedPreferenceClass {
    private static final String USER_PREF = "user_todo";
    private SharedPreferences appShared;
    private SharedPreferences.Editor prefsEditor;

    public SharedPreferenceClass(Context context) {
        appShared = context.getSharedPreferences(USER_PREF, Activity.MODE_PRIVATE);
        this.prefsEditor = appShared.edit();
    }

    // int
    public int getValue_int(String key) {
        return appShared.getInt(key, 0);
    }

    public void setValue_int(String key, int value) {
        prefsEditor.putInt(key, value).commit();
    }

    // string
    public String getValue_string(String key) {
        return appShared.getString(key, "");
    }

    public void setValue_string(String key, String value) {
        prefsEditor.putString(key, value).commit();
    }


    // boolean
    public boolean getValue_boolean(String key) {
        return appShared.getBoolean(key, false);
    }

    public void setValue_boolean(String key, boolean value) {
        prefsEditor.putBoolean(key, value).commit();
    }

    // 폰에 저장된 token 삭제
    public void clear() {
        prefsEditor.clear().commit();
    }

}