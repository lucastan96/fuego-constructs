package com.fuego.mobile_ca1.Classes;

import com.fuego.mobile_ca1.enums.InOrOut;

public class GeofenceTracker {

    private String time;
    private InOrOut inOrOut;

    public GeofenceTracker(String time, InOrOut inOrOut) {
        this.time = time;
        this.inOrOut = inOrOut;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public InOrOut getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(InOrOut inOrOut) {
        this.inOrOut = inOrOut;
    }

    @Override
    public String toString() {
        return "GeofenceTracker{" +
                "time='" + time + '\'' +
                ", inOrOut=" + inOrOut +
                '}';
    }
}
