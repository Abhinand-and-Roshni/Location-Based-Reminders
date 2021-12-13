package com.example.reminderapp;

public class reminderDetails {

    private String reminderName;
    private String reminderLocation;
    private String latitude;
    private String longitude;

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public String getReminderLocation() {
        return reminderLocation;
    }

    public void setReminderLocation(String reminderLocation) {
        this.reminderLocation = reminderLocation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public reminderDetails(String reminderName, String reminderLocation, String latitude, String longitude)
    {
        this.reminderLocation=reminderLocation;
        this.reminderName=reminderName;
        this.latitude=latitude;
        this.longitude=longitude;
    }

}
