package com.robj.deviceutils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by jj on 25/09/16.
 */
public class PermissionsUtil {

    public static final String CONTACTS_PERM = Manifest.permission.READ_CONTACTS;
    public static final String PHONE_STATE_PERM = Manifest.permission.READ_PHONE_STATE;
    public static final String SEND_SMS_PERM = Manifest.permission.SEND_SMS;
    public static final String READ_SMS_PERM = Manifest.permission.READ_SMS;
    public static final String RECEIVE_SMS_PERM = Manifest.permission.RECEIVE_SMS;
    public static final String BLUETOOTH_PERM = Manifest.permission.BLUETOOTH;

    public static boolean hasPermission(Context context, String permission) {
        if(VersionUtils.isMarshmallow())
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
