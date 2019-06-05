package com.cocos.bcx_sdk.bcx_wallet.chain;

import java.util.List;


public class global_property_object {

    public object_id id;
    public int next_available_vote_id = 0;
    public List<object_id> active_committee_members;
    public List<object_id> active_witnesses;
}
