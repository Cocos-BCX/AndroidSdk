package com.cocos.bcx_sdk.bcx_wallet.chain;


public class asset_fee_object {

    public String amount;

    public object_id<asset_object> asset_id;

    public asset_fee_object(String lAmount, object_id<asset_object> assetObjectobjectId) {
        amount = lAmount;
        asset_id = assetObjectobjectId;
    }

}
