package com.cocos.bcx_sdk.bcx_wallet.chain;

import com.cocos.bcx_sdk.bcx_wallet.authority1;
import com.cocos.bcx_sdk.bcx_wallet.fc.io.base_encoder;
import com.cocos.bcx_sdk.bcx_wallet.fc.io.raw_type;
import com.google.common.primitives.UnsignedInteger;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 *
 */
public class operations {

    public static final int ID_TRANSFER_OPERATION = 0;

    public static final int ID_CALCULATE_INVOKING_CONTRACT_OPERATION = 44;

    public static final int ID_TRANSFER_NH_ASSET_OPERATION = 51;

    public static final int ID_BUY_NH_ASSET_OPERATION = 54;

    public static final int ID_UPGRADE_TO_LIFETIME_MEMBER_OPERATION = 8;

    public static final int ID_CREATE_CHILD_ACCOUNT_OPERATION = 5;

    public static operation_id_map operations_map = new operation_id_map();

    public static class operation_id_map {

        private HashMap<Integer, Type> mHashId2Operation = new HashMap<>();

        public operation_id_map() {
            mHashId2Operation.put(ID_TRANSFER_OPERATION, transfer_operation.class);
            mHashId2Operation.put(ID_CALCULATE_INVOKING_CONTRACT_OPERATION, invoking_contract_operation.class);
            mHashId2Operation.put(ID_TRANSFER_NH_ASSET_OPERATION, transfer_nhasset_operation.class);
            mHashId2Operation.put(ID_BUY_NH_ASSET_OPERATION, buy_nhasset_operation.class);
            mHashId2Operation.put(ID_UPGRADE_TO_LIFETIME_MEMBER_OPERATION, upgrade_to_lifetime_member_operation.class);
            mHashId2Operation.put(ID_CREATE_CHILD_ACCOUNT_OPERATION, create_child_account_operation.class);
        }

        public Type getOperationObjectById(int nId) {
            return mHashId2Operation.get(nId);
        }
    }

    public static class operation_type {
        public int nOperationType;
        public Object operationContent;

        public static class operation_type_deserializer implements JsonDeserializer<operation_type> {
            @Override
            public operation_type deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                operation_type operationType = new operation_type();
                JsonArray jsonArray = json.getAsJsonArray();

                operationType.nOperationType = jsonArray.get(0).getAsInt();
                Type type = operations_map.getOperationObjectById(operationType.nOperationType);

                if (type != null) {
                    operationType.operationContent = context.deserialize(jsonArray.get(1), type);
                } else {
                    operationType.operationContent = context.deserialize(jsonArray.get(1), Object.class);
                }

                return operationType;
            }
        }

        public static class operation_type_serializer implements JsonSerializer<operation_type> {
            @Override
            public JsonElement serialize(operation_type src, Type typeOfSrc, JsonSerializationContext context) {
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(src.nOperationType);
                Type type = operations_map.getOperationObjectById(src.nOperationType);
                assert (type != null);
                jsonArray.add(context.serialize(src.operationContent, type));
                return jsonArray;
            }
        }
    }


    public interface base_operation {
        Collection<? extends authority> get_required_authorities();

        List<object_id<account_object>> get_required_active_authorities();

        Collection<? extends object_id<account_object>> get_required_owner_authorities();

        void write_to_encoder(base_encoder baseEncoder);

        object_id<account_object> fee_payer();
    }


    /**
     * transfer operation
     */
    public static class transfer_operation implements base_operation {

        public asset fee;
        public object_id<account_object> from;
        public object_id<account_object> to;
        public asset amount;
        public memo_data memo;
        public Set<types.void_t> extensions;

        @Override
        public Collection<? extends authority> get_required_authorities() {
            return new ArrayList<>();
        }

        @Override
        public List<object_id<account_object>> get_required_active_authorities() {
            List<object_id<account_object>> activeList = new ArrayList<>();
            activeList.add(fee_payer());
            return activeList;
        }

        @Override
        public Collection<? extends object_id<account_object>> get_required_owner_authorities() {
            return new ArrayList<>();
        }

        @Override
        public void write_to_encoder(base_encoder baseEncoder) {
            raw_type rawObject = new raw_type();
            fee.write_to_encoder(baseEncoder);
            //baseEncoder.write(rawObject.get_byte_array(from.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(from.get_instance()));
            //baseEncoder.write(rawObject.get_byte_array(to.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(to.get_instance()));
            baseEncoder.write(rawObject.get_byte_array(amount.amount));
            //baseEncoder.write(rawObject.get_byte_array(amount.asset_id.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(amount.asset_id.get_instance()));
            baseEncoder.write(rawObject.get_byte(memo != null));

            if (memo != null) {
                baseEncoder.write(memo.from.key_data);
                baseEncoder.write(memo.to.key_data);
                baseEncoder.write(rawObject.get_byte_array(memo.nonce));
                byte[] byteMessage = memo.message.array();
                rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(byteMessage.length));
                baseEncoder.write(byteMessage);
            }

            //baseEncoder.write(rawObject.get_byte_array(extensions.size()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(extensions.size()));
        }

        @Override
        public object_id<account_object> fee_payer() {
            return from;
        }

    }


    /**
     * invoking contract operation
     */
    public static class invoking_contract_operation implements base_operation {


        public static class v {
            public String v;
        }

        public asset fee;
        public object_id<account_object> caller;
        public object_id<contract_object> contract_id;
        public String function_name;
        public List<List<Object>> value_list;
        public Set<types.void_t> extensions;


        @Override
        public Collection<? extends authority> get_required_authorities() {
            return new ArrayList<>();
        }

        @Override
        public List<object_id<account_object>> get_required_active_authorities() {
            List<object_id<account_object>> activeList = new ArrayList<>();
            activeList.add(fee_payer());
            return activeList;
        }

        @Override
        public Collection<? extends object_id<account_object>> get_required_owner_authorities() {
            return new ArrayList<>();
        }

        @Override
        public void write_to_encoder(base_encoder baseEncoder) {
            raw_type rawObject = new raw_type();
            fee.write_to_encoder(baseEncoder);
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(caller.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(contract_id.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(function_name.length()));
            baseEncoder.write(function_name.getBytes());
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(value_list.size()));
            for (List<Object> value : value_list) {
                rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits((Integer) value.get(0)));
                v baseValues = (v) value.get(1);
                rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(baseValues.v.length()));
                baseEncoder.write(baseValues.v.getBytes());
            }
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(extensions.size()));
        }

        @Override
        public object_id<account_object> fee_payer() {
            return caller;
        }

    }


    /**
     * transfer nhasset operation
     */
    public static class transfer_nhasset_operation implements base_operation {

        public asset fee;
        public object_id<account_object> from;
        public object_id<account_object> to;
        public object_id<nhasset_object> nh_asset;


        @Override
        public Collection<? extends authority> get_required_authorities() {
            return new ArrayList<>();
        }

        @Override
        public List<object_id<account_object>> get_required_active_authorities() {
            List<object_id<account_object>> activeList = new ArrayList<>();
            activeList.add(fee_payer());
            return activeList;
        }

        @Override
        public Collection<? extends object_id<account_object>> get_required_owner_authorities() {
            return new ArrayList<>();
        }

        @Override
        public void write_to_encoder(base_encoder baseEncoder) {
            raw_type rawObject = new raw_type();
            fee.write_to_encoder(baseEncoder);
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(from.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(to.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(nh_asset.get_instance()));
        }

        @Override
        public object_id<account_object> fee_payer() {
            return from;
        }

    }


    /**
     * transfer nhasset operation
     */
    public static class buy_nhasset_operation implements base_operation {

        public asset fee;
        public object_id<nh_asset_order_object> order;
        public object_id<account_object> fee_paying_account;
        public object_id<account_object> seller;
        public object_id<nhasset_object> nh_asset;
        public String price_amount;
        public object_id<asset_object> price_asset_id;
        public String price_asset_symbol;
        public Set<types.void_t> extensions;

        @Override
        public Collection<? extends authority> get_required_authorities() {
            return new ArrayList<>();
        }

        @Override
        public List<object_id<account_object>> get_required_active_authorities() {
            List<object_id<account_object>> activeList = new ArrayList<>();
            activeList.add(fee_payer());
            return activeList;
        }

        @Override
        public Collection<? extends object_id<account_object>> get_required_owner_authorities() {
            return new ArrayList<>();
        }

        @Override
        public void write_to_encoder(base_encoder baseEncoder) {
            raw_type rawObject = new raw_type();
            fee.write_to_encoder(baseEncoder);
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(order.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(fee_paying_account.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(seller.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(nh_asset.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(price_amount.length()));
            baseEncoder.write(price_amount.getBytes());
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(price_asset_id.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(price_asset_symbol.length()));
            baseEncoder.write(price_asset_symbol.getBytes());
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(extensions.size()));
        }

        @Override
        public object_id<account_object> fee_payer() {
            return seller;
        }

    }


    /**
     * upgrade to lifetime member operation
     */
    public static class upgrade_to_lifetime_member_operation implements base_operation {

        public asset fee;
        public object_id<account_object> account_to_upgrade;
        public boolean upgrade_to_lifetime_member;
        public Set<types.void_t> extensions;

        @Override
        public Collection<? extends authority> get_required_authorities() {
            return new ArrayList<>();
        }

        @Override
        public List<object_id<account_object>> get_required_active_authorities() {
            List<object_id<account_object>> activeList = new ArrayList<>();
            activeList.add(fee_payer());
            return activeList;
        }

        @Override
        public Collection<? extends object_id<account_object>> get_required_owner_authorities() {
            return new ArrayList<>();
        }

        @Override
        public void write_to_encoder(base_encoder baseEncoder) {
            raw_type rawObject = new raw_type();
            fee.write_to_encoder(baseEncoder);
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(account_to_upgrade.get_instance()));
            baseEncoder.write(rawObject.get_byte(upgrade_to_lifetime_member));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(extensions.size()));
        }

        @Override
        public object_id<account_object> fee_payer() {
            return account_to_upgrade;
        }
    }


    /**
     * create child account operation
     */
    public static class create_child_account_operation implements base_operation {

        public asset fee;
        public object_id<account_object> registrar;
        public object_id<account_object> referrer;
        public int referrer_percent;
        public String name;
        public authority1 owner;
        public authority1 active;
        public types.account_options options;
        public Set<types.void_t> extensions;

        @Override
        public Collection<? extends authority> get_required_authorities() {
            return new ArrayList<>();
        }

        @Override
        public List<object_id<account_object>> get_required_active_authorities() {
            List<object_id<account_object>> activeList = new ArrayList<>();
            activeList.add(fee_payer());
            return activeList;
        }

        @Override
        public Collection<? extends object_id<account_object>> get_required_owner_authorities() {
            return new ArrayList<>();
        }

        @Override
        public void write_to_encoder(base_encoder baseEncoder) {
            raw_type rawObject = new raw_type();
            if (fee == null) {
                object_id<asset_object> object_id = com.cocos.bcx_sdk.bcx_wallet.chain.object_id.create_from_string("1.3.0");
                new asset(1, object_id).write_to_encoder(baseEncoder);
            } else {
                fee.write_to_encoder(baseEncoder);
            }
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(registrar.get_instance()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(referrer.get_instance()));
            Integer reffer = Integer.valueOf(referrer_percent);
            baseEncoder.write(rawObject.get_byte_array(reffer.shortValue()));
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(name.length()));
            baseEncoder.write(name.getBytes());
            owner.write_to_encode(baseEncoder);
            active.write_to_encode(baseEncoder);
            options.write_to_encode(baseEncoder);
            rawObject.pack(baseEncoder, UnsignedInteger.fromIntBits(extensions.size()));
        }

        @Override
        public object_id<account_object> fee_payer() {
            return registrar;
        }
    }


}
