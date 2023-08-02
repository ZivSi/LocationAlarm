package com.example.locationalarm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFinder extends ActivityCompat {
    private int UPDATE_INTERVAL = 1000 * 5; // 2 seconds

    // Of user
    private String latitude = "0";
    private String longitude = "0";

    // Destination location
    private String destLatitude;
    private String destLongitude;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Geocoder geocoder;

    Context context;

    @SuppressLint("MissingPermission")
    public LocationFinder(String destLongitude, String destLatitude, Context context) {
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
        this.context = context;

        setLocationRequest();

        setLocationCallback();

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Ask for location permission
     */
    public static void getLocationPermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    /**
     * Calculate distance between two points
     *
     * @return Distance in Meters
     */
    public double calcDistance(double user_lat, double dest_lat, double user_long, double dest_long) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(dest_lat - user_lat); // turn degrees to radians
        double lonDistance = Math.toRadians(dest_long - user_long);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) // take in the earth curvature to the equation
                + Math.cos(Math.toRadians(user_lat)) * Math.cos(Math.toRadians(dest_lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (R * c * 1000); // Convert to meters
    }

    /**
     * Get the absolute distance between the user and the destination using `calcDistance`
     *
     * @return Distance in Meters
     */
    public int getDistanceFromUserToDestination() {

        double lat1 = Double.parseDouble(latitude);
        double long1 = Double.parseDouble(longitude);
        Log.d("location", "get destination");
        // calculate the distance between the user and the destination
        double distance = calcDistance(lat1, Double.parseDouble(destLatitude), long1, Double.parseDouble(destLongitude));
        return Math.abs((int) distance); // absolute value of the distance
    }

    /**
     * Function that gets the user's location
     */
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            // Initialize location
            Location location = task.getResult();

            if (location != null) {
                // Success
                try {
                    geocoder = new Geocoder(context, Locale.getDefault());

                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses.size() == 0) {
                        Toast.makeText(context, "No address found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    latitude = String.valueOf(addresses.get(0).getLatitude());
                    longitude = String.valueOf(addresses.get(0).getLongitude());

                    printCurrentAccuracy(location.getAccuracy());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Print the current accuracy of the location
     */
    private void printCurrentAccuracy(float accuracy) {
        if (accuracy <= 2) {
            Log.d("Location Update", "Accuracy: Very High");
        } else if (accuracy <= 8) {
            Log.d("Location Update", "Accuracy: High");

        } else if (accuracy <= 20) {
            Log.d("Location Update", "Accuracy: Medium");

        } else if (accuracy <= 30) {
            Log.d("Location Update", "Accuracy: Low");

        } else if (accuracy <= 60) {
            Log.d("Location Update", "Accuracy: Very Low");
        }
    }


    /**
     * Function that sets the location request. The priority is set to high accuracy.
     */
    private void setLocationRequest() {
        this.locationRequest = LocationRequest.create();
        this.locationRequest.setInterval(UPDATE_INTERVAL); // Update every 2 seconds
        this.locationRequest.setFastestInterval(UPDATE_INTERVAL); // The fastest update
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Set as high priority
    }

    /*
     * Init to the location callback object
     */
    public void setLocationCallback() {
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());


                    Log.i("Location Update", "Location: " + location.getLatitude() + " | " + location.getLongitude() + "\n");
                }
            }
        };
    }

    /**
     * Set time to update location
     */
    public void setUPDATE_INTERVAL(int UPDATE_INTERVAL) {
        this.UPDATE_INTERVAL = UPDATE_INTERVAL;

        // Update the location request
        setLocationRequest();
    }

    public void stopLocationUpdates() {
        this.fusedLocationClient.removeLocationUpdates(locationCallback);

        Log.i("Location Update", "Location updates stopped");
    }
}
