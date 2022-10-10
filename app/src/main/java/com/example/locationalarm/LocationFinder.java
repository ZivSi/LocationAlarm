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
    private String latitude;
    private String longitude;
    private String destaddress;
    private String destlatitude;
    private String destlongitude;
    private FusedLocationProviderClient fusedLocationClient;

    public LocationFinder(String destLongitude, String destLatitude, Context context) {
        this.destlatitude = destLatitude;
        this.destlongitude = destLongitude;

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
    public String[] getUserLocation(Context context){
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
}
