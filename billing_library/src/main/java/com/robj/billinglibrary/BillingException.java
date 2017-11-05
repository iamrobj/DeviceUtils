package com.robj.billinglibrary;

/**
 * Created by jj on 05/11/17.
 */
public class BillingException extends RuntimeException {

    public enum ErrorType {
        BILLING_CANCELLED, NO_SKU_DETAILS, SKU_DETAILS_ERROR, UNABLE_TO_CHECK_PURCHASES, ALREADY_OWNED, ITEM_UNAVAILABLE, UNKNOWN
    }

    public final ErrorType errorType;

    public BillingException(ErrorType errorType) {
        this.errorType = errorType;
    }
}
