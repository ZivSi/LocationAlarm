package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

public class EditLayout extends AppCompatActivity {
    TextInputEditText nameBox, xCoordiantesBox, yCoordinatesBox;

    TextView distanceTextView;
    SeekBar seekBar;
    Chip chip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_layout);

        getSupportActionBar().hide();

        initViews();
    }

    private void initViews() {
        nameBox = findViewById(R.id.nameBox);
        xCoordiantesBox = findViewById(R.id.xCoordinatesBox);
        yCoordinatesBox = findViewById(R.id.yCoordinatesBox);
        distanceTextView = findViewById(R.id.distanceTextView);
        seekBar = findViewById(R.id.seekBar);
        chip = findViewById(R.id.chipRingtoneSelection);
    }

    
}