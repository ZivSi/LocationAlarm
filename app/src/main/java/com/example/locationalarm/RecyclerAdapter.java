package com.example.locationalarm;


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
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    // All data that will be in the recycler view will be here
    ArrayList<ItemData> dataArray;
    Context context;
    Animation flip, flip_back;
    ViewGroup.LayoutParams params;
    int cardSize;
    final int COLLAPSE_SIZE = 480;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleLocation;
        private Chip x, y, distanceAlert, address;
        private ImageButton arrowButton;
        private MaterialButton activateButton, deleteButton;
        private ConstraintLayout cardLayout;

        public ViewHolder(View view) {
            super(view);

            // Define click listeners for all the ViewHolder's View
            titleLocation = view.findViewById(R.id.nameTextView);
            x = view.findViewById(R.id.xCoordinatesChip);
            y = view.findViewById(R.id.yCoordinatesChip);
            distanceAlert = view.findViewById(R.id.distanceAlertFromLocation);
            arrowButton = view.findViewById(R.id.arrow_button);
            address = view.findViewById(R.id.addressChip);
            activateButton = view.findViewById(R.id.activateButton);
            deleteButton = view.findViewById(R.id.deleteButton);
            cardLayout = view.findViewById(R.id.cardLayout);
            params = cardLayout.getLayoutParams();
            cardSize = params.width;
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

    private void collapse(ConstraintLayout cardLayout, ItemData itemData) {
        params = cardLayout.getLayoutParams();

        // Size
        params.height = COLLAPSE_SIZE;
        params.width = cardSize;

        cardLayout.setLayoutParams(params);

        itemData.setExpanded(false);
    }

    private void expand(ConstraintLayout cardLayout, ItemData itemData) {
        params = cardLayout.getLayoutParams();

        // Size
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = cardSize;

        cardLayout.setLayoutParams(params);

        itemData.setExpanded(true);
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_location_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData currentItem = dataArray.get(position);

        // Collapse when creating the current item as default
        collapse(holder.getCardLayout(), currentItem);

        holder.getTitleLocation().setText(currentItem.getName());
        holder.getX().setText("X: " + currentItem.getLongitude());
        holder.getY().setText("Y: " + currentItem.getLatitude());

        // TODO: Set length for chips

        // Address too long
        if(currentItem.getAddress().length() > 30) {
            holder.getAddress().setText("Address: " + currentItem.getAddress().substring(0, 30) + "...");
        } else {
            holder.getAddress().setText("Address: " + currentItem.getAddress());
        }

        holder.getDistanceAlert().setText(currentItem.getAlarmDistance() + "M");

        // Set color to chip distance by far or close
        if (Integer.parseInt(currentItem.getAlarmDistance()) > 10000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.OrangeRed)));
        } else if (Integer.parseInt(currentItem.getAlarmDistance()) > 1000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.Peru)));
        }

        holder.arrowButton.setOnClickListener((v) -> {
            if (currentItem.isExpanded()) {
                holder.arrowButton.startAnimation(flip_back);
                currentItem.setExpanded(false);

                collapse(holder.getCardLayout(), currentItem);
            } else {
                holder.arrowButton.startAnimation(flip);
                currentItem.setExpanded(true);

                expand(holder.getCardLayout(), currentItem);
            }

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
