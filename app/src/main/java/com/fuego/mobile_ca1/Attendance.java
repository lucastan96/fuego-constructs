package com.fuego.mobile_ca1;

import com.fuego.mobile_ca1.enums.InOrOut;

import java.util.Objects;

public class Attendance {
    private String date;
    private String time;
    private String latitude;
    private String longitude;
    private InOrOut inOrOut;

    public Attendance() {
    }

    public Attendance(String date, String time, String latitude, String longitude, InOrOut inOrOut) {
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public InOrOut getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(InOrOut inOrOut) {
        this.inOrOut = inOrOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendance)) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(getDate(), that.getDate()) &&
                Objects.equals(getTime(), that.getTime()) &&
                Objects.equals(getLatitude(), that.getLatitude()) &&
                Objects.equals(getLongitude(), that.getLongitude()) &&
                getInOrOut() == that.getInOrOut();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getTime(), getLatitude(), getLongitude(), getInOrOut());
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", inOrOut=" + inOrOut +
                '}';
    }

}
