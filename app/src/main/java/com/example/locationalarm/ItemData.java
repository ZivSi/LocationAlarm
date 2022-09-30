package com.example.locationalarm;

public class ItemData {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private int alarmDistance; // in meters
    private String ringtone;

    public ItemData(String name, String address, String latitude, String longitude, int alarmDistance, String ringtone) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.alarmDistance = alarmDistance;
        this.ringtone = ringtone;
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

    public String getRingtone() {
        return this.ringtone;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setAlarmDistance(int alarmDistance) {
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

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

}
