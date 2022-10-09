package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class EditLayout extends AppCompatActivity {
    // main variables
    TextInputEditText nameBox, xCoordiantesBox, yCoordinatesBox;
    TextView distanceTextView;
    SeekBar seekBar;

    // error snackbar
    Snackbar snackbar;
    Snackbar.SnackbarLayout snackbarView;
    ConstraintLayout layout;

    // Get address from coordinates
    Geocoder geocoder;
    List<Address> addresses = new ArrayList<>();
    String country = "";
    String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_layout);

        getSupportActionBar().setTitle("");
        initViews();
        setDefaultDist();
    }

    // Initialize all the views
    private void initViews() {
        nameBox = findViewById(R.id.nameBox);
        xCoordiantesBox = findViewById(R.id.xCoordinatesBox);
        yCoordinatesBox = findViewById(R.id.yCoordinatesBox);
        distanceTextView = findViewById(R.id.distanceTextView);
        seekBar = findViewById(R.id.seekBar);
        layout = findViewById(R.id.mainEditLayout);
        snackbar = Snackbar.make(layout, "", Snackbar.LENGTH_SHORT);
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_error, null);

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

        geocoder = new Geocoder(this, Locale.getDefault());

        // get fields
        String name = nameBox.getText().toString().replaceAll(":", "");
        String x = xCoordiantesBox.getText().toString();
        String y = yCoordinatesBox.getText().toString();
        int distance = getDistance();

        // check for empty fields
        if (name.isEmpty() || x.isEmpty() || y.isEmpty()) {
            ((TextView) snackbarView.findViewById(R.id.error_message)).setText("Please fill all the fields");
            snackbar.show();
            return;
        }
        // check if the name is already taken
        if (MainActivity.data.containsKey(name)) {
            ((TextView) snackbarView.findViewById(R.id.error_message)).setText("Name already exists");
            snackbar.show();
            return;
        }

        // Get address
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(x), Double.parseDouble(y), 1);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // No data found
        if (addresses.size() == 0) {
            country = "Not Found";
        } else {
            country = addresses.get(0).getCountryName() + ", ";
        }

        // address is curently empty untill we add the google maps api
        MainActivity.data.put(name, new ItemData(name, country + address, x, y, String.valueOf(distance)));

        Functions.SaveData(this, MainActivity.data);
        finish();
    }

    // get the distance from the seekbar, set it to correct value and update textview
    private int getDistance() {
        int dist = seekBar.getProgress();
        int[] distances = {10, 50, 100, 250, 500, 750, 1000, 2000, 5000, 10000};
        dist = distances[dist];

        // change the meter sign to km if needed
        String distance;
        if (dist >= 1000) {
            dist /= 1000;

            distance = dist + "Km";
        } else {
            distance = dist + "M";
        }

        distanceTextView.setText("Distance: " + distance);
        return dist;
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

}