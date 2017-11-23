package com.robj.deviceutils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    public static final String ALL = "ALL";

    public static Observable<List<App>> getInstalledApps(Context context) {
        return Observable.create(e -> {
            Log.d(TAG, "Retrieving installed apps..");
            Intent i = new Intent("android.intent.action.MAIN");
            i.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(i, PackageManager.GET_META_DATA);
            ArrayList<App> apps = new ArrayList();
            if (list != null && list.size() > 0) {
                for (ResolveInfo packageInfo : list) {
                    App tmp = new App(packageInfo.activityInfo.packageName, packageInfo.loadLabel(context.getPackageManager()).toString());
                    apps.add(tmp);
                }
            }
            e.onNext(apps);
        });
    }

    public static App addExtrasToList(List<App> list) {
        Log.d(TAG, "Adding extras to app list..");
        App tmp = new App("com.android.systemui", "Android System UI");
        int position = list.indexOf(tmp);
        if (position >= 0)
            return list.get(position);
        else
            return tmp;
    }

    public static void dedupeList(ArrayList<App> list) {
        Set<App> hs = new LinkedHashSet<>();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);
    }

    public static Bitmap getAppIcon(Context context, String packageName, int size) {
        try {
            Drawable d = context.getPackageManager().getApplicationIcon(packageName);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("NotificationUtils", "No icon found..");
            return null;
        }
    }

    public static String getAppLabel(Context context, String packageName) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (appInfo != null)
                return appInfo.loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public static class App {
        public final String packageName;
        public final String displayName;
        public App(String packageName, String displayName) {
            this.packageName = packageName;
            this.displayName = displayName;
        }
    }

}
