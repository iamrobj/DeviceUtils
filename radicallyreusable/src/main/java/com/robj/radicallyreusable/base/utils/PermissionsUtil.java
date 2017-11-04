package com.robj.radicallyreusable.base.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by jj on 25/09/16.
 */
public class PermissionsUtil {

    public static boolean hasPermission(Context context, String permission) {
        if(VersionUtils.isMarshmallow())
            return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
        return true;
    }

    public static void showPermissionPopup(Fragment fragment, String permission, int request, int message,
                                           @StyleRes int styleResId, @StringRes int titleResId, @StringRes int acceptBtnResId, @StringRes int rejectBtnResId) {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
            fragment.requestPermissions(new String[]{permission}, request);
            return;
        }
        showPermissionRationale(fragment, permission, request, message, styleResId, titleResId, acceptBtnResId, rejectBtnResId);
    }

    public static void showPermissionPopup(Fragment fragment, String permission, int request, String message,
                                           @StyleRes int styleResId, @StringRes int titleResId, @StringRes int acceptBtnResId, @StringRes int rejectBtnResId) {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
            fragment.requestPermissions(new String[]{permission}, request);
            return;
        }
        showPermissionRationale(fragment, permission, request, message, styleResId, titleResId, acceptBtnResId, rejectBtnResId);
    }

    private static void showPermissionRationale(final Fragment fragment, final String permission, final int request, int message,
                                                @StyleRes int styleResId, @StringRes int titleResId, @StringRes int acceptBtnResId, @StringRes int rejectBtnResId) {
        showPermissionRationale(fragment, permission, request, fragment.getString(message), styleResId, titleResId, acceptBtnResId, rejectBtnResId);
    }

    private static void showPermissionRationale(final Fragment fragment, final String permission, final int request, String message,
                                                @StyleRes int styleResId, @StringRes int titleResId, @StringRes int acceptBtnResId, @StringRes int rejectBtnResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity(), styleResId);
        builder.setTitle(titleResId);
        builder.setMessage(message);
        builder.setPositiveButton(acceptBtnResId, (dialog, which) -> fragment.requestPermissions(new String[]{permission}, request));
        builder.setCancelable(false);
        builder.setNegativeButton(rejectBtnResId, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            fragment.onRequestPermissionsResult(request, new String[]{ permission }, new int[]{ PackageManager.PERMISSION_DENIED });
        });
        builder.show();
    }

}
