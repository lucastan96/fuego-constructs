package com.fuego.mobile_ca1.Classes;

import com.google.firebase.Timestamp;

public class GeofenceTracker {

    private Timestamp time;
    private String event;

    public GeofenceTracker(Timestamp time, String event) {
        this.time = time;
        this.event = event;
    }
}
