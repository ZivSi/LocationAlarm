package com.example.locationalarm;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


/*
Service that asks every couple of seconds to update the current location.
Then it will use the LocationFinder object to set the current location and calculate the location from the destination.
 */

public class AppService extends Service {

    boolean stopSelf = false;

    String destLatitude;
    String destLongitude;
    int distanceAlert;
    LocationFinder locationFinder;
    Intent serviceIntent;

    @Override
    public void onCreate() {
        stopSelf = false;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // Get data from intent
        this.serviceIntent = intent;

        // Get destination location and distance alert from intent
        String[] temp = intent.getStringExtra(MainActivity.COORDINATED_TAG).split(",");
        this.destLatitude = temp[0];
        this.destLongitude = temp[1];
        this.distanceAlert = intent.getIntExtra(MainActivity.DISTANCE_TAG, 100);


        // Add missing permissions
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!permissionGranted) {
            Toast.makeText(this, "Permission not granted\nchange permissions in settings", Toast.LENGTH_LONG).show();
            return;
        }

        // Get destination location and initialize locationFinder
        init();


        // Start loop
        startLocationLoop(this, locationFinder, distanceAlert);
        Toast.makeText(this, "Location active", Toast.LENGTH_SHORT).show();
    }

    private void init() {
        String dest = this.serviceIntent.getStringExtra(MainActivity.COORDINATED_TAG);
        this.destLatitude = String.valueOf(Double.parseDouble(dest.split(",")[0]));
        this.destLongitude = String.valueOf(Double.parseDouble(dest.split(",")[1]));

        // Get distance alert
        this.distanceAlert = this.serviceIntent.getIntExtra(MainActivity.DISTANCE_TAG, 1000);

        this.locationFinder = new LocationFinder(destLongitude + "", destLatitude + "", this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf = true;

        // Stop location updates
        this.locationFinder.stopLocationUpdates();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Start checking location on loop
     */
    private void startLocationLoop(Context context, LocationFinder locationFinder, int distanceAlert) {
        // TODO: make this a thread that runs in the background
        Thread tr = new Thread(() -> {
            Looper.prepare();
            Intent broadcastIntent = new Intent("com.example.locationalarm.distance");
            while (!stopSelf) {
                locationFinder.getLocation();
                broadcastIntent.putExtra("distance", locationFinder.getDistanceFromUserToDestination());
                sendBroadcast(broadcastIntent);

                if (distanceAlert >= locationFinder.getDistanceFromUserToDestination()) {
                    // Stop service
                    stopSelf = true;


                    // TODO: Show notification and start alarm
                    Toast.makeText(context, "You are near your destination", Toast.LENGTH_LONG).show();
                    locationFinder.stopLocationUpdates();
                    // TODO: create an alarm activity and Start alarm
                }


                // Print current distance from user to destination
                Log.d("Location Update", "Distance from destination: " + locationFinder.getDistanceFromUserToDestination());
                // Print destination location
                Log.d("Location Update", "Destination location: " + destLatitude + ", " + destLongitude);

                // Sleep for 5 seconds
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tr.setDaemon(true);
        tr.start();
    }
}
