package com.example.myapp;

import static android.content.ContentValues.TAG;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

class PorSection extends Section {
    List<String> itemList = Arrays.asList("AAPL", "AMZN", "TSLA","Item 4");
    List<favitem> list;
    public PorSection(List<favitem> list) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.favitem)
                .headerResourceId(R.layout.porheader)
                .build());
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new FavItemViewHolder(view);
    }



    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        FavItemViewHolder itemHolder = (FavItemViewHolder) holder;
        //final favitem item = list.get(position);
        // bind your view here
        if(position>0){
            //Log.i(TAG,"tstpor position:" + position);

            itemHolder.tvItem.setText(list.get(position).getTicker());
            itemHolder.tvItem.setTextColor(Color.BLACK);
            itemHolder.tvItem.setTypeface(null, Typeface.BOLD);
            itemHolder.tvItem.setTextSize(20);

            itemHolder.nameItem.setText(list.get(position).getComName());

            itemHolder.priceItem.setText(String.format("%.2f",list.get(position).getPrice()*list.get(position).getShares()));
            itemHolder.priceItem.setTextColor(Color.BLACK);
            itemHolder.priceItem.setTypeface(null, Typeface.BOLD);
            itemHolder.priceItem.setTextSize(20);

            double change = list.get(position).getPrice()*list.get(position).getShares() - list.get(position).getCost();
            change =round(change,2);
            double changeper = change/(list.get(position).getPrice()*list.get(position).getShares()) * 100;
            itemHolder.pricechangeItem.setText("$"+String.format("%.2f",change)+"("+String.format("%.2f",changeper)+"%)");

            if(change>0){
                itemHolder.pricechangeItem.setTextColor(Color.parseColor("#4CAF50"));
                itemHolder.arrow.setBackgroundResource(R.drawable.ic_up);
            }
            else if(change<0){
                itemHolder.pricechangeItem.setTextColor(Color.parseColor("#F44336"));
                itemHolder.arrow.setBackgroundResource(R.drawable.ic_down);
            }

            itemHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"is exist right click ok" );
                    Intent intent = new Intent(v.getContext(),SearchableActivity.class);
                    intent.setAction(Intent.ACTION_SEARCH);
                    intent.putExtra(SearchManager.QUERY, list.get(position).getTicker());
                    v.getContext().startActivity(intent);
                    //Log.i("this peer", "peers"+comPeers[1]);

                }
            });

        }
        else{
            itemHolder.tvItem.setText("Net Worth");
            itemHolder.priceItem.setText("Cash Balance");
            itemHolder.nameItem.setText("$"+String.format("%.2f",list.get(position).getPriceChange()));
            itemHolder.pricechangeItem.setText("$"+String.format("%.2f",list.get(position).getPrice()));
            itemHolder.tvItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            itemHolder.priceItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            itemHolder.nameItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            itemHolder.pricechangeItem.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

            itemHolder.tvItem.setTypeface(null, Typeface.BOLD);
            itemHolder.priceItem.setTypeface(null, Typeface.BOLD);
            itemHolder.nameItem.setTypeface(null, Typeface.BOLD);
            itemHolder.pricechangeItem.setTypeface(null, Typeface.BOLD);
            itemHolder.imageView.setVisibility(View.GONE);

        }


    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new SectionedRecyclerViewAdapter.EmptyViewHolder(view);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
