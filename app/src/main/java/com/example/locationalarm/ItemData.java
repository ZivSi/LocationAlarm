package com.example.locationalarm;

public class ItemData {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String alarmDistance; // in meters

    public ItemData(String name, String address, String latitude, String longitude, String alarmDistance) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.alarmDistance = alarmDistance;
    }


    public String getName() {
        return this.name;
    }

    public String getAlarmDistance() {
        return this.alarmDistance;
    }

    public String getAddress() {
        return this.address;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setAlarmDistance(String alarmDistance) {
        this.alarmDistance = alarmDistance;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + MainActivity.SPLITTER +
                address + MainActivity.SPLITTER +
                latitude + MainActivity.SPLITTER +
                longitude + MainActivity.SPLITTER +
                alarmDistance;
    }
}
