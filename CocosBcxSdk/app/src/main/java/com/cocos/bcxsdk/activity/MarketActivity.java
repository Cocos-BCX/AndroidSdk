package com.cocos.bcxsdk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cocos.bcxsdk.R;
import com.cocos.bcxsdk.fragments.AllLimitOrderHistoryFragment;
import com.cocos.bcxsdk.fragments.AssetPublishFeedFragment;
import com.cocos.bcxsdk.fragments.CreateLimitOrderFragment;
import com.cocos.bcxsdk.fragments.MyLimitOrderFragment;
import com.cocos.bcxsdk.fragments.MyLimitOrderHistoryFragment;
import com.cocos.bcxsdk.fragments.SettleAssetFragment;
import com.cocos.bcxsdk.fragments.TradeLimitOrderFragment;
import com.cocos.bcxsdk.fragments.TradePairHistoryFragment;
import com.cocos.bcxsdk.utils.Utils;
import com.cocos.bcxsdk.utils.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

/**
 * @author ningkang.guo
 * @Date 2019/9/4
 */
public class MarketActivity extends AppCompatActivity implements OnTabSelectListener, ViewPager.OnPageChangeListener {

    private String[] mTitles = {Utils.getString(R.string.settle_asset),
            Utils.getString(R.string.asset_publish_feed),
            Utils.getString(R.string.create_limit_order),
            Utils.getString(R.string.trade_limit_order),
            Utils.getString(R.string.my_limit_order),
            Utils.getString(R.string.my_limit_order_history),
            Utils.getString(R.string.all_limit_order_history),
            Utils.getString(R.string.trade_pair_history)
    };
    private ArrayList<Fragment> mFragments;
    public SlidingTabLayout tab_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        mFragments = new ArrayList<>();
        SettleAssetFragment settle_asset = new SettleAssetFragment();
        AssetPublishFeedFragment asset_publish_feed = new AssetPublishFeedFragment();
        CreateLimitOrderFragment create_limit_order = new CreateLimitOrderFragment();
        TradeLimitOrderFragment trade_limit_order = new TradeLimitOrderFragment();
        MyLimitOrderFragment my_limit_order = new MyLimitOrderFragment();
        MyLimitOrderHistoryFragment my_limit_order_history = new MyLimitOrderHistoryFragment();
        AllLimitOrderHistoryFragment all_limit_order_history = new AllLimitOrderHistoryFragment();
        TradePairHistoryFragment trade_pair_history = new TradePairHistoryFragment();
        mFragments.add(settle_asset);
        mFragments.add(asset_publish_feed);
        mFragments.add(create_limit_order);
        mFragments.add(trade_limit_order);
        mFragments.add(my_limit_order);
        mFragments.add(my_limit_order_history);
        mFragments.add(all_limit_order_history);
        mFragments.add(trade_pair_history);

        View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.view_pager);
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(mFragments.size() - 1);
        tab_layout = ViewFindUtils.find(decorView, R.id.tab_layout);
        tab_layout.setViewPager(vp, mTitles, this, mFragments);
    }

    @Override
    public void onTabSelect(int position) {
        tab_layout.setCurrentTab(position);
    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tab_layout.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


}
