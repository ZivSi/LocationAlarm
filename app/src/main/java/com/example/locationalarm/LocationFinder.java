package com.example.locationalarm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationFinder {
    // init variables
    public String latitude;
    public String longitude;

    private String destlatitude;
    private String destlongitude;

    private FusedLocationProviderClient fusedLocationClient;
    Context context;

    public LocationFinder(String destLongitude, String destLatitude, Context context) {
        this.destlatitude = destLatitude;
        this.destlongitude = destLongitude;
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    // get the permission to access the location of the user
    public static boolean getLocationPermission(Context context) {
        if(ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if permission is granted
            return true;
        }
        else{
            // if permission is denied
            ActivityCompat.requestPermissions((MainActivity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return false;
        }
    }

    // get the current location of the user
    @SuppressLint("MissingPermission")
    private String[] getUserLocation(){
        if(getLocationPermission(context)) {
            // check if permission is granted
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    // extract location and convert it to longitude and latitude
                    Location location = task.getResult();
                    if(location != null){
                        // get locations
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    }
                }
            });
            return new String[]{latitude, longitude};
        }
        // if permission is not granted
        return new String[]{"", ""};
    }


    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }


    /**
     * Calculate distance between two points in latitude and longitude . If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point
     * @return Distance in Meters
     */
    private double calcDistance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1); // turn degrees into radians
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) // take in the earth curvature to the equation
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (R * c * 1000); // convert to meters
    }


    // get the absolute distance between the user and the destination
    public int getDistanceFromUserToDestination(){
        if (getLocationPermission((context))){
            // get the current location of the user
            String[] userLocation = getUserLocation();
            // fixme: the user location is not working and is always null
            double long1 = Double.parseDouble(userLocation[0]);
            double lat1 = Double.parseDouble(userLocation[1]);
            // calculate the distance between the user and the destination
            double distance = calcDistance(lat1, Double.parseDouble(destlatitude), long1, Double.parseDouble(destlongitude));
            return Math.abs((int) distance); // absolute value of the distance
        }
        return 0;
    }
}
