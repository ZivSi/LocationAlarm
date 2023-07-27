package com.example.locationalarm;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

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

    ItemData currentItem;
    final int[] distances = {100, 250, 500, 750, 1000, 2000, 3000, 5000, 7000, 10000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_layout);

        if (thereAreExtras()) {
            extractExtras();
        }

        getSupportActionBar().setTitle("");
        initViews();
        if (!isEdit) setDefaultDist();
    }

    private void extractExtras() {
        Bundle extras = getIntent().getExtras();
        String name;
        isEdit = true;
        name = extras.getString("name");
        currentItem = MainActivity.data.get(name);
    }

    private boolean thereAreExtras() {
        return getIntent().getExtras() != null;
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

        if (isEdit) {
            putSavedData();
        }

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarView.setPadding(-10, 0, -10, 0);
        snackbarView.addView(custom_view, 0);

        // onchange listener for seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getDistance();
                updateImages(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateImages(int progress) {
        ImageView person, location, arrow;
        person = findViewById(R.id.personImageView);
        location = findViewById(R.id.locationImageView);
        arrow = findViewById(R.id.arrowImageView);

        int horizontalIncreaseInPixels = 25 * progress;

        ViewGroup.MarginLayoutParams arrowLayoutParams = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();

        arrowLayoutParams.width = 70 + horizontalIncreaseInPixels;
        arrow.setScaleX((float) (1.5 + progress * 0.4));
        arrow.setLayoutParams(arrowLayoutParams);
    }

    private void putSavedData() {
        nameBox.setText(currentItem.getName());
        addressBox.setText(currentItem.getAddress());
        xCoordiantesBox.setText(String.valueOf(currentItem.getLongitude()));
        yCoordinatesBox.setText(String.valueOf(currentItem.getLatitude()));
        distanceTextView.setText(currentItem.getAlarmDistance());
        seekBar.setProgress(getDistFromFullValue(Integer.parseInt(currentItem.getAlarmDistance())));
        getDistance();
    }


    private void setDefaultDist() {
        if (Settings.allEntries == null) { // We have some errors here. allEntries is null
            return;
        }

        seekBar.setProgress(Integer.parseInt(String.valueOf(Settings.allEntries.get("default_distance"))));
        getDistance();
    }

    /**
     * Save the new data in file, after checked all properties are valid
     */
    public void saveData() {
        if (isEdit) {
            MainActivity.data.remove(currentItem.getName());
        }

        geocoder = new Geocoder(this, Locale.getDefault());

        String[] qord = extractAddressAndCoordinates();
        String x = qord[0], y = qord[1];

        String name = nameBox.getText().toString().replaceAll(":", "");
        String address = addressBox.getText().toString();

        if (isAddress && (name.isEmpty() || address.isEmpty()) || !isAddress && (x.isEmpty() || y.isEmpty() || name.isEmpty())) {
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

        int distance = getDistance();

        saveItem(x, y, name, distance);

        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveItem(String x, String y, String name, int distance) {
        MainActivity.data.put(name, new ItemData(name, address, x, y, String.valueOf(distance)));

        Functions.SaveData(this, MainActivity.data);
    }


    /**
     * extracts the address and coordinates from the boxes of the location settings
     * that the user enterd, if the user entered an address it will get the coordinates
     * from the address and vice versa
     *
     * @return the coordinates of the location
     */
    private String[] extractAddressAndCoordinates() {
        String x, y;
        List<Address> addresses;
        List<Address> coordinatesOfAddress;

        if (isAddress) {
            address = addressBox.getText().toString();
            try {
                coordinatesOfAddress = geocoder.getFromLocationName(addressBox.getText().toString(), 1);
            } catch (Exception e) {
                return new String[]{"0", "0"};
            }

            if (notFound(coordinatesOfAddress)) {
                return new String[]{"0", "0"};
            }

            x = String.valueOf(coordinatesOfAddress.get(0).getLatitude());
            y = String.valueOf(coordinatesOfAddress.get(0).getLongitude());

            return new String[]{x, y};
        } else {
            String[] coords = getCoordsFromBoxes();
            double xDouble = Double.parseDouble(coords[0]);
            double yDouble = Double.parseDouble(coords[1]);

            try {
                addresses = geocoder.getFromLocation(xDouble, yDouble, 1);
            } catch (Exception e) {
                address = "Not found";

                return new String[]{coords[0], coords[1]};
            }

            if (notFound(addresses)) {
                address = "Not found";
            } else {
                address = addresses.get(0).getCountryName() + ", " + addresses.get(0).getAddressLine(0);
            }

            return new String[]{coords[0], coords[1]};
        }
    }

    private String[] getCoordsFromBoxes() {
        String xVal = ((TextInputEditText) findViewById(R.id.xCoordinatesBox)).getText().toString();
        String yVal = ((TextInputEditText) findViewById(R.id.yCoordinatesBox)).getText().toString();

        if (xVal.equals("")) {
            xVal = "0";
        }
        if (yVal.equals("")) {
            yVal = "0";
        }

        return new String[]{xVal, yVal};
    }

    private boolean notFound(List<Address> list) {
        return list.size() == 0;
    }

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