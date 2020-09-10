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
import android.widget.Toast;

import com.cocos.bcx_sdk.bcx_api.CocosBcxApi;
import com.cocos.bcx_sdk.bcx_api.CocosBcxApiWrapper;
import com.cocos.bcx_sdk.bcx_callback.IBcxCallBack;
import com.cocos.bcx_sdk.bcx_callback.ResponseData;
import com.cocos.bcx_sdk.bcx_error.AssetNotFoundException;
import com.cocos.bcx_sdk.bcx_error.NetworkStatusException;
import com.cocos.bcx_sdk.bcx_error.UnLegalInputException;
import com.cocos.bcx_sdk.bcx_wallet.chain.account_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.global_config_object;
import com.cocos.bcx_sdk.bcx_wallet.chain.limit_orders_object;
import com.cocos.bcxsdk.R;
import com.cocos.bcxsdk.utils.MainHandler;
import com.cocos.bcxsdk.utils.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class MyLimitOrderFragment extends Fragment {

    private EditText transaction_pair;
    private EditText transaction_limit;
    private CheckBox is_subscribed;
    private TextView get_all_limit_orders;
    private RecyclerView rv_all_limit_orders;
    private EditText all_limit_orders_result;
    private EditText account_name;
    private EditText account_password;
    MyTradeLimitOrderAdapter myTradeLimitOrderAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_trade_limit_order, null);
        account_name = v.findViewById(R.id.account_name);
        account_password = v.findViewById(R.id.account_password);
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
                    if (TextUtils.isEmpty(account_name.getText().toString())) {
                        myTradeLimitOrderAdapter = new MyTradeLimitOrderAdapter(getContext(), limit_orders_objects);
                        rv_all_limit_orders.setAdapter(myTradeLimitOrderAdapter);
                        return;
                    }
                    List<limit_orders_object> my_limit_orders_objects = new ArrayList<>();
                    for (limit_orders_object limit_orders_object : limit_orders_objects) {
                        try {
                            account_object account_object = CocosBcxApi.getBcxInstance().get_accounts(limit_orders_object.seller.toString());
                            if (TextUtils.equals(account_object.name, account_name.getText().toString())) {
                                my_limit_orders_objects.add(limit_orders_object);
                            }
                        } catch (NetworkStatusException e) {
                            all_limit_orders_result.setText(e.getMessage());
                        } catch (UnLegalInputException e) {
                            all_limit_orders_result.setText(e.getMessage());
                        } catch (Exception e) {
                            all_limit_orders_result.setText(e.getMessage());
                        }
                    }
                    myTradeLimitOrderAdapter = new MyTradeLimitOrderAdapter(getContext(), my_limit_orders_objects);
                    rv_all_limit_orders.setAdapter(myTradeLimitOrderAdapter);
                } catch (NetworkStatusException e) {
                    all_limit_orders_result.setText(e.getMessage());
                } catch (AssetNotFoundException e) {
                    all_limit_orders_result.setText(e.getMessage());
                } catch (UnLegalInputException e) {
                    all_limit_orders_result.setText(e.getMessage());
                } catch (Exception e) {
                    all_limit_orders_result.setText(e.getMessage());
                }
            }
        });

    }

    public class MyTradeLimitOrderAdapter extends RecyclerView.Adapter<MyTradeLimitOrderAdapter.MyViewHolder> {
        Context context;
        List<limit_orders_object> datas;

        public MyTradeLimitOrderAdapter(Context context, List<limit_orders_object> datas) {
            this.context = context;
            this.datas = datas;
        }

        //创建ViewHolder
        @NonNull
        @Override
        public MyTradeLimitOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //实例化得到Item布局文件的View对象
            View v = View.inflate(context, R.layout.trade_limit_order_item, null);
            //返回MyViewHolder的对象
            return new MyViewHolder(v);
        }

        //绑定数据
        @Override
        public void onBindViewHolder(@NonNull MyTradeLimitOrderAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            limit_orders_object limit_orders_object = datas.get(position);
            try {
                account_object account_object = CocosBcxApi.getBcxInstance().get_accounts(limit_orders_object.seller.toString());
                holder.seller.setText(String.valueOf(account_object.name));
                if (!TextUtils.isEmpty(account_name.getText().toString()) && TextUtils.equals(account_object.name, account_name.getText().toString())) {
                    holder.cancel_order.setVisibility(View.VISIBLE);
                } else {
                    holder.cancel_order.setVisibility(View.GONE);
                }
                holder.cancel_order.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(account_password.getText().toString())) {
                            Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_LONG).show();
                            return;
                        }
                        CocosBcxApiWrapper.getBcxInstance().cancel_limit_order(account_object.name, account_password.getText().toString(), limit_orders_object.id.toString(), "COCOS", new IBcxCallBack() {
                            @Override
                            public void onReceiveValue(String value) {
                                ResponseData responseData = global_config_object.getInstance().getGsonBuilder().create().fromJson(value, ResponseData.class);
                                MainHandler.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (responseData.getCode() == 1) {
                                            datas.remove(limit_orders_object);
                                            myTradeLimitOrderAdapter.notifyItemRemoved(position);
                                        } else {
                                            all_limit_orders_result.setText(responseData.getMessage());
                                            myTradeLimitOrderAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
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
            TextView cancel_order;

            public MyViewHolder(View itemView) {
                super(itemView);
                price = itemView.findViewById(R.id.price);
                amount = itemView.findViewById(R.id.amount);
                valide_time = itemView.findViewById(R.id.valide_time);
                deal_amount = itemView.findViewById(R.id.deal_amount);
                seller = itemView.findViewById(R.id.seller);
                cancel_order = itemView.findViewById(R.id.cancel_order);
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
