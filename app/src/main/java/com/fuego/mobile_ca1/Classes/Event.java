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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public GeoPoint getLatLng() {
        return latLng;
    }

    public void setLatLng(GeoPoint latLng) {
        this.latLng = latLng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp.toString() +
                ", latLng=" + latLng.toString() +
                ", type='" + type + '\'' +
                '}';
    }
}
