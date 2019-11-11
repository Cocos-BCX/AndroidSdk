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
        String chainId = "c1ac4bb7bd7d94874a1cb98b39a8a582421d03d022dfa4be8c70567076e03ad0";
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
