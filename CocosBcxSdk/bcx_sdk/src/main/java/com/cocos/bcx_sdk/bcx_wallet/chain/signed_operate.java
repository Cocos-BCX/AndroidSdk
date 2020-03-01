package com.cocos.bcx_sdk.bcx_wallet.chain;


import android.text.TextUtils;

import com.cocos.bcx_sdk.bcx_api.CocosBcxApi;
import com.cocos.bcx_sdk.bcx_error.AccountNotFoundException;
import com.cocos.bcx_sdk.bcx_log.LogUtils;
import com.cocos.bcx_sdk.bcx_server.ConnectServer;
import com.cocos.bcx_sdk.bcx_utils.bitlib.crypto.InMemoryPrivateKey;
import com.cocos.bcx_sdk.bcx_utils.bitlib.crypto.PublicKey;
import com.cocos.bcx_sdk.bcx_utils.bitlib.crypto.SignedMessage;
import com.cocos.bcx_sdk.bcx_utils.bitlib.crypto.WrongSignatureException;
import com.cocos.bcx_sdk.bcx_utils.bitlib.lambdaworks.crypto.Base64;
import com.cocos.bcx_sdk.bcx_utils.bitlib.util.Sha256Hash;
import com.cocos.bcx_sdk.bcx_wallet.fc.crypto.sha256_object;
import com.cocos.bcx_sdk.bcx_wallet.fc.io.raw_type;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedInteger;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class signed_operate extends transaction {

    List<compact_signature> signatures = new ArrayList<>();

    public void sign(types.private_key_type privateKeyType, sha256_object chain_id) {
        sha256_object digest = sig_digest(chain_id);
        signatures.add(privateKeyType.getPrivateKey().sign_compact(digest));
    }

    /**
     * 签名
     *
     * @param privateKey
     * @param message
     * @return
     */
    public String signMessage(String privateKey, String message) {
        types.private_key_type privateKeyType = null;
        try {
            privateKeyType = new types.private_key_type(privateKey);
            sha256_object digest = generaDigest(message);
            return global_config_object.getInstance().getGsonBuilder().create().toJson(privateKeyType.getPrivateKey().sign_compact(digest));
        } catch (Exception e) {
            return "";
        }
    }


    public List<verify_result> recoverMessage(String srcMessage, String signature, String public_key) {
        try {
            byte[] key_data = new byte[32];
            byte[] key_data1 = new byte[32];
            sha256_object digest = generaDigest(srcMessage);
            BaseEncoding encoding = BaseEncoding.base16().lowerCase();
            byte[] signatureByte;
            try {
                signatureByte = encoding.decode(signature.replace("\"", ""));
            } catch (Exception e) {
                signatureByte = encoding.decode(signature);
            }
            String signatureBaseByte = Base64.encodeToString(signatureByte, false);
            types.public_key_type public_key_type = new types.public_key_type(public_key);
            public_key public_key1 = public_key_type.getPublicKey();
            PublicKey publicKey = SignedMessage.recoverFromSignature(digest, signatureBaseByte);
            public_key publicKey1 = new public_key(publicKey.getPublicKeyBytes());

            System.arraycopy(publicKey1.getKeyByte(), 1, key_data, 0, key_data.length);
            System.arraycopy(public_key1.getKeyByte(), 1, key_data1, 0, key_data1.length);

            if (!TextUtils.equals(Arrays.toString(key_data), Arrays.toString(key_data1))) {
                throw new AccountNotFoundException("This key has no account information");
            }
            List<String> publicKeyTypes = new ArrayList<>();
            publicKeyTypes.add(public_key_type.toString());
            // request to get accoount id
            List<List<String>> objects = ConnectServer.getBcxWebServerInstance().get_key_references(publicKeyTypes);
            // if id[]  null ,you need know the response about this rpc:get_key_references
            if (objects == null || objects.size() <= 0 || objects.get(0).size() <= 0) {
                throw new AccountNotFoundException("This key has no account information");
            }
            List<verify_result> verify_results = new ArrayList<>();
            String account_id = null;
            for (List<String> account_ids : objects) {
                for (String id : account_ids) {
                    if (TextUtils.equals(id, account_id)) {
                        continue;
                    }
                    account_id = id;
                    // get account object
                    account_object account_object = CocosBcxApi.getBcxInstance().get_account_object(id);
                    verify_result verify_result = new verify_result();
                    verify_result.accountid = account_object.id.toString();
                    verify_result.accountname = account_object.name;
                    verify_results.add(verify_result);
                }
            }
            return verify_results;
        } catch (WrongSignatureException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private sha256_object generaDigest(String message) {
        raw_type rawObject = new raw_type();
        sha256_object.encoder baseEncoder = new sha256_object.encoder();
        rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(message.getBytes().length));
        baseEncoder.write(message.getBytes());
        return baseEncoder.result();
    }


}
