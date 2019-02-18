package com.fuego.mobile_ca1.Classes;

import com.fuego.mobile_ca1.enums.UserType;

import java.util.Objects;

public class User {
    private String uid;
    private String name;
    private int assignedSiteID;
    private String startTime;
    private String endTime;
    private UserType typeOfUser;
    private Attendance attendance;
    private GeofenceTracker geofenceTracker;

    public User() {
    }

    public User(String uid, String name, int assignedSiteID, String startTime, String endTime, UserType typeOfUser, Attendance attendance, GeofenceTracker geofenceTracker) {
        this.uid = uid;
        this.name = name;
        this.assignedSiteID = assignedSiteID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.typeOfUser = typeOfUser;
        this.attendance = attendance;
        this.geofenceTracker = geofenceTracker;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAssignedSite() {
        return assignedSiteID;
    }

    public void setAssignedSite(int assignedSiteID) {
        this.assignedSiteID = assignedSiteID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public UserType getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(UserType typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public GeofenceTracker getGeofenceTracker() {
        return geofenceTracker;
    }

    public void setGeofenceTracker(GeofenceTracker geofenceTracker) {
        this.geofenceTracker = geofenceTracker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getUid(), user.getUid()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getAssignedSite(), user.getAssignedSite()) &&
                Objects.equals(getStartTime(), user.getStartTime()) &&
                Objects.equals(getEndTime(), user.getEndTime()) &&
                getTypeOfUser() == user.getTypeOfUser() &&
                Objects.equals(getAttendance(), user.getAttendance()) &&
                Objects.equals(getGeofenceTracker(), user.getGeofenceTracker());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), getName(), getAssignedSite(), getStartTime(), getEndTime(), getTypeOfUser(), getAttendance(), getGeofenceTracker());
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", assignedSite='" + assignedSiteID + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", typeOfUser=" + typeOfUser +
                ", attendance=" + attendance +
                ", geofenceTracker=" + geofenceTracker +
                '}';
    }

}





