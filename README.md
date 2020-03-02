[中文](https://github.com/Cocos-BCX/AndroidSdk/blob/master/README_cn.md "中文")

# Part 1
## 1.0 Application Scope

This document is for Android COCOS wallet development.  
The SDK is applicable for Android 4.0 (API Level 14) or later, and the SDK currently chooses version 27 for beta compilation.  
Note:
Android P (version 27) limits the http network requests. However, http request is used in the SDK.
if your application is target Android 9 (API level 28) or higher, 
you can modify AndroidManifest.xml like below:

AndroidManifest.xml :

<manifest ...> <application ... android:usesCleartextTraffic="true" //add ...>  	
	
ref url: https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
 
## 1.1 Library Reference

1. project root build.gradle add：  maven { url 'https://dl.bintray.com/cocos-bcx/maven' }

```Java
   allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url 'https://jitpack.io' }
        maven { url 'https://dl.bintray.com/cocos-bcx/maven' }
    }
}
```
2. app/base moudle build.gradle add：   
```
implementation 'com.cocosbcx.androidsdk:bcx_sdk:1.1.0'
```

###### Note: 

```
NOTE1:.Avoid creating a database of the same name:cocos_bcx_android_sdk.db;  

NOTE2:.ERROR: INSTALL_FAILED_NO_MATCHING_ABIS
Solution: Add the following code to the app module's build.gradle android:  
 packagingOptions {  
        exclude 'lib/x86_64/darwin/libscrypt.dylib'  
        exclude 'lib/x86_64/freebsd/libscrypt.so'  
        exclude 'lib/x86_64/linux/libscrypt.so'  
    }

NOTE3: When compiling on Android, you may got a similar error in `Error: Cannot fit requested classes in a single dex file (# methods: 149346 > 65536). This is because Android has a limit on the number of methods in a single jar.

you can fix it with the solution below:

build.gradle

...
dependencies {
    ...

    implementation 'com.android.support:multidex:1.0.3' //add
}
...
android {

	defaultConfig {
        ...

		multiDexEnabled true //add
	}
}
...
ref url: https://stackoverflow.com/questions/48249633/errorcannot-fit-requested-classes-in-a-single-dex-file-try-supplying-a-main-dex .

NOTE4: if your application is target Android 9 (API level 28) or higher, you might got "RPC Connect failed", when connect to BCX blockchain. one possible reason is "CLEARTEXT communication is not permitted". you can modify AndroidManifest.xml like below:

AndroidManifest.xml :

<?xml version="1.0" encoding="utf-8"?>
<manifest ...>
    <application
        ...
        android:usesCleartextTraffic="true" //add
        ...>
        ...
    </application>
</manifest>
ref url: https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
```

## 1.2 SDK initialization (items must be initialized before calling other interfaces, otherwise a null pointer error will be reported)  

Below is the example of SDK initialization(Unified initialization in Application recommended.)

```Java
        List<String> mListNode = Arrays.asList("ws://47.93.62.96:8050", "ws://39.96.33.61:8080", "ws://39.96.29.40:8050", "ws://39.106.126.54:8050");
        String[] faucetUrl = {"http://47.93.62.96:3000/api/v1/accounts"};
        String chainId = "53b98adf376459cc29e5672075ed0c0b1672ea7dce42b0b1fe5e021c02bda640";
        String coreAsset = "COCOS";
        boolean isOpenLog = true;
        CocosBcxApiWrapper.getBcxInstance().init(this, chainId, mListNode, faucetUrl, coreAsset, isOpenLog,
                new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("initBcxSdk", value);
                  }
                });
```

       
   ## 1.3 SDK API User Guide

Callback data is a unified string type;  
The API call object is a singleton column object;  

Below is an example of an API call:  

```Java
  /**
     * account model  create account
     *
     * @param strAccountName accountName
     * @param strPassword    Password
     * @param isAutoLogin    true :   log in, false:just register
     * @param callBack
     */           
CocosBcxApiWrapper.getBcxInstance().create_password_account("*****", "*****", true, new IBcxCallBack() {
                @Override
                public void onReceiveValue(String value) {
                    Log.i("createaccwithpassword", value);
                    ToastUtils.showShort(value);
                }
            });
 ```

## 1.4 Status Code

| code | message | Description |
| --- | --- | --- | 
| 300 | Chain sync error, please check your system clock | Chain sync error, please check your system clock | 
| 301 | RPC connection failed. Please check your network | RPC connection failed. Please check your network | 
| 1 | None | Operation succeeded | 
| 0 | failed | The operation failed, and the error status description is not fixed. You can directly prompt res.message or to prompt the operation failure | 
| 101 | Parameter is missing | Parameter is missing |
| 1011 | Parameter error | Parameter error |
| 102 | The network is busy, please check your network connection | The network is busy, please check your network connection | 
| 103 | Please enter the correct account name(/^[a-z]([a-z0-9\.-]){4,63}/$) | Please enter the correct account name(/^a-z{4,63}/$) | 
| 104 | XX not found | XX not found |
| 105 | wrong password | wrong password |
| 106 | The account is already unlocked | The account is already unlocked | 
| 107 | Please import the private key | Please import the private key | 
| 108 | User name or password error (please confirm that your account is registered in account mode, and the account registered in wallet mode cannot be logged in using account mode) | User name or password error (please confirm that your account is registered in account mode, and the account registered in wallet mode cannot be logged in using account mode) | 
| 109 | Please enter the correct private key | Please enter the correct private key | 
| 110 | The private key has no account information | The private key has no account information | 
| 111 | Please login first | Please login first | 
| 112 | Must have owner permission to change the password, please confirm that you imported the ownerPrivateKey | Must have owner permission to change the password, please confirm that you imported the ownerPrivateKey | 
| 113 | Please enter the correct original/temporary password | Please enter the correct original/temporary password | 
| 114 | Account is locked or not logged in. | Account is locked or not logged in | 
| 115 | There is no asset XX on block chain | There is no asset XX on block chain | 
| 116 | Account receivable does not exist | Account receivable does not exist | 
| 117 | The current asset precision is configured as X ,and the decimal cannot exceed X | The current asset precision is configured as X ,and the decimal cannot exceed X | 
| 118 | Encrypt memo failed | Encrypt memo failed |
| 119 | Expiry of the transaction | Expiry of the transaction | 
| 120 | Error fetching account record | Error fetching account record | 
| 121 | block and transaction information cannot be found | block and transaction information cannot be found | 
| 122 | Parameter blockOrTXID is incorrect | Parameter blockOrTXID is incorrect | 
| 123 | Parameter account can not be empty | Parameter account can not be empty | 
| 124 | Receivables account name can not be empty | Receivables account name can not be empty | 
| 125 | Users do not own XX assets | Users do not own XX assets | 
| 127 | No reward available | No reward available | 
| 129 | Parameter 'memo' can not be empty | Parameter ‘memo’ can not be empty | 
| 130 | Please enter the correct contract name(/^[a-z]([a-z0-9\.-]){4,63}$/) | Please enter the correct contract name(/^[a-z]([a-z0-9\.-]){4,63}$/) | 
| 131 | Parameter 'worldView' can not be empty | Parameter ‘worldView’ can not be empty | 
| 133 | Parameter 'toAccount' can not be empty | Parameter ‘toAccount’ can not be empty | 
| 135 | Please check parameter data type | Please check parameter data type | 
| 136 | Parameter 'orderId' can not be empty | Parameter ‘orderId’ can not be empty | 
| 137 | Parameter 'NHAssetHashOrIds' can not be empty | Parameter ‘NHAssetHashOrIds’ can not be empty |
| 138 | Parameter 'url' can not be empty | Parameter ‘url’ can not be empty | 
| 139 | Node address must start with ws:// or wss:// | Node address must start with ws:// or wss:// | 
| 140 | API server node address already exists | API server node address already exists |
| 141 | Please check the data in parameter NHAssets | Please check the data in parameter NHAssets | 
| 142 | Please check the data type of parameter NHAssets | Please check the data type of parameter NHAssets |
| 144 | Your current batch creation / deletion / transfer number is X , and batch operations can not exceed X | Your current batch creation / deletion / transfer number is X , and batch operations can not exceed X |
| 145 | XX contract not found | XX contract not found |
| 146 | The account does not contain information about the contract | The account does not contain information about the contract | 
| 147 | NHAsset do not exist | NHAsset do not exist | 
| 148 | Request timeout, please try to unlock the account or login the account | Request timeout, please try to unlock the account or login the account | 
| 149 | This wallet has already been imported | This wallet has already been imported | 
| 150 | Key import error | Key import error |
| 151 | File saving is not supported | File saving is not supported | 
| 152 | Invalid backup to download conversion | Invalid backup to download conversion | 
| 153 | Please unlock your wallet first | Please unlock your wallet first | 
| 154 | Please restore your wallet first | Please restore your wallet first | 
| 155 | Your browser may not support wallet file recovery | Your browser may not support wallet file recovery | 
| 156 | The wallet has been imported. Do not repeat import | The wallet has been imported. Do not repeat import | 
| 157 | Can't delete wallet, does not exist in index | Can't delete wallet, does not exist in index | 
| 158 | Imported Wallet core assets can not be XX , and it should be XX | Imported Wallet core assets can not be XX , and it should be XX | 
| 159 | Account exists | Account exists |
| 160 | You are not the creator of the Asset XX . | You are not the creator of the Asset XX. | 
| 161 | Orders do not exist | Orders do not exist |
| 162 | The asset already exists | The asset already exists |
| 163 | The wallet already exists. Please try importing the private key | The wallet already exists. Please try importing the private key |
| 164 | WorldViews do not exist | WorldViews do not exist | 
| 165 | There is no wallet account information on the chain | There is no wallet account information on the chain | 
| 166 | The Wallet Chain ID does not match the current chain configuration information. The chain ID of the wallet is: XX | The Wallet Chain ID does not match the current chain configuration information. The chain ID of the wallet is: XX | 
| 167 | The current contract version ID was not found | The curre contract version ID was not found | 
| 168 | This subscription does not exist | This subscription does not exist | 
| 169 | Method does not exist | Method does not exist |

# Part 2

## 1.1 API Examples

#### 1.1.0 Wallet mode - create an account

```Java
  /**
     * wallet model  create account
     *
     * @param strAccountName accountName
     * @param strPassword    Password
     * @param isAutoLogin    true :   log in， false:just register
     * @param callBack
     */
  CocosBcxApiWrapper.getBcxInstance().create_wallet_account("testtest3", "111111", true, new IBcxCallBack() {
                @Override
                public void onReceiveValue(String value) {
                    Log.i("create_wallet_account", value);
                }
            });
```

```
Return data 1: {"code":1,"data":{"account":{"active_key":"COCOS6BGnotPV3232ZJBvp5FgZuZ5cGPqNgqaSHnbSoaJLjaKmV8LLE","name":"testtest3","owner_key":"COCOS8F9BeMjVqBVakgJhTm2pQoMido4ZBksfHL6oQb1ZWXtpmpBJF5"}}}

```

```
Return data 2: {"code":159,"message":account exist}
```

```
Return data 3: {"code":102,"message":It doesn't connect to the server.}
```

#### 1.1.1 Transfer

```Java
 /**
     * transfer
     * @param password (Temporary password/account password)  
     * @param strFrom transfer from
     * @param strTo transfer to
     * @param strAmount transfer amount
     * @param strAssetSymbol transferred asset symbol
     * @param strMemo Memo
     */
 CocosBcxApiWrapper.getBcxInstance().transfer("111111", "testtest3", "gnkhandsome1", "10", "COCOS", "testting", new IBcxCallBack() {
                @Override
                public void onReceiveValue(String value) {
                    Log.i("transfer", value);
                }
            });
```

```
 Return data1 ：{"code":1,"message":bf3c058914399136d384de714a3c57d2966e1513}
NOTE: hash : bf3c058914399136d384de714a3c57d2966e1513
```

```
 Return data2: {"code":105,"message":wrong password}
```

#### 1.1.2 Export private key (The account logged in through the private key import can only export the private key imported at login.)  

```Java
CocosBcxApiWrapper.getBcxInstance().export_private_key("gnkhandsome1", "123456", new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(final String value) {
                                   Log.i("export_private_key", value);
                            }
                        });

```

Return data (public key (Key), private key (Value)):  

```json
{"code":1,"data":{"COCOS6G55VgR94GZmELS4UHEf2eVggmhPRnWLTWgGiEmzuBKdvEwoAB":"5Hy7aVcZFyHa7UKURN22m9gB7xp4KS7Bo1dibWSVZZYAg6Br1bu","COCOS8Dw7QjWVFggYCvp9c8XbsXssqizN1MqkwPfSAVTQppQLhUcTC2":"5JgPmrWHevyH4ZzLkgZL3yAaddXE6phrKJYCfKyAJmhhjbmZyF7"},"message":""}
```

#### 1.1.3 Get the account balance (parameter 1: user ID, parameter 2: the asset ID whose balance is to be gotten, if it is empty, get the balance information of all assets in the account)  

  ```Java
 List<Object> assetSymbolOrId = new ArrayList<>();
        // todo default asset
 assetSymbolOrId.add("1.3.0");
CocosBcxApiWrapper.getBcxInstance().get_account_balances("1.2.76", unit, new IBcxCallBack() {
            @Override
            public void onReceiveValue(final String value) {
                    Log.i("get_account_balances", value);
            }
        });
```

Return data:  

```
{"id":1,"data":[{"amount":55362897,"asset_id":"1.3.0"}]}
```

