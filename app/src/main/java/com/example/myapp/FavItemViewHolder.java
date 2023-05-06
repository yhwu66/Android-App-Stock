package com.example.myapp;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class FavItemViewHolder extends RecyclerView.ViewHolder {
    final TextView tvItem;
    final TextView nameItem;
    final TextView priceItem;
    final TextView pricechangeItem;
    final ImageView imageView;
    final ImageView arrow;

    public FavItemViewHolder(View itemView) {
        super(itemView);

        tvItem = (TextView) itemView.findViewById(R.id.tvItem);
        nameItem = (TextView) itemView.findViewById(R.id.nameItem);
        priceItem = (TextView) itemView.findViewById(R.id.priceItem);
        pricechangeItem = (TextView) itemView.findViewById(R.id.pricechangeItem);
        imageView = (ImageView) itemView.findViewById(R.id.imageViewAA);
        arrow = (ImageView) itemView.findViewById(R.id.imageView_arrow);
    }
}
