package com.cocos.bcx_sdk.bcx_version;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author ningkang.guo
 * @Date 2019/3/21
 */
public class VersionManager {


    private static final String VERSION_NAME = "cocos_sdk_1.0.0";

    /**
     * 获取sdk版本信息
     *
     * @return
     */
    public static String getVersionInfo() {
        return VERSION_NAME;
    }
}
