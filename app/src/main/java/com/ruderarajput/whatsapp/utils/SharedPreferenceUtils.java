package com.ruderarajput.whatsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {
    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }


    public static void setLoggedIn(Context context, boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_LOGGED_IN, isLoggedIn);
        editor.apply();
    }
}

