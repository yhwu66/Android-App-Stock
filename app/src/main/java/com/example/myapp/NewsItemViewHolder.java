package com.example.myapp;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class NewsItemViewHolder extends RecyclerView.ViewHolder {
    final TextView source;
    final TextView headline;
    final ImageView newsimg;
    final View view;
    final TextView timediff;

    public NewsItemViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        source = (TextView) itemView.findViewById(R.id.source);
        headline = (TextView) itemView.findViewById(R.id.headline);
        newsimg = (ImageView) itemView.findViewById(R.id.newsimg);
        timediff = (TextView) itemView.findViewById(R.id.timediff);

    }
}
