package com.robj.billinglibrary;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import io.reactivex.Observable;

/**
 * Created by jj on 05/11/17.
 */

public class BillingUtils {

    private static final String TAG = BillingUtils.class.getSimpleName();

    public static Observable<Optional<Purchase>> reevaluatePurchasedStatus(Context context) {
        return Billing.getInstance().getPurchases()
                .doOnNext(purchaseOptional -> {
                    if(purchaseOptional.isEmpty()) {
                        BillingManager.savePurchase(context, null);
                    } else {
                        Purchase purchase = purchaseOptional.get();
                        BillingManager.savePurchase(context, purchase.getSku());
                    }
                    BillingManager.setLastPurchaseCheckedDate(context, System.currentTimeMillis());
                });
    }

    public static Observable<Purchase> makePurchase(Activity activity, String skuType, String sku) {
        return Billing.getInstance().launchBillingFlow(activity, skuType, sku)
                .doOnNext(purchase -> {
                    Log.d(TAG, "Purchase success..");
                    BillingManager.savePurchase(activity, purchase.getSku());
                });
    }

    public static Observable<SkuDetails> getSkuInfo(String skuType, String sku) {
        return Billing.getInstance().getSkuInfo(skuType, sku);
    }

    public static void clearPurchases() {
        Billing.getInstance().clearPurchases();
    }
}
