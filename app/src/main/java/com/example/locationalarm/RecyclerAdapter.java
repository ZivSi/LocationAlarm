package com.example.locationalarm;


import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    // All data that will be in the recycler view will be here
    ArrayList<ItemData> dataArray;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleLocation;
        private TextInputEditText textInputEditText;
        private Chip x, y, distanceAlert;

        public ViewHolder(View view) {
            super(view);

            // Define click listeners for all the ViewHolder's View
            titleLocation = view.findViewById(R.id.nameTextView);
            x = view.findViewById(R.id.xCoordinatesChip);
            y = view.findViewById(R.id.yCoordinatesChip);
            distanceAlert = view.findViewById(R.id.distanceAlertFromLocation);


            // * Click listeners

        }

        public TextView getTitleLocation() {
            return titleLocation;
        }

        public Chip getX() {
            return x;
        }

        public Chip getY() {
            return y;
        }

        public Chip getDistanceAlert() {
            return distanceAlert;
        }
    }

    // Constructor to initialize data
    public RecyclerAdapter(ArrayList<ItemData> dataArray, Context context) {
        // Copy data
        this.dataArray = new ArrayList<>(dataArray);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // * With creation of every view

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_location_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // * Set all properties when creating the item

        holder.getTitleLocation().setText(dataArray.get(position).getName());
        holder.getX().setText(dataArray.get(position).getLongitude());
        holder.getY().setText(dataArray.get(position).getLatitude());

        // TODO: Set length for chips

        holder.getDistanceAlert().setText(dataArray.get(position).getAlarmDistance() + "M");

        // Set color to chip distance by far or close
        if (Integer.parseInt(dataArray.get(position).getAlarmDistance()) > 10000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.OrangeRed)));
        } else if (Integer.parseInt(dataArray.get(position).getAlarmDistance()) > 1000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.Peru)));
        }
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }
}
