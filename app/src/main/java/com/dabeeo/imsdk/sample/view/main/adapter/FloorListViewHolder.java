package com.dabeeo.imsdk.sample.view.main.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dabeeo.imsdk.sample.R;

public class FloorListViewHolder extends RecyclerView.ViewHolder {
    public TextView title;

    public FloorListViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.textTitle);
    }
}