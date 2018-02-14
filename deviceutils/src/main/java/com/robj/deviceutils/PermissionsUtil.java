package com.robj.deviceutils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by Rob J on 25/09/16.
 */
public class PermissionsUtil {

    public static boolean hasPermission(Context context, String permission) {
        if(Build.VERSION.SDK_INT >= 23)
            return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
        return true;
    }

    public static class PermissionException extends RuntimeException {
        public final String missingPermission;
        public PermissionException(String missingPermission) {
            this.missingPermission = missingPermission;
        }
    }

}
