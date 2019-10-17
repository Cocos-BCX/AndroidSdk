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
import com.cocos.bcxsdk.activity.MarketActivity;
import com.cocos.bcxsdk.utils.MainHandler;

import java.math.BigDecimal;

/**
 * 创建代币资产‘限价单’交易
 *
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class CreateLimitOrderFragment extends Fragment {

    private EditText createLimitOrder_account;
    private EditText createLimitOrder_password;
    private EditText createLimitOrder_transaction_pair;
    private EditText createLimitOrder_type;
    private EditText createLimitOrder_price;
    private EditText createLimitOrder_amount;
    private EditText createLimitOrder_valide_time;
    private TextView createLimitOrder;
    private TextView to_my_limit_order;
    private TextView to_all_limit_order;
    private EditText createLimitOrder_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_limit_order, null);
        createLimitOrder_account = v.findViewById(R.id.createLimitOrder_account);
        createLimitOrder_password = v.findViewById(R.id.createLimitOrder_password);
        createLimitOrder_transaction_pair = v.findViewById(R.id.createLimitOrder_transaction_pair);
        createLimitOrder_valide_time = v.findViewById(R.id.createLimitOrder_valide_time);
        createLimitOrder_type = v.findViewById(R.id.createLimitOrder_type);
        createLimitOrder_price = v.findViewById(R.id.createLimitOrder_price);
        createLimitOrder_amount = v.findViewById(R.id.createLimitOrder_amount);
        createLimitOrder = v.findViewById(R.id.createLimitOrder);
        to_my_limit_order = v.findViewById(R.id.to_my_limit_order);
        to_all_limit_order = v.findViewById(R.id.to_all_limit_order);
        createLimitOrder_result = v.findViewById(R.id.createLimitOrder_result);
        initData();
        return v;
    }

    private void initData() {

        /**
         *   创建代币资产‘限价单’交易
         *   create_limit_order
         */
        createLimitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().create_limit_order(createLimitOrder_account.getText().toString(),
                        createLimitOrder_password.getText().toString(),
                        createLimitOrder_transaction_pair.getText().toString(),
                        Integer.parseInt(createLimitOrder_type.getText().toString()),
                        Integer.parseInt(createLimitOrder_valide_time.getText().toString()),
                        new BigDecimal(createLimitOrder_price.getText().toString()),
                        new BigDecimal(createLimitOrder_amount.getText().toString()),
                         new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("create_limit_order", value);
                                MainHandler.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        createLimitOrder_result.setText(value);
                                    }
                                });
                            }
                        });
            }
        });

        to_my_limit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketActivity marketActivity = (MarketActivity) getActivity();
                marketActivity.tab_layout.setCurrentTab(4);
            }
        });

        to_all_limit_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketActivity marketActivity = (MarketActivity) getActivity();
                marketActivity.tab_layout.setCurrentTab(3);
            }
        });

    }
}
