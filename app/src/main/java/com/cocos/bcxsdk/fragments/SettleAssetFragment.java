package com.cocos.bcxsdk.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cocos.bcx_sdk.bcx_api.CocosBcxApiWrapper;
import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcxsdk.R;
import com.cocos.bcxsdk.utils.MainHandler;

/**
 * 资产清算页
 *
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class SettleAssetFragment extends Fragment {

    private EditText settle_asset_account;
    private EditText settle_asset_password;
    private EditText settle_asset_symbol;
    private EditText settle_asset_amount;
    private TextView settle_asset;
    private EditText global_settle_asset_symbol;
    private EditText global_settle_asset_price;
    private EditText global_settle_asset_account;
    private EditText global_settle_asset_password;
    private TextView global_settle_asset;
    private EditText settle_asset_result;
    private EditText global_settle_asset_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settle_asset, null);
        settle_asset_account = v.findViewById(R.id.settle_asset_account);
        settle_asset_password = v.findViewById(R.id.settle_asset_password);
        settle_asset_symbol = v.findViewById(R.id.settle_asset_symbol);
        settle_asset_amount = v.findViewById(R.id.settle_asset_amount);
        settle_asset = v.findViewById(R.id.settle_asset);
        settle_asset_result = v.findViewById(R.id.settle_asset_result);
        global_settle_asset_symbol = v.findViewById(R.id.global_settle_asset_symbol);
        global_settle_asset_price = v.findViewById(R.id.global_settle_asset_price);
        global_settle_asset = v.findViewById(R.id.global_settle_asset);
        global_settle_asset_account = v.findViewById(R.id.global_settle_asset_account);
        global_settle_asset_password = v.findViewById(R.id.global_settle_asset_password);
        global_settle_asset_result = v.findViewById(R.id.global_settle_asset_result);
        try {
            initData();
        } catch (Exception e) {
            settle_asset_result.setText(e.getMessage());
            global_settle_asset_result.setText(e.getMessage());
        }
        return v;
    }

    private void initData() {

        /**
         *  个人资产清算
         * asset settle
         */
        settle_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().asset_settle(settle_asset_account.getText().toString(), settle_asset_password.getText().toString(), settle_asset_symbol.getText().toString(), settle_asset_amount.getText().toString(), "COCOS", new IBcxCallBack() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("asset_settle", value);
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                settle_asset_result.setText(value);
                            }
                        });
                    }
                });
            }
        });


        /**
         * 全局资产清算
         * global asset settle
         */
        global_settle_asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().global_asset_settle(global_settle_asset_account.getText().toString(), global_settle_asset_password.getText().toString(), global_settle_asset_symbol.getText().toString(), global_settle_asset_price.getText().toString(), "COCOS", new IBcxCallBack() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("global_asset_settle", value);
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                global_settle_asset_result.setText(value);
                            }
                        });
                    }
                });
            }
        });

    }


}
