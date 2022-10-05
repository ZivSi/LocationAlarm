package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class EditLayout extends AppCompatActivity {
    TextInputEditText nameBox, xCoordiantesBox, yCoordinatesBox;

    TextView distanceTextView;
    SeekBar seekBar;
    MaterialButton chooseRingtone;

    Snackbar snackbar;
    Snackbar.SnackbarLayout snackbarView;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_layout);

        getSupportActionBar().setTitle("");


        initViews();
    }

    // Initialize all the views
    private void initViews() {
        nameBox = findViewById(R.id.nameBox);
        xCoordiantesBox = findViewById(R.id.xCoordinatesBox);
        yCoordinatesBox = findViewById(R.id.yCoordinatesBox);
        distanceTextView = findViewById(R.id.distanceTextView);
        chooseRingtone = findViewById(R.id.chooseRingtoneMaterialButton);
        seekBar = findViewById(R.id.seekBar);
        layout = findViewById(R.id.mainEditLayout);
        snackbar = Snackbar.make(layout, "", Snackbar.LENGTH_SHORT);
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_error, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarView.setPadding(-10, 0, -10, 0);
        snackbarView.addView(custom_view, 0);


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


    /**
     * Save the new data in file, after checked all properties are valid
     */
    public void saveData() {
        // get fields
        String name = nameBox.getText().toString();
        String x = xCoordiantesBox.getText().toString();
        String y = yCoordinatesBox.getText().toString();
        int distance = getDistance();
        // todo: add ringtone

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

        // address is curently empty untill we add the google maps api
        // ringtone is empty until we add the ringtone selection
        MainActivity.data.put(name, new ItemData(name, " ", x, y, String.valueOf(distance), " "));

        // TODO: Save map in file
        finish();
    }

    // get the distance from the seekbar, set it to correct value and update textview
    private int getDistance() {
        int dist = seekBar.getProgress();

        switch (dist) {
            case 0:
                dist = 10;
                break;
            case 1:
                dist = 50;
                break;
            case 2:
                dist = 100;
                break;
            case 3:
                dist = 250;
                break;
            case 4:
                dist = 500;
                break;
            case 5:
                dist = 750;
                break;
            case 6:
                dist = 1000;
                break;
            case 7:
                dist = 2000;
                break;
            case 8:
                dist = 5000;
                break;
            case 9:
                dist = 10000;
                break;
        }
        // Default will never be called...

        String distance = "Distance: " + dist + "m";
        distanceTextView.setText(distance);
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