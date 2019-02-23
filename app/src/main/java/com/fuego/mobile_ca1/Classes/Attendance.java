package com.fuego.mobile_ca1.Classes;

import com.fuego.mobile_ca1.enums.InOrOut;
import com.google.android.gms.maps.model.LatLng;

public class Attendance {
    private String date;
    private String time;
    private LatLng latLng;
    private InOrOut inOrOut;

    public Attendance() {
    }

    public Attendance(String date, String time, LatLng latLng, InOrOut inOrOut) {
        this.date = date;
        this.time = time;
        this.latLng = latLng;
        this.inOrOut = inOrOut;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public InOrOut getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(InOrOut inOrOut) {
        this.inOrOut = inOrOut;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", latLng=" + latLng +
                ", inOrOut=" + inOrOut +
                '}';
    }
}
