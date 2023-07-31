package com.example.locationalarm;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Get data from intent
        this.serviceIntent = intent;

        // Get destination location and distance alert from intent
        String[] temp = intent.getStringExtra(MainActivity.COORDINATED_TAG).split(",");
        this.destLatitude = temp[0];
        this.destLongitude = temp[1];
        this.distanceAlert = intent.getIntExtra(MainActivity.DISTANCE_TAG, 100);


        // Add missing permissions
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!permissionGranted || intent == null) {
            return START_NOT_STICKY;
        }

        switch (serviceIntent.getAction()) {
            case START_TRACKING:
                String[] coordinates = getDestination(intent);
                int distnace = getDistanceAlert(intent);
                startLocationLoop(this, new LocationFinder(coordinates[0], coordinates[1], this), distnace);
                break;


        return START_STICKY;
    }

    private int getDistanceAlert(Intent intent) {
        return intent.getIntExtra(DISTANCE_TAG, 100);
    }

    private String[] getDestination(Intent intent) {
        String val = intent.getStringExtra(COORDINATED_TAG);

        if (val == ",") {
            showToast("Destination is null!");

            return new String[]{"0", "0"};
        }

        return val.split(",");
        // Start loop
        startLocationLoop(this, locationFinder, distanceAlert);
        Toast.makeText(this, "Location active", Toast.LENGTH_SHORT).show();
    }

    private void init() {
        String dest = this.serviceIntent.getStringExtra(COORDINATED_TAG);
        this.destLatitude = String.valueOf(Double.parseDouble(dest.split(",")[0]));
        this.destLongitude = String.valueOf(Double.parseDouble(dest.split(",")[1]));

        // Get distance alert
        this.distanceAlert = this.serviceIntent.getIntExtra(DISTANCE_TAG, 1000);

        this.locationFinder = new LocationFinder(destLongitude + "", destLatitude + "", this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf = true;
        stopForeground(true);

        // Stop location updates
        this.locationFinder.stopLocationUpdates();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
    private void startLocationLoop(Context context, LocationFinder locationFinder, int distanceAlert) {
        // TODO: make this a thread that runs in the background
        Thread tr = new Thread(() -> {
            Looper.prepare();
            Intent distanceBroadcastIntent = new Intent("com.example.locationalarm.distance");
            Intent arrivalBroadcastIntent = new Intent("com.example.locationalarm.hasArrived");
            while (!stopSelf) {
                locationFinder.getLocation();
                distanceBroadcastIntent.putExtra("distance", locationFinder.getDistanceFromUserToDestination());
                sendBroadcast(distanceBroadcastIntent);
                String message;

                if (distanceAlert >= locationFinder.getDistanceFromUserToDestination()) {
                    // Stop service
                    stopSelf = true;
                    locationFinder.stopLocationUpdates();

                    arrivalBroadcastIntent.putExtra("hasArrived", true);
                    sendBroadcast(arrivalBroadcastIntent);
                    message = "You are close to your destination\n. You are Xm from your destination";
                } else {
                    message = "You are not close to your destination. Relax";
                }

                if (notificationActive) {
                    updateNotification(message);
                } else {
                    showNotification(message);
                }

                Log.d("Location Update", "Distance from destination: " + locationFinder.getDistanceFromUserToDestination());
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
                sleep();
            }
        }).start();
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setContentTitle("Location Service").setContentText(message).setSmallIcon(R.drawable.ic_baseline_location_on_24).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(false);

        Notification notification = builder.build();

        notificationManager.notify(NOTIFICATION_ID, notification);

        notificationActive = true;
    }

    private void updateNotification(String newMessage) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setContentTitle("Location Service").setContentText(newMessage).setSmallIcon(R.drawable.ic_baseline_location_on_24).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(false);

        Notification notification = builder.build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
