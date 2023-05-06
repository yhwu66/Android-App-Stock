package com.example.myapp;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 2;
    private String str = "";
    private String ticker = "";
    private String strhour = "";
    private double change;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,String xx,String ticker1,String yy,double pchange) {
        super(fragmentActivity);
        str = xx;
        ticker = ticker1;
        strhour = yy;
        change = pchange;
    }

    @NonNull @Override public Fragment createFragment(int position) {
        //return new HisChartFragment();
        if(position==1)
            return HisChartsFragment.newInstance(position,str,ticker);
        else
            return HourChartsFragment.newInstance(position,strhour,ticker,change);
        /*Fragment fragment = new DemoObjectFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(DemoObjectFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;*/
    }


    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}