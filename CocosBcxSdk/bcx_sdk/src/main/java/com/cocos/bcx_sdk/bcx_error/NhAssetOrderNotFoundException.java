package com.cocos.bcx_sdk.bcx_error;

public class NhAssetOrderNotFoundException extends Exception {

    public NhAssetOrderNotFoundException(String strMessage) {
        super(strMessage);
    }

    public NhAssetOrderNotFoundException(Throwable throwable) {
        super(throwable);
    }

}