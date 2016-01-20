package com.heaven7.pulltorefresh.swipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RefreshTime {
    final static String PRE_NAME = "heaven7_swipe_refresh_time";
    final static String SET_FRESHTIME = "heaven7_swipe_set_refresh_time";

    public static String getRefreshTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_APPEND);
        return preferences.getString(SET_FRESHTIME, "");
    }

    public static void setRefreshTime(Context context, String newPasswd) {
        SharedPreferences preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_APPEND);
        Editor editor = preferences.edit();
        editor.putString(SET_FRESHTIME, newPasswd);
        editor.commit();
    }

}
