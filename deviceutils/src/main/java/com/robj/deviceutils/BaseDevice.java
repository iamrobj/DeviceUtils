package com.robj.deviceutils;

/**
 * Created by jj on 15/02/18.
 */

abstract class BaseDevice {

    private final String uniqueIdentifier;
    private final String name;

    protected BaseDevice(String uniqueIdentifier, String name) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.name = name;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public String getName() {
        return name;
    }

}
