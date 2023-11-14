package com.ruderarajput.whatsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public MySharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void putBoolean(String prefsName, String key, boolean value) {
        editor.putBoolean(prefsName + key, value);
        editor.apply();
    }

    public boolean getBoolean(String prefsName, String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(prefsName + key, defaultValue);
    }
}
