package com.cocos;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.cocos.bcx_sdk.bcx_api.CocosBcxApiWrapper;
import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcxsdk.utils.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author ningkang.guo
 * @Date 2019/3/11
 */
public class BcxSdkApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Utils.init(this);
        //初始化工具类
        List<String> mListNode = Arrays.asList("ws://test.cocosbcx.net", "ws://test.cocosbcx.net");
        String faucetUrl = "http://test-faucet.cocosbcx.net";
        String chainId = "1ae3653a3105800f5722c5bda2b55530d0e9e8654314e2f3dc6d2b010da641c5";
        String coreAsset = "COCOS";
        boolean isOpenLog = true;
        CocosBcxApiWrapper.getBcxInstance().init(this);
        CocosBcxApiWrapper.getBcxInstance().connect(this, chainId, mListNode, faucetUrl, coreAsset, isOpenLog,
                new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("initBcxSdk", value);
                    }
                });
    }

}
