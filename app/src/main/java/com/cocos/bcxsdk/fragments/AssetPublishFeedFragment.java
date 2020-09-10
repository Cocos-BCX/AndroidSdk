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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 资产喂价
 *
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class AssetPublishFeedFragment extends Fragment {

    private EditText assetUpdateFeed_symbol_account;
    private EditText assetUpdateFeed_symbol_password;
    private EditText assetUpdateFeed_symbol;
    private EditText assetUpdateFeed_producers;
    private TextView assetUpdateFeed;
    private EditText assetPublishFeed_account;
    private EditText assetPublishFeed_password;
    private EditText assetPublishFeed_symbol;
    private EditText assetPublishFeed_price;
    private EditText assetPublishFeed_maintaining_mortgage_rate;
    private EditText assetPublishFeed_upper_limit_of_compulsory_ratio;
    private EditText assetPublishFeed_number_benchmark_assets;
    private EditText assetPublishFeed_number_of_listed_assets;
    private TextView assetPublishFeed;
    private EditText assetUpdateFeed_producers_result;
    private EditText assetPublishFeed_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_asset_publish, null);
        assetUpdateFeed_symbol_account = v.findViewById(R.id.assetUpdateFeed_symbol_account);
        assetUpdateFeed_symbol_password = v.findViewById(R.id.assetUpdateFeed_symbol_password);
        assetUpdateFeed_symbol = v.findViewById(R.id.assetUpdateFeed_symbol);
        assetUpdateFeed_producers = v.findViewById(R.id.assetUpdateFeed_producers);
        assetUpdateFeed = v.findViewById(R.id.assetUpdateFeed);
        assetUpdateFeed_producers_result = v.findViewById(R.id.assetUpdateFeed_producers_result);

        assetPublishFeed_account = v.findViewById(R.id.assetPublishFeed_account);
        assetPublishFeed_password = v.findViewById(R.id.assetPublishFeed_password);
        assetPublishFeed_symbol = v.findViewById(R.id.assetPublishFeed_symbol);
        assetPublishFeed_price = v.findViewById(R.id.assetPublishFeed_price);
        assetPublishFeed_maintaining_mortgage_rate = v.findViewById(R.id.assetPublishFeed_maintaining_mortgage_rate);
        assetPublishFeed_upper_limit_of_compulsory_ratio = v.findViewById(R.id.assetPublishFeed_upper_limit_of_compulsory_ratio);
        assetPublishFeed_number_benchmark_assets = v.findViewById(R.id.assetPublishFeed_number_benchmark_assets);
        assetPublishFeed_number_of_listed_assets = v.findViewById(R.id.assetPublishFeed_number_of_listed_assets);
        assetPublishFeed = v.findViewById(R.id.assetPublishFeed);
        assetPublishFeed_result = v.findViewById(R.id.assetPublishFeed_result);
        initData();
        return v;
    }

    private void initData() {

        /**
         *  更新喂价提供者
         *   update_feed_product
         */
        assetUpdateFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strings = assetUpdateFeed_producers.getText().toString().trim().split(",");
                List<String> products = Arrays.asList(strings);
                CocosBcxApiWrapper.getBcxInstance().update_feed_product(assetUpdateFeed_symbol_account.getText().toString(), assetUpdateFeed_symbol_password.getText().toString(), assetUpdateFeed_symbol.getText().toString(), products, "COCOS", new IBcxCallBack() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("update_feed_product", value);
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                assetUpdateFeed_producers_result.setText(value);
                            }
                        });
                    }
                });
            }
        });

        /**
         * 资产喂价
         *  publish_feed
         */
        assetPublishFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().publish_feed(assetPublishFeed_account.getText().toString(),
                        assetPublishFeed_password.getText().toString(),
                        assetPublishFeed_symbol.getText().toString(),
                        assetPublishFeed_price.getText().toString(),
                        new BigDecimal(assetPublishFeed_maintaining_mortgage_rate.getText().toString()),
                        new BigDecimal(assetPublishFeed_upper_limit_of_compulsory_ratio.getText().toString()),
                        assetPublishFeed_number_benchmark_assets.getText().toString(),
                        assetPublishFeed_number_of_listed_assets.getText().toString(),
                        "COCOS", "COCOS", new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("publish_feed", value);
                                MainHandler.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        assetPublishFeed_result.setText(value);
                                    }
                                });
                            }
                        });
            }
        });


    }
}
