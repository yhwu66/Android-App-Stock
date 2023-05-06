package com.example.myapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class PorItemViewHolder extends RecyclerView.ViewHolder {
    final TextView tvItem;

    public PorItemViewHolder(View itemView) {
        super(itemView);

        tvItem = (TextView) itemView.findViewById(R.id.tvItem);
    }
}