package com.fuego.mobile_ca1.Classes;

import java.util.List;

public class User {
    private String uid;
    private String startTime;
    private String endTime;
    private List<Event> events;
    private List<GeofenceTracker> geofenceTracker;

    public User() {
    }

    public User(String uid) {
        this.uid = uid;
    }

    public User(String uid, String startTime, String endTime) {
        this.uid = uid;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<GeofenceTracker> getGeofenceTracker() {
        return geofenceTracker;
    }

    public void setGeofenceTracker(List<GeofenceTracker> geofenceTracker) {
        this.geofenceTracker = geofenceTracker;
    }
}