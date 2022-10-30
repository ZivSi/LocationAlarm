package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditLayout extends AppCompatActivity {
    boolean isEdit = false, isAddress = true;
    // main variables
    TextInputEditText nameBox, xCoordiantesBox, yCoordinatesBox, addressBox;
    TextView distanceTextView;
    SeekBar seekBar;

    // error snackbar
    Snackbar snackbar;
    Snackbar.SnackbarLayout snackbarView;
    ConstraintLayout layout;

    // Get address from coordinates
    Geocoder geocoder;
    List<Address> addresses = new ArrayList<>();
    String address = "";

    ItemData editItem;
    final int[] distances = {100, 250, 500, 750, 1000, 2000, 3000, 5000, 7000, 10000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_layout);

        // get extras
        String name;
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isEdit = true;
            name = extras.getString("name");
            editItem = MainActivity.data.get(name);
        }
        getSupportActionBar().setTitle("");
        initViews();
        if (!isEdit) setDefaultDist();
    }

    // Initialize all the views
    private void initViews() {
        nameBox = findViewById(R.id.nameBox);
        xCoordiantesBox = findViewById(R.id.xCoordinatesBox);
        yCoordinatesBox = findViewById(R.id.yCoordinatesBox);
        addressBox = findViewById(R.id.addressBox);
        distanceTextView = findViewById(R.id.distanceTextView);
        seekBar = findViewById(R.id.seekBar);
        layout = findViewById(R.id.mainEditLayout);
        snackbar = Snackbar.make(layout, "", Snackbar.LENGTH_SHORT);
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_error, null);
        findViewById(R.id.xCoordinatesLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.yCoordinatesLayout).setVisibility(View.INVISIBLE);

        if (isEdit) { // if the user is editing an item set the values of the boxes to the current item
            nameBox.setText(editItem.getName());
            addressBox.setText(editItem.getAddress());
            xCoordiantesBox.setText(String.valueOf(editItem.getLongitude()));
            yCoordinatesBox.setText(String.valueOf(editItem.getLatitude()));
            distanceTextView.setText(editItem.getAlarmDistance());
            seekBar.setProgress(getDistFromFullValue(Integer.parseInt(editItem.getAlarmDistance())));
            getDistance();
        }

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarView.setPadding(-10, 0, -10, 0);
        snackbarView.addView(custom_view, 0);


        Settings.getInfo(this);

        // onchange listener for seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getDistance();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void setDefaultDist() {
        Settings.getInfo(this);
        // get the value from the settings
        seekBar.setProgress(Integer.parseInt(String.valueOf(Settings.allEntries.get("default_distance"))));
        getDistance();
    }

    /**
     * Save the new data in file, after checked all properties are valid
     */
    public void saveData() {
        String x, y;

        if (isEdit) { // remove the original and save the new one
            MainActivity.data.remove(editItem.getName());
        } ;
        geocoder = new Geocoder(this, Locale.getDefault());

        String[] qord = extractAddressAndCoordinates();
        x = qord[0];
        y = qord[1];

        // get fields
        String name = nameBox.getText().toString().replaceAll(":", "");
        // check for empty fields
        if (name.isEmpty()) {
            ((TextView) snackbarView.findViewById(R.id.error_message)).setText("Please fill all the fields");
            snackbar.show();
            return;
        }
        if (x.isEmpty() || y.isEmpty())return;
        // check if the name is already taken
        if (MainActivity.data.containsKey(name)) {
            ((TextView) snackbarView.findViewById(R.id.error_message)).setText("Name already exists");
            snackbar.show();
            return;
        }

        int distance = getDistance();
        // Get address from coordinates

        // address is curently empty untill we add the google maps api
        MainActivity.data.put(name, new ItemData(name, address, x, y, String.valueOf(distance)));

        Functions.SaveData(this, MainActivity.data);
        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }


    /**
     * extracts the address and coordinates from the boxes of the location settings
     * that the user enterd, if the user entered an address it will get the coordinates
     * from the address and vice versa
     * @return the coordinates of the location
     */
    private String[] extractAddressAndCoordinates() {
        String x = "", y = "";

        if (isAddress) {
            try { // try and extract the coordinates from the address
                addresses = geocoder.getFromLocationName(addressBox.getText().toString(), 1);
                if (addresses.size() == 0) { // no address found
                    ((TextView) snackbarView.findViewById(R.id.error_message)).setText("Address not found");
                    snackbar.show();
                    return new String[]{x, y};
                }
                else if (addresses.size() > 0) { // get long and lad
                    address = addresses.get(0).getAddressLine(0);
                    x = String.valueOf(addresses.get(0).getLatitude());
                    y = String.valueOf(addresses.get(0).getLongitude());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // get the address from the coordinates
        else {
            x = xCoordiantesBox.getText().toString();
            y = yCoordinatesBox.getText().toString();
            try { // get address
                addresses = geocoder.getFromLocation(Double.parseDouble(x), Double.parseDouble(y), 1);
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }

            // No data found
            if (addresses.size() == 0) {
                address = "Not Found";
            } else {
                address = addresses.get(0).getCountryName() + ", " + address;
            }
        }
        return new String[]{x, y};
    }

    // get the distance from the seekbar, set it to correct value and update textview
    private int getDistance() {
        int dist = seekBar.getProgress();
        dist = distances[dist];

        // change the meter sign to km if needed
        String distance;
        int dupe = dist;
        if (dupe >= 1000) {
            dupe /= 1000;

            distance = dupe + "Km";
        } else {
            distance = dupe + "M";
        }

        distanceTextView.setText("Distance: " + distance);
        return dist;
    }

    // get the distance for the seekbar from a full value (ex: 1000m return 6)
    public int getDistFromFullValue(int value) {
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] == value) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.check_button_menu) {
            // Pressed on save button in action bar
            saveData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // set the text inputs to coordinates
    public void showCoordinatesInput(View view) {
        // set input text visible
        findViewById(R.id.xCoordinatesLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.yCoordinatesLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.addressLayout).setVisibility(View.INVISIBLE);
        isAddress = false;
    }

    // set the text input to address
    public void showAddressInput(View view) {
        // set input text visible
        findViewById(R.id.yCoordinatesLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.xCoordinatesLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.addressLayout).setVisibility(View.VISIBLE);
        isAddress = true;
    }
}