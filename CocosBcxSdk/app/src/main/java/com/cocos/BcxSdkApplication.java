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
        List<String> mListNode = Arrays.asList("ws://123.57.19.148:9049", "ws://123.57.19.148:9049");
        String faucetUrl = "http://47.93.62.96:8041";
        String chainId = "9e0ef9444fc780fa91aaef2e63c18532634ad67dcc436a4b4915d3adeef62c62";
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
