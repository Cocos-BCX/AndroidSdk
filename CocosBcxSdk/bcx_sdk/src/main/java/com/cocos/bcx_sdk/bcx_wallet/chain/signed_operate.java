package com.cocos.bcx_sdk.bcx_wallet.chain;


import com.cocos.bcx_sdk.bcx_wallet.fc.crypto.sha256_object;

import java.util.ArrayList;
import java.util.List;


public class signed_operate extends transaction {

    List<compact_signature> signatures = new ArrayList<>();

    public void sign(types.private_key_type privateKeyType, sha256_object chain_id) {
        sha256_object digest = sig_digest(chain_id);
        signatures.add(privateKeyType.getPrivateKey().sign_compact(digest));
    }

    public static compact_signature signMessage(types.private_key_type privateKeyType, sha256_object sha256Object) {
        sha256_object.encoder enc = new sha256_object.encoder();
        enc.write(sha256Object.hash, 0, sha256Object.hash.length);
        return privateKeyType.getPrivateKey().sign_compact(enc.result());
    }

}
