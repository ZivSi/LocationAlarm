package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

public class EditLayout extends AppCompatActivity {
    TextInputEditText nameBox, xCoordiantesBox, yCoordinatesBox;

    TextView distanceTextView;
    SeekBar seekBar;
    Chip ringtoneChip, saveChip, backChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_layout);

        getSupportActionBar().hide();

        initViews();
    }

    // Initialize all the views
    private void initViews() {
        nameBox = findViewById(R.id.nameBox);
        xCoordiantesBox = findViewById(R.id.xCoordinatesBox);
        yCoordinatesBox = findViewById(R.id.yCoordinatesBox);
        distanceTextView = findViewById(R.id.distanceTextView);
        ringtoneChip = findViewById(R.id.chipRingtoneSelection);
        saveChip = findViewById(R.id.saveButton);
        backChip = findViewById(R.id.backButton);
        seekBar = findViewById(R.id.seekBar);
        // onchange listener for seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getDistance();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    public void backToMain(View view) {
        finish();
    }

    /**
     * take the input data from the user and save it to a ItemData object then save it to the data map
     * @param view view
     */
    public void saveData(View view) {
        // get fields
        String name = nameBox.getText().toString();
        String x = xCoordiantesBox.getText().toString();
        String y = yCoordinatesBox.getText().toString();
        int distance = getDistance();
        // todo: add ringtone

        // check for empty fields
        if(name.isEmpty() || x.isEmpty() || y.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if the name is already taken
        if (MainActivity.data.containsKey(name)) {
            Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // address is curently empty untill we add the google maps api
        // ringtone is empty until we add the ringtone selection
        MainActivity.data.put(name, new ItemData(name, " ", x, y, String.valueOf(distance), " "));
        finish();
    }

    // get the distance from the seekbar, set it to correct value and update textview
    private int getDistance() {
        int dist = seekBar.getProgress();

        switch (dist){
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
            default:
                dist = 100;
        }
        String distance = "Distance: " + dist + "m";
        distanceTextView.setText(distance);
        return dist;
    }

}