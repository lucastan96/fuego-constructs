package com.fuego.mobile_ca1.Classes;

import com.google.firebase.firestore.GeoPoint;

public class User {
    private GeoPoint latlng;
    private boolean status;

    public User() {
    }

    public User(GeoPoint latlng, boolean status) {
        this.latlng = latlng;
        this.status = status;
    }

    public GeoPoint getLatlng() {
        return latlng;
    }

    public void setLatlng(GeoPoint latlng) {
        this.latlng = latlng;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "latlng=" + latlng +
                ", status=" + status +
                '}';
    }
}
