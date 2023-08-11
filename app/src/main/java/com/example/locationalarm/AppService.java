package com.example.locationalarm;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.Objects;


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
    Context context;

    boolean notificationActive = false;

    NotificationManager notificationManager;

    private static final String NOTIFICATION_CHANNEL_ID = "location_service_channel";
    private static final int NOTIFICATION_ID = 9999;
    public static final String START_TRACKING = "START_TRACKING";
    static String COORDINATED_TAG = "COORDINATED_TAG";
    static String DISTANCE_TAG = "DISTANCE_TAG";

    private void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Location Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildForegroundNotification() {
        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setContentTitle("Location Service").setContentText("Running in the background").setSmallIcon(R.drawable.ic_baseline_location_on_24).build();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        stopSelf = false;
        createNotificationChannel();

        this.context = this;

        System.out.println("onCreate service called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        // TODO: fix this if statement of setAction
        if (!permissionGranted || intent.getAction() == null) { // for some reason intent.getAction() is null
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            return START_NOT_STICKY;
        }

        switch (intent.getAction()) {
            case START_TRACKING:
                String[] coordinates = getDestination(intent);
                int distnace = getDistanceAlert(intent);

                startLocationLoop(this, new LocationFinder(coordinates[1], coordinates[0], this), distnace);
                break;

            default:
                Toast.makeText(this, "Action not recognized", Toast.LENGTH_LONG).show();

        }
        return START_STICKY;
    }

    private int getDistanceAlert(Intent intent) {
        return intent.getIntExtra(DISTANCE_TAG, 100);
    }

    private String[] getDestination(Intent intent) {
        String val = intent.getStringExtra(COORDINATED_TAG);

        if (Objects.equals(val, ",")) {
            showToast("Destination is null!");

            return new String[]{"0", "0"};
        }

        return val.split(",");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationFinder.stopLocationUpdates();
        stopSelf = true;
        stopForeground(true);
    }


    private void showToast(final String message) {
        Handler toastHandler = new Handler(Looper.getMainLooper());
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Start checking location on loop
     */
    private void startLocationLoop(Context context, LocationFinder locFinder, int distanceAlert) {

        locationFinder = locFinder;
        Thread tr = new Thread(() -> {
            boolean hasArived = false;
            Looper.prepare();
            Intent distanceBroadcastIntent = new Intent("com.example.locationalarm.distance");
            Intent arrivalBroadcastIntent = new Intent("com.example.locationalarm.hasArrived");

            locationFinder.getLocation(); // saves new location in the LocationFinder object
            int distance = locationFinder.getDistanceFromUserToDestination();

            while (!stopSelf) {
                locationFinder.getLocation(); // saves new location in the LocationFinder object
                distance = locationFinder.getDistanceFromUserToDestination();
                distanceBroadcastIntent.putExtra("distance", distance);
                sendBroadcast(distanceBroadcastIntent);
                String message;

                if (distanceAlert >= distance) {
                    // Stop service
                    stopSelf = true;
                    hasArived = true;
                    locationFinder.stopLocationUpdates();

                    arrivalBroadcastIntent.putExtra("hasArrived", true);
                    sendBroadcast(arrivalBroadcastIntent);
                    message = "Distance: " + distance + " meters\nYou are close to your destination.";
                } else {
                    message = "Distance: " + distance + " meters";
                }

                if (notificationActive) {
                    updateNotification(message);
                } else {
                    showNotification(message);
                }

                Log.d("Location Update", "Distance from destination: " + distance + " meters");

                // Sleep for 5 seconds
                try {
                    if (1000 >= distance)
                        Thread.sleep(1500);
                    else
                        Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // start alarm
            if (hasArived){
                Toast.makeText(getApplicationContext(), "You have arrived", Toast.LENGTH_LONG).show();
                Context cont = getApplicationContext();
                Functions.startAlarm(cont);
            }

        });

        tr.setDaemon(true);
        tr.start();
        sleep();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void showNotification(String message) {

        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Location Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        updateNotification(message);

        notificationActive = true;
    }

    private void updateNotification(String newMessage) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText(newMessage)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setAutoCancel(false);

        Notification notification = builder.build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
