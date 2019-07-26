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
import com.cocos.bcx_sdk.bcx_error.KeyInvalideException;
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
import com.cocos.bcx_sdk.bcx_wallet.chain.block_info;
import com.cocos.bcx_sdk.bcx_wallet.chain.contract_callback;
import com.cocos.bcx_sdk.bcx_wallet.chain.contract_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.contract_operations;
import com.cocos.bcx_sdk.bcx_wallet.chain.global_config_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.nh_asset_order_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.nhasset_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.object_id;
import com.cocos.bcx_sdk.bcx_wallet.chain.operation_history_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.operation_results_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.operations;
import com.cocos.bcx_sdk.bcx_wallet.chain.transaction_in_block_info;
import com.cocos.bcx_sdk.bcx_wallet.chain.transactions_object;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import org.bitcoinj.core.AddressFormatException;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cocos.bcx_sdk.bcx_error.ErrorCode.AUTHORITY_EXCEPTION;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.CHAIN_ID_NOT_MATCH;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_ACCOUNT_OBJECT_EXIST;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_CONTRACT_NOT_FOUND;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_INVALID_PRIVATE_KEY;
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
     */
    public void init(Context context) {
        //init ThreadPool
        proxy = ThreadManager.getThreadPollProxy();
        // need to init in case some class can not use
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        // init db dao
        accountDao = new AccountDao(context);
        //  class to deal business logic
        cocosBcxApi = CocosBcxApi.getBcxInstance();
    }

    /**
     * connect server
     *
     * @param nodeUrls  api Node
     * @param faucetUrl Address
     * @param coreAsset Chain identifier
     * @param chainId   Chain ID
     * @param callBack  Status of connection
     */
    public void connect(Context context, String chainId, List<String> nodeUrls, String faucetUrl, String coreAsset, boolean isOpenLog, final IBcxCallBack callBack) {
        this.context = context;
        this.faucetUrl = faucetUrl;
        CocosBcxApiWrapper.nodeUrls = nodeUrls;
        CocosBcxApiWrapper.coreAsset = coreAsset;
        CocosBcxApiWrapper.chainId = chainId;
        // set is open log
        LogUtils.isOpenLog = isOpenLog;
        if (null == proxy || null == accountDao || null == cocosBcxApi) {
            init(context);
        }
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                // build socket connect
                build_connect(callBack);
            }
        });
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
    public account_object get_account_object(final String strAccountNameOrId) throws NullPointerException {
        try {
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
     * lookup_nh_asset get NH asset detail by NH asset id
     *
     * @throws NetworkStatusException
     */
    public nhasset_object lookup_nh_asset_object(final String nh_asset_ids_or_hash) {
        try {
            return cocosBcxApi.lookup_nh_asset_object(nh_asset_ids_or_hash);
        } catch (NetworkStatusException e) {
            return null;
        } catch (NhAssetNotFoundException e) {
            return null;
        }
    }

    /**
     * get nh asset order object
     *
     * @throws NetworkStatusException
     */
    public nh_asset_order_object get_nhasset_order_object(final String nh_asset_order_ids) {
        try {
            return cocosBcxApi.get_nhasset_order_object(nh_asset_order_ids);
        } catch (NetworkStatusException e) {
            return null;
        }
    }


    /**
     * get nh asset order object
     *
     * @throws NetworkStatusException
     */
    public List<asset_object> list_assets(String strLowerBound, int nLimit) {
        try {
            return cocosBcxApi.list_assets(strLowerBound, nLimit);
        } catch (NetworkStatusException e) {
            return new ArrayList<>();
        }
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
     * list nh asset order no filter options
     *
     * @throws NetworkStatusException
     */
    public void list_nh_asset_order(final int page, final int pageSize, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.list_nh_asset_order(page, pageSize)).toString();
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
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.transfer_nh_asset_fee(account_from, account_to, fee_asset_symbol, nh_asset_id).get(0)).toString();
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
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get delete nhasset fee
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public void delete_nh_asset_fee(final String fee_paying_account, final String nhasset_id, final String fee_symbol, final IBcxCallBack callBack) {

        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.delete_nh_asset_fee(fee_paying_account, nhasset_id, fee_symbol).get(0)).toString();
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
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * delete nhasset
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public void delete_nh_asset(final String fee_paying_account, final String password, final String nhasset_id, final String fee_symbol, final IBcxCallBack callBack) {

        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.delete_nh_asset(fee_paying_account, password, nhasset_id, fee_symbol, accountDao)).toString();
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
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get cancel nhasset order fee
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public void cancel_nh_asset_order_fee(final String fee_paying_account, final String order_id, final String fee_symbol, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.cancel_nh_asset_order_fee(fee_paying_account, order_id, fee_symbol).get(0)).toString();
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
                } catch (NhAssetOrderNotFoundException e) {
                    rspText = new ResponseData(ERROR_ORDERS_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * cancel nhasset order
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public void cancel_nh_asset_order(final String fee_paying_account, final String password, final String order_id, final String fee_symbol, final IBcxCallBack callBack) {

        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.cancel_nh_asset_order(fee_paying_account, password, order_id, fee_symbol, accountDao)).toString();
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
                } catch (NhAssetOrderNotFoundException e) {
                    rspText = new ResponseData(ERROR_ORDERS_DO_NOT_EXIST, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
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
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.buy_nh_asset_fee(fee_paying_account, order_Id).get(0)).toString();
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
                } catch (NhAssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_NHASSET_DO_NOT_EXIST, e.getMessage(), null).toString();
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
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * create nh asset order fee
     *
     * @param pending_order_nh_asset
     * @param pending_order_fee
     * @param pending_order_memo
     * @param pending_order_price
     * @param pending_order_price_symbol
     * @return
     * @throws NetworkStatusException
     * @throws NhAssetNotFoundException
     */
    public void create_nh_asset_order_fee(final String otcaccount, final String seller, final String pending_order_nh_asset, final String pending_order_fee, final String pending_order_fee_symbol, final String pending_order_memo, final String pending_order_price, final String pending_order_price_symbol, final long pending_order_valid_time_millis, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.create_nh_asset_order_fee(otcaccount, seller, pending_order_nh_asset, pending_order_fee, pending_order_fee_symbol, pending_order_memo, pending_order_price, pending_order_price_symbol, pending_order_valid_time_millis).get(0)).toString();
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
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (ParseException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }

    /**
     * create nh asset order
     *
     * @param pending_order_nh_asset
     * @param pending_order_fee
     * @param pending_order_memo
     * @param pending_order_price
     * @param pending_order_price_symbol
     * @return
     * @throws NetworkStatusException
     * @throws NhAssetNotFoundException
     */
    public void create_nh_asset_order(final String otcaccount, final String seller, final String password, final String pending_order_nh_asset, final String pending_order_fee, final String pending_order_fee_symbol, final String pending_order_memo, final String pending_order_price, final String pending_order_price_symbol, final long pending_order_valid_time_millis, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.create_nh_asset_order(otcaccount, seller, password, accountDao, pending_order_nh_asset, pending_order_fee, pending_order_fee_symbol, pending_order_memo, pending_order_price, pending_order_price_symbol, pending_order_valid_time_millis)).toString();
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
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (PasswordVerifyException e) {
                    rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (ParseException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
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
    public void upgrade_to_lifetime_member_fee(final String upgrade_account_id_or_symbol, final String fee_paying_asset_id_or_symbol, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.upgrade_to_lifetime_member_fee(upgrade_account_id_or_symbol, fee_paying_asset_id_or_symbol).get(0)).toString();
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
    public void upgrade_to_lifetime_member(final String upgrade_account_id_or_symbol, final String upgrade_account_password, final String fee_paying_asset_id_or_symbol, final IBcxCallBack callBack) {
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
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
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
    public void create_child_account_fee(final String child_account, final String child_account_password, final String registrar_account_id_or_symbol, final String pay_asset_symbol_or_id, final String accountType, final IBcxCallBack callBack) {
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
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.create_child_account_fee(paramEntity, registrar_account_id_or_symbol, pay_asset_symbol_or_id).get(0)).toString();
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
    public void create_child_account(final String child_account, final String child_account_password, final String registrar_account_id_or_symbol, final String registrar_account_password, final String pay_asset_symbol_or_id, final String accountType, final IBcxCallBack callBack) {
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
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
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
     * get object
     *
     * @param id ：
     * @return object
     * @throws NetworkStatusException
     */
    public void get_objects(final String id, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_objects(id)).toString();
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
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
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
     * @param from             account from
     * @param to               account to
     * @param strAmount        transfer amount
     * @param strAssetSymbol   tranfer asset symbol
     * @param strFeeSymbolOrId tranfer asset fee symbol
     * @param strMemo          memo
     * @param callBack
     */
    public void transfer_calculate_fee(final String from, final String to, final String strAmount, final String strAssetSymbol, final String strFeeSymbolOrId, final String strMemo, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<asset_fee_object> requiredFees = cocosBcxApi.calculate_transfer_fee(from, to, strAmount, strAssetSymbol, strFeeSymbolOrId, strMemo);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", requiredFees.get(0)).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AccountNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AssetNotFoundException e) {
                    rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AuthorityException e) {
                    rspText = new ResponseData(AUTHORITY_EXCEPTION, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
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
    private void get_invoking_contract_tx_id(final String strAccount, final String password, final String feeAssetSymbol, final String contractNameOrId, final String functionName, final String params, final IBcxCallBack callBack) {
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
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * @param strAccount
     * @param password
     * @param feeAssetSymbol
     * @param contractNameOrId
     * @param functionName
     * @param params
     * @param callBack
     */
    public void invoking_contract(final String strAccount, final String password, final String feeAssetSymbol, final String contractNameOrId, final String functionName, final String params, final IBcxCallBack callBack) {

        get_invoking_contract_tx_id(strAccount, password, feeAssetSymbol, contractNameOrId, functionName, params, new IBcxCallBack() {

            private object_id<account_object> caller = null;

            @Override
            public void onReceiveValue(String text) {
                try {
                    ResponseData baseResult = global_config_object.getInstance().getGsonBuilder().create().fromJson(text, ResponseData.class);
                    if (baseResult.getCode() != 1) {
                        rspText = new ResponseData(baseResult.getCode(), baseResult.getMessage(), null).toString();
                        callBack.onReceiveValue(rspText);
                        return;
                    }
                    transaction_in_block_info transactionInBlockInfo = null;
                    long startTime = System.currentTimeMillis();
                    long endTime;
                    do {
                        endTime = System.currentTimeMillis();
                        if (endTime - startTime > 7000) {
                            rspText = new ResponseData(OPERATE_FAILED, "operate failed", null).toString();
                            callBack.onReceiveValue(rspText);
                            return;
                        }
                        transactionInBlockInfo = cocosBcxApi.get_transaction_in_block_info(baseResult.getData().toString());
                    } while (transactionInBlockInfo == null);
                    final transaction_in_block_info finalTransactionInBlockInfo = transactionInBlockInfo;
                    block_info block = cocosBcxApi.get_block(String.valueOf(transactionInBlockInfo.getBlock_num()));
                    contract_callback contractCallback = new contract_callback();
                    contractCallback.code = 1;
                    contract_callback.TrxDataBean trxData = new contract_callback.TrxDataBean();
                    trxData.trx_id = finalTransactionInBlockInfo.getTrx_hash();
                    trxData.block_num = finalTransactionInBlockInfo.getBlock_num();
                    contractCallback.trx_data = trxData;
                    List<contract_callback.DataBean> data = new ArrayList<>();
                    List<contract_callback.DataBean.ContractAffectedsBean> contract_affecteds = new ArrayList<>();
                    contract_callback.DataBean dataBean = new contract_callback.DataBean();
                    HashMap<String, transactions_object> transactions = block.transactions;
                    for (Map.Entry<String, transactions_object> transaction : transactions.entrySet()) {
                        transactions_object transactions_object = transaction.getValue();
                        String transactionKey = transaction.getKey();
                        for (Map.Entry<Integer, contract_operations> operation : transactions_object.operations.entrySet()) {
                            Integer operationType = operation.getKey();
                            contract_operations operationValue = operation.getValue();
                            if (operationType == operations.ID_CALCULATE_INVOKING_CONTRACT_OPERATION) {
                                dataBean.contract_id = operationValue.contract_id;
                                caller = operationValue.caller;
                            }
                        }
                        for (Map.Entry<Integer, operation_results_object> operation_results : transactions_object.operation_results.entrySet()) {
                            operation_results_object operationValue = operation_results.getValue();
                            if (TextUtils.equals(trxData.trx_id, transactionKey)) {
                                dataBean.real_running_time = operationValue.real_running_time;
                                dataBean.process_value = operationValue.process_value;
                                dataBean.existed_pv = operationValue.existed_pv;
                                dataBean.additional_cost = operationValue.additional_cost;

                                for (Object o : operationValue.contract_affecteds) {
                                    ArrayList arrayList = (ArrayList) o;
                                    double type = (double) arrayList.get(0);
                                    LinkedTreeMap linkedTreeMap = (LinkedTreeMap) arrayList.get(1);
                                    String affected_account = (String) linkedTreeMap.get("affected_account");
                                    contract_callback.DataBean.ContractAffectedsBean.RawDataBean rawDataBean = new contract_callback.DataBean.ContractAffectedsBean.RawDataBean();
                                    contract_callback.DataBean.ContractAffectedsBean.RawDataBean.AffectedAssetBean affectedAssetBean = new contract_callback.DataBean.ContractAffectedsBean.RawDataBean.AffectedAssetBean();
                                    contract_callback.DataBean.ContractAffectedsBean contractAffectedsBean = new contract_callback.DataBean.ContractAffectedsBean();
                                    contract_callback.DataBean.ContractAffectedsBean.ResultBean resultBean = new contract_callback.DataBean.ContractAffectedsBean.ResultBean();
                                    contractAffectedsBean.block_num = trxData.block_num;
                                    contractAffectedsBean.type_name = contractAffectedsBean.getTypeName(type, -11d);
                                    contractAffectedsBean.type = contractAffectedsBean.getType(type, -11d);
                                    rawDataBean.affected_account = affected_account;
                                    String accountName = get_account_name_by_id(affected_account);
                                    resultBean.affected_account = accountName;
                                    if (type == 0) {
                                        LinkedTreeMap affected_asset = (LinkedTreeMap) linkedTreeMap.get("affected_asset");
                                        Double amount = (Double) affected_asset.get("amount");
                                        String asset_id = (String) affected_asset.get("asset_id");
                                        affectedAssetBean.amount = amount;
                                        affectedAssetBean.asset_id = asset_id;
                                        rawDataBean.affected_asset = affectedAssetBean;
                                        asset_object asset_object = get_asset_object(asset_id);
                                        resultBean.aseet_amount = (amount > 0 ? "+" : "") + amount / (Math.pow(10, asset_object.precision)) + asset_object.symbol;
                                        contractAffectedsBean.raw_data = rawDataBean;
                                        contractAffectedsBean.result = resultBean;
                                        contractAffectedsBean.result_text = accountName + " " + resultBean.aseet_amount;
                                    } else if (type == 1) {
                                        Double action = (Double) linkedTreeMap.get("action");
                                        String affected_item = (String) linkedTreeMap.get("affected_item");
                                        contractAffectedsBean.type_name = contractAffectedsBean.getTypeName(type, action);
                                        contractAffectedsBean.type = contractAffectedsBean.getType(type, action);
                                        rawDataBean.affected_item = affected_item;
                                        rawDataBean.action = action;
                                        contractAffectedsBean.raw_data = rawDataBean;
                                        resultBean.affected_item = affected_item;
                                        contractAffectedsBean.result = resultBean;
                                        if (action == 0) {
                                            contractAffectedsBean.result_text = accountName + "的NH资产 " + affected_item + " 转出";
                                        } else if (action == 1) {
                                            contractAffectedsBean.result_text = "NH资产 " + affected_item + " 转入 " + accountName;
                                        } else if (action == 2) {
                                            List<String> modifieds = (List<String>) linkedTreeMap.get("modified");
                                            contractAffectedsBean.raw_data.modified = modifieds;
                                            JsonObject stringHashMap = new JsonObject();
                                            stringHashMap.addProperty(modifieds.get(0), modifieds.get(1));
                                            contractAffectedsBean.result.modified = stringHashMap.toString();
                                            contractAffectedsBean.result_text = accountName + "的NH资产 " + affected_item + " 修改数据 ";
                                        } else if (action == 3) {
                                            contractAffectedsBean.result_text = accountName + " 创建了NH资产 " + affected_item;
                                        } else if (action == 4) {
                                            if (null == caller) {
                                                contractAffectedsBean.result_text = accountName + " 拥有NH资产 " + affected_item;
                                            } else {
                                                String callerName = get_account_name_by_id(caller.toString());
                                                contractAffectedsBean.result_text = callerName + " 创建了NH资产 " + affected_item + ",该拥有者是 " + accountName;
                                            }
                                        }
                                        contractAffectedsBean.result_text = action == 0 ? accountName + " 的NH资产 " + affected_item + " 转出" : "NH资产 " + affected_item + " 转入 " + accountName;
                                    } else if (type == 2) {


                                    } else if (type == 3) {
                                        String message = (String) linkedTreeMap.get("message");
                                        rawDataBean.message = message;
                                        resultBean.message = message;
                                        contractAffectedsBean.raw_data = rawDataBean;
                                        contractAffectedsBean.result = resultBean;
                                        contractAffectedsBean.result_text = affected_account + " " + message;
                                    }
                                    contract_affecteds.add(contractAffectedsBean);
                                }
                            }
                        }
                    }
                    dataBean.contract_affecteds = contract_affecteds;
                    data.add(dataBean);
                    contractCallback.data = data;
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", contractCallback).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(OPERATE_FAILED, e.getMessage(), null).toString();
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
                } catch (KeyInvalideException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                } catch (AddressFormatException e) {
                    rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get account operate history
     *
     * @param nBlockNumber
     * @return
     * @throws NetworkStatusException
     */
    public void get_block(final String nBlockNumber, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    block_info block = cocosBcxApi.get_block(nBlockNumber);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", block).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
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
     * search contract
     *
     * @param contractNameOrId
     * @return
     * @throws ContractNotFoundException
     * @throws NetworkStatusException
     */
    public contract_object get_contract_object(String contractNameOrId) throws ContractNotFoundException, NetworkStatusException {
        contract_object contract_object = cocosBcxApi.get_contract(contractNameOrId);
        if (null == contract_object) {
            throw new ContractNotFoundException("contract does not exist!");
        }
        return contract_object;
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
     * get transaction in block info
     *
     * @throws NetworkStatusException
     */
    public void get_transaction_in_block_info(final String tr_id, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    transaction_in_block_info transactionInBlockInfo = cocosBcxApi.get_transaction_in_block_info(tr_id);
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", transactionInBlockInfo).toString();
                    callBack.onReceiveValue(rspText);
                } catch (NetworkStatusException e) {
                    rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        });
    }


    /**
     * get transaction by tx_id
     *
     * @throws NetworkStatusException
     */
    public void get_transaction_by_id(final String tr_id, final IBcxCallBack callBack) {
        proxy.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    rspText = new ResponseData(OPERATE_SUCCESS, "success", cocosBcxApi.get_transaction_by_id(tr_id)).toString();
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
