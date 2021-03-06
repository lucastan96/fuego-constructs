package com.fuego.mobile_ca1;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.fuego.mobile_ca1.Activities.MainActivity;
import com.fuego.mobile_ca1.Classes.Event;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.fuego.mobile_ca1.App.CHANNEL_1_ID;

public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Event event;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        assert geofencingEvent != null;
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onHandleIntent: Error code " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Location location = geofencingEvent.getTriggeringLocation();
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);
            Log.d(TAG, "onHandleIntent: " + geofenceTransitionDetails);

            mSharedPreferences = getApplicationContext().getSharedPreferences("geofence", Context.MODE_PRIVATE);


            boolean geofenceStatus = mSharedPreferences.getBoolean("inside", false);
            String notificationTitle = "", notificationText = "";
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER && !geofenceStatus) {
                notificationTitle = "Just entered the construction site";
                notificationText = "Have fun at work!";

                mEditor = mSharedPreferences.edit();
                mEditor.putBoolean("inside", true);
                mEditor.apply();

                event = new Event(auth.getUid(), new Timestamp(new Date()), geoPoint, true);
                db.collection("geofence").add(event);

                sendNotification(notificationTitle, notificationText);

            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                notificationTitle = "Just left the construction site";
                notificationText = "See you tomorrow!";

                mEditor = mSharedPreferences.edit();
                mEditor.putBoolean("inside", false);
                mEditor.apply();

                event = new Event(auth.getUid(), new Timestamp(new Date()), geoPoint, false);
                db.collection("geofence").add(event);

                sendNotification(notificationTitle, notificationText);
            }

        } else {
            Log.d(TAG, "onHandleIntent: Invalid transition type");
        }
    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        String geofenceTransitionString = getTransitionString(geofenceTransition);
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered the site";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Leaved the site";
            default:
                return "Unknown geofence transition";
        }
    }

    private void sendNotification(String notificationTitle, String notificationText) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_constructs_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
    }
}
