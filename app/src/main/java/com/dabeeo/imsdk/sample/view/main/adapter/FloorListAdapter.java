package com.dabeeo.imsdk.sample.view.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dabeeo.imsdk.model.common.FloorInfo;
import com.dabeeo.imsdk.sample.R;

import java.util.List;

public class FloorListAdapter extends RecyclerView.Adapter<FloorListViewHolder> {


    private List<FloorInfo> items;
    private FloorListItemCallback callback;
    public int currentFloorLevel;

    public FloorListAdapter() {

    }

    public FloorListAdapter(List<FloorInfo> items, FloorListItemCallback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public FloorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_floor, parent, false);


        return new FloorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FloorListViewHolder holder, int position) {
        FloorInfo item = items.get(position);

        holder.title.setText(item.getName().get(0).getText());

        Context context = holder.itemView.getContext();
        int textColor = item.getLevel() == currentFloorLevel ? ContextCompat.getColor(context, R.color.flame) : ContextCompat.getColor(context, R.color.light_gray);
        holder.title.setTextColor(textColor);

        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onItemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<FloorInfo> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setCallback(FloorListItemCallback callback) {
        this.callback = callback;
    }

    public void setFloor(int floorLevel) {
        currentFloorLevel = floorLevel;
        notifyDataSetChanged();
    }

    public interface FloorListItemCallback {
        void onItemClicked(FloorInfo item);
    }
}

