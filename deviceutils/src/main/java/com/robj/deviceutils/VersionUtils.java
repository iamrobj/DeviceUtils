package com.robj.deviceutils;

import android.os.Build;

public class VersionUtils {

    private static final String TAG = VersionUtils.class.getSimpleName();

    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT >= 19; //15
    }

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= 26;
    }

    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= 18;
    }
    
}
