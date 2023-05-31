package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class ActiveTracking extends AppCompatActivity {

    String coordinates;
    int distanceAlert;

    private int distance;
    private int time; // TODO: chage to time object for good fromat
    private String destination;

    private BroadcastReceiver timeBroadcastReceiver;

    MaterialButton stopTracking;

    TextView destinationTextView;
    TextView distanceTextView;
    TextView timeTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_tracking_layout);
        Intent intent = getIntent();
        // get extras and init variables
        destination = intent.getStringExtra("name");
        coordinates = intent.getStringExtra(MainActivity.COORDINATED_TAG);
        distanceAlert = intent.getIntExtra(MainActivity.DISTANCE_TAG, 100);

        initViews();
        startTimeUpdater();
        startDistanceUpdater();

        // start location tracking
        // Set the dest variables in the service, and start the service
        Intent it = new Intent(getApplicationContext(), AppService.class);

        // Put data which coordinates to go to and the distance to alert
        it.putExtra(MainActivity.COORDINATED_TAG, coordinates);
        it.putExtra(MainActivity.DISTANCE_TAG, distanceAlert);
        ComponentName locationService = getApplicationContext().startService(it);
    }

    private void initViews() {
        destinationTextView = findViewById(R.id.locationName);
        distanceTextView = findViewById(R.id.distanceTextView);
        timeTextView = findViewById(R.id.activeTimeText);

        destinationTextView.setText(destination);
        updateDistance(0);
        updateTime(0);

        stopTracking = findViewById(R.id.stopTracker);
        stopTracking.setOnClickListener(v -> {
            // stop the service
            Intent it = new Intent(getApplicationContext(), AppService.class);
            getApplicationContext().stopService(it);
            // stop the activity
            finish();
        });
    }

    public void startTimeUpdater() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                time++;
                updateTime(time);
            }
        };
        registerReceiver(timeBroadcastReceiver, intentFilter);
    }

    public void startDistanceUpdater() {
        IntentFilter intentFilter = new IntentFilter("com.example.locationalarm.distance");
        timeBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                distance = intent.getIntExtra("distance", 0);
                updateDistance(distance);
            }
        };
        registerReceiver(timeBroadcastReceiver, intentFilter);
    }

    public void updateDistance(int distance) {
        this.distance = distance;
        distanceTextView.setText("Distance: " +  distance + " meters");
    }

    public void updateTime(int time){
        this.time = time;
        timeTextView.setText("Active time: " + String.valueOf(time) + " minutes");
    }
}