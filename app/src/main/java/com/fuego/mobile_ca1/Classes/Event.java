package com.fuego.mobile_ca1.Classes;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Event {
    private String uid;
    private Timestamp timestamp;
    private GeoPoint latLng;
    private String type;
    private String direction;

    public Event(String uid, Timestamp timestamp, GeoPoint latLng, String type, String direction) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.latLng = latLng;
        this.type = type;
        this.direction = direction;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Event{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                ", latLng=" + latLng +
                ", type='" + type + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
