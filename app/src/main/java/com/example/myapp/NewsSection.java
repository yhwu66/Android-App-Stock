package com.example.myapp;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

class NewsSection extends Section {
    List<String> itemList = Arrays.asList("Item1", "Item2", "Item3","Item 4");
    List<newsitem> list;
    Context mContext;
    public NewsSection(List<newsitem> list,Context context) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.newsitem)
                .headerResourceId(R.layout.newshead)
                .build());
        this.list = list;
        mContext = context;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new NewsItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        NewsItemViewHolder itemHolder = (NewsItemViewHolder) holder;

        // bind your view here
        itemHolder.source.setText(list.get(position).getSource());
        itemHolder.headline.setText(list.get(position).getHeadline());
        String imgurl = list.get(position).getImage();
        if(!imgurl.equals("")) {
            Picasso.get().load(imgurl).into(itemHolder.newsimg);
        }
        String TAG = "timestamp";
        long curtimemili = System.currentTimeMillis();
        int curtime = (int) (curtimemili/1000);
        int newstime = list.get(position).getTimeStamp();
        int diffi = curtime-newstime;
        long diff = diffi *1000;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        String diffstr = "";
        diffstr = diffHours + " hours ago";
        itemHolder.timediff.setText(diffstr);
        itemHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,""+newstime);
                final Dialog dialog2 = new Dialog(mContext);
                // Include dialog.xml file
                dialog2.setContentView(R.layout.dialog_news);
                // Set dialog title
                dialog2.setTitle("Con Dialog");
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                long newtimelong = (long) newstime;
                long newtimestamp = newtimelong*1000;
                Log.i(TAG,""+newtimestamp);
                Timestamp ts=new Timestamp(newtimestamp);
                Date date=ts;
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date);
                Formatter fmt1 = new Formatter();
                fmt1 = new Formatter();
                fmt1.format("%tB",cal1);
                String todaymonth =fmt1.toString();
                Formatter fmt2 = new Formatter();
                Calendar cal2 = Calendar.getInstance();
                fmt2 = new Formatter();
                fmt2.format("%td",cal2);
                String todaydate =fmt2.toString();
                Formatter fmt3 = new Formatter();
                Calendar cal3 = Calendar.getInstance();
                fmt3 = new Formatter();
                fmt3.format("%tY",cal3);
                String todayyear =fmt3.toString();


                TextView text = (TextView) dialog2.findViewById(R.id.dialog_source);
                String htmlString2 = "<h1>"+list.get(position).getSource()+"</h1>";
                Spanned spanned2 = HtmlCompat.fromHtml(htmlString2, HtmlCompat.FROM_HTML_MODE_COMPACT);
                text.setText(spanned2);
                text.setTextColor(Color.BLACK);
                TextView text2 = (TextView) dialog2.findViewById(R.id.dialog_date);
                text2.setText(todaymonth+" "+todaydate+", "+todayyear);

                TextView text3 = (TextView) dialog2.findViewById(R.id.dialog_headline);
                text3.setText(list.get(position).getHeadline());

                TextView text4 = (TextView) dialog2.findViewById(R.id.dialog_summary);
                text4.setText(list.get(position).getSummary());

                ImageView chrome = (ImageView) dialog2.findViewById(R.id.chromeImg);
                chrome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getUrl()));
                        mContext.startActivity(browserIntent);
                    }
                });

                ImageView twitter = (ImageView) dialog2.findViewById(R.id.twitterImg);
                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sharedUrl =list.get(position).getUrl();
                        String url = "http://www.twitter.com/intent/tweet?url="+sharedUrl+"&text="+"Check out this Link";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        mContext.startActivity(i);
                    }
                });

                ImageView facebook = (ImageView) dialog2.findViewById(R.id.facebookImg);
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sharedUrl =list.get(position).getUrl();
                        String url = "https://www.facebook.com/sharer/sharer.php?u=" + sharedUrl;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        mContext.startActivity(i);
                    }
                });


                dialog2.show();

            }
        });
        //Glide.with(View.this)
          //      .load("https://s.yimg.com/ny/api/res/1.2/ny2LI5UFRHEWBtxxjQ0Dgw--/YXBwaWQ9aGlnaGxhbmRlcjt3PTEyMDA7aD02NzU-/https://s.yimg.com/os/creatr-uploaded-images/2022-04/1f016080-c7fd-11ec-afde-7e50a0b40944")
            //    .into(itemHolder.newsimg);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new SectionedRecyclerViewAdapter.EmptyViewHolder(view);
    }
}
