package com.fuego.mobile_ca1.Classes;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import androidx.annotation.Nullable;

public class GeofenceTransitionsIntentService extends IntentService {
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geoFenceEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // do something
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // do something else
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            // do something else again
        }
    }
}
