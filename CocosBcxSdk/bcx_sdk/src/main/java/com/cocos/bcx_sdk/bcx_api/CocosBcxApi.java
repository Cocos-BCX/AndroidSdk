package com.cocos.bcx_sdk.bcx_api;


import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcx_sdk.bcx_callback.ResponseData;
import com.cocos.bcx_sdk.bcx_entity.AccountEntity;
import com.cocos.bcx_sdk.bcx_entity.AccountType;
import com.cocos.bcx_sdk.bcx_entity.CreateAccountParamEntity;
import com.cocos.bcx_sdk.bcx_entity.CreateAccountRequestParamsEntity;
import com.cocos.bcx_sdk.bcx_error.AccountExistException;
import com.cocos.bcx_sdk.bcx_error.AccountNotFoundException;
import com.cocos.bcx_sdk.bcx_error.AssetNotFoundException;
import com.cocos.bcx_sdk.bcx_error.AuthorityException;
import com.cocos.bcx_sdk.bcx_error.ContractNotFoundException;
import com.cocos.bcx_sdk.bcx_error.CreateAccountException;
import com.cocos.bcx_sdk.bcx_error.KeyInvalideException;
import com.cocos.bcx_sdk.bcx_error.NetworkStatusException;
import com.cocos.bcx_sdk.bcx_error.NhAssetNotFoundException;
import com.cocos.bcx_sdk.bcx_error.NotAssetCreatorException;
import com.cocos.bcx_sdk.bcx_error.OrderNotFoundException;
import com.cocos.bcx_sdk.bcx_error.PasswordVerifyException;
import com.cocos.bcx_sdk.bcx_error.UnLegalInputException;
import com.cocos.bcx_sdk.bcx_error.WordViewNotExistException;
import com.cocos.bcx_sdk.bcx_log.LogUtils;
import com.cocos.bcx_sdk.bcx_server.ConnectServer;
import com.cocos.bcx_sdk.bcx_sql.dao.AccountDao;
import com.cocos.bcx_sdk.bcx_wallet.authority1;
import com.cocos.bcx_sdk.bcx_wallet.chain.account_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.account_related_word_view_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.asset_fee_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.asset_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.block_header;
import com.cocos.bcx_sdk.bcx_wallet.chain.block_info;
import com.cocos.bcx_sdk.bcx_wallet.chain.contract_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.create_account_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.dynamic_global_property_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.feed_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.fill_order_history_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.global_config_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.global_property_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.limit_orders_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.market_history_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.memo_data;
import com.cocos.bcx_sdk.bcx_wallet.chain.nh_asset_order_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.nhasset_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.object_id;
import com.cocos.bcx_sdk.bcx_wallet.chain.operation_history_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.operations;
import com.cocos.bcx_sdk.bcx_wallet.chain.private_key;
import com.cocos.bcx_sdk.bcx_wallet.chain.public_key;
import com.cocos.bcx_sdk.bcx_wallet.chain.settle_price_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.signed_operate;
import com.cocos.bcx_sdk.bcx_wallet.chain.transaction_in_block_info;
import com.cocos.bcx_sdk.bcx_wallet.chain.types;
import com.cocos.bcx_sdk.bcx_wallet.chain.world_view_object;
import com.cocos.bcx_sdk.bcx_wallet.fc.crypto.aes;
import com.cocos.bcx_sdk.bcx_wallet.fc.crypto.sha256_object;
import com.cocos.bcx_sdk.bcx_wallet.fc.crypto.sha512_object;
import com.cocos.bcx_sdk.bcx_wallet.fc.io.base_encoder;
import com.cocos.bcx_sdk.bcx_wallet.fc.io.data_stream_encoder;
import com.cocos.bcx_sdk.bcx_wallet.fc.io.data_stream_size_encoder;
import com.cocos.bcx_sdk.bcx_wallet.fc.io.raw_type;
import com.google.common.primitives.UnsignedInteger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bitcoinj.core.AddressFormatException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.cocos.bcx_sdk.bcx_error.ErrorCode.CHAIN_ID_NOT_MATCH;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_ACCOUNT_OBJECT_EXIST;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_INVALID_PRIVATE_KEY;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_NETWORK_FAIL;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_OBJECT_NOT_FOUND;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_PARAMETER;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_PARAMETER_DATA_TYPE;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_UNLOCK_ACCOUNT;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.ERROR_WRONG_PASSWORD;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.OPERATE_FAILED;
import static com.cocos.bcx_sdk.bcx_error.ErrorCode.OPERATE_SUCCESS;


/**
 * CocosBcxApi
 * <p>
 * this class is between rpc api and sdk api
 * most business logic write here,
 */
public class CocosBcxApi {


    private ConnectServer mWebSocketApi;
    private String rspText;
    private HashMap<types.public_key_type, types.private_key_type> mHashMapPub2Private = new HashMap<>();
    private wallet_object mWalletObject = new wallet_object();
    private sha512_object mCheckSum;
    private String active_key_auths;
    private String owner_key_auths;


    private CocosBcxApi() {
        mWebSocketApi = ConnectServer.getBcxWebServerInstance();
    }


    private static class CocosBcxApiInstanceHolder {
        static final CocosBcxApi INSTANCE = new CocosBcxApi();
    }


    public static CocosBcxApi getBcxInstance() {
        return CocosBcxApiInstanceHolder.INSTANCE;
    }


    class wallet_object {
        sha256_object chain_id;
        List<account_object> my_accounts = new ArrayList<>();
        ByteBuffer cipher_keys;
        List<Object> extra_keys = new ArrayList<>();

        void update_account(account_object accountObject, object_id<account_object> id, List<types.public_key_type> listPublicKeyType) {
            my_accounts.clear();
            extra_keys.clear();
            my_accounts.add(accountObject);
            List<Object> extra_keys2 = new ArrayList<>();
            extra_keys2.add(id);
            extra_keys2.add(listPublicKeyType);
            extra_keys.add(extra_keys2);
        }
    }


    static class plain_keys {
        Map<types.public_key_type, String> keys;
        sha512_object checksum;

        void write_to_encoder(base_encoder encoder) {
            raw_type rawType = new raw_type();
            rawType.pack(encoder, UnsignedInteger.fromIntBits(keys.size()));
            for (Map.Entry<types.public_key_type, String> entry : keys.entrySet()) {
                encoder.write(entry.getKey().key_data);
                byte[] byteValue = entry.getValue().getBytes();
                rawType.pack(encoder, UnsignedInteger.fromIntBits(byteValue.length));
                encoder.write(byteValue);
            }
            encoder.write(checksum.hash);
        }

        static plain_keys from_input_stream(InputStream inputStream) {
            plain_keys keysResult = new plain_keys();
            keysResult.keys = new HashMap<>();
            keysResult.checksum = new sha512_object();
            raw_type rawType = new raw_type();
            UnsignedInteger size = rawType.unpack(inputStream);
            try {
                for (int i = 0; i < size.longValue(); ++i) {
                    types.public_key_type publicKeyType = new types.public_key_type();
                    inputStream.read(publicKeyType.key_data);
                    UnsignedInteger strSize = rawType.unpack(inputStream);
                    byte[] byteBuffer = new byte[strSize.intValue()];
                    inputStream.read(byteBuffer);
                    String strPrivateKey = new String(byteBuffer);
                    keysResult.keys.put(publicKeyType, strPrivateKey);
                }
                inputStream.read(keysResult.checksum.hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return keysResult;
        }
    }


    /**
     * initialize server build
     *
     * @return code
     */
    int initialize() {
        int nRet = mWebSocketApi.connect();
        if (nRet == OPERATE_SUCCESS) {
            sha256_object sha256Object = null;
            try {
                sha256Object = mWebSocketApi.get_chain_id();
                mWalletObject.chain_id = sha256Object;
                if (CocosBcxApiWrapper.chainId != null && !CocosBcxApiWrapper.chainId.equals(sha256Object.toString())) {
                    nRet = CHAIN_ID_NOT_MATCH;
                    return nRet;
                }
            } catch (NetworkStatusException e) {
                nRet = ERROR_NETWORK_FAIL;
            }
        }
        return nRet;
    }


    /**
     * create account
     *
     * @param faucetUrl
     * @param paramEntity paramEntity
     * @param isAutoLogin true :   log in， false:just register
     * @param accountDao
     * @param callBack
     * @throws CreateAccountException
     * @throws NetworkStatusException
     */
    public void createAccount(String faucetUrl, CreateAccountParamEntity paramEntity, boolean isAutoLogin, AccountDao accountDao, IBcxCallBack callBack) throws NetworkStatusException, CreateAccountException, UnLegalInputException {
        private_key privateActiveKey = private_key.from_seed(paramEntity.getActiveSeed());
        private_key privateOwnerKey = private_key.from_seed(paramEntity.getOwnerSeed());
        types.public_key_type publicActiveKeyType = new types.public_key_type(privateActiveKey.get_public_key());
        types.public_key_type publicOwnerKeyType = new types.public_key_type(privateOwnerKey.get_public_key());
        account_object accountObject = lookup_account_names(paramEntity.getAccountName());
        if (accountObject != null) {
            rspText = new ResponseData(ERROR_ACCOUNT_OBJECT_EXIST, "Account already exist", null).toString();
            callBack.onReceiveValue(rspText);
            return;
        }
        CreateAccountRequestParamsEntity.CreateAccountParams createAccountRequestParamsEntitys = new CreateAccountRequestParamsEntity.CreateAccountParams();
        createAccountRequestParamsEntitys.name = paramEntity.getAccountName();
        createAccountRequestParamsEntitys.active_key = publicActiveKeyType;
        createAccountRequestParamsEntitys.owner_key = publicOwnerKeyType;
        createAccountRequestParamsEntitys.memo_key = publicActiveKeyType;
        createAccountRequestParamsEntitys.refcode = null;
        createAccountRequestParamsEntitys.referrer = "";

        CreateAccountRequestParamsEntity createAccountRequestParamsEntity = new CreateAccountRequestParamsEntity();
        createAccountRequestParamsEntity.account = createAccountRequestParamsEntitys;
        Gson gson = global_config_object.getInstance().getGsonBuilder().create();
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(createAccountRequestParamsEntity));
        Request request = new Request.Builder().url(faucetUrl + "/api/v1/accounts").header("Accept", "application/json").addHeader("Authorization", "YnVmZW5nQDIwMThidWZlbmc=").post(requestBody).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String strResponse = response.body().string();
            if (response.isSuccessful()) {
                // parse create account data model
                create_account_object createAccountObject = global_config_object.getInstance().getGsonBuilder().create().fromJson(strResponse, create_account_object.class);

                if (isAutoLogin) {
                    long startTime = System.currentTimeMillis();
                    long endTime;
                    do {
                        accountObject = lookup_account_names(createAccountObject.getData().getAccount().getName());
                        endTime = System.currentTimeMillis();
                        if (endTime - startTime > 6000) {
                            rspText = new ResponseData(OPERATE_FAILED, "operate failed", null).toString();
                            callBack.onReceiveValue(rspText);
                            return;
                        }
                    } while (accountObject == null);

                    // get account object
                    //prepare data to store keystore
                    List<types.public_key_type> listPublicKeyType = new ArrayList<>();
                    listPublicKeyType.add(publicActiveKeyType);
                    listPublicKeyType.add(publicOwnerKeyType);
                    mWalletObject.update_account(accountObject, accountObject.id, listPublicKeyType);
                    mHashMapPub2Private.put(publicActiveKeyType, new types.private_key_type(privateActiveKey));
                    mHashMapPub2Private.put(publicOwnerKeyType, new types.private_key_type(privateOwnerKey));
                    save_account(paramEntity.getAccountName(), accountObject.id.toString(), paramEntity.getPassword(), paramEntity.getAccountType().name(), accountDao);
                    rspText = new ResponseData(OPERATE_SUCCESS, createAccountObject.getMsg(), createAccountObject.getData()).toString();
                    callBack.onReceiveValue(rspText);
                }
            } else {
                if (response.body().contentLength() != 0) {
                    rspText = new ResponseData(OPERATE_FAILED, "", strResponse).toString();
                    callBack.onReceiveValue(rspText);
                }
            }
        } catch (Exception e) {
            rspText = new ResponseData(OPERATE_FAILED, e.getMessage(), null).toString();
            callBack.onReceiveValue(rspText);
        }
    }


    /**
     * account model login
     *
     * @param strAccountName
     * @param strPassword
     * @param accountDao
     * @param callBack
     * @throws NetworkStatusException
     */
    public void password_login(String strAccountName, String strPassword, AccountDao accountDao, IBcxCallBack callBack) throws NetworkStatusException, UnLegalInputException {
        // get public key
        private_key privateActiveKey = private_key.from_seed(strAccountName + "active" + strPassword);
        private_key privateOwnerKey = private_key.from_seed(strAccountName + "owner" + strPassword);
        types.public_key_type publicActiveKeyType = new types.public_key_type(privateActiveKey.get_public_key());
        types.public_key_type publicOwnerKeyType = new types.public_key_type(privateOwnerKey.get_public_key());

        //prepare data to store keystore
        account_object accountObject = lookup_account_names(strAccountName);
        if (accountObject == null) {
            rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, "Account does not exist", null).toString();
            callBack.onReceiveValue(rspText);
            return;
        }
        // verify password
        if (accountObject != null && !accountObject.active.is_public_key_type_exist(publicActiveKeyType) &&
                !accountObject.owner.is_public_key_type_exist(publicActiveKeyType) &&
                !accountObject.active.is_public_key_type_exist(publicOwnerKeyType) &&
                !accountObject.owner.is_public_key_type_exist(publicOwnerKeyType)) {
            rspText = new ResponseData(ERROR_WRONG_PASSWORD, "Wrong password", null).toString();
            callBack.onReceiveValue(rspText);
            return;
        }


        //prepare data to store keystore
        List<types.public_key_type> listPublicKeyType = new ArrayList<>();
        listPublicKeyType.add(publicActiveKeyType);
        listPublicKeyType.add(publicOwnerKeyType);
        mWalletObject.update_account(accountObject, accountObject.id, listPublicKeyType);
        mHashMapPub2Private.put(publicActiveKeyType, new types.private_key_type(privateActiveKey));
        mHashMapPub2Private.put(publicOwnerKeyType, new types.private_key_type(privateOwnerKey));

        //return account object
        rspText = new ResponseData(OPERATE_SUCCESS, "success", accountObject).toString();
        callBack.onReceiveValue(rspText);

        // save_account account
        save_account(accountObject.name, accountObject.id.toString(), strPassword, AccountType.ACCOUNT.name(), accountDao);
    }


    /**
     * import keystore
     *
     * @param keystore is json string the data type must as some as you exported;
     * @param password
     */
    public void import_keystore(String keystore, String password, String accountType, AccountDao accountDao, IBcxCallBack callBack) {
        try {
            // parse keystore
            Gson gson = global_config_object.getInstance().getGsonBuilder().create();
            mWalletObject = gson.fromJson(keystore, wallet_object.class);
            // decrypt keystore
            Map<String, String> private_keys = decrypt_keystore_callback_private_key(password);
            // return  private key
            rspText = new ResponseData(OPERATE_SUCCESS, "success", private_keys).toString();
            callBack.onReceiveValue(rspText);
            account_object account_object = lookup_account_names(mWalletObject.my_accounts.get(0).name);
            save_account(account_object.name, account_object.id.toString(), password, accountType, accountDao);
        } catch (JsonSyntaxException e) {
            rspText = new ResponseData(ERROR_PARAMETER_DATA_TYPE, "Please check parameter type", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (NetworkStatusException e) {
            rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
            callBack.onReceiveValue(rspText);
        } catch (UnLegalInputException e) {
            rspText = new ResponseData(ERROR_PARAMETER, e.getMessage(), null).toString();
            callBack.onReceiveValue(rspText);
        } catch (KeyInvalideException e) {
            rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, "Please enter the correct private key", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (AddressFormatException e) {
            rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, "Please enter the correct private key", null).toString();
            callBack.onReceiveValue(rspText);
        }

    }


    /**
     * export keystore
     *
     * @param accountName
     * @param password
     * @param callBack    you can get the keystore of the account you input,you can read the keystore in file to save .
     */
    public void export_keystore(String accountName, String password, AccountDao accountDao, IBcxCallBack callBack) {
        try {

            AccountEntity.AccountBean accountBean = get_dao_account_by_name(accountName, accountDao);
            if (null == accountBean) {
                throw new AccountNotFoundException("Account does not exist");
            }
            if (unlock(accountName, password, accountDao) != OPERATE_SUCCESS && verify_password(accountName, password).size() <= 0) {
                throw new PasswordVerifyException("Wrong password");
            }
            String resultStr = accountBean.getKeystore();
            mWalletObject = global_config_object.getInstance().getGsonBuilder().create().fromJson(resultStr, wallet_object.class);
            HashMap<types.public_key_type, Integer> activeAuths = mWalletObject.my_accounts.get(0).active.key_auths;
            for (Map.Entry<types.public_key_type, Integer> entry : activeAuths.entrySet()) {
                active_key_auths = resultStr.replace("{\"" + entry.getKey() + "\":" + entry.getValue() + "}", "[[" + "\"" + entry.getKey() + "\"," + entry.getValue() + "]]");
            }
            HashMap<types.public_key_type, Integer> auths = mWalletObject.my_accounts.get(0).owner.key_auths;
            for (Map.Entry<types.public_key_type, Integer> entry : auths.entrySet()) {
                owner_key_auths = active_key_auths.replace("{\"" + entry.getKey() + "\":" + entry.getValue() + "}", "[[" + "\"" + entry.getKey() + "\"," + entry.getValue() + "]]");
            }
            callBack.onReceiveValue(owner_key_auths);
            lock();
        } catch (NetworkStatusException e) {
            rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
            callBack.onReceiveValue(rspText);
        } catch (AccountNotFoundException e) {
            rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, "Account does not exist", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (PasswordVerifyException e) {
            rspText = new ResponseData(ERROR_WRONG_PASSWORD, "Wrong password", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (KeyInvalideException e) {
            rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, "Please enter the correct private key", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (AddressFormatException e) {
            rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, "Please enter the correct private key", null).toString();
            callBack.onReceiveValue(rspText);
        }
    }


    /**
     * import private key
     *
     * @param wifKey   private key
     * @param password to encrypt your private key,
     */
    public List<String> import_wif_key(String wifKey, String password, String accountType, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, PasswordVerifyException, KeyInvalideException, AddressFormatException {
        // get public key
        types.private_key_type privateKeyType = new types.private_key_type(wifKey);
        public_key publicKey = privateKeyType.getPrivateKey().get_public_key();
        types.public_key_type publicKeyType = new types.public_key_type(publicKey);
        List<String> publicKeyTypes = new ArrayList<>();
        publicKeyTypes.add(publicKeyType.toString());

        // request to get accoount id
        List<List<String>> objects = mWebSocketApi.get_key_references(publicKeyTypes);

        // if id[]  null ,you need know the callback  about this rpc:get_key_references
        if (objects == null || objects.size() <= 0 || objects.get(0).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        LinkedHashSet<String> account_ids_set = new LinkedHashSet<>();
        for (List<String> account_ids : objects) {
            for (String id : account_ids) {
                account_ids_set.add(id);
            }
        }
        List<String> account_names = new ArrayList<>();
        for (String id : account_ids_set) {
            // get account object
            account_object account_object = get_account_object(id);
            //account object null
            if (account_object == null) {
                throw new AccountNotFoundException("The private key has no account information");
            }
            //prepare data to store keystore
            List<types.public_key_type> listPublicKeyType = new ArrayList<>();
            listPublicKeyType.add(publicKeyType);
            mWalletObject.update_account(account_object, account_object.id, listPublicKeyType);
            mHashMapPub2Private.put(publicKeyType, privateKeyType);
            account_names.add(account_object.name);
            // save_account account
            if (TextUtils.equals(AccountType.ACCOUNT.name(), accountType) && account_names.size() == 1) {
                save_account(account_object.name, account_object.id.toString(), password, accountType, accountDao);
                return account_names;
            }
            save_account(account_object.name, account_object.id.toString(), password, accountType, accountDao);
        }
        if (TextUtils.equals(AccountType.WALLET.name(), accountType)) {
            return account_names;
        }
        return null;
    }


    /**
     * transfer
     *
     * @param strFrom
     * @param strTo
     * @param strAmount
     * @param strAssetSymbolOrId
     * @param strMemo
     * @return
     * @throws NetworkStatusException
     */
    public String transfer(String password, String strFrom, String strTo, String strAmount, String strAssetSymbolOrId, String strFeeSymbolOrId, String strMemo, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, PasswordVerifyException, AuthorityException, AssetNotFoundException, KeyInvalideException, AddressFormatException {
        // get asset object
        asset_object assetObject = lookup_asset_symbols(strAssetSymbolOrId);
        asset_object feeAssetObject = lookup_asset_symbols(strFeeSymbolOrId);

        if (null == assetObject) {
            throw new AssetNotFoundException("Transfer asset does not exist");
        }

        if (null == feeAssetObject) {
            throw new AssetNotFoundException("Transfer-Fee asset does not exist");
        }

        if (TextUtils.equals(assetObject.symbol, feeAssetObject.symbol)) {
            feeAssetObject = assetObject;
        }

        // get account object to get account id
        account_object accountObjectFrom = get_account_object(strFrom);
        account_object accountObjectTo = get_account_object(strTo);
        if (accountObjectTo == null) {
            throw new AccountNotFoundException("Account to does not exist");
        }

        if (accountObjectFrom == null) {
            throw new AccountNotFoundException("Account from does not exist");
        }

        // verify tempory password and account model password
        if (unlock(accountObjectFrom.name, password, accountDao) != OPERATE_SUCCESS && verify_password(accountObjectFrom.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.transfer_operation transferOperation = new operations.transfer_operation();
        transferOperation.from = accountObjectFrom.id;
        transferOperation.to = accountObjectTo.id;
        transferOperation.amount = assetObject.amount_from_string(strAmount);
        transferOperation.fee = feeAssetObject.amount_from_string("0");
        transferOperation.extensions = new HashSet<>();

        //sign  memo
        if (!TextUtils.isEmpty(strMemo)) {
            transferOperation.memo = new memo_data();
            transferOperation.memo.from = accountObjectFrom.options.memo_key;
            transferOperation.memo.to = accountObjectTo.options.memo_key;

            types.private_key_type privateKeyType = mHashMapPub2Private.get(accountObjectFrom.options.memo_key);

            if (privateKeyType == null) {
                // Must have active permission to transfer  please confirm that you imported the activePrivateKey
                throw new AuthorityException("Transfer requires the private key of activity mode, make sure that the private key of the activity mode is imported");
            }

            transferOperation.memo.set_message(
                    privateKeyType.getPrivateKey(),
                    accountObjectTo.options.memo_key.getPublicKey(),
                    strMemo,
                    0
            );
            transferOperation.memo.get_message(
                    privateKeyType.getPrivateKey(),
                    accountObjectTo.options.memo_key.getPublicKey()
            );
        }

        //calculate transfer fee
        List<Object> objects1 = new ArrayList<>();
        objects1.add(0);
        objects1.add(transferOperation);
        List<Object> objects3 = new ArrayList<>();
        objects3.add(objects1);
        List<Object> objects = new ArrayList<>();
        objects.add(objects3);
        objects.add(feeAssetObject.id.toString());

        List<asset_fee_object> requiredFees = mWebSocketApi.get_required_fees(objects);
        // transfer operation fee
        transferOperation.fee = feeAssetObject.amount_from_string(String.valueOf(Double.valueOf(requiredFees.get(0).amount) / (Math.pow(10, feeAssetObject.precision))));
        // prepare to sign transfer
        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = transferOperation;
        operationType.nOperationType = operations.ID_TRANSFER_OPERATION;

        signed_operate tx = new signed_operate();
        tx.operations = new ArrayList<>();
        tx.operations.add(operationType);
        tx.extensions = new HashSet<>();
        return sign_transaction(tx, accountObjectFrom);
    }


    /**
     * sign transaction
     *
     * @param tx
     * @return
     * @throws NetworkStatusException
     */
    private String sign_transaction(signed_operate tx, account_object account_object) throws NetworkStatusException, AuthorityException {
        dynamic_global_property_object dynamicGlobalPropertyObject = get_dynamic_global_properties();
        tx.set_reference_block(dynamicGlobalPropertyObject.head_block_id);
        Date dateObject = dynamicGlobalPropertyObject.time;
        Calendar calender = Calendar.getInstance();
        calender.setTime(dateObject);
        calender.add(Calendar.SECOND, 30);
        dateObject = calender.getTime();
        tx.set_expiration(dateObject);
        HashMap<types.public_key_type, Integer> key_auths = account_object.active.key_auths;
        for (Map.Entry<types.public_key_type, Integer> entry : key_auths.entrySet()) {
            types.private_key_type privateKey = mHashMapPub2Private.get(entry.getKey());
            if (privateKey != null) {
                tx.sign(privateKey, mWebSocketApi.get_chain_id());
            } else {
                throw new AuthorityException("Author failed! make sure you logged and have active permission");
            }
        }
        LogUtils.i("sign_transaction", global_config_object.getInstance().getGsonBuilder().create().toJson(tx));
        return mWebSocketApi.broadcast_transaction(tx);
    }


    /**
     * verify account password
     *
     * @return
     */
    private Map<String, String> verify_password(String accountName, String strPassword) throws NetworkStatusException, PasswordVerifyException, AccountNotFoundException {

        private_key privateActiveKey = private_key.from_seed(accountName + "active" + strPassword);
        private_key privateOwnerKey = private_key.from_seed(accountName + "owner" + strPassword);
        types.public_key_type publicActiveKeyType = new types.public_key_type(privateActiveKey.get_public_key());
        types.public_key_type publicOwnerKeyType = new types.public_key_type(privateOwnerKey.get_public_key());

        List<String> publicKeyTypes1 = new ArrayList<>();
        publicKeyTypes1.add(publicOwnerKeyType.toString());
        publicKeyTypes1.add(publicActiveKeyType.toString());
        //get account id
        List<List<String>> publicKeyTypes1List = mWebSocketApi.get_key_references(publicKeyTypes1);
        // if id[]  null ,you need know the callback  about this rpc:get_key_references
        if (publicKeyTypes1List == null || publicKeyTypes1List.size() <= 0 || publicKeyTypes1List.get(0).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }
        // load private key in memory
        List<types.public_key_type> listPublicKeyType = new ArrayList<>();
        listPublicKeyType.add(publicActiveKeyType);
        listPublicKeyType.add(publicOwnerKeyType);
        account_object accountObject = get_account_object(publicKeyTypes1List.get(0).get(0));
        if (accountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        Map<String, String> strings = new HashMap<>();
        types.private_key_type private_active_key_type = new types.private_key_type(privateActiveKey);
        types.private_key_type private_owner_key_type = new types.private_key_type(privateOwnerKey);
        mWalletObject.update_account(accountObject, accountObject.id, listPublicKeyType);
        mHashMapPub2Private.put(publicActiveKeyType, private_active_key_type);
        mHashMapPub2Private.put(publicOwnerKeyType, private_owner_key_type);
        strings.put(publicActiveKeyType.toString(), private_active_key_type.toString());
        strings.put(publicOwnerKeyType.toString(), private_owner_key_type.toString());
        return strings;
    }

    /**
     * calculate transfer fee
     *
     * @param strFrom            account from
     * @param strTo              account to
     * @param strAmount          transfer amount
     * @param strAssetSymbolOrId transfer asset symbol or id
     * @param strFeeSymbolOrId   transfer fee symbol or id
     * @param strMemo            memo
     */
    public List<asset_fee_object> calculate_transfer_fee(String password, String strFrom, String strTo, String strAmount, String strAssetSymbolOrId, String strFeeSymbolOrId, String strMemo, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, AssetNotFoundException, AuthorityException, AddressFormatException, PasswordVerifyException, KeyInvalideException {

        asset_object assetObject = lookup_asset_symbols(strAssetSymbolOrId);
        asset_object assetFeeObject = lookup_asset_symbols(strFeeSymbolOrId);

        if (assetObject == null) {
            throw new AssetNotFoundException("Transfer asset does not exist");
        }

        if (assetFeeObject == null) {
            throw new AssetNotFoundException("Transfer-Fee asset does not exist");
        }

        account_object accountObjectFrom = get_account_object(strFrom);
        account_object accountObjectTo = get_account_object(strTo);
        if (accountObjectTo == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        // verify tempory password and account model password
        if (unlock(accountObjectFrom.name, password, accountDao) != OPERATE_SUCCESS && verify_password(accountObjectFrom.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.transfer_operation transferOperation = new operations.transfer_operation();
        transferOperation.from = accountObjectFrom.id;
        transferOperation.to = accountObjectTo.id;
        transferOperation.amount = assetObject.amount_from_string(strAmount);
        transferOperation.fee = assetFeeObject.amount_from_string("0");
        transferOperation.extensions = new HashSet<>();

        if (!TextUtils.isEmpty(strMemo)) {
            transferOperation.memo = new memo_data();
            transferOperation.memo.from = accountObjectFrom.options.memo_key;
            transferOperation.memo.to = accountObjectTo.options.memo_key;
            types.private_key_type privateKeyType = mHashMapPub2Private.get(accountObjectFrom.options.memo_key);

            if (privateKeyType == null) {
                throw new AuthorityException("Author failed! make sure you logged and have active permission");
            }
            transferOperation.memo.set_message(
                    privateKeyType.getPrivateKey(),
                    accountObjectTo.options.memo_key.getPublicKey(),
                    strMemo,
                    0
            );
            transferOperation.memo.get_message(privateKeyType.getPrivateKey(), accountObjectTo.options.memo_key.getPublicKey());
        }
        List<Object> objects1 = new ArrayList<>();
        objects1.add(0);
        objects1.add(transferOperation);

        List<Object> objects3 = new ArrayList<>();
        objects3.add(objects1);

        List<Object> objects = new ArrayList<>();
        objects.add(objects3);
        objects.add(assetFeeObject.id.toString());

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objects);

        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, assetFeeObject.precision)));

        return asset_fee_objects;
    }


    /**
     * invoking contract method
     *
     * @param strAccount
     * @param password
     * @param feeAssetSymbolOrId
     * @param contractNameOrId
     * @param functionName
     * @param params
     * @param accountDao
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws AuthorityException
     * @throws PasswordVerifyException
     * @throws ContractNotFoundException
     */
    public String invoking_contract(String strAccount, String password, String feeAssetSymbolOrId, String contractNameOrId, String functionName, String params, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, AuthorityException, ContractNotFoundException, AssetNotFoundException, PasswordVerifyException, KeyInvalideException, AddressFormatException {

        //search contract
        contract_object contractObject = mWebSocketApi.get_contract(contractNameOrId);
        if (contractObject == null) {
            throw new ContractNotFoundException("Contract does not exist");
        }

        // search fee asset object
        asset_object assetObject = lookup_asset_symbols(feeAssetSymbolOrId);
        if (assetObject == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }
        // search account object
        account_object accountObject = get_account_object(strAccount);
        if (accountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        // verify tempory password and account model password
        if (unlock(accountObject.name, password, accountDao) != OPERATE_SUCCESS && verify_password(accountObject.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.invoking_contract_operation invokingContractOperation = new operations.invoking_contract_operation();
        invokingContractOperation.fee = assetObject.amount_from_string("0");
        invokingContractOperation.caller = accountObject.id;
        invokingContractOperation.contract_id = contractObject.id;
        invokingContractOperation.function_name = functionName;
        invokingContractOperation.extensions = new HashSet<>();
        String[] paramsArray = params.split(",");
        List<String> paramList = Arrays.asList(paramsArray);
        invokingContractOperation.value_list = new ArrayList<>();
        for (String param : paramList) {
            List<Object> base_encoder = new ArrayList<>();
            operations.invoking_contract_operation.v baseValues = new operations.invoking_contract_operation.v();
            baseValues.v = param;
            base_encoder.add(2);
            base_encoder.add(baseValues);
            invokingContractOperation.value_list.add(base_encoder);
        }
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CALCULATE_INVOKING_CONTRACT_OPERATION);
        operateList.add(invokingContractOperation);
        List<Object> feeList = new ArrayList<>();
        feeList.add(operateList);
        List<Object> feeLists = new ArrayList<>();
        feeLists.add(feeList);
        feeLists.add(assetObject.id);

        // get this operate fees
        List<asset_fee_object> requiredFees = mWebSocketApi.get_required_fees(feeLists);
        invokingContractOperation.fee = assetObject.amount_from_string(String.valueOf(Double.valueOf(requiredFees.get(0).amount) / (Math.pow(10, assetObject.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = invokingContractOperation;
        operationType.nOperationType = operations.ID_CALCULATE_INVOKING_CONTRACT_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();

        return sign_transaction(transactionWithCallback, accountObject);
    }


    /**
     * get contract info
     *
     * @param contractNameOrId
     * @return
     */
    public contract_object get_contract(String contractNameOrId) throws NetworkStatusException {
        return mWebSocketApi.get_contract(contractNameOrId);
    }

    /**
     * get nh asset order object
     *
     * @throws NetworkStatusException
     */
    public nh_asset_order_object get_nhasset_order_object(String nh_order_id) throws NetworkStatusException {
        return mWebSocketApi.get_nhasset_order_object(nh_order_id);
    }

    /**
     * get nh asset order object
     *
     * @throws NetworkStatusException
     */
    public limit_orders_object get_limit_order_object(String limit_order_id) throws NetworkStatusException {
        return mWebSocketApi.get_limit_order_object(limit_order_id);
    }

    /**
     * private method to append params for get invoking contract fee
     *
     * @param strAccount
     * @param feeAssetSymbol
     * @param contractNameOrId
     * @param functionName
     * @param params
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     */
    public List<asset_fee_object> calculate_invoking_contract_fee(String strAccount, String feeAssetSymbol, String contractNameOrId, String functionName, String params) throws NetworkStatusException, AccountNotFoundException, ContractNotFoundException, AssetNotFoundException {
        asset_object feeAssetObject = lookup_asset_symbols(feeAssetSymbol);
        if (feeAssetObject == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }
        account_object accountObject = get_account_object(strAccount);
        if (accountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        contract_object contractObject = mWebSocketApi.get_contract(contractNameOrId);
        if (contractObject == null) {
            throw new ContractNotFoundException("Contract does not exist");
        }

        operations.invoking_contract_operation invokingContractOperation = new operations.invoking_contract_operation();
        invokingContractOperation.fee = feeAssetObject.amount_from_string("0");
        invokingContractOperation.caller = accountObject.id;
        invokingContractOperation.contract_id = contractObject.id;
        invokingContractOperation.function_name = functionName;
        invokingContractOperation.extensions = new HashSet<>();
        String[] paramsArray = params.split(",");
        List<String> paramList = Arrays.asList(paramsArray);
        invokingContractOperation.value_list = new ArrayList<>();
        for (String param : paramList) {
            List<Object> base_encoder = new ArrayList<>();
            operations.invoking_contract_operation.v baseValue = new operations.invoking_contract_operation.v();
            baseValue.v = param;
            base_encoder.add(2);
            base_encoder.add(baseValue);
            invokingContractOperation.value_list.add(base_encoder);
        }
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CALCULATE_INVOKING_CONTRACT_OPERATION);
        operateList.add(invokingContractOperation);
        List<Object> feeList = new ArrayList<>();
        feeList.add(operateList);
        List<Object> feeLists = new ArrayList<>();
        feeLists.add(feeList);
        feeLists.add(feeAssetObject.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(feeLists);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, feeAssetObject.precision)));
        return asset_fee_objects;
    }


    /**
     * transfer nh asset
     *
     * @param password
     * @param account_from
     * @param account_to
     * @param fee_asset_symbol
     * @param nh_asset_id
     * @param accountDao
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     * @throws AuthorityException
     * @throws PasswordVerifyException
     */
    public String transfer_nh_asset(String password, String account_from, String account_to, String fee_asset_symbol, String nh_asset_id, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, NhAssetNotFoundException, AuthorityException, PasswordVerifyException, KeyInvalideException, AddressFormatException, AssetNotFoundException, NotAssetCreatorException {

        account_object accountObjectFrom = get_account_object(account_from);
        if (accountObjectFrom == null) {
            throw new AccountNotFoundException("Transfer account does not exist");
        }
        account_object accountObjectTo = get_account_object(account_to);
        if (accountObjectTo == null) {
            throw new AccountNotFoundException("Receiving account does not exist");
        }
        nhasset_object nhasset_object = lookup_nh_asset_object(nh_asset_id);
        if (nhasset_object == null) {
            throw new NhAssetNotFoundException("NhAsset does not exist");
        }
        if (!TextUtils.equals(nhasset_object.nh_asset_owner.toString(), accountObjectFrom.id.toString())) {
            throw new NotAssetCreatorException("You are not the asset owner");
        }
        asset_object fee_asset_object = lookup_asset_symbols(fee_asset_symbol);
        if (fee_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        // verify tempory password and account model password
        if (unlock(accountObjectFrom.name, password, accountDao) != OPERATE_SUCCESS && verify_password(accountObjectFrom.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.transfer_nhasset_operation transfer_nhasset_operation = new operations.transfer_nhasset_operation();
        transfer_nhasset_operation.fee = fee_asset_object.amount_from_string("0");
        transfer_nhasset_operation.from = accountObjectFrom.id;
        transfer_nhasset_operation.to = accountObjectTo.id;
        transfer_nhasset_operation.nh_asset = nhasset_object.id;
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_TRANSFER_NH_ASSET_OPERATION);
        operateList.add(transfer_nhasset_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);
        List<asset_fee_object> requiredFees = mWebSocketApi.get_required_fees(objectList);
        transfer_nhasset_operation.fee = fee_asset_object.amount_from_string(String.valueOf(Double.valueOf(requiredFees.get(0).amount) / (Math.pow(10, fee_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = transfer_nhasset_operation;
        operationType.nOperationType = operations.ID_TRANSFER_NH_ASSET_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, accountObjectFrom);
    }


    /**
     * calculate transfer nhasset fee
     *
     * @param account_from
     * @param account_to
     * @param fee_asset_symbol
     * @param nh_asset_id
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public List<asset_fee_object> transfer_nh_asset_fee(String account_from, String account_to, String fee_asset_symbol, String nh_asset_id) throws NetworkStatusException, AccountNotFoundException, NhAssetNotFoundException, AssetNotFoundException, NotAssetCreatorException {

        account_object accountObjectFrom = get_account_object(account_from);
        if (accountObjectFrom == null) {
            throw new AccountNotFoundException("Transfer account does not exist");
        }

        account_object accountObjectTo = get_account_object(account_to);
        if (accountObjectTo == null) {
            throw new AccountNotFoundException("Receiving account does not exist");
        }

        nhasset_object nhasset_object = lookup_nh_asset_object(nh_asset_id);
        if (nhasset_object == null) {
            throw new NhAssetNotFoundException("NhAsset does not exist");
        }

        if (!TextUtils.equals(nhasset_object.nh_asset_owner.toString(), accountObjectFrom.id.toString())) {
            throw new NotAssetCreatorException("You are not the asset owner");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_asset_symbol);
        if (fee_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        operations.transfer_nhasset_operation transfer_nhasset_operation = new operations.transfer_nhasset_operation();
        transfer_nhasset_operation.fee = fee_asset_object.amount_from_string("0");
        transfer_nhasset_operation.from = accountObjectFrom.id;
        transfer_nhasset_operation.to = accountObjectTo.id;
        transfer_nhasset_operation.nh_asset = nhasset_object.id;
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_TRANSFER_NH_ASSET_OPERATION);
        operateList.add(transfer_nhasset_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_object.precision)));
        return asset_fee_objects;
    }


    /**
     * get delete nhasset fee
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public List<asset_fee_object> delete_nh_asset_fee(String fee_paying_account, String nhasset_id, String fee_symbol) throws NetworkStatusException, AccountNotFoundException, NhAssetNotFoundException, AssetNotFoundException, NotAssetCreatorException {

        account_object feePayaccountObject = get_account_object(fee_paying_account);
        if (feePayaccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        nhasset_object nhasset_object = lookup_nh_asset_object(nhasset_id);
        if (nhasset_object == null) {
            throw new NhAssetNotFoundException("NhAsset does not exist");
        }

        if (!TextUtils.equals(nhasset_object.nh_asset_owner.toString(), feePayaccountObject.id.toString())) {
            throw new NotAssetCreatorException("You are not the asset owner");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_symbol);
        if (fee_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        operations.delete_nhasset_operation delete_nhasset_operation = new operations.delete_nhasset_operation();
        delete_nhasset_operation.fee = fee_asset_object.amount_from_string("0");
        delete_nhasset_operation.fee_paying_account = feePayaccountObject.id;
        delete_nhasset_operation.nh_asset = nhasset_object.id;
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_DELETE_NH_ASSET_OPERATION);
        operateList.add(delete_nhasset_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_object.precision)));
        return asset_fee_objects;
    }


    /**
     * delete nhasset
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public String delete_nh_asset(String fee_paying_account, String password, String nhasset_id, String fee_symbol, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, NhAssetNotFoundException, AssetNotFoundException, KeyInvalideException, AddressFormatException, PasswordVerifyException, AuthorityException, NotAssetCreatorException {

        account_object feePayaccountObject = get_account_object(fee_paying_account);
        if (feePayaccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        nhasset_object nhasset_object = lookup_nh_asset_object(nhasset_id);
        if (nhasset_object == null) {
            throw new NhAssetNotFoundException("NhAsset does not exist");
        }

        if (!TextUtils.equals(nhasset_object.nh_asset_owner.toString(), feePayaccountObject.id.toString())) {
            throw new NotAssetCreatorException("You are not the asset owner");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_symbol);
        if (fee_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        if (unlock(feePayaccountObject.name, password, accountDao) != OPERATE_SUCCESS && verify_password(feePayaccountObject.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.delete_nhasset_operation delete_nhasset_operation = new operations.delete_nhasset_operation();
        delete_nhasset_operation.fee = fee_asset_object.amount_from_string("0");
        delete_nhasset_operation.fee_paying_account = feePayaccountObject.id;
        delete_nhasset_operation.nh_asset = nhasset_object.id;
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_DELETE_NH_ASSET_OPERATION);
        operateList.add(delete_nhasset_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> requiredFees = mWebSocketApi.get_required_fees(objectList);
        delete_nhasset_operation.fee = fee_asset_object.amount_from_string(String.valueOf(Double.valueOf(requiredFees.get(0).amount) / (Math.pow(10, fee_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = delete_nhasset_operation;
        operationType.nOperationType = operations.ID_DELETE_NH_ASSET_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, feePayaccountObject);
    }


    /**
     * get cancel nhasset order fee
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     */
    public List<asset_fee_object> cancel_nh_asset_order_fee(String fee_paying_account, String order_id, String fee_symbol) throws NetworkStatusException, AccountNotFoundException, AssetNotFoundException, OrderNotFoundException, NotAssetCreatorException {

        account_object feePayaccountObject = get_account_object(fee_paying_account);
        if (feePayaccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        nh_asset_order_object nh_asset_order_object = get_nhasset_order_object(order_id);
        if (null == nh_asset_order_object) {
            throw new OrderNotFoundException("Order does not exist");
        }

        if (!TextUtils.equals(nh_asset_order_object.seller.toString(), feePayaccountObject.id.toString())) {
            throw new NotAssetCreatorException("You are not the order creator");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_symbol);
        if (fee_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        operations.cancel_nhasset_order_operation cancel_nhasset_order_operation = new operations.cancel_nhasset_order_operation();
        cancel_nhasset_order_operation.fee = fee_asset_object.amount_from_string("0");
        cancel_nhasset_order_operation.order = nh_asset_order_object.id;
        cancel_nhasset_order_operation.fee_paying_account = feePayaccountObject.id;
        cancel_nhasset_order_operation.extensions = new HashSet<>();
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CANCEL_NH_ASSET_ORDER_OPERATION);
        operateList.add(cancel_nhasset_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_object.precision)));
        return asset_fee_objects;
    }


    /**
     * cancel nhasset order
     *
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     */
    public String cancel_nh_asset_order(String fee_paying_account, String password, String order_id, String fee_symbol, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, AssetNotFoundException, KeyInvalideException, AddressFormatException, PasswordVerifyException, AuthorityException, OrderNotFoundException, NotAssetCreatorException {

        account_object feePayaccountObject = get_account_object(fee_paying_account);
        if (feePayaccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        nh_asset_order_object nh_asset_order_object = get_nhasset_order_object(order_id);
        if (null == nh_asset_order_object) {
            throw new OrderNotFoundException("Order does not exist");
        }

        if (!TextUtils.equals(nh_asset_order_object.seller.toString(), feePayaccountObject.id.toString())) {
            throw new NotAssetCreatorException("You are not the order creator");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_symbol);
        if (fee_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        if (unlock(feePayaccountObject.name, password, accountDao) != OPERATE_SUCCESS && verify_password(feePayaccountObject.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.cancel_nhasset_order_operation cancel_nhasset_order_operation = new operations.cancel_nhasset_order_operation();
        cancel_nhasset_order_operation.fee = fee_asset_object.amount_from_string("0");
        cancel_nhasset_order_operation.order = nh_asset_order_object.id;
        cancel_nhasset_order_operation.fee_paying_account = feePayaccountObject.id;
        cancel_nhasset_order_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CANCEL_NH_ASSET_ORDER_OPERATION);
        operateList.add(cancel_nhasset_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> requiredFees = mWebSocketApi.get_required_fees(objectList);
        cancel_nhasset_order_operation.fee = fee_asset_object.amount_from_string(String.valueOf(Double.valueOf(requiredFees.get(0).amount) / (Math.pow(10, fee_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = cancel_nhasset_order_operation;
        operationType.nOperationType = operations.ID_CANCEL_NH_ASSET_ORDER_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, feePayaccountObject);
    }

    /**
     * calculate buy nhasset fee
     *
     * @param fee_paying_account
     * @param order_Id
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     * @throws OrderNotFoundException
     * @throws UnLegalInputException
     */
    public List<asset_fee_object> buy_nh_asset_fee(String fee_paying_account, String order_Id) throws NetworkStatusException, AccountNotFoundException, OrderNotFoundException, NhAssetNotFoundException {

        account_object feePayaccountObject = get_account_object(fee_paying_account);
        if (feePayaccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        nh_asset_order_object nh_asset_order_object = get_nhasset_order_object(order_Id);
        if (null == nh_asset_order_object) {
            throw new OrderNotFoundException("Order does not exist");
        }
        asset_object price_asset_object = lookup_asset_symbols(nh_asset_order_object.price.asset_id.toString());
        operations.buy_nhasset_operation buy_nhasset_operation = new operations.buy_nhasset_operation();
        buy_nhasset_operation.fee = price_asset_object.amount_from_string("0");
        buy_nhasset_operation.order = nh_asset_order_object.id;
        buy_nhasset_operation.fee_paying_account = feePayaccountObject.id;
        buy_nhasset_operation.seller = nh_asset_order_object.seller;
        buy_nhasset_operation.nh_asset = nh_asset_order_object.nh_asset_id;
        buy_nhasset_operation.price_amount = String.valueOf(nh_asset_order_object.price.amount / (Math.pow(10, price_asset_object.precision)));
        buy_nhasset_operation.price_asset_id = nh_asset_order_object.price.asset_id;
        buy_nhasset_operation.price_asset_symbol = price_asset_object.symbol;
        buy_nhasset_operation.extensions = new HashSet<>();
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_BUY_NH_ASSET_OPERATION);
        operateList.add(buy_nhasset_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(price_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, price_asset_object.precision)));
        return asset_fee_objects;
    }


    /**
     * buy nhasset
     *
     * @param password
     * @param fee_paying_account
     * @param order_Id
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     * @throws NhAssetNotFoundException
     */
    public String buy_nh_asset(String password, String fee_paying_account, String order_Id, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, OrderNotFoundException, AuthorityException, PasswordVerifyException, KeyInvalideException, AddressFormatException {

        account_object feePayAccountObject = get_account_object(fee_paying_account);
        if (feePayAccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        nh_asset_order_object nh_asset_order_object = get_nhasset_order_object(order_Id);
        if (null == nh_asset_order_object) {
            throw new OrderNotFoundException("Order does not exist");
        }
        asset_object price_asset_object = lookup_asset_symbols(nh_asset_order_object.price.asset_id.toString());

        // verify tempory password and account model password
        if (unlock(feePayAccountObject.name, password, accountDao) != OPERATE_SUCCESS && verify_password(feePayAccountObject.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.buy_nhasset_operation buy_nhasset_operation = new operations.buy_nhasset_operation();
        buy_nhasset_operation.fee = price_asset_object.amount_from_string("0");
        buy_nhasset_operation.order = nh_asset_order_object.id;
        buy_nhasset_operation.fee_paying_account = feePayAccountObject.id;
        buy_nhasset_operation.seller = nh_asset_order_object.seller;
        buy_nhasset_operation.nh_asset = nh_asset_order_object.nh_asset_id;
        buy_nhasset_operation.price_amount = String.valueOf(nh_asset_order_object.price.amount / (Math.pow(10, price_asset_object.precision)));
        buy_nhasset_operation.price_asset_id = nh_asset_order_object.price.asset_id;
        buy_nhasset_operation.price_asset_symbol = price_asset_object.symbol;
        buy_nhasset_operation.extensions = new HashSet<>();
        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_BUY_NH_ASSET_OPERATION);
        operateList.add(buy_nhasset_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(price_asset_object.id);

        List<asset_fee_object> requiredFees = mWebSocketApi.get_required_fees(objectList);
        buy_nhasset_operation.fee = price_asset_object.amount_from_string(String.valueOf(Double.valueOf(requiredFees.get(0).amount) / (Math.pow(10, price_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = buy_nhasset_operation;
        operationType.nOperationType = operations.ID_BUY_NH_ASSET_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();

        return sign_transaction(transactionWithCallback, feePayAccountObject);
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
    public List<asset_fee_object> create_nh_asset_order_fee(String otcaccount, String seller, String pending_order_nh_asset, String pending_order_fee, String pending_order_fee_symbol, String pending_order_memo, String pending_order_price, String pending_order_price_symbol, long pending_order_valid_time_millis) throws NetworkStatusException, NhAssetNotFoundException, AssetNotFoundException, AccountNotFoundException, ParseException {

        account_object feePayAccountObject = get_account_object(seller);
        account_object otcAccount = get_account_object(otcaccount);
        nhasset_object nhasset_object = lookup_nh_asset_object(pending_order_nh_asset);
        asset_object pending_order_price_asset_object = lookup_asset_symbols(pending_order_price_symbol);
        asset_object pending_order_fee_asset = lookup_asset_symbols(pending_order_fee_symbol);

        if (feePayAccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }
        if (nhasset_object == null) {
            throw new NhAssetNotFoundException("NhAsset does not exist");
        }
        if (null == pending_order_price_asset_object) {
            throw new AssetNotFoundException("Price asset does not exist");
        }

        operations.create_nhasset_order_operation create_nhasset_order_operation = new operations.create_nhasset_order_operation();
        create_nhasset_order_operation.fee = pending_order_fee_asset.amount_from_string("0");
        create_nhasset_order_operation.seller = feePayAccountObject.id;
        create_nhasset_order_operation.otcaccount = otcAccount.id;
        create_nhasset_order_operation.pending_orders_fee = pending_order_fee_asset.amount_from_string(pending_order_fee);
        create_nhasset_order_operation.nh_asset = nhasset_object.id;
        create_nhasset_order_operation.memo = pending_order_memo;
        create_nhasset_order_operation.price = pending_order_price_asset_object.amount_from_string(pending_order_price);

        long valid_time = System.currentTimeMillis() + pending_order_valid_time_millis * 1000;
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        String expiration = sDateFormat.format(valid_time);
        Date dateObject = sDateFormat.parse(expiration);
        create_nhasset_order_operation.expiration = dateObject;

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CREATE_NH_ASSET_ORDER_OPERATION);
        operateList.add(create_nhasset_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(pending_order_fee_asset.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, pending_order_fee_asset.precision)));
        return asset_fee_objects;
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
    public String create_nh_asset_order(String otcaccount, String seller, String password, AccountDao accountDao, String pending_order_nh_asset, String pending_order_fee, String pending_order_fee_symbol, String pending_order_memo, String pending_order_price, String pending_order_price_symbol, long pending_order_valid_time_millis) throws NetworkStatusException, NhAssetNotFoundException, AssetNotFoundException, AccountNotFoundException, AuthorityException, PasswordVerifyException, KeyInvalideException, AddressFormatException, ParseException {

        account_object feePayAccountObject = get_account_object(seller);
        account_object otcAccount = get_account_object(otcaccount);
        nhasset_object nhasset_object = lookup_nh_asset_object(pending_order_nh_asset);
        asset_object pending_order_price_asset_object = lookup_asset_symbols(pending_order_price_symbol);
        asset_object pending_order_fee_asset = lookup_asset_symbols(pending_order_fee_symbol);

        if (feePayAccountObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        if (unlock(feePayAccountObject.name, password, accountDao) != OPERATE_SUCCESS && verify_password(feePayAccountObject.name, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        if (nhasset_object == null) {
            throw new NhAssetNotFoundException("NhAsset does not exist");
        }
        if (null == pending_order_price_asset_object) {
            throw new AssetNotFoundException("Price asset does not exist");
        }

        operations.create_nhasset_order_operation create_nhasset_order_operation = new operations.create_nhasset_order_operation();
        create_nhasset_order_operation.fee = pending_order_fee_asset.amount_from_string("0");
        create_nhasset_order_operation.seller = feePayAccountObject.id;
        create_nhasset_order_operation.otcaccount = otcAccount.id;
        create_nhasset_order_operation.pending_orders_fee = pending_order_fee_asset.amount_from_string(pending_order_fee);
        create_nhasset_order_operation.nh_asset = nhasset_object.id;
        create_nhasset_order_operation.memo = pending_order_memo;
        create_nhasset_order_operation.price = pending_order_price_asset_object.amount_from_string(pending_order_price);

        long valid_time = System.currentTimeMillis() + pending_order_valid_time_millis * 1000;
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        String expiration = sDateFormat.format(valid_time);
        LogUtils.i("SimpleDateFormat", expiration);
        Date dateObject = sDateFormat.parse(expiration);
        create_nhasset_order_operation.expiration = dateObject;

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CREATE_NH_ASSET_ORDER_OPERATION);
        operateList.add(create_nhasset_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(pending_order_fee_asset.id);

        List<asset_fee_object> requiredFees = mWebSocketApi.get_required_fees(objectList);
        create_nhasset_order_operation.fee = pending_order_fee_asset.amount_from_string(String.valueOf(Double.valueOf(requiredFees.get(0).amount) / (Math.pow(10, pending_order_fee_asset.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = create_nhasset_order_operation;
        operationType.nOperationType = operations.ID_CREATE_NH_ASSET_ORDER_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();

        return sign_transaction(transactionWithCallback, feePayAccountObject);
    }


    /**
     * calculate upgrade to lifetime member fee
     *
     * @param fee_paying_asset_id_or_symbol
     * @param upgrade_account_id_or_symbol
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     */
    public List<asset_fee_object> upgrade_to_lifetime_member_fee(String upgrade_account_id_or_symbol, String fee_paying_asset_id_or_symbol) throws NetworkStatusException, AccountNotFoundException, AssetNotFoundException {

        account_object upgrade_account_object = get_account_object(upgrade_account_id_or_symbol);
        if (upgrade_account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }
        asset_object fee_paying_asset_object = lookup_asset_symbols(fee_paying_asset_id_or_symbol);
        if (fee_paying_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }
        operations.upgrade_to_lifetime_member_operation upgrade_to_lifetime_member_operation = new operations.upgrade_to_lifetime_member_operation();
        upgrade_to_lifetime_member_operation.fee = fee_paying_asset_object.amount_from_string("0");
        upgrade_to_lifetime_member_operation.account_to_upgrade = upgrade_account_object.id;
        upgrade_to_lifetime_member_operation.upgrade_to_lifetime_member = true;
        upgrade_to_lifetime_member_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_UPGRADE_TO_LIFETIME_MEMBER_OPERATION);
        operateList.add(upgrade_to_lifetime_member_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_paying_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_paying_asset_object.precision)));
        return asset_fee_objects;
    }


    /**
     * calculate upgrade to lifetime member
     *
     * @param fee_paying_asset_id_or_symbol
     * @param upgrade_account_id_or_symbol
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     */
    public String upgrade_to_lifetime_member(String upgrade_account_id_or_symbol, String upgrade_account_password, String fee_paying_asset_id_or_symbol, AccountDao accountDao) throws NetworkStatusException, AccountNotFoundException, AssetNotFoundException, AuthorityException, PasswordVerifyException, KeyInvalideException, AddressFormatException {

        account_object upgrade_account_object = get_account_object(upgrade_account_id_or_symbol);
        if (upgrade_account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        if (unlock(upgrade_account_object.name, upgrade_account_password, accountDao) != OPERATE_SUCCESS && verify_password(upgrade_account_object.name, upgrade_account_password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        asset_object fee_paying_asset_object = lookup_asset_symbols(fee_paying_asset_id_or_symbol);
        if (fee_paying_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }
        operations.upgrade_to_lifetime_member_operation upgrade_to_lifetime_member_operation = new operations.upgrade_to_lifetime_member_operation();
        upgrade_to_lifetime_member_operation.fee = fee_paying_asset_object.amount_from_string("0");
        upgrade_to_lifetime_member_operation.account_to_upgrade = upgrade_account_object.id;
        upgrade_to_lifetime_member_operation.upgrade_to_lifetime_member = true;
        upgrade_to_lifetime_member_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_UPGRADE_TO_LIFETIME_MEMBER_OPERATION);
        operateList.add(upgrade_to_lifetime_member_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_paying_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        upgrade_to_lifetime_member_operation.fee = fee_paying_asset_object.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_paying_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = upgrade_to_lifetime_member_operation;
        operationType.nOperationType = operations.ID_UPGRADE_TO_LIFETIME_MEMBER_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();

        return sign_transaction(transactionWithCallback, upgrade_account_object);
    }


    /**
     * calculate create child account fee
     *
     * @param paramEntity
     * @param registrar_account_id_or_symbol
     * @param pay_asset_symbol_or_id
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     */
    public List<asset_fee_object> create_child_account_fee(CreateAccountParamEntity paramEntity, String registrar_account_id_or_symbol, String pay_asset_symbol_or_id) throws NetworkStatusException, AccountNotFoundException, AssetNotFoundException, AccountExistException, UnLegalInputException {

        private_key privateActiveKey = private_key.from_seed(paramEntity.getActiveSeed());
        private_key privateOwnerKey = private_key.from_seed(paramEntity.getOwnerSeed());
        types.public_key_type publicActiveKeyType = new types.public_key_type(privateActiveKey.get_public_key());
        types.public_key_type publicOwnerKeyType = new types.public_key_type(privateOwnerKey.get_public_key());
        account_object child_account_object = lookup_account_names(paramEntity.getAccountName());
        account_object registrar_account_object = get_account_object(registrar_account_id_or_symbol);
        asset_object pay_asset_object = lookup_asset_symbols(pay_asset_symbol_or_id);

        if (registrar_account_object == null) {
            throw new AccountNotFoundException("Registrar account does not exist");
        }
        if (pay_asset_symbol_or_id == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }
        if (child_account_object != null) {
            throw new AccountExistException("Account already exist");
        }
        authority1 owner = new authority1(1, publicOwnerKeyType, 1);
        authority1 active = new authority1(1, publicActiveKeyType, 1);
        types.account_options options = new types.account_options();
        options.extensions = new HashSet<>();
        options.memo_key = publicActiveKeyType;
        options.votes = new HashSet<>();
        options.num_committee = 0;
        options.num_witness = 0;
        options.voting_account = new object_id<>(1, 2, 5);
        operations.create_child_account_operation create_child_account_operation = new operations.create_child_account_operation();
        create_child_account_operation.fee = pay_asset_object.amount_from_string("0");
        create_child_account_operation.registrar = registrar_account_object.id;
        create_child_account_operation.referrer = registrar_account_object.id;
        create_child_account_operation.referrer_percent = 0;
        create_child_account_operation.name = paramEntity.getAccountName();
        create_child_account_operation.owner = owner;
        create_child_account_operation.active = active;
        create_child_account_operation.options = options;
        create_child_account_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CREATE_CHILD_ACCOUNT_OPERATION);
        operateList.add(create_child_account_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(pay_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        LogUtils.i("create_child_asset_fee_objects", global_config_object.getInstance().getGsonBuilder().create().toJson(asset_fee_objects));
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, pay_asset_object.precision)));
        return asset_fee_objects;
    }


    /**
     * calculate create child account
     *
     * @param paramEntity
     * @param registrar_account_id_or_symbol
     * @param pay_asset_symbol_or_id
     * @return
     * @throws NetworkStatusException
     * @throws AccountNotFoundException
     */
    public String create_child_account(CreateAccountParamEntity paramEntity, String registrar_account_id_or_symbol, String registrar_account_password, String pay_asset_symbol_or_id, AccountDao accountDao)
            throws NetworkStatusException, AccountNotFoundException, AssetNotFoundException, AccountExistException, UnLegalInputException, AuthorityException, PasswordVerifyException, KeyInvalideException, AddressFormatException {

        private_key privateActiveKey = private_key.from_seed(paramEntity.getActiveSeed());
        private_key privateOwnerKey = private_key.from_seed(paramEntity.getOwnerSeed());
        types.public_key_type publicActiveKeyType = new types.public_key_type(privateActiveKey.get_public_key());
        types.public_key_type publicOwnerKeyType = new types.public_key_type(privateOwnerKey.get_public_key());
        account_object child_account_object = lookup_account_names(paramEntity.getAccountName());
        account_object registrar_account_object = get_account_object(registrar_account_id_or_symbol);
        asset_object pay_asset_object = lookup_asset_symbols(pay_asset_symbol_or_id);

        if (registrar_account_object == null) {
            throw new AccountNotFoundException("Registrar account does not exist");
        }

        if (unlock(registrar_account_object.name, registrar_account_password, accountDao) != OPERATE_SUCCESS && verify_password(registrar_account_object.name, registrar_account_password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        if (pay_asset_object == null) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        if (child_account_object != null) {
            throw new AccountExistException("Account already exist");
        }
        authority1 owner = new authority1(1, publicOwnerKeyType, 1);
        authority1 active = new authority1(1, publicActiveKeyType, 1);
        types.account_options options = new types.account_options();
        options.extensions = new HashSet<>();
        options.memo_key = publicActiveKeyType;
        options.votes = new HashSet<>();
        options.num_committee = 0;
        options.num_witness = 0;
        options.voting_account = new object_id<>(1, 2, 5);
        operations.create_child_account_operation create_child_account_operation = new operations.create_child_account_operation();
        create_child_account_operation.fee = pay_asset_object.amount_from_string("0");
        create_child_account_operation.registrar = registrar_account_object.id;
        create_child_account_operation.referrer = registrar_account_object.id;
        create_child_account_operation.referrer_percent = 0;
        create_child_account_operation.name = paramEntity.getAccountName();
        create_child_account_operation.owner = owner;
        create_child_account_operation.active = active;
        create_child_account_operation.options = options;
        create_child_account_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CREATE_CHILD_ACCOUNT_OPERATION);
        operateList.add(create_child_account_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(pay_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        create_child_account_operation.fee = pay_asset_object.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, pay_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = create_child_account_operation;
        operationType.nOperationType = operations.ID_CREATE_CHILD_ACCOUNT_OPERATION;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();

        return sign_transaction(transactionWithCallback, registrar_account_object);
    }


    /**
     * get asset object by asset symbol
     *
     * @param assetsSymbolOrId ：asset symbol or asset id
     * @return asset object
     * @throws NetworkStatusException
     */
    public asset_object lookup_asset_symbols(String assetsSymbolOrId) throws NetworkStatusException {
        return mWebSocketApi.lookup_asset_symbols(assetsSymbolOrId);
    }

    /**
     * get asset by id
     *
     * @param listAssetObjectId ：asset ID
     * @return asset object
     * @throws NetworkStatusException
     */
    private List<asset_object> get_assets(List<object_id<asset_object>> listAssetObjectId) throws NetworkStatusException {
        return mWebSocketApi.get_assets(listAssetObjectId);
    }


    /**
     * get account by account id
     *
     * @param account_id ：account id
     * @return account object
     * @throws NetworkStatusException
     */
    public account_object get_accounts(String account_id) throws NetworkStatusException, UnLegalInputException {
        List<object_id<account_object>> account_ids = new ArrayList<>();
        object_id<account_object> accountId = object_id.create_from_string(account_id);
        if (null == accountId) {
            throw new UnLegalInputException("Please input account id");
        }
        account_ids.add(accountId);
        return mWebSocketApi.get_accounts(account_ids).get(0);
    }


    /**
     * get account info
     *
     * @param strAccountNameOrId ：strAccountNameOrId
     * @return account info
     */
    public account_object get_account_object(String strAccountNameOrId) throws NetworkStatusException, AccountNotFoundException {

        object_id<account_object> objectId = object_id.create_from_string(strAccountNameOrId);

        List<account_object> listAccountObject = null;

        if (objectId == null) {
            listAccountObject = mWebSocketApi.lookup_account_names(strAccountNameOrId);
        } else {
            List<object_id<account_object>> listObjectId = new ArrayList<>();
            listObjectId.add(objectId);
            listAccountObject = mWebSocketApi.get_accounts(listObjectId);
        }
        if (listAccountObject.isEmpty() || null == listAccountObject.get(0)) {
            throw new AccountNotFoundException("Account does not exist");
        }
        return listAccountObject.get(0);
    }


    /**
     * get account info by account name
     *
     * @param account_name ：account_name
     * @return account info
     */
    public account_object lookup_account_names(String account_name) throws NetworkStatusException, UnLegalInputException {
        object_id<account_object> accountId = object_id.create_from_string(account_name);
        if (null != accountId) {
            throw new UnLegalInputException("Please input account name");
        }
        return mWebSocketApi.lookup_account_names(account_name).get(0);
    }


    /**
     * get_full_accounts and subscribe
     *
     * @throws NetworkStatusException
     */
    public Object get_full_accounts(String names_or_id, boolean subscribe) throws NetworkStatusException {
        return mWebSocketApi.get_full_accounts(names_or_id, subscribe);
    }


    /**
     * list all assets in chain
     *
     * @throws NetworkStatusException
     */
    public List<asset_object> list_assets(String strLowerBound, int nLimit) throws NetworkStatusException {
        return mWebSocketApi.list_assets(strLowerBound, nLimit);
    }


    /**
     * lookup_nh_asset get NH asset detail by NH asset id or hash
     *
     * @param nh_asset_ids_or_hash
     * @return
     * @throws NetworkStatusException
     * @throws NhAssetNotFoundException
     */
    public List<Object> lookup_nh_asset(List<String> nh_asset_ids_or_hash) throws NetworkStatusException, NhAssetNotFoundException {
        List<Object> nhasset_object = mWebSocketApi.lookup_nh_asset(nh_asset_ids_or_hash);
        if (nhasset_object == null || nhasset_object.size() <= 0) {
            throw new NhAssetNotFoundException("Nhasset does not exist");
        }
        return nhasset_object;
    }

    /**
     * lookup_nh_asset get NH asset detail by NH asset id or hash
     *
     * @param nh_asset_ids_or_hash
     * @return
     * @throws NetworkStatusException
     * @throws NhAssetNotFoundException
     */
    public nhasset_object lookup_nh_asset_object(String nh_asset_ids_or_hash) throws NetworkStatusException, NhAssetNotFoundException {
        List<nhasset_object> nhasset_object = mWebSocketApi.lookup_nh_asset_object(nh_asset_ids_or_hash);
        if (nhasset_object == null || nhasset_object.size() <= 0) {
            throw new NhAssetNotFoundException("Nhasset does not exist");
        }
        return nhasset_object.get(0);
    }


    /**
     * lookup_nh_asset get account NH asset by account id or name
     *
     * @throws NetworkStatusException
     */
    public List<Object> list_account_nh_asset(String account_id_or_name, List<String> world_view_name_or_ids, int page, int pageSize) throws NetworkStatusException, AccountNotFoundException {
        account_object account_object = get_account_object(account_id_or_name);
        if (account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }
        return mWebSocketApi.list_account_nh_asset(account_object.id.toString(), world_view_name_or_ids, page, pageSize);
    }


    /**
     * list account nh asset order
     *
     * @throws NetworkStatusException
     */
    public List<Object> list_account_nh_asset_order(String account_id_or_name, int pageSize, int page) throws NetworkStatusException, AccountNotFoundException {
        account_object account_object = get_account_object(account_id_or_name);
        if (account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }
        return mWebSocketApi.list_account_nh_asset_order(account_object.id.toString(), pageSize, page);
    }

    /**
     * list nh asset order
     *
     * @throws NetworkStatusException
     */
    public List<Object> list_nh_asset_order(String asset_id_or_symbol, String world_view_name_or_ids, String baseDescribe, int pageSize, int page) throws NetworkStatusException {
        return mWebSocketApi.list_nh_asset_order(asset_id_or_symbol, world_view_name_or_ids, baseDescribe, pageSize, page);
    }

    /**
     * list nh asset order no filter options
     *
     * @throws NetworkStatusException
     */
    public List<Object> list_nh_asset_order(int page, int pageSize) throws NetworkStatusException {
        return mWebSocketApi.list_nh_asset_order("", "", "", pageSize, page);
    }


    /**
     * Seek World View Details
     *
     * @throws NetworkStatusException
     */
    public List<world_view_object> lookup_world_view(List<String> world_view_names) throws NetworkStatusException, WordViewNotExistException {
        if (mWebSocketApi.lookup_world_view(world_view_names) == null || mWebSocketApi.lookup_world_view(world_view_names).get(0) == null) {
            throw new WordViewNotExistException("worldViews do not exist");
        }
        return mWebSocketApi.lookup_world_view(world_view_names);
    }

    /**
     * get Developer-Related World View
     *
     * @throws NetworkStatusException
     */
    public account_related_word_view_object get_nh_creator(String account_id_or_name) throws NetworkStatusException, AccountNotFoundException {
        account_object account_object = get_account_object(account_id_or_name);
        if (account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }
        return mWebSocketApi.get_nh_creator(account_object.id.toString());
    }


    /**
     * Query NH assets created by developers
     *
     * @throws NetworkStatusException
     */
    public List<Object> list_nh_asset_by_creator(String account_id, int page, int pageSize) throws NetworkStatusException, AccountNotFoundException, UnLegalInputException {
        account_object account_object = get_account_object(account_id);
        if (account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }
        return mWebSocketApi.list_nh_asset_by_creator(account_object.id.toString(), page, pageSize);
    }


    /**
     * get account by account_names_or_ids
     *
     * @param ids ：ids
     * @return account object
     * @throws NetworkStatusException
     */
    public List<Object> get_objects(List<String> ids) throws NetworkStatusException {
        return mWebSocketApi.get_objects(ids);
    }


    /**
     * get account by account_names_or_ids
     *
     * @param id ：ids
     * @return account object
     * @throws NetworkStatusException
     */
    public Object get_objects(String id) throws NetworkStatusException {
        return mWebSocketApi.get_objects(id);
    }


    /**
     * get account operate history
     *
     * @param accountNameOrId
     * @param nLimit
     * @return
     * @throws NetworkStatusException
     */
    public List<operation_history_object> get_account_history(String accountNameOrId, int nLimit) throws NetworkStatusException, AccountNotFoundException {
        account_object objectId = get_account_object(accountNameOrId);
        object_id<operation_history_object> startId = new object_id<>(0, operation_history_object.class);
        return mWebSocketApi.get_account_history(objectId.id, startId, nLimit);
    }


    /**
     * get all assets balances of account
     *
     * @param account_id
     * @return account balance
     * @throws NetworkStatusException
     */
    public List<asset_fee_object> get_all_account_balances(String account_id) throws NetworkStatusException, UnLegalInputException, AccountNotFoundException {

        object_id<account_object> accountId = object_id.create_from_string(account_id);
        if (null == accountId) {
            account_object account_object = lookup_account_names(account_id);
            if (null == account_object) {
                throw new AccountNotFoundException("Account does not exist");
            }
            account_id = account_object.id.toString();
        }

        List<asset_fee_object> assets = mWebSocketApi.get_all_account_balances(account_id);
        List<asset_fee_object> assetsObjects = new ArrayList<>();
        for (asset_fee_object asset : assets) {
            asset_object asset_object = lookup_asset_symbols(asset.asset_id.toString());
            asset.amount = String.valueOf(Double.valueOf(asset.amount) / (Math.pow(10, asset_object.precision)));
            assetsObjects.add(asset);
        }
        return assetsObjects;
    }

    /**
     * get_account_balances。
     *
     * @param account_id
     * @return account balance
     * @throws NetworkStatusException
     */
    public asset_fee_object get_account_balances(String account_id, String assetsId) throws NetworkStatusException, UnLegalInputException, AssetNotFoundException, AccountNotFoundException {

        object_id<account_object> accountId = object_id.create_from_string(account_id);
        if (null == accountId) {
            account_object account_object = lookup_account_names(account_id);
            if (null == account_object) {
                throw new AccountNotFoundException("Account does not exist");
            }
            account_id = account_object.id.toString();
        }

        asset_object asset_object = mWebSocketApi.lookup_asset_symbols(assetsId);
        if (null == asset_object) {
            throw new AssetNotFoundException("Asset does not exist");
        }

        List<Object> objects = new ArrayList<>();
        objects.add(asset_object.id.toString());
        List<asset_fee_object> assets = mWebSocketApi.get_account_balances(account_id, objects);
        asset_fee_object asset = assets.get(0);
        asset.amount = String.valueOf(Double.valueOf(asset.amount) / (Math.pow(10, asset_object.precision)));
        return asset;
    }


    /**
     * search  current dynamic_global_property_object
     *
     * @return
     * @throws NetworkStatusException
     */
    public dynamic_global_property_object get_dynamic_global_properties() throws NetworkStatusException {
        return mWebSocketApi.get_dynamic_global_properties();
    }


    /**
     * search  current  global_property_object
     *
     * @return
     * @throws NetworkStatusException
     */
    public global_property_object get_global_properties() throws NetworkStatusException {
        return mWebSocketApi.get_global_properties();
    }


    /**
     * get block header。
     *
     * @throws NetworkStatusException
     */
    public block_header get_block_header(String nBlockNumber) throws NetworkStatusException {
        return mWebSocketApi.get_block_header(nBlockNumber);
    }

    /**
     * get block header。
     *
     * @throws NetworkStatusException
     */
    public block_info get_block(String nBlockNumber) throws NetworkStatusException {
        return mWebSocketApi.get_block(nBlockNumber);
    }

    /**
     * get transaction in block info
     *
     * @throws NetworkStatusException
     */
    public transaction_in_block_info get_transaction_in_block_info(String tr_id) throws NetworkStatusException {
        return mWebSocketApi.get_transaction_in_block_info(tr_id);
    }


    /**
     * get transaction by tx_id
     *
     * @throws NetworkStatusException
     */
    public Object get_transaction_by_id(String tr_id) throws NetworkStatusException {
        return mWebSocketApi.get_transaction_by_id(tr_id);
    }


    /**
     * decrypt memo
     *
     * @param mMemoJson
     * @return
     */
    public void decrypt_memo_message(String accountName, String password, String mMemoJson, AccountDao accountDao, IBcxCallBack callBack) {
        try {
            int result = unlock(accountName, password, accountDao);
            if (result == ERROR_OBJECT_NOT_FOUND) {
                rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, "Account does not exist", null).toString();
                callBack.onReceiveValue(rspText);
                return;
            } else if (result == ERROR_WRONG_PASSWORD) {
                rspText = new ResponseData(ERROR_WRONG_PASSWORD, "Wrong password", null).toString();
                callBack.onReceiveValue(rspText);
                return;
            }
            memo_data memoData = global_config_object.getInstance().getGsonBuilder().create().fromJson(mMemoJson, memo_data.class);
            String strMessage = null;
            types.private_key_type privateKeyType = mHashMapPub2Private.get(memoData.to);
            if (privateKeyType != null) {
                strMessage = memoData.get_message(privateKeyType.getPrivateKey(), memoData.from.getPublicKey());
            } else {
                privateKeyType = mHashMapPub2Private.get(memoData.from);
                if (privateKeyType != null) {
                    strMessage = memoData.get_message(privateKeyType.getPrivateKey(), memoData.to.getPublicKey());
                }
            }
            rspText = new ResponseData(OPERATE_SUCCESS, "success", strMessage).toString();
            callBack.onReceiveValue(rspText);
        } catch (JsonSyntaxException e) {
            rspText = new ResponseData(ERROR_PARAMETER_DATA_TYPE, "Please check parameter type", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (KeyInvalideException e) {
            e.printStackTrace();
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
    }


    /**
     * encrypt private key ;
     *
     * @return keyStore
     */
    private void encrypt_keys(String name, String accountId, String password, String accountType, AccountDao accountDao) {
        mCheckSum = sha512_object.create_from_string(password);
        plain_keys data = new plain_keys();
        data.keys = new HashMap<>();

        for (Map.Entry<types.public_key_type, types.private_key_type> entry : mHashMapPub2Private.entrySet()) {
            data.keys.put(entry.getKey(), entry.getValue().toString());
        }

        data.checksum = mCheckSum;
        data_stream_size_encoder sizeEncoder = new data_stream_size_encoder();
        data.write_to_encoder(sizeEncoder);
        data_stream_encoder encoder = new data_stream_encoder(sizeEncoder.getSize());
        data.write_to_encoder(encoder);
        byte[] byteKey = new byte[32];
        System.arraycopy(mCheckSum.hash, 0, byteKey, 0, byteKey.length);
        byte[] ivBytes = new byte[16];
        System.arraycopy(mCheckSum.hash, 32, ivBytes, 0, ivBytes.length);
        ByteBuffer byteResult = aes.encrypt(byteKey, ivBytes, encoder.getData());
        mWalletObject.cipher_keys = byteResult;
        Gson gson = global_config_object.getInstance().getGsonBuilder().create();
        accountDao.insertAccount(name, accountId, gson.toJson(mWalletObject), accountType, CocosBcxApiWrapper.chainId);
    }


    /**
     * save_account account : encrypt and save account
     *
     * @return
     */
    private void save_account(String name, String accountId, String password, String accountType, AccountDao accountDao) {
        // encrypt and save account
        encrypt_keys(name, accountId, password, accountType, accountDao);
        // clear data in memory
        lock();
    }


    /**
     * clear private data out of memory;
     *
     * @return
     */
    public void lock() {
        mCheckSum = new sha512_object();
        mHashMapPub2Private.clear();
    }


    /**
     * unlock account :means load private key in memory
     *
     * @param accountName
     * @param strPassword
     * @param accountDao
     * @return
     */
    private int unlock(String accountName, String strPassword, AccountDao accountDao) throws KeyInvalideException, AddressFormatException {

        assert (strPassword.length() > 0);
        sha512_object passwordHash = sha512_object.create_from_string(strPassword);
        byte[] byteKey = new byte[32];
        System.arraycopy(passwordHash.hash, 0, byteKey, 0, byteKey.length);
        byte[] ivBytes = new byte[16];
        System.arraycopy(passwordHash.hash, 32, ivBytes, 0, ivBytes.length);
        // get keystore from db
        AccountEntity.AccountBean accountBean = get_dao_account_by_name(accountName, accountDao);
        if (accountBean == null || TextUtils.isEmpty(accountBean.getKeystore())) {
            return ERROR_OBJECT_NOT_FOUND;
        }
        Gson gson = global_config_object.getInstance().getGsonBuilder().create();
        mWalletObject = gson.fromJson(accountBean.getKeystore(), wallet_object.class);
        ByteBuffer byteDecrypt = aes.decrypt(byteKey, ivBytes, mWalletObject.cipher_keys.array());
        if (byteDecrypt == null || byteDecrypt.array().length == 0) {
            return ERROR_WRONG_PASSWORD;
        }

        plain_keys dataResult = plain_keys.from_input_stream(new ByteArrayInputStream(byteDecrypt.array()));
        for (Map.Entry<types.public_key_type, String> entry : dataResult.keys.entrySet()) {
            types.private_key_type privateKeyType = new types.private_key_type(entry.getValue());
            mHashMapPub2Private.put(entry.getKey(), privateKeyType);
        }
        mCheckSum = passwordHash;
        if (passwordHash.equals(dataResult.checksum)) {
            return OPERATE_SUCCESS;
        } else {
            return ERROR_WRONG_PASSWORD;
        }
    }


    /**
     * export private key
     *
     * @param accountName account name
     * @param password    the key you set to encrypt your private key;
     * @return
     */
    public void export_private_key(String accountName, String password, AccountDao accountDao, IBcxCallBack callBack) {
        try {
            // get keystore
            AccountEntity.AccountBean accountBean = get_dao_account_by_name(accountName, accountDao);
            if (null == accountBean) {
                rspText = new ResponseData(ERROR_UNLOCK_ACCOUNT, "Please login in or import the private key first", null).toString();
                callBack.onReceiveValue(rspText);
                return;
            }
            Gson gson = global_config_object.getInstance().getGsonBuilder().create();
            mWalletObject = gson.fromJson(accountBean.getKeystore(), wallet_object.class);
            //decrypt keystore return private keys
            Map<String, String> private_keys = decrypt_keystore_callback_private_key(password);
            if (private_keys.size() <= 0) {
                private_keys = verify_password(accountName, password);
            }
            rspText = new ResponseData(OPERATE_SUCCESS, "success", private_keys).toString();
            callBack.onReceiveValue(rspText);
            // clear data out of memory;
            lock();
        } catch (JsonSyntaxException e) {
            rspText = new ResponseData(ERROR_PARAMETER_DATA_TYPE, "Please check parameter types", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (NetworkStatusException e) {
            rspText = new ResponseData(ERROR_NETWORK_FAIL, e.getMessage(), null).toString();
            callBack.onReceiveValue(rspText);
        } catch (AccountNotFoundException e) {
            rspText = new ResponseData(ERROR_OBJECT_NOT_FOUND, e.getMessage(), null).toString();
            callBack.onReceiveValue(rspText);
        } catch (PasswordVerifyException e) {
            rspText = new ResponseData(ERROR_WRONG_PASSWORD, e.getMessage(), null).toString();
            callBack.onReceiveValue(rspText);
        } catch (KeyInvalideException e) {
            rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, "Please enter the correct private key", null).toString();
            callBack.onReceiveValue(rspText);
        } catch (AddressFormatException e) {
            rspText = new ResponseData(ERROR_INVALID_PRIVATE_KEY, "Please enter the correct private key", null).toString();
            callBack.onReceiveValue(rspText);
        }
    }


    /**
     * decrypt keyStore
     *
     * @param strPassword
     * @return privateKeys
     */
    private Map<String, String> decrypt_keystore_callback_private_key(String strPassword) throws KeyInvalideException, AddressFormatException {

        assert (strPassword.length() > 0);
        sha512_object passwordHash = sha512_object.create_from_string(strPassword);
        byte[] byteKey = new byte[32];
        System.arraycopy(passwordHash.hash, 0, byteKey, 0, byteKey.length);
        byte[] ivBytes = new byte[16];
        System.arraycopy(passwordHash.hash, 32, ivBytes, 0, ivBytes.length);

        ByteBuffer byteDecrypt = aes.decrypt(byteKey, ivBytes, this.mWalletObject.cipher_keys.array());
        if (byteDecrypt == null || byteDecrypt.array().length == 0) {
            return new HashMap<>();
        }
        mCheckSum = passwordHash;
        plain_keys dataResult = plain_keys.from_input_stream(new ByteArrayInputStream(byteDecrypt.array()));
        Map<String, String> private_key = new HashMap();

        for (Map.Entry<types.public_key_type, String> entry : dataResult.keys.entrySet()) {
            types.private_key_type privateKeyType = new types.private_key_type(entry.getValue());
            mHashMapPub2Private.put(entry.getKey(), privateKeyType);
            private_key.put(entry.getKey().toString(), entry.getValue());
        }
        if (passwordHash.equals(dataResult.checksum)) {
            return private_key;
        } else {
            return new HashMap<>();
        }
    }


    /**
     * createLimitOrder
     *
     * @return
     */
    public List<asset_fee_object> create_limit_order_fee(String seller, String transactionPair, int type, int validTime, BigDecimal price, BigDecimal amount, String fee_pay_asset_symbol_or_id) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, ParseException, AuthorityException, PasswordVerifyException, KeyInvalideException, UnLegalInputException {

        if (TextUtils.isEmpty(transactionPair)) {
            throw new UnLegalInputException("transactionPair can not be empty");
        }

        if (!transactionPair.contains("_")) {
            throw new UnLegalInputException("transactionPair is not right");
        }

        // get asset object
        asset_object feeAssetObject = lookup_asset_symbols(fee_pay_asset_symbol_or_id);

        String[] paris = transactionPair.split("_");

        asset_object firstAssetObject = lookup_asset_symbols(paris[0].trim());

        asset_object secondAssetObject = lookup_asset_symbols(paris[1].trim());

        if (null == firstAssetObject) {
            throw new AssetNotFoundException(paris[0].trim() + "does not exist");
        }

        if (null == secondAssetObject) {
            throw new AssetNotFoundException(paris[1].trim() + "does not exist");
        }

        account_object sellerObject = get_account_object(seller);
        if (sellerObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        if (validTime > 200000) {
            throw new InputMismatchException("error: validTime bigger than 200000");
        }

        operations.create_limit_order_operation create_limit_order_operation = new operations.create_limit_order_operation();
        create_limit_order_operation.fee = feeAssetObject.amount_from_string("0");
        create_limit_order_operation.seller = sellerObject.id;
        if (type == 0) {
            create_limit_order_operation.amount_to_sell = secondAssetObject.amount_from_string(price.multiply(amount).toString());
            create_limit_order_operation.min_to_receive = firstAssetObject.amount_from_string(amount.toString());
        } else if (type == 1) {
            create_limit_order_operation.amount_to_sell = firstAssetObject.amount_from_string(amount.toString());
            create_limit_order_operation.min_to_receive = secondAssetObject.amount_from_string(price.multiply(amount).toString());
        } else {
            throw new InputMismatchException("type not right");
        }

        dynamic_global_property_object dynamicGlobalPropertyObject = get_dynamic_global_properties();
        Date dateObject = dynamicGlobalPropertyObject.time;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObject);
        cal.add(Calendar.DATE, validTime);
        create_limit_order_operation.expiration = cal.getTime();

        create_limit_order_operation.fill_or_kill = false;
        create_limit_order_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CREATE_LIMIT_ORDER);
        operateList.add(create_limit_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(feeAssetObject.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);
        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, feeAssetObject.precision)));

        return asset_fee_objects;
    }


    /**
     * createLimitOrder
     *
     * @return
     */
    public String create_limit_order(String seller, String password, String transactionPair, int type, int validTime, BigDecimal price, BigDecimal amount, String fee_pay_asset_symbol_or_id, AccountDao accountDao) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, AuthorityException, PasswordVerifyException, KeyInvalideException, UnLegalInputException {

        if (TextUtils.isEmpty(transactionPair)) {
            throw new UnLegalInputException("transactionPair can not be empty");
        }

        if (!transactionPair.contains("_")) {
            throw new UnLegalInputException("transactionPair is not right");
        }

        // get asset object
        asset_object feeAssetObject = lookup_asset_symbols(fee_pay_asset_symbol_or_id);

        String[] paris = transactionPair.split("_");

        asset_object firstAssetObject = lookup_asset_symbols(paris[0].trim());

        asset_object secondAssetObject = lookup_asset_symbols(paris[1].trim());


        if (unlock(seller, password, accountDao) != OPERATE_SUCCESS && verify_password(seller, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        if (null == firstAssetObject) {
            throw new AssetNotFoundException(paris[0].trim() + "does not exist");
        }

        if (null == secondAssetObject) {
            throw new AssetNotFoundException(paris[1].trim() + "does not exist");
        }

        if (validTime > 200000) {
            throw new InputMismatchException("error: validTime bigger than 200000");
        }

        account_object sellerObject = get_account_object(seller);
        if (sellerObject == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        operations.create_limit_order_operation create_limit_order_operation = new operations.create_limit_order_operation();
        create_limit_order_operation.fee = feeAssetObject.amount_from_string("0");
        create_limit_order_operation.seller = sellerObject.id;
        if (type == 0) {
            create_limit_order_operation.amount_to_sell = firstAssetObject.amount_from_string(amount.toString());
            create_limit_order_operation.min_to_receive = secondAssetObject.amount_from_string(price.multiply(amount).toString());
        } else if (type == 1) {
            create_limit_order_operation.amount_to_sell = secondAssetObject.amount_from_string(price.multiply(amount).toString());
            create_limit_order_operation.min_to_receive = firstAssetObject.amount_from_string(amount.toString());
        } else {
            throw new InputMismatchException("type not right");
        }

        dynamic_global_property_object dynamicGlobalPropertyObject = get_dynamic_global_properties();
        Date dateObject = dynamicGlobalPropertyObject.time;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObject);
        cal.add(Calendar.DATE, validTime);
        create_limit_order_operation.expiration = cal.getTime();

        create_limit_order_operation.fill_or_kill = false;
        create_limit_order_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CREATE_LIMIT_ORDER);
        operateList.add(create_limit_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(feeAssetObject.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        create_limit_order_operation.fee = feeAssetObject.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, feeAssetObject.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = create_limit_order_operation;
        operationType.nOperationType = operations.ID_CREATE_LIMIT_ORDER;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, sellerObject);
    }


    /**
     * cancel limit order
     *
     * @return
     */
    public List<asset_fee_object> cancel_limit_order_fee(String fee_paying_account, String limit_order_id, String fee_pay_asset_symbol_or_id) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, OrderNotFoundException, NotAssetCreatorException {

        // get asset object
        asset_object feeAssetObject = lookup_asset_symbols(fee_pay_asset_symbol_or_id);

        account_object fee_paying_account_object = get_account_object(fee_paying_account);

        limit_orders_object limit_order_object = get_limit_order_object(limit_order_id);

        if (limit_order_object == null) {
            throw new OrderNotFoundException("order does not exist");
        }

        if (fee_paying_account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        if (feeAssetObject == null) {
            throw new AssetNotFoundException("Fee pay asset does not exist");
        }

        if (!TextUtils.equals(limit_order_object.seller.toString(), fee_paying_account_object.id.toString())) {
            throw new NotAssetCreatorException("You are not the order creator");
        }

        operations.cancel_limit_order_operation cancel_limit_order_operation = new operations.cancel_limit_order_operation();
        cancel_limit_order_operation.fee = feeAssetObject.amount_from_string("0");
        cancel_limit_order_operation.fee_paying_account = fee_paying_account_object.id;
        cancel_limit_order_operation.order = limit_order_object.id;
        cancel_limit_order_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CANCEL_LIMIT_ORDER);
        operateList.add(cancel_limit_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(feeAssetObject.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, feeAssetObject.precision)));

        return asset_fee_objects;
    }


    /**
     * cancel limit order
     *
     * @return
     */
    public String cancel_limit_order(String fee_paying_account, String password, String limit_order_id, String fee_pay_asset_symbol_or_id, AccountDao accountDao) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, AuthorityException, PasswordVerifyException, KeyInvalideException, OrderNotFoundException, NotAssetCreatorException {

        // get asset object
        asset_object feeAssetObject = lookup_asset_symbols(fee_pay_asset_symbol_or_id);

        account_object fee_paying_account_object = get_account_object(fee_paying_account);

        limit_orders_object limit_order_object = get_limit_order_object(limit_order_id);

        if (limit_order_object == null) {
            throw new OrderNotFoundException("order does not exist");
        }

        if (fee_paying_account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        if (feeAssetObject == null) {
            throw new AssetNotFoundException("Fee pay asset does not exist");
        }

        if (!TextUtils.equals(limit_order_object.seller.toString(), fee_paying_account_object.id.toString())) {
            throw new NotAssetCreatorException("You are not the order creator");
        }

        if (unlock(fee_paying_account, password, accountDao) != OPERATE_SUCCESS && verify_password(fee_paying_account, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.cancel_limit_order_operation cancel_limit_order_operation = new operations.cancel_limit_order_operation();
        cancel_limit_order_operation.fee = feeAssetObject.amount_from_string("0");
        cancel_limit_order_operation.fee_paying_account = fee_paying_account_object.id;
        cancel_limit_order_operation.order = limit_order_object.id;
        cancel_limit_order_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_CANCEL_LIMIT_ORDER);
        operateList.add(cancel_limit_order_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(feeAssetObject.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        cancel_limit_order_operation.fee = feeAssetObject.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, feeAssetObject.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = cancel_limit_order_operation;
        operationType.nOperationType = operations.ID_CANCEL_LIMIT_ORDER;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, fee_paying_account_object);
    }


    /**
     * get_limit_orders
     *
     * @return get_dao_account_by_name
     */
    public List<limit_orders_object> get_limit_orders(String transactionPair, int nLimit) throws NetworkStatusException, AssetNotFoundException, UnLegalInputException {

        if (TextUtils.isEmpty(transactionPair)) {
            throw new UnLegalInputException("transactionPair can not be empty");
        }

        if (!transactionPair.contains("_")) {
            throw new UnLegalInputException("transactionPair is not right");
        }

        String[] paris = transactionPair.split("_");

        asset_object firstAssetObject = lookup_asset_symbols(paris[0].trim());

        asset_object secondAssetObject = lookup_asset_symbols(paris[1].trim());

        if (null == firstAssetObject) {
            throw new AssetNotFoundException(paris[0].trim() + "does not exist");
        }

        if (null == secondAssetObject) {
            throw new AssetNotFoundException(paris[1].trim() + "does not exist");
        }
        return mWebSocketApi.get_limit_orders(firstAssetObject.id.toString(), secondAssetObject.id.toString(), nLimit);
    }


    /**
     * get_fill_order_history
     */
    public List<fill_order_history_object> get_fill_order_history(String transactionPair, int nLimit) throws NetworkStatusException, AssetNotFoundException, UnLegalInputException {

        if (TextUtils.isEmpty(transactionPair)) {
            throw new UnLegalInputException("transactionPair can not be empty");
        }

        if (!transactionPair.contains("_")) {
            throw new UnLegalInputException("transactionPair is not right");
        }

        String[] paris = transactionPair.split("_");

        asset_object firstAssetObject = lookup_asset_symbols(paris[0].trim());

        asset_object secondAssetObject = lookup_asset_symbols(paris[1].trim());

        if (null == firstAssetObject) {
            throw new AssetNotFoundException(paris[0].trim() + "does not exist");
        }

        if (null == secondAssetObject) {
            throw new AssetNotFoundException(paris[1].trim() + "does not exist");
        }
        return mWebSocketApi.get_fill_order_history(firstAssetObject.id.toString(), secondAssetObject.id.toString(), nLimit);
    }


    /**
     * get market history
     */
    public List<market_history_object> get_market_history(String quote_asset_symbol_or_id, String base_asset_symbol_or_id, long seconds, String start_time, String end_time) throws NetworkStatusException, AssetNotFoundException {

        asset_object quoteAssetObject = lookup_asset_symbols(quote_asset_symbol_or_id);

        asset_object baseAssetObject = lookup_asset_symbols(base_asset_symbol_or_id);

        if (null == quoteAssetObject) {
            throw new AssetNotFoundException("quote asset does not exist");
        }

        if (null == baseAssetObject) {
            throw new AssetNotFoundException("base asset does not exist");
        }

        return mWebSocketApi.get_market_history(quoteAssetObject.id.toString(), baseAssetObject.id.toString(), seconds, start_time, end_time);
    }

    /**
     * get market history
     */
    public List<asset_fee_object> update_feed_product_fee(String issuer, String asset_symbol_or_id, List<String> products, String fee_asset_symbol) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, UnLegalInputException {

        account_object issuerObject = get_account_object(issuer);
        if (issuerObject == null) {
            throw new AccountNotFoundException("issuer does not exist");
        }

        asset_object fee_asset_symbol_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_symbol_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        asset_object asset_to_update_object = lookup_asset_symbols(asset_symbol_or_id);

        if (null == asset_to_update_object) {
            throw new AssetNotFoundException("issue asset does not exist");
        }

        if (null == products || products.size() <= 0) {
            throw new UnLegalInputException("products can not be null");
        }

        List<object_id<account_object>> productIds = new ArrayList<>();

        for (String product : products) {
            account_object productObject = get_account_object(product);
            if (productObject == null) {
                productIds.clear();
                throw new AccountNotFoundException(product + "does not exist");
            }
            productIds.add(productObject.id);
        }

        operations.update_feed_product_operation update_feed_product_operation = new operations.update_feed_product_operation();
        update_feed_product_operation.fee = fee_asset_symbol_object.amount_from_string("0");
        update_feed_product_operation.issuer = issuerObject.id;
        update_feed_product_operation.asset_to_update = asset_to_update_object.id;
        update_feed_product_operation.new_feed_producers = productIds;
        update_feed_product_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_UPDATE_FEED_PRODUCT);
        operateList.add(update_feed_product_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_symbol_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_symbol_object.precision)));

        return asset_fee_objects;
    }


    /**
     * update feed_object product
     */
    public String update_feed_product(String issuer, String password, String asset_symbol_or_id, List<String> products, String fee_asset_symbol, AccountDao accountDao) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, PasswordVerifyException, KeyInvalideException, UnLegalInputException, AuthorityException {

        account_object issuerObject = get_account_object(issuer);
        if (issuerObject == null) {
            throw new AccountNotFoundException("issuer does not exist");
        }

        asset_object fee_asset_symbol_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_symbol_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        asset_object asset_to_update_object = lookup_asset_symbols(asset_symbol_or_id);
        if (null == asset_to_update_object) {
            throw new AssetNotFoundException("issue asset does not exist");
        }

        if (null == products || products.size() <= 0) {
            throw new UnLegalInputException("products can not be null");
        }

        List<object_id<account_object>> productIds = new ArrayList<>();

        for (String product : products) {
            account_object productObject = get_account_object(product);
            if (productObject == null) {
                productIds.clear();
                throw new AccountNotFoundException(product + "does not exist");
            }
            productIds.add(productObject.id);
        }

        if (unlock(issuer, password, accountDao) != OPERATE_SUCCESS && verify_password(issuer, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.update_feed_product_operation update_feed_product_operation = new operations.update_feed_product_operation();
        update_feed_product_operation.fee = fee_asset_symbol_object.amount_from_string("0");
        update_feed_product_operation.issuer = issuerObject.id;
        update_feed_product_operation.asset_to_update = asset_to_update_object.id;
        update_feed_product_operation.new_feed_producers = productIds;
        update_feed_product_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_UPDATE_FEED_PRODUCT);
        operateList.add(update_feed_product_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_symbol_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        update_feed_product_operation.fee = fee_asset_symbol_object.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_symbol_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = update_feed_product_operation;
        operationType.nOperationType = operations.ID_UPDATE_FEED_PRODUCT;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, issuerObject);
    }


    /**
     * publish feed
     */
    public String publish_feed(String publisher, String password, String base_asset_symbol, String price,
                               BigDecimal maintenance_collateral_ratio, BigDecimal maximum_short_squeeze_ratio,
                               String core_exchange_rate_base, String core_exchange_rate_quote, String core_exchange_quote_symbol, String fee_asset_symbol, AccountDao accountDao) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, PasswordVerifyException, KeyInvalideException, UnLegalInputException, AuthorityException {

        account_object publisherObject = get_account_object(publisher);
        if (publisherObject == null) {
            throw new AccountNotFoundException("publisher does not exist");
        }

        asset_object quote_asset_symbol_object = lookup_asset_symbols(core_exchange_quote_symbol);
        if (null == quote_asset_symbol_object) {
            throw new AssetNotFoundException("quote asset does not exist");
        }

        asset_object base_asset_symbol_object = lookup_asset_symbols(base_asset_symbol);
        if (null == base_asset_symbol_object) {
            throw new AssetNotFoundException("base asset does not exist");
        }

        asset_object fee_asset_symbol_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_symbol_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        if (unlock(publisher, password, accountDao) != OPERATE_SUCCESS && verify_password(publisher, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.publish_feed_operation publish_feed_operation = new operations.publish_feed_operation();
        feed_object feed_object = new feed_object();
        feed_object.CoreExchangeRateBean core_exchange_rate = new feed_object.CoreExchangeRateBean();
        feed_object.SettlementPriceBean settlement_price = new feed_object.SettlementPriceBean();
        core_exchange_rate.base = base_asset_symbol_object.amount_from_string(core_exchange_rate_base);
        core_exchange_rate.quote = quote_asset_symbol_object.amount_from_string(core_exchange_rate_quote);
        settlement_price.base = base_asset_symbol_object.amount_from_string(price);
        settlement_price.quote = quote_asset_symbol_object.amount_from_string(price);
        feed_object.core_exchange_rate = core_exchange_rate;
        feed_object.settlement_price = settlement_price;
        feed_object.maintenance_collateral_ratio = maintenance_collateral_ratio.multiply(BigDecimal.valueOf(1000));
        feed_object.maximum_short_squeeze_ratio = maximum_short_squeeze_ratio.multiply(BigDecimal.valueOf(1000));
        publish_feed_operation.fee = fee_asset_symbol_object.amount_from_string("0");
        publish_feed_operation.publisher = publisherObject.id;
        publish_feed_operation.asset_id = base_asset_symbol_object.id;
        publish_feed_operation.feed = feed_object;
        publish_feed_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_PUBLISH_FEED);
        operateList.add(publish_feed_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_symbol_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        publish_feed_operation.fee = fee_asset_symbol_object.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_symbol_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = publish_feed_operation;
        operationType.nOperationType = operations.ID_PUBLISH_FEED;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, publisherObject);
    }


    /**
     * publish feed fee
     */
    public List<asset_fee_object> publish_feed_fee(String publisher, String base_asset_symbol, String price,
                                                   BigDecimal maintenance_collateral_ratio, BigDecimal maximum_short_squeeze_ratio,
                                                   String core_exchange_rate_base, String core_exchange_rate_quote, String core_exchange_quote_symbol, String fee_asset_symbol) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException {

        account_object publisherObject = get_account_object(publisher);
        if (publisherObject == null) {
            throw new AccountNotFoundException("publisher does not exist");
        }

        asset_object quote_asset_symbol_object = lookup_asset_symbols(core_exchange_quote_symbol);
        if (null == quote_asset_symbol_object) {
            throw new AssetNotFoundException("quote asset does not exist");
        }

        asset_object base_asset_symbol_object = lookup_asset_symbols(base_asset_symbol);
        if (null == base_asset_symbol_object) {
            throw new AssetNotFoundException("base asset does not exist");
        }

        asset_object fee_asset_symbol_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_symbol_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        operations.publish_feed_operation publish_feed_operation = new operations.publish_feed_operation();
        feed_object feed_object = new feed_object();
        feed_object.CoreExchangeRateBean core_exchange_rate = new feed_object.CoreExchangeRateBean();
        feed_object.SettlementPriceBean settlement_price = new feed_object.SettlementPriceBean();
        core_exchange_rate.base = base_asset_symbol_object.amount_from_string(core_exchange_rate_base);
        core_exchange_rate.quote = quote_asset_symbol_object.amount_from_string(core_exchange_rate_quote);
        settlement_price.base = base_asset_symbol_object.amount_from_string(price);
        settlement_price.quote = quote_asset_symbol_object.amount_from_string(price);
        feed_object.core_exchange_rate = core_exchange_rate;
        feed_object.settlement_price = settlement_price;
        feed_object.maintenance_collateral_ratio = maintenance_collateral_ratio.multiply(BigDecimal.valueOf(1000));
        feed_object.maximum_short_squeeze_ratio = maximum_short_squeeze_ratio.multiply(BigDecimal.valueOf(1000));
        publish_feed_operation.fee = fee_asset_symbol_object.amount_from_string("0");
        publish_feed_operation.publisher = publisherObject.id;
        publish_feed_operation.asset_id = base_asset_symbol_object.id;
        publish_feed_operation.feed = feed_object;
        publish_feed_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_PUBLISH_FEED);
        operateList.add(publish_feed_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_symbol_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_symbol_object.precision)));

        return asset_fee_objects;
    }


    /**
     * asset settle
     */
    public String asset_settle(String account, String password, String settle_asset_symbol, String settle_asset_amount, String fee_asset_symbol, AccountDao accountDao) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, PasswordVerifyException, KeyInvalideException, AuthorityException {

        account_object account_object = get_account_object(account);
        if (account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        asset_object settle_asset_object = lookup_asset_symbols(settle_asset_symbol);
        if (null == settle_asset_object) {
            throw new AssetNotFoundException("settle asset does not exist");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        if (unlock(account, password, accountDao) != OPERATE_SUCCESS && verify_password(account, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.asset_settle_operation asset_settle_operation = new operations.asset_settle_operation();
        asset_settle_operation.fee = fee_asset_object.amount_from_string("0");
        asset_settle_operation.account = account_object.id;
        asset_settle_operation.amount = settle_asset_object.amount_from_string(settle_asset_amount);
        asset_settle_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_ASSET_SETTLE);
        operateList.add(asset_settle_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        asset_settle_operation.fee = fee_asset_object.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = asset_settle_operation;
        operationType.nOperationType = operations.ID_ASSET_SETTLE;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, account_object);
    }


    /**
     * asset settle fee
     */
    public List<asset_fee_object> asset_settle_fee(String account, String settle_asset_symbol, String settle_asset_amount, String fee_asset_symbol) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException {

        account_object account_object = get_account_object(account);
        if (account_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        asset_object settle_asset_object = lookup_asset_symbols(settle_asset_symbol);
        if (null == settle_asset_object) {
            throw new AssetNotFoundException("settle asset does not exist");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        operations.asset_settle_operation asset_settle_operation = new operations.asset_settle_operation();
        asset_settle_operation.fee = fee_asset_object.amount_from_string("0");
        asset_settle_operation.account = account_object.id;
        asset_settle_operation.amount = settle_asset_object.amount_from_string(settle_asset_amount);
        asset_settle_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_ASSET_SETTLE);
        operateList.add(asset_settle_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_object.precision)));

        return asset_fee_objects;
    }


    /**
     * global asset settle
     */
    public String global_asset_settle(String issuer, String password, String settle_asset_symbol, String settle_asset_price, String fee_asset_symbol, AccountDao accountDao) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException, PasswordVerifyException, KeyInvalideException, AuthorityException {

        account_object issuer_object = get_account_object(issuer);
        if (issuer_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        asset_object settle_asset_object = lookup_asset_symbols(settle_asset_symbol);
        if (null == settle_asset_object) {
            throw new AssetNotFoundException("settle asset does not exist");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        if (unlock(issuer, password, accountDao) != OPERATE_SUCCESS && verify_password(issuer, password).size() <= 0) {
            throw new PasswordVerifyException("Wrong password");
        }

        operations.global_asset_settle_operation global_asset_settle_operation = new operations.global_asset_settle_operation();
        global_asset_settle_operation.fee = fee_asset_object.amount_from_string("0");
        global_asset_settle_operation.issuer = issuer_object.id;
        global_asset_settle_operation.asset_to_settle = settle_asset_object.id;
        settle_price_object settle_price_object = new settle_price_object();
        settle_price_object.base = settle_asset_object.amount_from_string("100000000");
        settle_price_object.quote = fee_asset_object.amount_from_string(settle_asset_price);
        global_asset_settle_operation.settle_price = settle_price_object;
        global_asset_settle_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_GLOBAL_ASSET_SETTLE);
        operateList.add(global_asset_settle_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        global_asset_settle_operation.fee = fee_asset_object.amount_from_string(String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_object.precision))));

        operations.operation_type operationType = new operations.operation_type();
        operationType.operationContent = global_asset_settle_operation;
        operationType.nOperationType = operations.ID_GLOBAL_ASSET_SETTLE;

        signed_operate transactionWithCallback = new signed_operate();
        transactionWithCallback.operations = new ArrayList<>();
        transactionWithCallback.operations.add(operationType);
        transactionWithCallback.extensions = new HashSet<>();
        return sign_transaction(transactionWithCallback, issuer_object);
    }


    /**
     * asset settle fee
     */
    public List<asset_fee_object> global_asset_settle_fee(String issuer, String settle_asset_symbol, String settle_asset_price, String fee_asset_symbol) throws NetworkStatusException, AssetNotFoundException, AccountNotFoundException {

        account_object issuer_object = get_account_object(issuer);
        if (issuer_object == null) {
            throw new AccountNotFoundException("Account does not exist");
        }

        asset_object settle_asset_object = lookup_asset_symbols(settle_asset_symbol);
        if (null == settle_asset_object) {
            throw new AssetNotFoundException("settle asset does not exist");
        }

        asset_object fee_asset_object = lookup_asset_symbols(fee_asset_symbol);
        if (null == fee_asset_object) {
            throw new AssetNotFoundException("fee asset does not exist");
        }

        operations.global_asset_settle_operation global_asset_settle_operation = new operations.global_asset_settle_operation();
        global_asset_settle_operation.fee = fee_asset_object.amount_from_string("0");
        global_asset_settle_operation.issuer = issuer_object.id;
        global_asset_settle_operation.asset_to_settle = settle_asset_object.id;
        settle_price_object settle_price_object = new settle_price_object();
        settle_price_object.base = settle_asset_object.amount_from_string("100000000");
        settle_price_object.quote = fee_asset_object.amount_from_string(settle_asset_price);
        global_asset_settle_operation.settle_price = settle_price_object;
        global_asset_settle_operation.extensions = new HashSet<>();

        List<Object> operateList = new ArrayList<>();
        operateList.add(operations.ID_GLOBAL_ASSET_SETTLE);
        operateList.add(global_asset_settle_operation);

        List<Object> operateLists = new ArrayList<>();
        operateLists.add(operateList);

        List<Object> objectList = new ArrayList<>();
        objectList.add(operateLists);
        objectList.add(fee_asset_object.id);

        List<asset_fee_object> asset_fee_objects = mWebSocketApi.get_required_fees(objectList);

        asset_fee_objects.get(0).amount = String.valueOf(Double.valueOf(asset_fee_objects.get(0).amount) / (Math.pow(10, fee_asset_object.precision)));

        return asset_fee_objects;
    }


    /**
     * get_dao_account_by_name
     *
     * @param accountName
     * @param accountDao
     * @return get_dao_account_by_name
     */
    private AccountEntity.AccountBean get_dao_account_by_name(String accountName, AccountDao accountDao) {
        return accountDao.queryAccountByName(accountName);
    }


    /**
     * log out
     *
     * @return
     */
    public int log_out() {
        lock();
        return OPERATE_SUCCESS;
    }

}


