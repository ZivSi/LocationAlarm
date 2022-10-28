package com.example.locationalarm;


import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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

    // popup menu variables
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ImageButton dismissButton;
    private TextView popupTitle;
    private MaterialButton activateButton;
    private Chip popupAddress, popupX, popupY;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleLocation;
        private Chip distanceAlert, address;
        private ImageButton moreButton, playButton;
        private ConstraintLayout cardLayout;

        public ViewHolder(View view) {
            super(view);

            // Define click listeners for all the ViewHolder's View
            titleLocation = view.findViewById(R.id.nameTextView);
            distanceAlert = view.findViewById(R.id.distanceAlertFromLocation);
            address = view.findViewById(R.id.addressChip);
            cardLayout = view.findViewById(R.id.cardLayout);
            moreButton = view.findViewById(R.id.moreBtn);
            playButton = view.findViewById(R.id.playButton);
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

        public ImageButton getPlayButton() {
            return playButton;
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

        // Set color to chip distance by far or close
        if (Integer.parseInt(currentItem.getAlarmDistance()) > 2000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.OrangeRed)));
        } else if (Integer.parseInt(currentItem.getAlarmDistance()) > 1000) {
            holder.getDistanceAlert().setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.PaleVioletRed)));
        }

        holder.moreButton.setOnClickListener((v) -> {
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(context, holder.moreButton);
            Functions.showIconsForPopupMenu(popupMenu, context);
            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.edit_delete_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String name = holder.getTitleLocation().getText().toString();
                    ItemData item = MainActivity.data.get(name);

                    // Toast message on menu item clicked
                    if (menuItem.getItemId() == R.id.edit_button_menu) {
                        // create intent of the edit activity
                        Intent intent = new Intent(context, EditLayout.class);
                        intent.putExtra("name", name);
                        startActivity(context, intent, null);

                    } else if (menuItem.getItemId() == R.id.delete_button_menu) {
                        // delete item from the database
                        MainActivity.data.remove(item.getName());
                        Functions.SaveData(context, MainActivity.data);

                        context.startActivity(new Intent(context, MainActivity.class));
                    } else if (menuItem.getItemId() == R.id.duplicate_button_menu) {
                        // duplicate item
                        String newName = item.getName() + "(copy)";
                        if (newName.length() < MainActivity.MAX_NAME_LENGTH) {
                            ItemData dupe = new ItemData(newName, item.getAddress(), item.getLatitude(), item.getLongitude(), item.getAlarmDistance());

                            MainActivity.data.put(newName, dupe);
                            Functions.SaveData(context, MainActivity.data);

                            context.startActivity(new Intent(context, MainActivity.class));
                        } else { // in case of name too long
                            Toast.makeText(context, "Cannot duplicate: Name is too long", Toast.LENGTH_LONG).show();
                        }
                    }
                    return true;
                }
            });
            // Showing the popup menu
            popupMenu.show();
        });

        holder.getPlayButton().setOnClickListener((v) -> {
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

            // Get the data of the current item clicked and set the variables to the data
            String name = holder.getTitleLocation().getText().toString();
            ItemData item = MainActivity.data.get(name);
            assert item != null;
            popupTitle.setText(item.getName());
            popupAddress.setText(item.getAddress());
            popupX.setText("X: " + item.getLongitude());
            popupY.setText("Y: " + item.getLatitude());


            // Create and show dialog
            builder.setView(popupView);
            dialog = builder.create();
            dialog.show();

            dismissButton.setOnClickListener(v12 -> {
                dialog.dismiss();

                context.stopService(new Intent(context, AppService.class));
            });

            activateButton.setOnClickListener(v1 -> {
                startService(context, item);
            });
        });
    }

    public void startService(Context context, ItemData item) {
        // Set the dest variables in the service, and start the service
        Intent intent = new Intent(context, AppService.class);

        // Put data which coordinates to go to and the distance to alert
        intent.putExtra(MainActivity.COORDINATED_TAG, item.getLatitude() + "," + item.getLongitude());
        intent.putExtra(MainActivity.DISTANCE_TAG, item.getAlarmDistance());

        context.startService(intent);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
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
