package com.fuego.mobile_ca1.Classes;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Event {
    private Timestamp timestamp;
    private GeoPoint latLng;
    private String type;

    public Event(Timestamp timestamp, GeoPoint latLng, String type) {
        this.timestamp = timestamp;
        this.latLng = latLng;
        this.type = type;
    }
}
