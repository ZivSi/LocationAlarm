package com.example.locationalarm;


import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    // All data that will be in the recycler view will be here
    ArrayList<ItemData> dataArray;
    Context context;
    Animation flip, flip_back;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleLocation;
        private Chip distanceAlert, address;
        private ImageButton arrowButton;
        private MaterialButton activateButton, deleteButton;
        private ConstraintLayout cardLayout;

        public ViewHolder(View view) {
            super(view);

            // Define click listeners for all the ViewHolder's View
            titleLocation = view.findViewById(R.id.nameTextView);
            distanceAlert = view.findViewById(R.id.distanceAlertFromLocation);
            arrowButton = view.findViewById(R.id.arrow_button);
            address = view.findViewById(R.id.addressChip);
            cardLayout = view.findViewById(R.id.cardLayout);
        }

        public TextView getTitleLocation() {
            return titleLocation;
        }

        public Chip getDistanceAlert() {
            return distanceAlert;
        }

        public ImageButton getArrowButton() {
            return arrowButton;
        }

        public Chip getAddress() {
            return address;
        }

        public ConstraintLayout getCardLayout() {
            return cardLayout;
        }

        public MaterialButton getActivateButton() {
            return activateButton;
        }

        public MaterialButton getDeleteButton() {
            return deleteButton;
        }
    }

    // Constructor to initialize data
    public RecyclerAdapter(ArrayList<ItemData> dataArray, Context context) {
        // Copy data
        this.dataArray = new ArrayList<>(dataArray);
        this.context = context;

        flip_back = AnimationUtils.loadAnimation(context, R.anim.arrow_flip_back);
        flip = AnimationUtils.loadAnimation(context, R.anim.arrow_flip);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // * With creation of every view, inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_location_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData currentItem = dataArray.get(position);

        holder.getTitleLocation().setText(currentItem.getName());

        // TODO: Set length for chips

        // Address too long
        if (currentItem.getAddress().length() > 30) {
            holder.getAddress().setText("Address: " + currentItem.getAddress().substring(0, 30) + "...");
        } else {
            holder.getAddress().setText("Address: " + currentItem.getAddress());
        }

        holder.getDistanceAlert().setText(currentItem.getAlarmDistance() + "M");

        // Set color to chip distance by far or close
        if (Integer.parseInt(currentItem.getAlarmDistance()) > 2000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.OrangeRed)));
        } else if (Integer.parseInt(currentItem.getAlarmDistance()) > 1000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.PaleVioletRed)));
        }

        holder.arrowButton.setOnClickListener((v) -> {
            // TODO: Open popup window
        });
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }

    public void updateData(ArrayList<ItemData> newData) {
        dataArray = newData;
        this.notifyDataSetChanged();
    }

}
