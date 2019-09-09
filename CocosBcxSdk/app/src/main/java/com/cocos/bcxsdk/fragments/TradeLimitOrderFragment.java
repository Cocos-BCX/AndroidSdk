package com.cocos.bcxsdk.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.cocos.bcx_sdk.bcx_wallet.chain.limit_orders_object;
import com.cocos.bcxsdk.R;
import com.cocos.bcxsdk.utils.Utils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 所有限价单
 *
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class TradeLimitOrderFragment extends Fragment {

    private EditText transaction_pair;
    private EditText transaction_limit;
    private CheckBox is_subscribed;
    private TextView get_all_limit_orders;
    private RecyclerView rv_all_limit_orders;
    private EditText all_limit_orders_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trade_limit_order, null);
        transaction_pair = v.findViewById(R.id.transaction_pair);
        transaction_limit = v.findViewById(R.id.transaction_limit);
        is_subscribed = v.findViewById(R.id.is_subscribed);
        get_all_limit_orders = v.findViewById(R.id.get_all_limit_orders);
        rv_all_limit_orders = v.findViewById(R.id.rv_all_limit_orders);
        all_limit_orders_result = v.findViewById(R.id.all_limit_orders_result);
        rv_all_limit_orders.setLayoutManager(new LinearLayoutManager(getContext()));
        int space = Utils.dip2px(20);
        rv_all_limit_orders.addItemDecoration(new SpacesItemDecoration(space));
        initData();
        return v;
    }

    private void initData() {
        /**
         *   查询资产‘限价单’
         *   get_limit_orders
         */
        get_all_limit_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<limit_orders_object> limit_orders_objects = CocosBcxApi.getBcxInstance().get_limit_orders(transaction_pair.getText().toString(), Integer.parseInt(transaction_limit.getText().toString()));
                    rv_all_limit_orders.setAdapter(new TradeLimitOrderAdapter(getContext(), limit_orders_objects));
                } catch (NetworkStatusException e) {
                    all_limit_orders_result.setText(e.getMessage());
                } catch (AssetNotFoundException e) {
                    all_limit_orders_result.setText(e.getMessage());
                } catch (UnLegalInputException e) {
                    all_limit_orders_result.setText(e.getMessage());
                }
            }
        });

    }

    public class TradeLimitOrderAdapter extends RecyclerView.Adapter<TradeLimitOrderAdapter.MyViewHolder> {
        Context context;
        List<limit_orders_object> datas;

        public TradeLimitOrderAdapter(Context context, List<limit_orders_object> datas) {
            this.context = context;
            this.datas = datas;
        }

        //创建ViewHolder
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //实例化得到Item布局文件的View对象
            View v = View.inflate(context, R.layout.trade_limit_order_item, null);
            //返回MyViewHolder的对象
            return new MyViewHolder(v);
        }

        //绑定数据
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            limit_orders_object limit_orders_object = datas.get(position);
            try {
                account_object account_object = CocosBcxApi.getBcxInstance().get_accounts(limit_orders_object.seller.toString());
                holder.seller.setText(String.valueOf(account_object.name));
            } catch (NetworkStatusException e) {
                e.printStackTrace();
            } catch (UnLegalInputException e) {
                e.printStackTrace();
            }
            holder.price.setText(String.valueOf(limit_orders_object.sell_price.base.amount));
            holder.amount.setText(String.valueOf(limit_orders_object.for_sale));
            holder.valide_time.setText(String.valueOf(limit_orders_object.expiration));

            holder.deal_amount.setText(String.valueOf(limit_orders_object.for_sale.multiply(BigDecimal.valueOf(limit_orders_object.sell_price.base.amount))));
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
            TextView seller;

            public MyViewHolder(View itemView) {
                super(itemView);
                price = itemView.findViewById(R.id.price);
                amount = itemView.findViewById(R.id.amount);
                valide_time = itemView.findViewById(R.id.valide_time);
                deal_amount = itemView.findViewById(R.id.deal_amount);
                seller = itemView.findViewById(R.id.seller);
            }
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }
}
