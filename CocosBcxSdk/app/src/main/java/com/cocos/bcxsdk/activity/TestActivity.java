package com.cocos.bcxsdk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cocos.bcx_sdk.bcx_api.CocosBcxApiWrapper;
import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcx_sdk.bcx_error.ContractNotFoundException;
import com.cocos.bcx_sdk.bcx_error.NetworkStatusException;
import com.cocos.bcx_sdk.bcx_wallet.chain.asset_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.contract_object;
import com.cocos.bcxsdk.R;
import com.cocos.bcxsdk.utils.MainHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestActivity extends AppCompatActivity {


    private EditText walletAccountName;
    private EditText walletAccountPwd;
    private TextView walletCreateAccount;

    private EditText accountAccountName;
    private EditText accountAccountPwd;
    private TextView CreateAccount;

    private EditText loginAccountName;
    private EditText loginPwd;
    private TextView tv_login;

    private EditText key_login_password;
    private EditText key_login_private_key;

    private TextView key_login;
    private TextView tv_logout;

    private EditText edt_account_name;

    private EditText et_get_private_key_account_name;
    private EditText et_get_private_key_pwd;
    private TextView tv_get_private_key;

    private EditText et_get_src_info_account;
    private EditText et_get_src_info_limit;
    private TextView tv_get_src_info;
    private EditText et_transfer_asset_from_account;
    private EditText et_transfer_asset_to_account;
    private EditText et_transfer_asset_password;
    private EditText et_transfer_asset_amount;
    private EditText et_transfer_asset_asset_type;
    private EditText et_transfer_asset_memo;
    private TextView tv_transfer_asset;
    private TextView tv_get_accounts;

    private EditText et_asset_memo_account_name;
    private EditText et_asset_memo_account_pwd;
    private EditText et_asset_memo_account_memmo;
    private TextView tv_get_memo;

    private EditText et_block_number;
    private TextView tv_get_block_header;

    private EditText et_asset_symbol;
    private TextView lookup_asset_symbols;

    private EditText et_account_id;
    private EditText et_asset_id;
    private TextView get_account_balances;

    private EditText et_export_account;
    private EditText et_export_pwd;
    private TextView get_keystore;

    private EditText et_import_keystore;
    private EditText et_import_pwd;
    private EditText et_import_wallet_type;
    private TextView tv_import_keyStore;

    private EditText et_delete_account_name;
    private TextView tv_delete_account_by_name;
    private TextView tv_get_account_names;

    private EditText et_calculate_invoking_contract_strAccount;
    private EditText et_calculate_invoking_contract_contractId;
    private EditText et_calculate_invoking_contract_functionName;
    private EditText et_calculate_invoking_contract_params;
    private EditText et_calculate_invoking_contract_pwd;
    private TextView tv_invoking_contract;
    private TextView tv_look_up_nh_asset;
    private EditText et_nh_asset_account;
    private EditText et_world_view;
    private EditText et_nh_asset_page;
    private EditText et_nh_asset_page_size;
    private TextView tv_look_up_account_nh_asset;
    private EditText ed_nh_asset_id_hash;
    private EditText et_word_view;
    private TextView tv_look_up_word_view;
    private EditText et_account_name;
    private TextView tv_get_nh_creator;
    private EditText et_create_nh_asset_account;
    private EditText et_create_nh_asset_page;
    private EditText et_create_nh_asset_page_size;
    private TextView tv_list_nh_asset_by_creator;
    private EditText et_create_nh_asset_account_order;
    private EditText et_create_nh_asset_page_order;
    private EditText et_create_nh_asset_page_size_order;
    private TextView tv_list_account_nh_asset_order;
    private EditText et_nh_asset_symbol_or_id;
    private EditText et_word_view_id_or_name;
    private EditText et_nh_asset_order_baseDescribe;
    private EditText et_nh_asset_page_order;
    private EditText et_nh_asset_page_size_order;
    private TextView tv_list_nh_asset_order;
    private EditText et_nh_asset_transfer_from_account;
    private EditText et_nh_asset_transfer_to_account;
    private EditText et_nh_asset_id;
    private EditText et_password;
    private TextView tv_send_nh_asset;
    private EditText et_nh_asset_buyer;
    private EditText et_nh_asset_order_id;
    private EditText et_buy_nh_password;
    private TextView tv_nh_asset_buy;
    private TextView tv_get_contract_object;
    private TextView get_all_account_balances;
    private TextView tv_get_account_object;
    private TextView tv_lookup_account_names;
    private TextView tv_get_full_accounts;
    private TextView tv_get_dao_accounts;
    private EditText et_upgrade_lifetime_account;
    private EditText et_upgrade_lifetime_password;
    private TextView tv_upgrade_lifetime_member;
    private EditText et_create_child_account_name;
    private EditText et_create_child_account_password;
    private EditText et_create_child_registrar;
    private EditText et_create_child_registrar_password;
    private TextView tv_create_child_account;
    private TextView tv_switch_node_url;
    private EditText et_get_transaction_in_block_info;
    private TextView tv_get_transaction_in_block_info;
    private TextView get_transaction_by_id;
    private TextView get_global_properties;
    private TextView tv_get_contract;
    private EditText et_nh_asset_otcaccount;
    private EditText et_nh_asset_seller;
    private EditText pending_order_seller_password;
    private EditText pending_order_nh_asset;
    private EditText pending_order_fee;
    private EditText pending_order_memo;
    private EditText pending_order_price;
    private EditText pending_order_price_symbol;
    private EditText pending_order_valid_time;
    private TextView tv_create_nh_asset_order;
    private EditText et_delete_nh_asset_account;
    private EditText et_delete_nh_asset_password;
    private EditText et_delete_nh_asset_id;
    private TextView tv_delete_nh_asset;
    private EditText et_cancel_nh_asset_account;
    private EditText et_cancel_nh_asset_password;
    private EditText et_cancel_nh_asset_order_id;
    private TextView tv_cancel_nh_asset_order;
    private TextView tv_list_assets;

    private TextView market;
    private EditText estimation_gas_amount;
    private EditText estimation_gas_mortgagor;
    private EditText estimation_gas_mortgagor_password;
    private EditText estimation_gas_beneficiary;
    private TextView get_estimation_gas;
    private TextView get_update_collateral_for_gas;
    private EditText et_create_nh_asset_creator;
    private EditText et_create_nh_asset_creator_password;
    private EditText et_create_nh_asset_owner;
    private EditText et_create_nh_asset_asset_id;
    private EditText et_create_nh_asset_world_view;
    private EditText et_create_nh_asset_base_describe;
    private TextView tv_create_nh_asset;
    private EditText et_register_creator;
    private EditText et_register_creator_password;
    private TextView tv_register_creator;
    private EditText et_create_world_view;
    private EditText et_create_worldview_account;
    private EditText et_create_worldview_account_password;
    private TextView tv_create_worldview;
    private EditText vesting_balances_account;
    private TextView tv_vesting_balances;
    private TextView tv_vesting_balances_content;
    private TextView tv_get_vesting_balances;
    private EditText get_vesting_balances_account;
    private EditText get_vesting_balances_account_password;
    private EditText get_vesting_balances_reward;
    private TextView tv_get_block;
    private TextView tv_get_committee_members;
    private EditText et_vote_account;
    private TextView tv_get_witnesses_members;
    private EditText et_support_account;
    private EditText et_vote_password;
    private EditText et_vote_number;
    private TextView tv_vote_members;
    private EditText et_create_committee_member_account;
    private EditText et_create_committee_member_account_password;
    private EditText et_create_committee_member_url;
    private TextView tv_create_committee_member;
    private EditText et_create_witness_account;
    private EditText et_create_witness_account_password;
    private EditText et_create_witness_url;
    private TextView tv_create_witness;
    private EditText et_create_nh_asset_by_creator_world_view;
    private EditText et_vote_type;
    private EditText et_vote_ids;


    @SuppressLint({"LongLogTag", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        walletAccountName = findViewById(R.id.et_wallet_signup_username);
        walletAccountPwd = findViewById(R.id.et_wallet_signup_pwd);
        walletCreateAccount = findViewById(R.id.tv_wallet_signup);

        accountAccountName = findViewById(R.id.et_account_signup_username);
        accountAccountPwd = findViewById(R.id.et_account_signup_pwd);
        CreateAccount = findViewById(R.id.tv_signup);

        loginAccountName = findViewById(R.id.et_login_username);
        loginPwd = findViewById(R.id.et_login_pwd);
        tv_login = findViewById(R.id.tv_login);


        key_login_password = findViewById(R.id.et_key_login_password);
        key_login_private_key = findViewById(R.id.et_key_login_private_key);
        key_login = findViewById(R.id.tv_key_login);

        tv_logout = findViewById(R.id.tv_logout);

        edt_account_name = findViewById(R.id.edt_account_name);

        tv_list_assets = findViewById(R.id.tv_list_assets);


        tv_get_account_object = findViewById(R.id.tv_get_account_object);
        tv_get_accounts = findViewById(R.id.tv_get_accounts);
        tv_lookup_account_names = findViewById(R.id.tv_lookup_account_names);
        tv_get_full_accounts = findViewById(R.id.tv_get_full_accounts);


        et_get_private_key_account_name = findViewById(R.id.et_get_private_key_account_name);
        et_get_private_key_pwd = findViewById(R.id.et_get_private_key_pwd);
        tv_get_private_key = findViewById(R.id.tv_get_private_key);

        et_get_src_info_account = findViewById(R.id.et_get_src_info_account);
        et_get_src_info_limit = findViewById(R.id.et_get_src_info_limit);
        tv_get_src_info = findViewById(R.id.tv_get_src_info);

        et_transfer_asset_from_account = findViewById(R.id.et_transfer_asset_from_account);
        et_transfer_asset_to_account = findViewById(R.id.et_transfer_asset_to_account);
        et_transfer_asset_password = findViewById(R.id.et_transfer_asset_password);
        et_transfer_asset_amount = findViewById(R.id.et_transfer_asset_amount);
        et_transfer_asset_asset_type = findViewById(R.id.et_transfer_asset_asset_type);
        et_transfer_asset_memo = findViewById(R.id.et_transfer_asset_memo);
        tv_transfer_asset = findViewById(R.id.tv_transfer_asset);
        tv_get_dao_accounts = findViewById(R.id.tv_get_dao_accounts);

        et_asset_memo_account_name = findViewById(R.id.et_asset_memo_account_name);
        et_asset_memo_account_pwd = findViewById(R.id.et_asset_memo_account_pwd);
        et_asset_memo_account_memmo = findViewById(R.id.et_asset_memo_account_memmo);
        tv_get_memo = findViewById(R.id.tv_get_memo);

        et_block_number = findViewById(R.id.et_block_number);
        tv_get_block = findViewById(R.id.tv_get_block);
        tv_get_block_header = findViewById(R.id.tv_get_block_header);


        et_asset_symbol = findViewById(R.id.et_asset_symbol);
        lookup_asset_symbols = findViewById(R.id.lookup_asset_symbols);

        et_account_id = findViewById(R.id.et_account_id);
        et_asset_id = findViewById(R.id.et_asset_id);
        get_account_balances = findViewById(R.id.get_account_balances);
        get_all_account_balances = findViewById(R.id.get_all_account_balances);


        et_export_account = findViewById(R.id.et_export_account);
        et_export_pwd = findViewById(R.id.et_export_pwd);
        get_keystore = findViewById(R.id.get_keystore);


        et_import_keystore = findViewById(R.id.et_import_keystore);
        et_import_pwd = findViewById(R.id.et_import_pwd);
        et_import_wallet_type = findViewById(R.id.et_import_wallet_type);
        tv_import_keyStore = findViewById(R.id.tv_import_keyStore);


        et_delete_account_name = findViewById(R.id.et_delete_account_name);
        tv_get_account_names = findViewById(R.id.tv_get_account_names);
        tv_delete_account_by_name = findViewById(R.id.tv_delete_account_by_name);


        et_calculate_invoking_contract_strAccount = findViewById(R.id.et_calculate_invoking_contract_strAccount);
        et_calculate_invoking_contract_contractId = findViewById(R.id.et_calculate_invoking_contract_contractId);
        et_calculate_invoking_contract_functionName = findViewById(R.id.et_calculate_invoking_contract_functionName);
        et_calculate_invoking_contract_params = findViewById(R.id.et_calculate_invoking_contract_params);
        et_calculate_invoking_contract_pwd = findViewById(R.id.et_calculate_invoking_contract_pwd);
        tv_invoking_contract = findViewById(R.id.tv_invoking_contract);
        tv_get_contract = findViewById(R.id.tv_get_contract);


        tv_look_up_nh_asset = findViewById(R.id.tv_look_up_nh_asset);
        et_nh_asset_account = findViewById(R.id.et_nh_asset_account);
        et_world_view = findViewById(R.id.et_world_view);
        et_nh_asset_page = findViewById(R.id.et_nh_asset_page);
        et_nh_asset_page_size = findViewById(R.id.et_nh_asset_page_size);
        tv_look_up_account_nh_asset = findViewById(R.id.tv_look_up_account_nh_asset);
        ed_nh_asset_id_hash = findViewById(R.id.ed_nh_asset_id_hash);
        et_word_view = findViewById(R.id.et_word_view);
        tv_look_up_word_view = findViewById(R.id.tv_look_up_word_view);

        et_account_name = findViewById(R.id.et_account_name);
        tv_get_nh_creator = findViewById(R.id.tv_get_nh_creator);

        et_create_nh_asset_account = findViewById(R.id.et_create_nh_asset_account);
        et_create_nh_asset_by_creator_world_view = findViewById(R.id.et_create_nh_asset_by_creator_world_view);
        et_create_nh_asset_page = findViewById(R.id.et_create_nh_asset_page);
        et_create_nh_asset_page_size = findViewById(R.id.et_create_nh_asset_page_size);
        tv_list_nh_asset_by_creator = findViewById(R.id.tv_list_nh_asset_by_creator);


        et_create_nh_asset_account_order = findViewById(R.id.et_create_nh_asset_account_order);
        et_create_nh_asset_page_order = findViewById(R.id.et_create_nh_asset_page_order);
        et_create_nh_asset_page_size_order = findViewById(R.id.et_create_nh_asset_page_size_order);
        tv_list_account_nh_asset_order = findViewById(R.id.tv_list_account_nh_asset_order);

        et_nh_asset_symbol_or_id = findViewById(R.id.et_nh_asset_symbol_or_id);
        et_word_view_id_or_name = findViewById(R.id.et_word_view_id_or_name);
        et_nh_asset_order_baseDescribe = findViewById(R.id.et_nh_asset_order_baseDescribe);
        et_nh_asset_page_order = findViewById(R.id.et_nh_asset_page_order);
        et_nh_asset_page_size_order = findViewById(R.id.et_nh_asset_page_size_order);
        tv_list_nh_asset_order = findViewById(R.id.tv_list_nh_asset_order);

        et_nh_asset_transfer_from_account = findViewById(R.id.et_nh_asset_transfer_from_account);
        et_nh_asset_transfer_to_account = findViewById(R.id.et_nh_asset_transfer_to_account);
        et_nh_asset_id = findViewById(R.id.et_nh_asset_id);
        et_password = findViewById(R.id.et_password);
        tv_send_nh_asset = findViewById(R.id.tv_send_nh_asset);

        et_delete_nh_asset_account = findViewById(R.id.et_delete_nh_asset_account);
        et_delete_nh_asset_password = findViewById(R.id.et_delete_nh_asset_password);
        et_delete_nh_asset_id = findViewById(R.id.et_delete_nh_asset_id);
        tv_delete_nh_asset = findViewById(R.id.tv_delete_nh_asset);

        et_cancel_nh_asset_account = findViewById(R.id.et_cancel_nh_asset_account);
        et_cancel_nh_asset_password = findViewById(R.id.et_cancel_nh_asset_password);
        et_cancel_nh_asset_order_id = findViewById(R.id.et_cancel_nh_asset_order_id);
        tv_cancel_nh_asset_order = findViewById(R.id.tv_cancel_nh_asset_order);

        et_nh_asset_buyer = findViewById(R.id.et_nh_asset_buyer);
        et_nh_asset_order_id = findViewById(R.id.et_nh_asset_order_id);
        tv_nh_asset_buy = findViewById(R.id.tv_nh_asset_buy);
        et_buy_nh_password = findViewById(R.id.et_buy_nh_password);
        tv_get_contract_object = findViewById(R.id.tv_get_contract_object);

        et_upgrade_lifetime_account = findViewById(R.id.et_upgrade_lifetime_account);
        et_upgrade_lifetime_password = findViewById(R.id.et_upgrade_lifetime_password);
        tv_upgrade_lifetime_member = findViewById(R.id.tv_upgrade_lifetime_member);


        et_create_child_account_name = findViewById(R.id.et_create_child_account_name);
        et_create_child_account_password = findViewById(R.id.et_create_child_account_password);
        et_create_child_registrar = findViewById(R.id.et_create_child_registrar);
        et_create_child_registrar_password = findViewById(R.id.et_create_child_registrar_password);
        tv_create_child_account = findViewById(R.id.tv_create_child_account);

        tv_switch_node_url = findViewById(R.id.tv_switch_node_url);

        et_get_transaction_in_block_info = findViewById(R.id.et_get_transaction_in_block_info);
        tv_get_transaction_in_block_info = findViewById(R.id.tv_get_transaction_in_block_info);
        get_transaction_by_id = findViewById(R.id.get_transaction_by_id);
        get_global_properties = findViewById(R.id.get_global_properties);

        et_nh_asset_otcaccount = findViewById(R.id.et_nh_asset_otcaccount);
        et_nh_asset_seller = findViewById(R.id.et_nh_asset_seller);
        pending_order_seller_password = findViewById(R.id.pending_order_seller_password);
        pending_order_nh_asset = findViewById(R.id.pending_order_nh_asset);
        pending_order_fee = findViewById(R.id.pending_order_fee);
        pending_order_memo = findViewById(R.id.pending_order_memo);
        pending_order_price = findViewById(R.id.pending_order_price);
        pending_order_price_symbol = findViewById(R.id.pending_order_price_symbol);
        pending_order_valid_time = findViewById(R.id.pending_order_valid_time);
        tv_create_nh_asset_order = findViewById(R.id.tv_create_nh_asset_order);

        market = findViewById(R.id.market);

        estimation_gas_mortgagor = findViewById(R.id.estimation_gas_mortgagor);
        estimation_gas_mortgagor_password = findViewById(R.id.estimation_gas_mortgagor_password);
        estimation_gas_beneficiary = findViewById(R.id.estimation_gas_beneficiary);
        estimation_gas_amount = findViewById(R.id.estimation_gas_amount);
        get_estimation_gas = findViewById(R.id.get_estimation_gas);
        get_update_collateral_for_gas = findViewById(R.id.get_update_collateral_for_gas);

        tv_register_creator = findViewById(R.id.tv_register_creator);
        et_register_creator = findViewById(R.id.et_register_creator);
        et_register_creator_password = findViewById(R.id.et_register_creator_password);

        et_create_world_view = findViewById(R.id.et_create_world_view);
        et_create_worldview_account = findViewById(R.id.et_create_worldview_account);
        et_create_worldview_account_password = findViewById(R.id.et_create_worldview_account_password);
        tv_create_worldview = findViewById(R.id.tv_create_worldview);

        et_create_nh_asset_creator = findViewById(R.id.et_create_nh_asset_creator);
        et_create_nh_asset_creator_password = findViewById(R.id.et_create_nh_asset_creator_password);
        et_create_nh_asset_owner = findViewById(R.id.et_create_nh_asset_owner);
        et_create_nh_asset_asset_id = findViewById(R.id.et_create_nh_asset_asset_id);
        et_create_nh_asset_world_view = findViewById(R.id.et_create_nh_asset_world_view);
        et_create_nh_asset_base_describe = findViewById(R.id.et_create_nh_asset_base_describe);
        tv_create_nh_asset = findViewById(R.id.tv_create_nh_asset);

        vesting_balances_account = findViewById(R.id.vesting_balances_account);
        tv_vesting_balances = findViewById(R.id.tv_vesting_balances);
        tv_vesting_balances_content = findViewById(R.id.tv_vesting_balances_content);
        tv_get_vesting_balances = findViewById(R.id.tv_get_vesting_balances);
        get_vesting_balances_account = findViewById(R.id.get_vesting_balances_account);
        get_vesting_balances_account_password = findViewById(R.id.get_vesting_balances_account_password);
        get_vesting_balances_reward = findViewById(R.id.get_vesting_balances_reward);


        tv_get_committee_members = findViewById(R.id.tv_get_committee_members);
        tv_get_witnesses_members = findViewById(R.id.tv_get_witnesses_members);
        et_support_account = findViewById(R.id.et_support_account);
        et_vote_account = findViewById(R.id.et_vote_account);
        et_vote_password = findViewById(R.id.et_vote_password);
        et_vote_type = findViewById(R.id.et_vote_type);
        et_vote_ids = findViewById(R.id.et_vote_ids);
        et_vote_number = findViewById(R.id.et_vote_number);
        tv_vote_members = findViewById(R.id.tv_vote_members);


        et_create_committee_member_account = findViewById(R.id.et_create_committee_member_account);
        et_create_committee_member_account_password = findViewById(R.id.et_create_committee_member_account_password);
        et_create_committee_member_url = findViewById(R.id.et_create_committee_member_url);
        tv_create_committee_member = findViewById(R.id.tv_create_committee_member);

        et_create_witness_account = findViewById(R.id.et_create_witness_account);
        et_create_witness_account_password = findViewById(R.id.et_create_witness_account_password);
        et_create_witness_url = findViewById(R.id.et_create_witness_url);
        tv_create_witness = findViewById(R.id.tv_create_witness);

        initListener();
    }

    private void initListener() {
        // 创建钱包模式账户
        walletCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_wallet_account(walletAccountName.getText().toString(), walletAccountPwd.getText().toString(), true, new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("create_wallet_account", value);
                    }
                });
            }
        });

        // 创建账户模式账户
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_password_account(accountAccountName.getText().toString(), accountAccountPwd.getText().toString(), true, new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("create_password_account", value);
                    }
                });
            }
        });

        // 账户模式登录
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().password_login(loginAccountName.getText().toString(), loginPwd.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("password_login", value);
                    }
                });
            }
        });


        // 私钥导入  (参数3： 账户类型，ACCOUNT/WALLET 存储在本地，需要区分钱包类型则需输入，否则可不填)
        key_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().import_wif_key(key_login_private_key.getText().toString(), key_login_password.getText().toString(), "WALLET", new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("import_wif_key", value);
                    }
                });
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//清除内存中的数据
                CocosBcxApiWrapper.getBcxInstance().log_out();
            }
        });


        tv_get_account_object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_account_object(edt_account_name.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_account_object", value);
                    }
                });
            }
        });

        tv_get_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_accounts(edt_account_name.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_accounts", value);
                    }
                });
            }
        });

        tv_lookup_account_names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().lookup_account_names(edt_account_name.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("lookup_account_names", value);
                    }
                });
            }
        });

        tv_get_full_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_full_accounts(edt_account_name.getText().toString(), false, new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_full_accounts", value);
                    }
                });
            }
        });

        // 获取用户私钥  首先需要登陆/导入
        tv_get_private_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().export_private_key(et_get_private_key_account_name.getText().toString(), et_get_private_key_pwd.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("export_private_key", value);
                    }
                });
            }
        });


        // 查询用户操作记录
        tv_get_src_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_account_history(et_get_src_info_account.getText().toString(), Integer.valueOf(et_get_src_info_limit.getText().toString()), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_account_history", value);
                    }
                });
            }
        });

        /**
         * 转账
         */
        tv_transfer_asset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().transfer(et_transfer_asset_password.getText().toString()
                        , et_transfer_asset_from_account.getText().toString(),
                        et_transfer_asset_to_account.getText().toString(),
                        et_transfer_asset_amount.getText().toString(),
                        et_transfer_asset_asset_type.getText().toString(),
                        et_transfer_asset_memo.getText().toString(), new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("transfer", value);
                            }
                        });
            }
        });

        // 查询所有账户的信息   首先需要登陆/导入
        tv_get_dao_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_dao_account_objects(new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_dao_account_objects", value);
                    }
                });
            }
        });

        tv_get_account_names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> strings = CocosBcxApiWrapper.getBcxInstance().get_dao_account_names();
                StringBuffer sb = new StringBuffer();
                for (String name : strings) {
                    sb.append(name + " ;");
                }
                Log.i("get_dao_account_names", sb.toString());
            }
        });

        tv_delete_account_by_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().delete_account_by_name(et_delete_account_name.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("delete_account_by_name", value);
                    }
                });
            }
        });

/**  "{\"from\":\"COCOS6G55VgR94GZmELS4UHEf2eVggmhPRnWLTWgGiEmzuBKdvEwoAB\",\"message\":\"1d89375e875d8f2da68a4f7e6c786892\",\"nonce\":11240986227921810428,\"to\":\"COCOS61qJmvPif4seDKUjEkoL9qUP9cSvwBvgme5djcK8thuUS6o4Mg\"}"
 *   memo :格式String json = "{from:COCOS6G55VgR94GZmELS4UHEf2eVggmhPRnWLTWgGiEmzuBKdvEwoAB,to:COCOS61qJmvPif4seDKUjEkoL9qUP9cSvwBvgme5djcK8thuUS6o4Mg,nonce:12970368479715836292,message:b9b37e032e205a78ce7238bfdeebc791}";
 */
        tv_get_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().decrypt_memo_message("gnkhandsome1", "123456", "{\"from\":\"COCOS6G55VgR94GZmELS4UHEf2eVggmhPRnWLTWgGiEmzuBKdvEwoAB\",\"message\":\"1d89375e875d8f2da68a4f7e6c786892\",\"nonce\":11240986227921810428,\"to\":\"COCOS61qJmvPif4seDKUjEkoL9qUP9cSvwBvgme5djcK8thuUS6o4Mg\"}", new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("decrypt_memo_message", value);
                    }
                });
            }
        });

        /**
         * 获取区块头信息；
         */
        tv_get_block_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_block_header(et_block_number.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_block_header", value);
                    }
                });
            }
        });

        /**
         * 获取区块信息；
         */
        tv_get_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_block(et_block_number.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_block", value);
                    }
                });
            }
        });


        /**
         * 按符号获取资产列表
         */
        lookup_asset_symbols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().lookup_asset_symbols(et_asset_symbol.getText().toString(), new IBcxCallBack() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("lookup_asset_symbols", value);
                    }
                });
            }
        });


        /**
         * 查询账户对应资产余额,
         */
        get_account_balances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_account_balances(et_account_id.getText().toString(), et_asset_id.getText().toString(),
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_account_balances", value);
                            }
                        });
            }
        });


        /**
         * 查询账户对应资产余额,
         */
        get_all_account_balances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_all_account_balances(et_account_id.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_all_account_balances", value);
                            }
                        });
            }
        });


        /**
         * 查询账户对应资产余额,
         */
        tv_list_assets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<asset_object> asset_objects = CocosBcxApiWrapper.getBcxInstance().list_assets("A", 100);
                Log.i("list_assets", String.valueOf(asset_objects.get(0).symbol));
            }
        });


        /**
         * 备份keyStore,
         */
        get_keystore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().export_keystore(et_export_account.getText().toString(), et_export_pwd.getText().toString(),
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("export_keystore", value);
                            }
                        });
            }
        });


        /**
         * 导入keyStore,
         */
        tv_import_keyStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountType;
                if (null == et_import_wallet_type.getText()) {
                    accountType = "";
                } else {
                    accountType = et_import_wallet_type.getText().toString();
                }

                CocosBcxApiWrapper.getBcxInstance().import_keystore(et_import_keystore.getText().toString(), et_import_wallet_type.getText().toString(), accountType,
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("import_keystore", value);
                            }
                        });
            }
        });


        /**
         * 调用合约方法,
         */
        tv_invoking_contract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().invoking_contract(et_calculate_invoking_contract_strAccount.getText().toString(),
                        et_calculate_invoking_contract_pwd.getText().toString(),
                        et_calculate_invoking_contract_contractId.getText().toString(),
                        et_calculate_invoking_contract_functionName.getText().toString(),
                        et_calculate_invoking_contract_params.getText().toString(),
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("invoking_contract", value);
                            }
                        });
            }
        });


        /**
         * 查询合约
         */
        tv_get_contract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_contract("1.16.2",
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_contract", value);
                            }
                        });
            }
        });


        /**
         * 获取合约详情
         */
        tv_get_contract_object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contract_object contract_object = CocosBcxApiWrapper.getBcxInstance().get_contract_object("contract.dicegame");
                    Log.i("get_contract_object", contract_object.contract_ABI.toString());
                } catch (ContractNotFoundException e) {
                    Log.i("get_contract_object", e.getMessage());
                } catch (NetworkStatusException e) {
                    Log.i("get_contract_object", e.getMessage());
                }
            }
        });


        /**
         * 注册开发者
         */
        tv_register_creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().register_creator(
                        et_register_creator.getText().toString(),
                        et_register_creator_password.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("tv_register_creator", value);
                            }
                        });
            }
        });

        /**
         * 创建世界观
         */
        tv_create_worldview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_worldview(
                        et_create_world_view.getText().toString(),
                        et_create_worldview_account.getText().toString(),
                        et_create_worldview_account_password.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("tv_create_worldview", value);
                            }
                        });
            }
        });

        /**
         * 创建NH资产
         */
        tv_create_nh_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_nh_asset(
                        et_create_nh_asset_creator.getText().toString(),
                        et_create_nh_asset_creator_password.getText().toString(),
                        et_create_nh_asset_owner.getText().toString(),
                        et_create_nh_asset_asset_id.getText().toString(),
                        et_create_nh_asset_world_view.getText().toString(),
                        et_create_nh_asset_base_describe.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("tv_create_nh_asset", value);
                            }
                        });
            }
        });


        /**
         * 转移NH资产
         */
        tv_send_nh_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> nhasset_ids = Arrays.asList(et_nh_asset_id.getText().toString().split(","));

                CocosBcxApiWrapper.getBcxInstance().transfer_nh_asset(et_password.getText().toString(),
                        et_nh_asset_transfer_from_account.getText().toString(),
                        et_nh_asset_transfer_to_account.getText().toString(),
                        nhasset_ids,
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("transfer_nh_asset", value);
                            }
                        });
            }
        });


        /**
         * 删除NH资产
         */
        tv_delete_nh_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> nhasset_ids = Arrays.asList(et_delete_nh_asset_id.getText().toString().split(","));

                CocosBcxApiWrapper.getBcxInstance().delete_nh_asset(et_delete_nh_asset_account.getText().toString(),
                        et_delete_nh_asset_password.getText().toString(),
                        nhasset_ids,
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("delete_nh_asset", value);
                            }
                        });
            }
        });


        /**
         * 创建nh资产订单(出售)
         */
        tv_create_nh_asset_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_nh_asset_order(
                        et_nh_asset_otcaccount.getText().toString(),
                        et_nh_asset_seller.getText().toString(),
                        pending_order_seller_password.getText().toString(),
                        pending_order_nh_asset.getText().toString(),
                        pending_order_fee.getText().toString(),
                        "COCOS",
                        pending_order_memo.getText().toString(),
                        pending_order_price.getText().toString(),
                        pending_order_price_symbol.getText().toString(),
                        Long.parseLong(pending_order_valid_time.getText().toString()),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("create_nh_asset_order", value);
                            }
                        });
            }
        });

        /**
         * 购买nh资产
         */
        tv_nh_asset_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().buy_nh_asset(
                        et_nh_asset_buyer.getText().toString(),
                        et_buy_nh_password.getText().toString(),
                        et_nh_asset_order_id.getText().toString(),
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("buy_nh_asset", value);
                            }
                        });
            }
        });


        /**
         * 取消nh资产订单
         */
        tv_cancel_nh_asset_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().cancel_nh_asset_order(
                        et_cancel_nh_asset_account.getText().toString(),
                        et_cancel_nh_asset_password.getText().toString(),
                        et_cancel_nh_asset_order_id.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("cancel_nh_asset_order", value);
                            }
                        });
            }
        });

        /**
         * 查询NH资产详情,
         */
        tv_look_up_nh_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> nh_asset_ids_or_hash = new ArrayList<>();
                nh_asset_ids_or_hash.add(ed_nh_asset_id_hash.getText().toString());
                CocosBcxApiWrapper.getBcxInstance().lookup_nh_asset(nh_asset_ids_or_hash,
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("lookup_nh_asset", value);
                            }
                        });
            }
        });


        /**
         * 查询账户下所拥有的NH资产,
         */
        tv_look_up_account_nh_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> nh_asset_ids_or_hash = new ArrayList<>();
                CocosBcxApiWrapper.getBcxInstance().list_account_nh_asset(et_nh_asset_account.getText().toString(),
                        nh_asset_ids_or_hash,
                        Integer.valueOf(et_nh_asset_page.getText().toString()),
                        Integer.valueOf(et_nh_asset_page_size.getText().toString()),
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("list_account_nh_asset", value);
                            }
                        });
            }
        });

        /**
         * Seek World View Details
         */
        tv_look_up_word_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> world_view_names = new ArrayList<>();
                world_view_names.add(et_word_view.getText().toString());
                CocosBcxApiWrapper.getBcxInstance().lookup_world_view(world_view_names,
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("lookup_world_view", value);
                            }
                        });
            }
        });

        /**
         * 查询开发者关联的世界观
         */
        tv_get_nh_creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_nh_creator("gnkhandsome1",
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_nh_creator", value);
                            }
                        });
            }
        });


        /**
         * 查询开发者所创建的NH资产
         */
        tv_list_nh_asset_by_creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().list_nh_asset_by_creator(et_create_nh_asset_account.getText().toString(),
                        et_create_nh_asset_by_creator_world_view.getText().toString(),
                        Integer.valueOf(et_create_nh_asset_page.getText().toString()),
                        Integer.valueOf(et_create_nh_asset_page_size.getText().toString()),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("list_nh_asset_by_creator", value);
                            }
                        });
            }
        });


        /**
         * 查询账户下的NH资产售卖单
         */
        tv_list_account_nh_asset_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().list_account_nh_asset_order(et_create_nh_asset_account_order.getText().toString(),
                        Integer.valueOf(et_create_nh_asset_page_size_order.getText().toString()),
                        Integer.valueOf(et_create_nh_asset_page_order.getText().toString()),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("list_account_nh_asset_order", value);
                            }
                        });
            }
        });


        /**
         * 查询全网NH资产售卖单
         */
        tv_list_nh_asset_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().list_nh_asset_order(et_nh_asset_symbol_or_id.getText().toString(),
                        et_word_view_id_or_name.getText().toString(),
                        et_nh_asset_order_baseDescribe.getText().toString(),
                        Integer.valueOf(et_nh_asset_page_size_order.getText().toString()),
                        Integer.valueOf(et_nh_asset_page_order.getText().toString()),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("list_nh_asset_order", value);
                            }
                        });
            }
        });


        /**
         * 升级终身会员
         */
        tv_upgrade_lifetime_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().upgrade_to_lifetime_member(et_upgrade_lifetime_account.getText().toString(),
                        et_upgrade_lifetime_password.getText().toString(), new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("upgrade_to_lifetime_member", value);
                            }
                        });
            }
        });


        /**
         * 注册子账号
         */
        tv_create_child_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_child_account(et_create_child_account_name.getText().toString(),
                        et_create_child_account_password.getText().toString(),
                        et_create_child_registrar.getText().toString(),
                        et_create_child_registrar_password.getText().toString(),
                        "ACCOUNT",
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("create_child_account", value);
                            }
                        });
            }
        });

        tv_switch_node_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化工具类
                List<String> mListNode = Arrays.asList("ws://47.93.62.96:8020", "ws://47.93.62.96:8020", "ws://47.93.62.96:8020", "ws://47.93.62.96:8020");
                String faucetUrl = "http://47.93.62.96:4000";
                String chainId = "9fc429a48b47447afa5e6618fde46d1a5f7b2266f00ce60866f9fdd92236e137";
                String coreAsset = "COCOS";
                boolean isOpenLog = true;
                CocosBcxApiWrapper.getBcxInstance().connect(TestActivity.this, chainId, mListNode, faucetUrl, coreAsset, isOpenLog,
                        new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("initBcxSdk", value);
                            }
                        });
            }
        });


        /**
         * get transaction in block info
         */
        tv_get_transaction_in_block_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_transaction_in_block_info(et_get_transaction_in_block_info.getText().toString(), new IBcxCallBack() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_transaction_in_block_info", value);
                    }
                });
            }
        });


        /**
         * get transaction by id
         */
        get_transaction_by_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_transaction_by_id(et_get_transaction_in_block_info.getText().toString(), new IBcxCallBack() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_transaction_by_id", value);
                    }
                });
            }
        });


        /**
         * get global properties
         */
        get_global_properties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_global_properties(new IBcxCallBack() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_global_properties", value);
                    }
                });
            }
        });

        market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this, MarketActivity.class));
            }
        });

        get_estimation_gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_estimation_gas(estimation_gas_amount.getText().toString(), new IBcxCallBack() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("get_estimation_gas", value);
                    }
                });
            }
        });

        get_update_collateral_for_gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().update_collateral_for_gas(estimation_gas_mortgagor.getText().toString(),
                        estimation_gas_mortgagor_password.getText().toString(),
                        estimation_gas_beneficiary.getText().toString(),
                        estimation_gas_amount.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("update_collateral_for_gas", value);
                            }
                        });
            }
        });

        tv_vesting_balances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_vesting_balances(vesting_balances_account.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_vesting_balances", value);
                                MainHandler.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_vesting_balances_content.setText(value);
                                    }
                                });
                            }
                        });
            }
        });

        tv_get_vesting_balances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().receive_vesting_balances(get_vesting_balances_account.getText().toString(),
                        get_vesting_balances_account_password.getText().toString(),
                        get_vesting_balances_reward.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("receive_vesting_balances", value);
                                MainHandler.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_vesting_balances_content.setText(value);
                                    }
                                });
                            }
                        });
            }
        });


        tv_get_committee_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_committee_members(et_support_account.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_committee_members", value);
                            }
                        });
            }
        });

        tv_get_witnesses_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_witnesses_members(et_support_account.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_witnesses_members", value);
                            }
                        });
            }
        });


        tv_vote_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String[] vote_ids = et_vote_ids.getText().toString().split(",");

                List<String> vote_ids = new ArrayList<>();
                vote_ids.add("1.2.5");

                CocosBcxApiWrapper.getBcxInstance().vote_members(et_vote_account.getText().toString(),
                        et_vote_password.getText().toString(),
                        et_vote_type.getText().toString(),
                        vote_ids,
                        et_vote_number.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("tv_vote_members", value);
                            }
                        });
            }
        });

        tv_create_committee_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_committee_member(et_create_committee_member_account.getText().toString(),
                        et_create_committee_member_account_password.getText().toString(),
                        et_create_committee_member_url.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("create_committee_member", value);
                            }
                        });
            }
        });

        tv_create_witness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_witness(et_create_witness_account.getText().toString(),
                        et_create_witness_account_password.getText().toString(),
                        et_create_witness_url.getText().toString(),
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("create_witness", value);
                            }
                        });
            }
        });
    }


}
