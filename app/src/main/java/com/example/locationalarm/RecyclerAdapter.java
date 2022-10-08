package com.example.locationalarm;


import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
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

    // popup menu variables
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ImageButton dismissButton;
    private MaterialButton activateButton;
    private TextView popupTitle;
    private Chip popupAddress, popupX, popupY;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleLocation;
        private Chip distanceAlert, address;
        private ImageButton moreButton, openActivatePopupBtn;
        private ConstraintLayout cardLayout;

        public ViewHolder(View view) {
            super(view);

            // Define click listeners for all the ViewHolder's View
            titleLocation = view.findViewById(R.id.nameTextView);
            distanceAlert = view.findViewById(R.id.distanceAlertFromLocation);
            address = view.findViewById(R.id.addressChip);
            cardLayout = view.findViewById(R.id.cardLayout);
            moreButton = view.findViewById(R.id.moreBtn);
            openActivatePopupBtn = view.findViewById(R.id.openActivatePopup);
        }

        public TextView getTitleLocation() {
            return titleLocation;
        }

        public Chip getDistanceAlert() {
            return distanceAlert;
        }

        public ImageButton getMoreButton() {
            return moreButton;
        }

        public Chip getAddress() {
            return address;
        }

        public ConstraintLayout getCardLayout() {
            return cardLayout;
        }

        public ImageButton getOpenActivatePopupBtn() {
            return openActivatePopupBtn;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_location_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("MissingInflatedId")
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

        holder.moreButton.setOnClickListener((v) -> {

        });

        holder.openActivatePopupBtn.setOnClickListener((v) -> {
            // create builder and inflate view
            builder = new AlertDialog.Builder(context);
            final View popupView = LayoutInflater.from(context).inflate(R.layout.item_pop_up_window, null);

            // set variables in the popup window
            dismissButton = popupView.findViewById(R.id.dismissButton);
            activateButton = popupView.findViewById(R.id.activateButton);
            popupTitle = popupView.findViewById(R.id.popupTitle);
            popupAddress = popupView.findViewById(R.id.popupAddressChip);
            popupX = popupView.findViewById(R.id.xCoordinatesChip);
            popupY = popupView.findViewById(R.id.yCoordinatesChip);

            // get the data of the current item clicked and set the variables to the data
            String name = holder.getTitleLocation().getText().toString();
            ItemData item = MainActivity.data.get(name);
            popupTitle.setText(item.getName());
            popupAddress.setText(item.getAddress());
            popupX.setText("X: " + item.getLongitude());
            popupY.setText("Y: " + item.getLatitude());


            // create and show dialog
            builder.setView(popupView);
            dialog = builder.create();
            dialog.show();

            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            activateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo: activate alarm
                }
            });
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
