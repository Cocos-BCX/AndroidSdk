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
        List<String> mListNode = Arrays.asList("ws://182.92.164.121:8021", "ws://182.92.164.121:8021");
        String faucetUrl = "http://47.93.62.96:8041";
        String chainId = "9aab2f1b44ffd6649985629a18154e713f7036f668e458d7568bbf7c01eed26d";
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
