package com.robj.billinglibrary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jj on 05/11/17.
 */

public class PrefsUtils {

    private static final String TAG = PrefsUtils.class.getSimpleName();

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName() + TAG, Context.MODE_PRIVATE);
    }

    static void writeStringPref(Context context, String name, String s) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(name, s);
        editor.apply();
    }

    static String readStringPref(Context context, String name) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(name, "");
    }

    static long readLongPref(Context context, String name) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getLong(name, 0);
    }

    static void writeLongPref(Context context, String name, long l) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(name, l);
        editor.apply();
    }

}
