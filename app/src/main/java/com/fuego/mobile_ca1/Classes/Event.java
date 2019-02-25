package com.fuego.mobile_ca1.Classes;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Event {
    private String uid;
    private Timestamp timestamp;
    private GeoPoint latLng;
    private Boolean type;

    public Event(String uid, Timestamp timestamp, GeoPoint latLng, Boolean type) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.latLng = latLng;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Event{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                ", latLng=" + latLng +
                ", type=" + type +
                '}';
    }
}
