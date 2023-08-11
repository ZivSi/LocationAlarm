package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.Timer;
import java.util.TimerTask;

public class ActiveTracking extends AppCompatActivity {

    String coordinates;
    int distanceAlert;

    Timer timer = new Timer();

    private int distance;
    private long time = 0;
    private String destination;

    private BroadcastReceiver timeBroadcastReceiver;
    private BroadcastReceiver distanceBroadcastReceiver;

    MaterialButton stopTracking;

    TextView destinationTextView;
    TextView distanceTextView;
    TextView timeTextView;

    private Intent broadcastIntent = new Intent("com.example.locationalarm.time");
    private Intent locationIntent;

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
        stopServicesListener();

        Functions.checkLocationActive(this);


        locationIntent = new Intent(getApplicationContext(), AppService.class);
        locationIntent.setAction(AppService.START_TRACKING);

        locationIntent.putExtra(MainActivity.COORDINATED_TAG, coordinates);
        locationIntent.putExtra(MainActivity.DISTANCE_TAG, distanceAlert);
        startService(locationIntent);
    }

    private void initViews() {
        destinationTextView = findViewById(R.id.locationName);
        distanceTextView = findViewById(R.id.distanceTextView);
        timeTextView = findViewById(R.id.activeTimeText);

        destinationTextView.setText(destination);
        updateDistance(0);
        updateTime();

        stopTracking = findViewById(R.id.stopTracker);
        stopTracking.setOnClickListener(v -> {
            // stop the service
            timer.cancel();
            stopService(locationIntent);
            // stop the activity
            finish();
        });

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendBroadcast(broadcastIntent);
            }
        }, 1000, 1000 );

    }

    public void stopServicesListener() {
        IntentFilter intentFilter = new IntentFilter("com.example.locationalarm.hasArrived");
        timeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                timer.cancel();
                stopService(locationIntent);
                Toast.makeText(getApplicationContext(), "You have arrived", Toast.LENGTH_LONG).show();
                Context cont = getApplicationContext();
                finish();
            }
        };
        registerReceiver(timeBroadcastReceiver, intentFilter);
    }

    public void startTimeUpdater() {
        IntentFilter intentFilter = new IntentFilter("com.example.locationalarm.time");
        timeBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                updateTime();
            }
        };
        registerReceiver(timeBroadcastReceiver, intentFilter);
    }

    public void startDistanceUpdater() {
        IntentFilter intentFilter = new IntentFilter("com.example.locationalarm.distance");
        distanceBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                distance = intent.getIntExtra("distance", 0);
                updateDistance(distance);
            }
        };
        registerReceiver(distanceBroadcastReceiver, intentFilter);
    }

    public void updateDistance(int distance) {
        this.distance = distance;
        if (distance >= 1000){
            float dist = (float)distance / 1000;
            distanceTextView.setText("Distance: " +  dist + " kilometers");
        }
        else{
            distanceTextView.setText("Distance: " +  distance + " meters");
        }
    }

    public void updateTime(){
        time++;
        String time_formated;

        int hours = (int) time / 3600;
        int minutes = (int) (time - (hours * 3600)) / 60;
        int seconds = (int) time - (hours * 3600) - (minutes * 60);

        String str_minutes = String.valueOf(minutes);
        String str_seconds = String.valueOf(seconds);
        String str_hours = String.valueOf(hours);

        if (hours   < 10) {str_hours  = "0"+hours;}
        if (minutes < 10) {str_minutes = "0"+minutes;}
        if (seconds < 10) {str_seconds = "0"+seconds;}

        time_formated = str_hours+":"+str_minutes+":"+str_seconds;


        timeTextView.setText("Active time: " + time_formated);
    }
}