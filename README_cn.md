[English](https://github.com/Cocos-BCX/AndroidSdk/blob/master/README.md "English")

# Part 1
## 1.0 适用范围

该文档适用于Android cocos 钱包开发.
SDK适用于Android4.0 (API Level 14)及以上版本，SDK目前测试版编译版本选择27.
注意：Android P(版本27以上) 对网络请求http限制，SDK中有使用http请求；
如果项目的 compileSdkVersion是 28 及以上, 
修改 AndroidManifest.xml 如下:

AndroidManifest.xml :

<manifest ...> <application ... android:usesCleartextTraffic="true" //add ...>  	
	
参考链接: https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted

## 1.1 类库引用

1. 项目build.gradle添加：  maven { url 'https://dl.bintray.com/cocos-bcx/maven' }

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
2. app/base模块build.gradle引用添加：   
```
implementation 'com.cocosbcx.androidsdk:bcx_sdk:1.3.2'
```
在1.3.2之前的版本可以采用上面的方式接入，在1.3.3以及以后版本可采用以下方式接入：
1.在项目根目录的build.gradle加入：
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2.在module的build.gradle加入依赖：
```
	dependencies {
	        implementation 'com.github.Cocos-BCX:AndroidSdk:V-2.2.1'
	}
```
###### 注意：

```
1.避免创建同名数据库：cocos_bcx_android_sdk.db;

2.ERROR：INSTALL_FAILED_NO_MATCHING_ABIS
解决方案：在app模块的build.gradle android下添加以下代码：
 packagingOptions {
        exclude 'lib/x86_64/darwin/libscrypt.dylib'
        exclude 'lib/x86_64/freebsd/libscrypt.so'
        exclude 'lib/x86_64/linux/libscrypt.so'
    }
```

## 1.2 SDK 初始化  （调用其他接口前必须初始化项目 ，否则会报空指针错误）

下面给出初始化sdk示例：(建议在Application中做统一初始化)

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

       
   ## 1.3 SDK API 使用说明

回调数据为统一string 类型；
API 调用对象为单列对象；

下面给出API调用示例：


```Java
  /**
     * account model  create account
     *
     * @param strAccountName accountName
     * @param strPassword    Password
     * @param isAutoLogin    true :   log in， false:just register
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

## 1.4 状态码

| code | message | 说明 |
| --- | --- | --- | 
| 300 | Chain sync error, please check your system clock | 链同步错误，请检查您的系统时钟 | 
| 301 | RPC connection failed. Please check your network | 连接RPC失败，请检查你的网络 | 
| 1 | 无 | 操作成功 | 
| 0 | failed | 操作失败，返回错误状态描述不固定，可直接提示res.message或统一提示为操作失败 | 
| 101 | Parameter is missing | 参数缺失 |
| 1011 | Parameter error | 参数错误 |
| 102 | The network is busy, please check your network connection | 网络繁忙，请检查你的网络连接 | 
| 103 | Please enter the correct account name(/^[a-z]([a-z0-9\.-]){4,63}/$) | 请输入正确的账户名(正则/^[a-z]([a-z0-9\.-]){4,63}/$) | 
| 104 | XX not found | XX 不存在 |
| 105 | wrong password | 密码错误 |
| 106 | The account is already unlocked | 账户已经处于解锁状态 | 
| 107 | Please import the private key | 请先导入私钥 | 
| 108 | User name or password error (please confirm that your account is registered in account mode, and the account registered in wallet mode cannot be logged in using account mode) | 用户名或密码错误(请确认你的账户是通过账户模式注册的，钱包模式注册的账户不能使用账户模式登录) | 
| 109 | Please enter the correct private key | 请输入正确的私钥 | 
| 110 | The private key has no account information | 该私钥没有对应的账户信息 | 
| 111 | Please login first | 请先登录 | 
| 112 | Must have owner permission to change the password, please confirm that you imported the ownerPrivateKey | 必须拥有owner权限才可以进行密码修改,请确认你导入的是ownerPrivateKey | 
| 113 | Please enter the correct original/temporary password | 请输入正确的原始密码/临时密码 | 
| 114 | Account is locked or not logged in. | 帐户被锁定或未登录 | 
| 115 | There is no asset XX on block chain | 区块链上不存在资产 XX | 
| 116 | Account receivable does not exist | 收款方账户不存在 | 
| 117 | The current asset precision is configured as X ,and the decimal cannot exceed X | 当前资产精度配置为 X ，小数点不能超过 X | 
| 118 | Encrypt memo failed | 备注加密失败 |
| 119 | Expiry of the transaction | 交易过期 | 
| 120 | Error fetching account record | 获取帐户记录错误 | 
| 121 | block and transaction information cannot be found | 查询不到相关区块及交易信息 | 
| 122 | Parameter blockOrTXID is incorrect | 参数blockOrTXID不正确 | 
| 123 | Parameter account can not be empty | 参数account不能为空 | 
| 124 | Receivables account name can not be empty | 收款方账户名不能为空 | 
| 125 | Users do not own XX assets | 用户未拥有 XX 资产 | 
| 127 | No reward available | 没有可领取的奖励 | 
| 129 | Parameter 'memo' can not be empty | memo不能为空 | 
| 130 | Please enter the correct contract name(/^[a-z]([a-z0-9\.-]){4,63}$/) | 请输入正确的合约名称(正则/^[a-z]([a-z0-9\.-]){4,63}$/) | 
| 131 | Parameter 'worldView' can not be empty | 世界观名称不能为空 | 
| 133 | Parameter 'toAccount' can not be empty | toAccount不能为空 | 
| 135 | Please check parameter data type | 请检查参数数据类型 | 
| 136 | Parameter 'orderId' can not be empty | orderId不能为空 | 
| 137 | Parameter 'NHAssetHashOrIds' can not be empty | NHAssetHashOrIds不能为空 |
| 138 | Parameter 'url' can not be empty | 接入点地址不能为空 | 
| 139 | Node address must start with ws:// or wss:// | 节点地址必须以 ws:// 或 wss:// 开头 | 
| 140 | API server node address already exists | API服务器节点地址已经存在 |
| 141 | Please check the data in parameter NHAssets | 请检查参数NHAssets中的数据 | 
| 142 | Please check the data type of parameter NHAssets | 请检查参数NHAssets的数据类型 |
| 144 | Your current batch creation / deletion / transfer number is X , and batch operations can not exceed X | 您当前批量 创建/删除/转移 NH资产数量为 X ，批量操作数量不能超过 X |
| 145 | XX contract not found | XX 合约不存在 |
| 146 | The account does not contain information about the contract | 账户没有该合约相关的信息 | 
| 147 | NHAsset do not exist | 非同质资产不存在 | 
| 148 | Request timeout, please try to unlock the account or login the account | 请求超时，请尝试解锁账户或登录账户 | 
| 149 | This wallet has already been imported | 此私钥已导入过钱包 | 
| 150 | Key import error | 导入私钥失败 |
| 151 | File saving is not supported | 您的浏览器不支持文件保存 | 
| 152 | Invalid backup to download conversion | 无效的备份下载转换 | 
| 153 | Please unlock your wallet first | 请先解锁钱包 | 
| 154 | Please restore your wallet first | 请先恢复你的钱包 | 
| 155 | Your browser may not support wallet file recovery | 浏览器不支持钱包文件恢复 | 
| 156 | The wallet has been imported. Do not repeat import | 该钱包已经导入，请勿重复导入 | 
| 157 | Can't delete wallet, does not exist in index | 请求超时，请尝试解锁账户或登录账户 | 
| 158 | Imported Wallet core assets can not be XX , and it should be XX | 导入的钱包核心资产不能为 XX ，应为 XX | 
| 159 | Account exists | 账户已存在 |
| 160 | You are not the creator of the Asset XX . | 你不是该资产的创建者 | 
| 161 | Orders do not exist | 订单不存在 |
| 162 | The asset already exists | 资产已存在 |
| 163 | The wallet already exists. Please try importing the private key | 钱包已经存在，请尝试导入私钥 |
| 164 | worldViews do not exist | 世界观不存在 | 
| 165 | There is no wallet account information on the chain | 链上没有该钱包账户信息 | 
| 166 | The Wallet Chain ID does not match the current chain configuration information. The chain ID of the wallet is: XX | 该钱包链id与当前链配置信息不匹配，该钱包的链id为： XXX | 
| 167 | The current contract version ID was not found | 当前合约版本id没有找到 X | 
| 168 | This subscription does not exist | 当前没有订阅此项 | 
| 169 | Method does not exist | API方法不存在 |

# Part 2

## 1.1 API 使用示例

#### 1.1.0 钱包模式-创建账户

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
返回数据1 ：{"code":1,"data":{"account":{"active_key":"COCOS6BGnotPV3232ZJBvp5FgZuZ5cGPqNgqaSHnbSoaJLjaKmV8LLE","name":"testtest3","owner_key":"COCOS8F9BeMjVqBVakgJhTm2pQoMido4ZBksfHL6oQb1ZWXtpmpBJF5"}}}

```

```
返回数据 2：{"code":159,"message":account exist}
```

```
返回数据 3：{"code":102,"message":It doesn't connect to the server.}
```

#### 1.1.1 转账

```Java
 /**
     * transfer
     * @param password 密码 (临时密码/账户密码)
     * @param strFrom 转出账户
     * @param strTo 转入账户
     * @param strAmount 转账金额
     * @param strAssetSymbol 转账币种
     * @param strMemo 备注信息
     */
 CocosBcxApiWrapper.getBcxInstance().transfer("111111", "testtest3", "gnkhandsome1", "10", "COCOS", "testting", new IBcxCallBack() {
                @Override
                public void onReceiveValue(String value) {
                    Log.i("transfer", value);
                }
            });
```

```
返回数据1 ：{"code":1,"message":bf3c058914399136d384de714a3c57d2966e1513}
注： hash : bf3c058914399136d384de714a3c57d2966e1513
```

```
返回数据2: {"code":105,"message":wrong password}
```

#### 1.1.2 导出私钥 (私通过钥导入登录的账户只能导出登陆时导入的私钥)

```Java
CocosBcxApiWrapper.getBcxInstance().export_private_key("gnkhandsome1", "123456", new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(final String value) {
                                   Log.i("export_private_key", value);
                            }
                        });

```

返回数据 (公钥（Key）,私钥(Value))：

```json
{"code":1,"data":{"COCOS6G55VgR94GZmELS4UHEf2eVggmhPRnWLTWgGiEmzuBKdvEwoAB":"5Hy7aVcZFyHa7UKURN22m9gB7xp4KS7Bo1dibWSVZZYAg6Br1bu","COCOS8Dw7QjWVFggYCvp9c8XbsXssqizN1MqkwPfSAVTQppQLhUcTC2":"5JgPmrWHevyH4ZzLkgZL3yAaddXE6phrKJYCfKyAJmhhjbmZyF7"},"message":""}
```

#### 1.1.3 获取账户余额（参数1:用户ID, 参数2：获取余额的资产ID，为空则获取账户的所有资产余额信息）

  ```Java
 List<Object> assetSymbolOrId = new ArrayList<>();
        // todo 默认币种类型
 assetSymbolOrId.add("1.3.0");
CocosBcxApiWrapper.getBcxInstance().get_account_balances("1.2.76", unit, new IBcxCallBack() {
            @Override
            public void onReceiveValue(final String value) {
                    Log.i("get_account_balances", value);
            }
        });
```

返回数据:

```
{"id":1,"data":[{"amount":55362897,"asset_id":"1.3.0"}]}
```

