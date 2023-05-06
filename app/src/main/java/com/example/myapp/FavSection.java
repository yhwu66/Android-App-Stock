package com.example.myapp;

import static android.content.ContentValues.TAG;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spanned;
import android.util.Log;
import android.view.View;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

class FavSection extends Section {
    List<String> itemList = Arrays.asList("AAPL", "AMZN", "TSLA","Item 4");
    List<favitem> list;
    public FavSection(List<favitem> list) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.favitem)
                .headerResourceId(R.layout.favheader)
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

    public void removeItem(){
        List<favitem> tmp = new ArrayList<>();
        for(int i=0;i<4;i++){}
    }


    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        FavItemViewHolder itemHolder = (FavItemViewHolder) holder;
        //final favitem item = list.get(position);
        // bind your view here

        itemHolder.tvItem.setText(list.get(position).getTicker());
        itemHolder.tvItem.setTextColor(Color.BLACK);
        itemHolder.tvItem.setTypeface(null, Typeface.BOLD);
        itemHolder.tvItem.setTextSize(20);
        itemHolder.nameItem.setText(list.get(position).getComName());


        itemHolder.priceItem.setText(String.format("%.2f",list.get(position).getPrice()));
        itemHolder.priceItem.setTextColor(Color.BLACK);
        itemHolder.priceItem.setTypeface(null, Typeface.BOLD);
        itemHolder.priceItem.setTextSize(20);

        itemHolder.pricechangeItem.setText("$"+String.format("%.2f",list.get(position).getPriceChange())+"("+String.format("%.2f",list.get(position).getPriceChangePer())+"%)");
        if(list.get(position).getPriceChange()>0){
            itemHolder.pricechangeItem.setTextColor(Color.parseColor("#4CAF50"));
            itemHolder.arrow.setBackgroundResource(R.drawable.ic_up);
        }
        else if(list.get(position).getPriceChange()<0){
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

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new SectionedRecyclerViewAdapter.EmptyViewHolder(view);
    }
}
