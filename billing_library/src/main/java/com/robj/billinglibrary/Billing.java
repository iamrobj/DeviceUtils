package com.robj.billinglibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Rob J on 27/08/17.
 */

class Billing implements BillingClientStateListener {

    private static final String TAG = Billing.class.getSimpleName();

    private static Billing billing;

    private final BillingClient mBillingClient;
    private final Context context;

    private ObservableEmitter<Purchase> purchaseObservableEmitter;

    public static void init(Application context) {
        if(billing != null)
            billing.finish();
        new Billing(context);
    }

    private void finish() {
        mBillingClient.endConnection();
    }

    private Billing(Application context) {
        this.billing = this;
        this.context = context;
        mBillingClient = new BillingClient.Builder(context)
                .setListener((responseCode, purchases) -> {
                    if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                            return;
                        }
                        handlePurchaseError(BillingException.ErrorType.UNKNOWN); //TODO: Properly
                    } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
                        handlePurchaseError(BillingException.ErrorType.BILLING_CANCELLED);
                    } else {
                        handleResponse(responseCode, null);
                    }
                }).build();
        mBillingClient.startConnection(this);
    }

    private void handlePurchaseError(BillingException.ErrorType errorType) {
        if(purchaseObservableEmitter != null) {
            purchaseObservableEmitter.onError(new BillingException(errorType));
            purchaseObservableEmitter = null;
        }
    }

    private void handlePurchase(Purchase purchase) {
        Log.d(TAG, "Purchase of sku " + purchase.getSku() + " was successful..");
        BillingManager.savePurchase(getContext(), purchase.getSku());
        if(purchaseObservableEmitter != null) {
            purchaseObservableEmitter.onNext(purchase);
            purchaseObservableEmitter = null;
        }
    }

    private Context getContext() {
        return context;
    }

    public void clearPurchases() {
        getPurchases()
                .doOnNext(purchaseOptional -> {
                    if(!purchaseOptional.isEmpty()) {
                        Purchase purchase = purchaseOptional.get();
                        mBillingClient.consumeAsync(purchase.getPurchaseToken(), (purchaseToken, resultCode) -> Log.d(TAG, "Consumed : " + resultCode));
                        BillingManager.savePurchase(getContext(), null);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(integer -> {

                }, throwable -> throwable.printStackTrace());
    }

    public Observable<SkuDetails> getSkuInfo(String skuType, String sku) {
        return Observable.create(e -> {
//            int isFeatureSupportedResult = mBillingClient.isFeatureSupported(BillingClient.FeatureType.);

//            if(isFeatureSupportedResult == BillingClient.BillingResponse.OK) {
                List<String> skuList = new ArrayList();
                skuList.add(sku);
                mBillingClient.querySkuDetailsAsync(skuType, skuList, result -> {
                    if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                        if (result.getSkuDetailsList() != null) {
                            for (SkuDetails skuDetails : result.getSkuDetailsList()) {
                                if (skuDetails.getSku().equals(sku)) {
                                    if(!e.isDisposed())
                                        e.onNext(skuDetails);
                                    return;
                                }
                            }
                        }
                        if(!e.isDisposed())
                            e.onError(new BillingException(BillingException.ErrorType.NO_SKU_DETAILS));
                    }
                    Log.e(TAG, "getSkuInfo response code: " + result.getResponseCode());
                    if(!e.isDisposed())
                        e.onError(new BillingException(BillingException.ErrorType.SKU_DETAILS_ERROR));
                });
//            } else
//                e.onError(new BillingException(R.string.error_billing_iap_not_supported));
        });
    }

    public Observable<Optional<Purchase>> getPurchases() {
        return getPurchases(BillingClient.SkuType.INAPP)
                .flatMap(purchaseOptional -> {
                    if(!purchaseOptional.isEmpty())
                        return Observable.just(purchaseOptional);
                    else
                        return getPurchases(BillingClient.SkuType.SUBS);
                });
    }

    private Observable<Optional<Purchase>> getPurchases(String skuType) {
        return Observable.create(e -> {
            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(skuType);
            if(purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK) {
                for (Purchase purchase : purchasesResult.getPurchasesList()) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        if(!e.isDisposed())
                            e.onNext(new Optional(purchase));
                        return;
                    }
                }
                if(!e.isDisposed())
                    e.onNext(new Optional(null));
                return;
            }
            Log.e(TAG, "getPurchases for sku type " + skuType + " with response code: " + purchasesResult.getResponseCode());
            if(!e.isDisposed())
                e.onError(new BillingException(BillingException.ErrorType.UNABLE_TO_CHECK_PURCHASES));
        });
    }

    public Observable<Boolean> confirmPurchase(String sku) {
        return Observable.create(e -> {
            List<String> skuList = new ArrayList();
            skuList.add(sku);
            mBillingClient.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList, result -> {
                if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                    if (result.getSkuDetailsList() != null && !result.getSkuDetailsList().isEmpty()) {
                        for (SkuDetails skuDetails : result.getSkuDetailsList()) {
                            if (skuDetails.getSku().equals(sku)) {
                                if(!e.isDisposed())
                                    e.onNext(true);
                                return;
                            }
                        }
                        if(!e.isDisposed())
                            e.onNext(false);
                        return;
                    }
                }
                Log.e(TAG, "getSkuInfo response code: " + result.getResponseCode());
                if(!e.isDisposed())
                    e.onError(new BillingException(BillingException.ErrorType.SKU_DETAILS_ERROR));
            });
        });
    }

    public Observable<Purchase> launchBillingFlow(Activity activity, String skuType, String skuId) {
        if(purchaseObservableEmitter != null)
            return null;
        return Observable.create(e -> {
            purchaseObservableEmitter = e;
            BillingFlowParams.Builder builder = new BillingFlowParams.Builder()
                    .setSku(skuId)
                    .setType(skuType);
            int code = mBillingClient.launchBillingFlow(activity, builder.build());
            handleResponse(code, skuId);
        });
    }

    private void handleResponse(int code, String sku) {
        switch (code) {
            case BillingClient.BillingResponse.OK:
                break;
            case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
                handlePurchaseError(BillingException.ErrorType.ALREADY_OWNED);
                BillingManager.savePurchase(getContext(), sku);
                break;
            case BillingClient.BillingResponse.ITEM_UNAVAILABLE:
                handlePurchaseError(BillingException.ErrorType.ITEM_UNAVAILABLE);
                break;
            case BillingClient.BillingResponse.SERVICE_DISCONNECTED:
//                break;
            case BillingClient.BillingResponse.ITEM_NOT_OWNED:
            case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
            case BillingClient.BillingResponse.DEVELOPER_ERROR:
            case BillingClient.BillingResponse.ERROR:
            case BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED:
            case BillingClient.BillingResponse.SERVICE_UNAVAILABLE:
            case BillingClient.BillingResponse.USER_CANCELED:
            default:
                handlePurchaseError(BillingException.ErrorType.UNKNOWN);
                break;
        }
    }

    @Override
    public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
        if (billingResponseCode == BillingClient.BillingResponse.OK)
            Log.d(TAG, "Setup finished successfully..");
        else
            Log.e(TAG, "Setup error occurred, response code: " + billingResponseCode);
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.d(TAG, "Setup finished successfully..");
    }

    public static Billing getInstance() {
        return billing;
    }


}
