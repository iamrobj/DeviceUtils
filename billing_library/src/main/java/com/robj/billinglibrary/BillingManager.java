package com.robj.billinglibrary;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import static com.robj.billinglibrary.PrefsUtils.*;

/**
 * Created by jj on 05/11/17.
 */

public class BillingManager {

    private static final String TAG = BillingManager.class.getSimpleName();

    private static final String INSTALL_DATE = "INSTALL_DATE";
    private static final String PURCHASED_SKU = "PURCHASED_SKU";
    private static final String LAST_PURCHASED_CHECK_DATE = "LAST_PURCHASED_CHECK_DATE";

    private static final long TRIAL_IN_DAYS = 14;
    private static final long TRIAL_TIME_IN_MILLIS = TimeUnit.DAYS.toMillis(TRIAL_IN_DAYS);

    public static void init(Application context) {
        getInstallDate(context); //Create the install date for trial if it's not already set
        Billing.init(context);
    }

    public static boolean isTrialPeriod(Context context) {
        if(isPurchased(context))
            return false;
        long diff = System.currentTimeMillis() - getInstallDate(context);
        return diff < TRIAL_TIME_IN_MILLIS;
    }

    public static boolean hasPaidFeatures(Context context) {
        return isPurchased(context) || isTrialPeriod(context);
    }

    public static int getTrialPeriodLeft(Context context) {
        if(!isTrialPeriod(context))
            return 0;
        long diff = System.currentTimeMillis() - getInstallDate(context);
        diff = TRIAL_TIME_IN_MILLIS - diff;
        return (int) Math.ceil((double) diff/86400000);
    }

    public static long getInstallDate(Context context) {
        String installDate = readStringPref(context, INSTALL_DATE);
        if(TextUtils.isEmpty(installDate)) {
            installDate = String.valueOf(System.currentTimeMillis());
            setInstallDate(context, installDate);
        }
        return Long.valueOf(installDate);
    }

    static void setInstallDate(Context context, String installDate) {
        writeStringPref(context, INSTALL_DATE, installDate);
    }

    public static boolean isPurchased(Context context) {
        String sku = readStringPref(context, PURCHASED_SKU);
        return !TextUtils.isEmpty(sku);
    }

    static void savePurchase(Context context, String sku) {
        writeStringPref(context, PURCHASED_SKU, sku);
    }

    public static String getPurchasedSku(Context context) {
        return readStringPref(context, PURCHASED_SKU);
    }

    public static long getLastPurchasedCheckDate(Context context) {
        return readLongPref(context, LAST_PURCHASED_CHECK_DATE);
    }

    static void setLastPurchaseCheckedDate(Context context, long dateInMillis) {
        writeLongPref(context, LAST_PURCHASED_CHECK_DATE, dateInMillis);
    }

}
