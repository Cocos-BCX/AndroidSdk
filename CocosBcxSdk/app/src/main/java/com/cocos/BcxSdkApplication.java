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
        List<String> mListNode = Arrays.asList("ws://39.106.126.54:8049", "ws://39.106.126.54:8049");
        String faucetUrl = "http://47.93.62.96:8041";
        String chainId = "7d89b84f22af0b150780a2b121aa6c715b19261c8b7fe0fda3a564574ed7d3e9";
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
