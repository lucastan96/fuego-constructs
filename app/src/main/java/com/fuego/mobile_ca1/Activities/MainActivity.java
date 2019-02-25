package com.fuego.mobile_ca1.Activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fuego.mobile_ca1.Classes.Event;
import com.fuego.mobile_ca1.Classes.GeofenceTransitionsIntentService;
import com.fuego.mobile_ca1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int GEOFENCE_RADIUS_IN_METERS = 200;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private Boolean mLocationPermissionGranted = false;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    private GeofencingClient mGeofencingClient;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FirebaseAuth auth;
    private FloatingActionButton fab;
    private FirebaseFirestore db;
    private GeoPoint geoPoint;
    private TextView siteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            siteName = findViewById(R.id.siteName);


            mDrawerLayout = findViewById(R.id.drawer_layout);
            mNavigationView = findViewById(R.id.drawer_menu);
            mNavigationView.setNavigationItemSelectedListener(this);

            View headerView = mNavigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.login_title);
            navUsername.setText("Logged in as " + Objects.requireNonNull(auth.getCurrentUser()).getEmail());

            fab = findViewById(R.id.floatingActionButton);
            fab.setOnClickListener(v -> {
                if (auth.getUid() != null) {
                    DocumentReference reference = db.collection("users").document(auth.getUid());
                    reference.get().addOnSuccessListener(snapshot -> {
                        boolean status = snapshot.getBoolean("status");
                        addEvent(!status);
                        reference.update("status", !status);
                    });
                }
            });

            if (auth.getCurrentUser() != null) {
                navUsername.setText("Logged in as " + auth.getCurrentUser().getEmail());
            } else {
                navUsername.setText("Logged in as unknown");
            }

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            setSiteName();
            addListener();
            initGeofence();
        }
    }

    public void addEvent(boolean type) {
        DocumentReference ref = db.collection("users").document(Objects.requireNonNull(auth.getUid()));
        ref.get().addOnSuccessListener(documentSnapshot -> {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnSuccessListener(location -> {
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Event event = new Event(auth.getUid(), new Timestamp(new Date()), geoPoint, type);
                db.collection("events").add(event);
                if (type) {
                    Toast.makeText(this, "Checked in", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Checked out", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void setSiteName() {
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

        locationResult.addOnSuccessListener(location -> {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String name = addresses.get(0).getAddressLine(0);
                siteName.setText("Site: " + name);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addListener() {
        final DocumentReference ref = db.collection("users").document(auth.getUid());
        ref.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            setSiteName();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map));
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mLocationPermissionGranted = true;
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                }
            }
        }
    }

    private void getCurrentLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            addGeofence();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(53.305494, -7.737649), 6));
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void initGeofence() {
        mGeofencePendingIntent = getGeofencePendingIntent();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }

    private void addGeofence() {
        DocumentReference ref = db.collection("users").document(Objects.requireNonNull(auth.getUid()));
        ref.get().addOnSuccessListener(snapshot -> {
            geoPoint = snapshot.getGeoPoint("latlng");
            if (geoPoint != null) {
                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId("geofence")
                        .setCircularRegion(
                                geoPoint.getLatitude(),
                                geoPoint.getLongitude(),
                                GEOFENCE_RADIUS_IN_METERS
                        )
                        .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
                mGeofencingClient.addGeofences(getGeofencingRequest(), mGeofencePendingIntent)
                        .addOnCompleteListener(taskGeofence -> drawCircle());
            }
        });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void drawCircle() {
        if (mMap == null) {
            return;
        } else {
            mMap.clear();
        }
        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        CircleOptions mCircleOptions = new CircleOptions()
                .center(latLng).radius(GEOFENCE_RADIUS_IN_METERS).fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT).strokeWidth(0);
        mMap.addCircle(mCircleOptions);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
