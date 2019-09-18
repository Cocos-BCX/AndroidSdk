package com.cocos.bcxsdk.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cocos.bcx_sdk.bcx_api.CocosBcxApi;
import com.cocos.bcx_sdk.bcx_error.AssetNotFoundException;
import com.cocos.bcx_sdk.bcx_error.NetworkStatusException;
import com.cocos.bcx_sdk.bcx_error.UnLegalInputException;
import com.cocos.bcx_sdk.bcx_wallet.chain.account_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.asset_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.fill_order_history_object;
import com.cocos.bcxsdk.R;
import com.cocos.bcxsdk.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class MyLimitOrderHistoryFragment extends Fragment {


    private EditText account_name;
    private EditText transaction_pair;
    private EditText transaction_limit;
    private CheckBox is_subscribed;
    private TextView get_my_limit_orders;
    private RecyclerView rv_my_limit_history_orders;
    private EditText my_limit_orders_history_result;
    private TradeLimitOrderHistoryAdapter tradeLimitOrderHistoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_history_order, null);
        account_name = v.findViewById(R.id.account_name);
        transaction_pair = v.findViewById(R.id.transaction_pair);
        transaction_limit = v.findViewById(R.id.transaction_limit);
        is_subscribed = v.findViewById(R.id.is_subscribed);
        get_my_limit_orders = v.findViewById(R.id.get_my_limit_orders);
        rv_my_limit_history_orders = v.findViewById(R.id.rv_my_limit_history_orders);
        my_limit_orders_history_result = v.findViewById(R.id.my_limit_orders_history_result);
        rv_my_limit_history_orders.setLayoutManager(new LinearLayoutManager(getContext()));
        int space = Utils.dip2px(20);
        rv_my_limit_history_orders.addItemDecoration(new SpacesItemDecoration(space));
        initData();
        return v;
    }

    private void initData() {
        /**
         *   查询资产‘限价单’
         *   get_limit_orders
         */
        get_my_limit_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<fill_order_history_object> fill_order_history_objects = CocosBcxApi.getBcxInstance().get_fill_order_history(transaction_pair.getText().toString(), Integer.parseInt(transaction_limit.getText().toString()));
                    if (TextUtils.isEmpty(account_name.getText().toString())) {
                        tradeLimitOrderHistoryAdapter = new TradeLimitOrderHistoryAdapter(getContext(), fill_order_history_objects);
                        rv_my_limit_history_orders.setAdapter(tradeLimitOrderHistoryAdapter);
                        return;
                    }
                    List<fill_order_history_object> my_limit_orders_objects = new ArrayList<>();
                    for (fill_order_history_object fill_order_history_object : fill_order_history_objects) {
                        try {
                            account_object account_object = CocosBcxApi.getBcxInstance().get_accounts(fill_order_history_object.op.account_id.toString());
                            if (TextUtils.equals(account_object.name, account_name.getText().toString())) {
                                my_limit_orders_objects.add(fill_order_history_object);
                            }
                        } catch (NetworkStatusException e) {
                            my_limit_orders_history_result.setText(e.getMessage());
                        } catch (UnLegalInputException e) {
                            my_limit_orders_history_result.setText(e.getMessage());
                        } catch (Exception e) {
                            my_limit_orders_history_result.setText(e.getMessage());
                        }
                    }
                    tradeLimitOrderHistoryAdapter = new TradeLimitOrderHistoryAdapter(getContext(), my_limit_orders_objects);
                    rv_my_limit_history_orders.setAdapter(tradeLimitOrderHistoryAdapter);
                } catch (NetworkStatusException e) {
                    my_limit_orders_history_result.setText(e.getMessage());
                } catch (AssetNotFoundException e) {
                    my_limit_orders_history_result.setText(e.getMessage());
                } catch (UnLegalInputException e) {
                    my_limit_orders_history_result.setText(e.getMessage());
                } catch (Exception e) {
                    my_limit_orders_history_result.setText(e.getMessage());
                }
            }
        });

    }

    public class TradeLimitOrderHistoryAdapter extends RecyclerView.Adapter<TradeLimitOrderHistoryAdapter.MyViewHolder> {
        Context context;
        List<fill_order_history_object> datas;

        public TradeLimitOrderHistoryAdapter(Context context, List<fill_order_history_object> datas) {
            this.context = context;
            this.datas = datas;
        }

        //创建ViewHolder
        @NonNull
        @Override
        public TradeLimitOrderHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //实例化得到Item布局文件的View对象
            View v = View.inflate(context, R.layout.trade_limit_order_history_item, null);
            //返回MyViewHolder的对象
            return new TradeLimitOrderHistoryAdapter.MyViewHolder(v);
        }

        //绑定数据
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull TradeLimitOrderHistoryAdapter.MyViewHolder holder, int position) {
            fill_order_history_object fill_order_history_object = datas.get(position);
            try {
                account_object account_object = CocosBcxApi.getBcxInstance().get_accounts(fill_order_history_object.op.account_id.toString());
                asset_object assetQuoteObject = CocosBcxApi.getBcxInstance().lookup_asset_symbols(fill_order_history_object.op.fill_price.quote.asset_id.toString());
                asset_object assetBaseObject = CocosBcxApi.getBcxInstance().lookup_asset_symbols(fill_order_history_object.op.fill_price.base.asset_id.toString());
                asset_object assetPayObject = CocosBcxApi.getBcxInstance().lookup_asset_symbols(fill_order_history_object.op.pays.asset_id.toString());

                if (fill_order_history_object.op.is_maker) {
                    holder.price.setText((fill_order_history_object.op.fill_price.quote.amount / Math.pow(10, assetQuoteObject.precision)) / (fill_order_history_object.op.fill_price.base.amount / Math.pow(10, assetBaseObject.precision)) + assetQuoteObject.symbol + "/" + assetBaseObject.symbol);
                    holder.amount.setText(fill_order_history_object.op.receives.amount / Math.pow(10, assetQuoteObject.precision) + assetQuoteObject.symbol);
                    holder.valide_time.setText(fill_order_history_object.time);
                    holder.deal_amount.setText(fill_order_history_object.op.pays.amount / Math.pow(10, assetPayObject.precision) + assetPayObject.symbol);
                    holder.tv_account.setText("卖方");
                    holder.account_name.setText(account_object.name);
                } else {
                    holder.price.setText((fill_order_history_object.op.fill_price.quote.amount / Math.pow(10, assetQuoteObject.precision)) / (fill_order_history_object.op.fill_price.base.amount / Math.pow(10, assetBaseObject.precision)) + assetQuoteObject.symbol + "/" + assetBaseObject.symbol);
                    holder.amount.setText(fill_order_history_object.op.receives.amount / Math.pow(10, assetQuoteObject.precision) + assetQuoteObject.symbol);
                    holder.valide_time.setText(fill_order_history_object.time);
                    holder.deal_amount.setText(fill_order_history_object.op.pays.amount / Math.pow(10, assetPayObject.precision) + assetPayObject.symbol);
                    holder.tv_account.setText("买方");
                    holder.account_name.setText(account_object.name);
                }

            } catch (NetworkStatusException e) {
                e.printStackTrace();
            } catch (UnLegalInputException e) {
                e.printStackTrace();
            }

        }

        //返回Item的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }

        //继承RecyclerView.ViewHolder抽象类的自定义ViewHolder
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView price;
            TextView amount;
            TextView valide_time;
            TextView deal_amount;
            TextView tv_account;
            TextView account_name;

            public MyViewHolder(View itemView) {
                super(itemView);
                price = itemView.findViewById(R.id.price);
                amount = itemView.findViewById(R.id.amount);
                valide_time = itemView.findViewById(R.id.valide_time);
                deal_amount = itemView.findViewById(R.id.deal_amount);
                tv_account = itemView.findViewById(R.id.tv_account);
                account_name = itemView.findViewById(R.id.account_name);
            }
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

}
