package com.fuego.mobile_ca1.Classes;

import com.google.firebase.firestore.GeoPoint;

public class User {
    private GeoPoint latlng;
    private String site;
    private boolean status;

    public User(GeoPoint latlng, String site, boolean status) {
        this.latlng = latlng;
        this.site = site;
        this.status = status;
    }

    public GeoPoint getLatlng() {
        return latlng;
    }

    public void setLatlng(GeoPoint latlng) {
        this.latlng = latlng;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
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
                ", site='" + site + '\'' +
                ", status=" + status +
                '}';
    }
}
