package com.robj.radicallyreusable.base.utils;

/**
 * Created by Rob J on 04/11/17.
 */

public class ExceptionUtils {

    public static class PermissionException extends RuntimeException {
        public final String missingPermission;
        public PermissionException(String missingPermission) {
            this.missingPermission = missingPermission;
        }
    }

}
