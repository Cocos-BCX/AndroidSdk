package com.cocos.bcxsdk.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cocos.bcx_sdk.bcx_api.CocosBcxApiWrapper;
import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcxsdk.R;

/**
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class MarketStateFragment extends Fragment {

    private EditText base_transaction;
    private EditText transaction_pair;
    private CheckBox is_subscribed;
    private TextView getMarketStats;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trade_pair_market_state, null);
        base_transaction = v.findViewById(R.id.base_transaction);
        transaction_pair = v.findViewById(R.id.transaction_pair);
        is_subscribed = v.findViewById(R.id.is_subscribed);
        getMarketStats = v.findViewById(R.id.getMarketStats);
        initData();
        return v;
    }

    private void initData() {
        /**
         *  查询交易对K线数据
         *  get_market_history
         */
        getMarketStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CocosBcxApiWrapper.getBcxInstance().get_market_history(base_transaction.getText().toString(),
                        transaction_pair.getText().toString(),
                        86400,
                        "2019-08-30T03:37:01",
                        "2019-09-1T03:37:01",
                        new IBcxCallBack() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onReceiveValue(String value) {
                                Log.i("get_market_history", value);
                            }
                        });
            }
        });

    }
}
