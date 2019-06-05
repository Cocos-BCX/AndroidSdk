package com.cocos.bcx_sdk.bcx_api;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcx_sdk.bcx_callback.ResponseData;
import com.cocos.bcx_sdk.bcx_entity.AccountEntity;
import com.cocos.bcx_sdk.bcx_entity.AccountType;
import com.cocos.bcx_sdk.bcx_entity.CreateAccountParamEntity;
import com.cocos.bcx_sdk.bcx_error.AccountExistException;
import com.cocos.bcx_sdk.bcx_error.AccountNotFoundException;
import com.cocos.bcx_sdk.bcx_error.AssetNotFoundException;
import com.cocos.bcx_sdk.bcx_error.AuthorityException;
import com.cocos.bcx_sdk.bcx_error.ContractNotFoundException;
import com.cocos.bcx_sdk.bcx_error.CreateAccountException;
import com.cocos.bcx_sdk.bcx_error.NetworkStatusException;
import com.cocos.bcx_sdk.bcx_error.NhAssetNotFoundException;
import com.cocos.bcx_sdk.bcx_error.NhAssetOrderNotFoundException;
import com.cocos.bcx_sdk.bcx_error.PasswordVerifyException;
import com.cocos.bcx_sdk.bcx_error.UnLegalInputException;
import com.cocos.bcx_sdk.bcx_error.WordViewNotExistException;
import com.cocos.bcx_sdk.bcx_log.LogUtils;
import com.cocos.bcx_sdk.bcx_sql.dao.AccountDao;
import com.cocos.bcx_sdk.bcx_utils.ThreadManager;
import com.cocos.bcx_sdk.bcx_version.VersionManager;
import com.cocos.bcx_sdk.bcx_wallet.chain.account_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.asset_fee_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.asset_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.contract_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.operation_history_object;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import static com.cocos.bcx_sdk.bcx_error.ErrorCode.AUTHORITY_EXCEPTION;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.CHAIN_ID_NOT_MATCH;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_ACCOUNT_OBJECT_EXIST;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_CONTRACT_NOT_FOUND;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_NETWORK_FAIL;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_NHASSET_DO_NOT_EXIST;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_OBJECT_NOT_FOUND;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_ORDERS_DO_NOT_EXIST;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_PARAMETER;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_WORLDVIEW_DO_NOT_EXIST;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_WRONG_PASSWORD;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.NO_ACCOUNT_INFORMATION;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.OPERATE_FAILED;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.OPERATE_SUCCESS;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.RPC_NETWORK_EXCEPTION;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.WEBSOCKET_CONNECT_INVALID;

/**
 * CocosBcxApiWrapper
 * sdk api
 */
public class CocosBcxApiWrapper {

    private static CocosBcxApi cocosBcxApi;
    private Context context;
    private String faucetUrl;
    public static String coreAsset;
    public static String chainId;
    public static List<String> nodeUrls;
    private AccountDao accountDao;
    private String rspText = "";
    private ThreadManager.ThreadPollProxy proxy;


    private CocosBcxApiWrapper() {

    }


    private static class CocosBcxApiWrapperInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final CocosBcxApiWrapper INSTANCE = new CocosBcxApiWrapper();
    }


    /**
     * Singleton method, returns the provider of the SDk's method
     *
     * @return
     */
    public static CocosBcxApiWrapper getBcxInstance() {
        return CocosBcxApiWrapperInstanceHolder.INSTANCE;
    }


    /**
     * Initialize SDK
     *
     * @param nodeUrls  api Node
     * @param faucetUrl Address
     * @param coreAsset Chain identifier
     * @param chainId   Chain ID
     * @param callBack  Status of connection
     */
    public void init(Context context, String chainId, List<String> nodeUrls, String faucetUrl, String coreAsset, boolean isOpenLog, final IBcxCallBack callBack) {
        this.context = context;
        this.faucetUrl = faucetUrl;
        CocosBcxApiWrapper.nodeUrls = nodeUrls;
        CocosBcxApiWrapper.coreAsset = coreAsset;
        CocosBcxApiWrapper.chainId = chainId;
        // set is open log
        LogUtils.isOpenLog = isOpenLog;
        //init ThreadPool
        proxy = ThreadManager.getThreadPollProxy();
        // need to init in case some class can not use
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        // init db dao
        init_db_dao();
        //  class to deal business logic
        cocosBcxApi = CocosBcxApi.getBcxInstance();
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                // build socket connect
                build_connect(callBack);
            }
        });
    }


    /**
     * init db dao
     */
    private void init_db_dao() {
        accountDao = new AccountDao(context);
    }


    /**
     * build socket connect
     *
     * @param callBack
     */
    private synchronized void build_connect(IBcxCallBack callBack) {
        int nRet = cocosBcxApi.initialize();
        if (nRet == OPERATE_SUCCESS) {
            // success
            rspText = new ResponseData(OPERATE_SUCCESS, "Rpc connected", null).toString();
            callBack.onReceiveValue(rspText);
        } else if (nRet == ERROR_NETWORK_FAIL) {
            // failed
            rspText = new ResponseData(ERROR_NETWORK_FAIL, "Rpc connection failed", null).toString();
            callBack.onReceiveValue(rspText);
        } else if (nRet == RPC_NETWORK_EXCEPTION) {
            // network exception
            rspText = new ResponseData(RPC_NETWORK_EXCEPTION, "Rpc connection failed. please check your network", null).toString();
            callBack.onReceiveValue(rspText);
        } else if (nRet == WEBSOCKET_CONNECT_INVALID) {
            // invalid url
            rspText = new ResponseData(WEBSOCKET_CONNECT_INVALID, "Rpc connection invalid", null).toString();
            callBack.onReceiveValue(rspText);
        } else if (nRet == CHAIN_ID_NOT_MATCH) {
            //  invalid chainId
            rspText = new ResponseData(CHAIN_ID_NOT_MATCH, "The wallet chain id does not match the current chain configuration information", null).toString();
            callBack.onReceiveValue(rspText);
        } else if (nRet == OPERATE_FAILED) {
            rspText = new ResponseData(OPERATE_FAILED, "Rpc connection failed", null).toString();
            callBack.onReceiveValue(rspText);
        }
    }

    /**
     * account model  create account
     *
     * @param strAccountName accountName
     * @param strPassword    Password
     * @param isAutoLogin    true :   log in， false:just register
     * @param callBack
     * @throws CreateAccountException
     */
    public void create_password_account(final String strAccountName, final String strPassword, final boolean isAutoLogin, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CreateAccountParamEntity paramEntity = new CreateAccountParamEntity();
                    paramEntity.setAccountName(strAccountName);
                    paramEntity.setPassword(strPassword);
                    paramEntity.setAccountType(AccountType.ACCOUNT);
                    cocosBcxApi.createAccount(faucetUrl, paramEntity, isAutoLogin, accountDao, callBack);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (CreateAccountException e) {
                    rspText = new ResponseData(OPERATE_FAILED, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * wallet model  create account
     *
     * @param strAccountName accountName
     * @param strPassword    Password
     * @param isAutoLogin    true :   log in， false:just register
     * @param callBack
     * @throws CreateAccountException
     */
    public void create_wallet_account(final String strAccountName, final String strPassword, final boolean isAutoLogin, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CreateAccountParamEntity paramEntity = new CreateAccountParamEntity();
                    paramEntity.setAccountName(strAccountName);
                    paramEntity.setPassword(strPassword);
                    paramEntity.setAccountType(AccountType.WALLET);
                    cocosBcxApi.createAccount(faucetUrl, paramEntity, isAutoLogin, accountDao, callBack);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (CreateAccountException e) {
                    rspText = new ResponseData(OPERATE_FAILED, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * account model  create account
     *
     * @param strAccountName accountName
     * @param strPassword    Password
     * @param isAutoLogin    true :   log in， false:just register
     * @param isAutoLogin    accountType :   "WALLET"，"ACCOUNT"
     * @param callBack
     * @throws CreateAccountException
     */
    public void create_account(final String strAccountName, final String strPassword, final AccountType accountType, final boolean isAutoLogin, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CreateAccountParamEntity paramEntity = new CreateAccountParamEntity();
                    paramEntity.setAccountName(strAccountName);
                    paramEntity.setPassword(strPassword);
                    paramEntity.setAccountType(accountType);
                    cocosBcxApi.createAccount(faucetUrl, paramEntity, isAutoLogin, accountDao, callBack);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (CreateAccountException e) {
                    rspText = new ResponseData(OPERATE_FAILED, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * query account which log in from database then get detail account info from net
     *
     * @return
     */
    public void get_dao_account_objects(final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                List<account_object> account_objects = new ArrayList<>();
                try {
                    for (int i = 0; i < accountDao.queryAllAccountByChainId().size(); i++) {
                        account_object account_object = cocosBcxApi.lookup_account_names(accountDao.queryAllAccountByChainId().get(i).getName());
                        account_objects.add(account_object);
                    }
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", account_objects).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get account object by account id
     *
     * @param accountId
     * @param callBack
     */
    public void get_accounts(final String accountId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    account_object account_object = cocosBcxApi.get_accounts(accountId);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", account_object).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get account info
     *
     * @param account_name ：account_name
     * @return account info
     */
    public void lookup_account_names(final String account_name, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.lookup_account_names(account_name)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * param strAccountNameOrId account name or id
     *
     * @return account info
     */
    public void get_account_object(final String strAccountNameOrId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //return account info
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_account_object(strAccountNameOrId)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * param strAccountNameOrId account name or id
     *
     * @return account info
     */
    public account_object get_account_object(final String strAccountNameOrId) {
        try {
            //return account object
            return cocosBcxApi.get_account_object(strAccountNameOrId);
        } catch (NetworkStatusException e) {
            return null;
        }
    }

    /**
     * get_account_id_by_name
     *
     * @param accountName
     * @return
     */
    public String get_account_id_by_name(String accountName) {
        try {
            account_object account_object = cocosBcxApi.get_account_object(accountName);
            return account_object.id.toString();
        } catch (NetworkStatusException e) {
            return "";
        }
    }


    /**
     * get_account_name_by_id
     *
     * @param accountId
     * @return
     */
    public String get_account_name_by_id(String accountId) {
        try {
            account_object account_object = cocosBcxApi.get_account_object(accountId);
            return account_object.name;
        } catch (NetworkStatusException e) {
            return "";
        }
    }


    /**
     * get_full_accounts and subscribe
     *
     * @throws NetworkStatusException
     */
    public void get_full_accounts(final String names_or_id, final boolean subscribe, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_full_accounts(names_or_id, subscribe)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * lookup_nh_asset get NH asset detail by NH asset id
     *
     * @throws NetworkStatusException
     */
    public void lookup_nh_asset(final List<String> nh_asset_ids_or_hash, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.lookup_nh_asset(nh_asset_ids_or_hash)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NhAssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_NHASSET_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * lookup_nh_asset get NH asset by nh asset id
     *
     * @throws NetworkStatusException
     */
    public void list_account_nh_asset(final String account_id_or_name, final List<String> world_view_name_or_ids, final int page, final int pageSize, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.list_account_nh_asset(account_id_or_name, world_view_name_or_ids, page, pageSize)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * lookup_nh_asset get NH asset by nh asset id
     *
     * @throws NetworkStatusException
     */
    public void list_account_nh_asset_order(final String account_id_or_name, final int pageSize, final int page, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.list_account_nh_asset_order(account_id_or_name, pageSize, page)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * lookup_nh_asset get NH asset by asset id
     *
     * @throws NetworkStatusException
     */
    public void list_nh_asset_order(final String asset_id_or_symbol, final String world_view_name_or_ids, final String baseDescribe, final int pageSize, final int page, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.list_nh_asset_order(asset_id_or_symbol, world_view_name_or_ids, baseDescribe, pageSize, page)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * Seek World View Details
     *
     * @throws NetworkStatusException
     */
    public void lookup_world_view(final List<String> world_view_names, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.lookup_world_view(world_view_names)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (WordViewNotExistException e) {
                    rspText = new ResponseData(ERROR_WORLDVIEW_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get Developer-Related World View
     *
     * @throws NetworkStatusException
     */
    public void get_nh_creator(final String account_id_or_name, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_nh_creator(account_id_or_name)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * Query NH assets created by developers
     *
     * @throws NetworkStatusException
     */
    public void list_nh_asset_by_creator(final String account_id, final int page, final int pageSize, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.list_nh_asset_by_creator(account_id, page, pageSize)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get NH assets transfer fee
     *
     * @throws NetworkStatusException
     */
    public void transfer_nh_asset_fee(final String account_from, final String account_to, final String fee_asset_symbol, final String nh_asset_id, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.transfer_nh_asset_fee(account_from, account_to, fee_asset_symbol, nh_asset_id)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NhAssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_NHASSET_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * transfer NH assets
     *
     * @throws NetworkStatusException
     */
    public void transfer_nh_asset(final String password, final String account_from, final String account_to, final String fee_asset_symbol, final String nh_asset_id, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.transfer_nh_asset(password, account_from, account_to, fee_asset_symbol, nh_asset_id, accountDao)).toString();
                    callBack.onReceiveValue(rspText);
                    cocosBcxApi.lock();
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NhAssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_NHASSET_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get buy NH assets operate fee
     *
     * @throws NetworkStatusException
     */
    public void buy_nh_asset_fee(final String fee_paying_account, final String order_Id, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.buy_nh_asset_fee(fee_paying_account, order_Id)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NhAssetOrderNotFoundException e) {
                    rspText = new ResponseData(ERROR_ORDERS_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * buy NH assets
     *
     * @throws NetworkStatusException
     */
    public void buy_nh_asset(final String password, final String fee_paying_account, final String order_Id, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.buy_nh_asset(password, fee_paying_account, order_Id, accountDao)).toString();
                    callBack.onReceiveValue(rspText);
                    cocosBcxApi.lock();
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NhAssetOrderNotFoundException e) {
                    rspText = new ResponseData(ERROR_ORDERS_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * calculate upgrade to lifetime member fee
     *
     * @param fee_paying_asset_id_or_symbol
     * @param upgrade_account_id_or_symbol
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     * @throws NhAssetOrderNotFoundException
     * @throws UnLegalInputException
     */
    private void upgrade_to_lifetime_member_fee(final String upgrade_account_id_or_symbol, final String fee_paying_asset_id_or_symbol, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.upgrade_to_lifetime_member_fee(upgrade_account_id_or_symbol, fee_paying_asset_id_or_symbol)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * calculate upgrade to lifetime member
     *
     * @param fee_paying_asset_id_or_symbol
     * @param upgrade_account_id_or_symbol
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     * @throws NhAssetOrderNotFoundException
     * @throws UnLegalInputException
     */
    private void upgrade_to_lifetime_member(final String upgrade_account_id_or_symbol, final String upgrade_account_password, final String fee_paying_asset_id_or_symbol, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.upgrade_to_lifetime_member(upgrade_account_id_or_symbol, upgrade_account_password, fee_paying_asset_id_or_symbol, accountDao)).toString();
                    callBack.onReceiveValue(rspText);
                    cocosBcxApi.lock();
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * create child account fee
     *
     * @param child_account
     * @param child_account_password
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     * @throws NhAssetOrderNotFoundException
     * @throws UnLegalInputException
     */
    private void create_child_account_fee(final String child_account, final String child_account_password, final String registrar_account_id_or_symbol, final String pay_asset_symbol_or_id, final String accountType, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CreateAccountParamEntity paramEntity = new CreateAccountParamEntity();
                    paramEntity.setAccountName(child_account);
                    paramEntity.setPassword(child_account_password);
                    if (TextUtils.equals(AccountType.ACCOUNT.name(), accountType)) {
                        paramEntity.setAccountType(AccountType.ACCOUNT);
                    } else if (TextUtils.equals(AccountType.WALLET.name(), accountType)) {
                        paramEntity.setAccountType(AccountType.WALLET);
                    }
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.create_child_account_fee(paramEntity, registrar_account_id_or_symbol, pay_asset_symbol_or_id)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountExistException e) {
                    rspText = new ResponseData(ERROR_ACCOUNT_OBJECT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * create child account
     *
     * @param child_account
     * @param child_account_password
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     * @throws NhAssetOrderNotFoundException
     * @throws UnLegalInputException
     */
    private void create_child_account(final String child_account, final String child_account_password, final String registrar_account_id_or_symbol, final String registrar_account_password, final String pay_asset_symbol_or_id, final String accountType, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CreateAccountParamEntity paramEntity = new CreateAccountParamEntity();
                    paramEntity.setAccountName(child_account);
                    paramEntity.setPassword(child_account_password);
                    if (TextUtils.equals(AccountType.ACCOUNT.name(), accountType)) {
                        paramEntity.setAccountType(AccountType.ACCOUNT);
                    } else if (TextUtils.equals(AccountType.WALLET.name(), accountType)) {
                        paramEntity.setAccountType(AccountType.WALLET);
                    }
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.create_child_account(paramEntity, registrar_account_id_or_symbol, registrar_account_password, pay_asset_symbol_or_id, accountDao)).toString();
                    callBack.onReceiveValue(rspText);
                    cocosBcxApi.lock();
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountExistException e) {
                    rspText = new ResponseData(ERROR_ACCOUNT_OBJECT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get objects by names_or_ids
     *
     * @param ids ：ids
     * @return object
     * @throws NetworkStatusException
     */
    public void get_objects(final List<String> ids, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_objects(ids)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get  contract objects
     *
     * @param contractId ：contractId
     * @return object
     * @throws NetworkStatusException
     */
    public void get_objects(final String contractId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_objects(contractId)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get  contract objects
     *
     * @param contractNameOrId ：contractNameOrId
     * @return object
     * @throws NetworkStatusException
     */
    public void get_contract(final String contractNameOrId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    contract_object contract_object = cocosBcxApi.get_contract(contractNameOrId);
                    if (null == contract_object) {
                        rspText = new ResponseData(ERROR_CONTRACT_NOT_FOUND, "Contract does not exist", null).toString();
                        callBack.onReceiveValue(rspText);
                        return;
                    }
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", contract_object).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * delete account from database by account name
     *
     * @return delete result
     */
    public void delete_account_by_name(final String accountName, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                int deleteNumber = accountDao.deleteAccountByName(accountName);
                if (deleteNumber == 0) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, "Account does not exist", null).toString();
                    callBack.onReceiveValue(rspText);
                } else if (deleteNumber > 0) {
                    rspText = new ResponseData(OPERATE_SUCCESS, "deleted", null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * delete account from database by account id
     *
     * @return delete result
     */
    public void delete_account_by_id(final String accountId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                int deleteNumber = accountDao.deleteAccountById(accountId);
                if (deleteNumber == 0) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, "Account does not exist", null).toString();
                    callBack.onReceiveValue(rspText);
                } else if (deleteNumber > 0) {
                    rspText = new ResponseData(OPERATE_SUCCESS, "deleted", null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * query account names from db
     * has already filter by chainid
     *
     * @return
     */
    public List<String> get_dao_account_names() {
        return accountDao.queryAccountNamesByChainId();
    }


    /**
     * query account by name from db
     *
     * @return
     */
    public AccountEntity.AccountBean get_dao_account_by_name(String account_name) {
        return accountDao.queryAccountByName(account_name);
    }


    /**
     * account model  log in by account name and password
     *
     * @param strAccountName
     * @param strPassword
     */
    public void password_login(final String strAccountName, final String strPassword, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    cocosBcxApi.password_login(strAccountName, strPassword, accountDao, callBack);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * import keystore
     *
     * @param keystore is json string the data type must as some as you exported;
     * @param password
     */
    public void import_keystore(final String keystore, final String password, final String accountType, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                cocosBcxApi.import_keystore(keystore, password, accountType, accountDao, callBack);
            }
        });
    }


    /**
     * export keystore
     *
     * @param accountName
     * @param password
     * @param callBack    you can get the keystore of the account you input,you can read the keystore in file to save .
     */
    public void export_keystore(final String accountName, final String password, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                cocosBcxApi.export_keystore(accountName, password, accountDao, callBack);
            }
        });
    }


    /**
     * import private key
     *
     * @param wifKey   private key
     * @param password to encrypt your private key,
     */
    public void import_wif_key(final String wifKey, final String password, final String accountType, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> result = cocosBcxApi.import_wif_key(wifKey, password, accountType, accountDao);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", result).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(NO_ACCOUNT_INFORMATION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * export private key
     *
     * @param accountName account name
     * @param password    the key you set to encrypt your private key;
     * @return
     */
    public void export_private_key(final String accountName, final String password, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                cocosBcxApi.export_private_key(accountName, password, accountDao, callBack);
            }
        });
    }


    /**
     * get asset object by asset symbol
     *
     * @param assetsSymbolOrId ：asset symbol or id
     * @return asset object
     * @throws NetworkStatusException
     */
    public void lookup_asset_symbols(final String assetsSymbolOrId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.lookup_asset_symbols(assetsSymbolOrId)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * get asset object by asset symbol
     *
     * @param assetsSymbolOrId ：asset symbol or asset id
     * @return asset object
     * @throws NetworkStatusException
     */
    public asset_object get_asset_object(final String assetsSymbolOrId) {
        try {
            return cocosBcxApi.lookup_asset_symbols(assetsSymbolOrId);
        } catch (NetworkStatusException e) {
            return null;
        }
    }


    /**
     * calculate transfer fee
     *
     * @param password         password
     * @param from             account from
     * @param to               account to
     * @param strAmount        transfer amount
     * @param strAssetSymbol   tranfer asset symbol
     * @param strFeeSymbolOrId tranfer asset fee symbol
     * @param strMemo          memo
     * @param callBack
     */
    public void transfer_calculate_fee(final String password, final String from, final String to, final String strAmount, final String strAssetSymbol, final String strFeeSymbolOrId, final String strMemo, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<asset_fee_object> requiredFees = cocosBcxApi.calculate_transfer_fee(password, from, to, strAmount, strAssetSymbol, strFeeSymbolOrId, strMemo, accountDao);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", requiredFees.get(0)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * calculate invoking contract fee
     *
     * @param strAccount
     * @param feeAssetSymbol
     * @param contractId
     * @param functionName
     * @param params
     * @param callBack
     */
    public void calculate_invoking_contract_fee(final String strAccount, final String feeAssetSymbol, final String contractId, final String functionName, final String params, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<asset_fee_object> requiredFees = cocosBcxApi.calculate_invoking_contract_fee(strAccount, feeAssetSymbol, contractId, functionName, params);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", requiredFees.get(0)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    // not found account
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (ContractNotFoundException e) {
                    rspText = new ResponseData(ERROR_CONTRACT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * calculate invoking contract fee
     *
     * @param strAccount
     * @param feeAssetSymbol
     * @param contractNameOrId
     * @param functionName
     * @param params
     * @param callBack
     */
    public void invoking_contract(final String strAccount, final String password, final String feeAssetSymbol, final String contractNameOrId, final String functionName, final String params, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String hash = cocosBcxApi.invoking_contract(strAccount, password, feeAssetSymbol, contractNameOrId, functionName, params, accountDao);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", hash).toString();
                    callBack.onReceiveValue(rspText);
                    cocosBcxApi.lock();
                } catch (AccountNotFoundException e) {
                    // not found account
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (ContractNotFoundException e) {
                    rspText = new ResponseData(ERROR_CONTRACT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * transfer
     *
     * @param strFrom
     * @param strTo
     * @param strAmount
     * @param strAssetSymbol
     * @param strMemo
     * @return
     * @throws NetworkStatusException
     */
    public void transfer(final String password, final String strFrom, final String strTo, final String strAmount, final String strAssetSymbol, final String strFeeSymbol, final String strMemo, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String hash = cocosBcxApi.transfer(password, strFrom, strTo, strAmount, strAssetSymbol, strFeeSymbol, strMemo, accountDao);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", hash).toString();
                    callBack.onReceiveValue(rspText);
                    cocosBcxApi.lock();
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get account operate history
     *
     * @param accountName
     * @param nLimit
     * @return
     * @throws NetworkStatusException
     */
    public void get_account_history(final String accountName, final int nLimit, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                List<operation_history_object> listHistoryObject = null;
                try {
                    listHistoryObject = cocosBcxApi.get_account_history(accountName, nLimit);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", listHistoryObject).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get all assets balances of account
     *
     * @param accountId
     * @return account balance
     * @throws NetworkStatusException
     */
    public void get_all_account_balances(final String accountId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<asset_fee_object> assets = cocosBcxApi.get_all_account_balances(accountId);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", assets).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * get_account_balances。
     *
     * @param accountId
     * @param assetsId
     * @return account balance
     * @throws NetworkStatusException
     */
    public void get_account_balances(final String accountId, final String assetsId, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    asset_fee_object assets = cocosBcxApi.get_account_balances(accountId, assetsId);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", assets).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (UnLegalInputException e) {
                    rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get block header。
     *
     * @throws NetworkStatusException
     */
    public void get_block_header(final double nBlockNumber, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_block_header(nBlockNumber)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * search  current global_property_object
     *
     * @return
     * @throws NetworkStatusException
     */
    public void get_global_properties(final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_global_properties()).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * search  current dynamic_global_property_object
     *
     * @return
     * @throws NetworkStatusException
     */
    public void get_dynamic_global_properties(final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_dynamic_global_properties()).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * decrypt memo
     *
     * @param mMemoJson
     * @return
     */
    public void decrypt_memo_message(final String accountName, final String password, final String mMemoJson, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                cocosBcxApi.decrypt_memo_message(accountName, password, mMemoJson, accountDao, callBack);
            }
        });
    }

    /**
     * generate Payment QrCode json string
     * use this json to generate Payment Two-Dimensional Code
     *
     * @return string
     */
    public String get_payment_qrcode_json(String accountName, String amount, String assetSymbol) {
        String message = "{\"address\":\"%s\",\"amount\":\"%s\",\"symbol\":\"%s\"}";
        return String.format(message, accountName, amount, assetSymbol);
    }


    /**
     * Get SDK's version
     */
    public String get_version_info() {
        return VersionManager.getVersionInfo();
    }


    /**
     * log out
     */
    public void log_out() {
        cocosBcxApi.log_out();
    }

}
