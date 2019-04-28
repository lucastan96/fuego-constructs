package com.fuego.mobile_ca1.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.aniket.mutativefloatingactionbutton.MutativeFab;
import com.fuego.mobile_ca1.Classes.Event;
import com.fuego.mobile_ca1.GeofenceTransitionsIntentService;
import com.fuego.mobile_ca1.MessengerService;
import com.fuego.mobile_ca1.R;
import com.fuego.mobile_ca1.TimeService;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import com.fuego.mobile_ca1.TimeService.MyLocalBinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int GEOFENCE_RADIUS_IN_METERS = 200;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE;
    private boolean wifiState;
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
    private FloatingActionButton btnMyLocation;
    private MutativeFab btnCheckin;
    private FirebaseFirestore db;
    private GeoPoint geoPoint;
    private ConstraintLayout siteNameLayout;
    private TextView siteName;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    TimeService myService;
    boolean isBound = false;

    private ServiceConnection localConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MyLocalBinder binder = (MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiState = true;
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    wifiState = false;
                    break;
            }
        }
    };

    private Messenger mService = null;
    private boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    @SuppressLint("SetTextI18n")
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
            Intent intent = new Intent(this, TimeService.class);
            bindService(intent, localConnection, Context.BIND_AUTO_CREATE);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setElevation(8);
            siteNameLayout = findViewById(R.id.site_name_layout);
            siteNameLayout.setElevation(8);
            siteName = findViewById(R.id.site_name);
            siteName.setText("Loading...");

            mDrawerLayout = findViewById(R.id.drawer_layout);
            mNavigationView = findViewById(R.id.drawer_menu);
            mNavigationView.setNavigationItemSelectedListener(this);

            View headerView = mNavigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.login_title);
            navUsername.setText("Logged in as " + Objects.requireNonNull(auth.getCurrentUser()).getEmail());

            mSharedPreferences = getApplicationContext().getSharedPreferences("geofence", Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("inside", false);
            mEditor.apply();

            btnCheckin = findViewById(R.id.btn_checkin);
            btnCheckin.setOnClickListener(v -> {
                if (wifiState) {
                    if (auth.getUid() != null) {
                        DocumentReference ref = db.collection("users").document(auth.getUid());
                        ref.get().addOnSuccessListener(snapshot -> {
                            boolean status = snapshot.getBoolean("status");
                            boolean geofenceStatus = mSharedPreferences.getBoolean("inside", true);
                            if (!status) {
                                if (geofenceStatus) {
                                    addEvent(!status);
                                    ref.update("status", !status);
                                    btnCheckin.setFabText("Check Out");
                                } else {
                                    Toast.makeText(this, "Please enter site to check in", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                addEvent(!status);
                                ref.update("status", !status);
                                btnCheckin.setFabText("Check In");
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Enable Wifi to check in", Toast.LENGTH_SHORT).show();
                }

            });

            btnMyLocation = findViewById(R.id.btn_mylocation);
            btnMyLocation.setOnClickListener(v -> checkPermission());

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
            setCheckInText();
            addListeners();
            initGeofence();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
        bindService(new Intent(this, MessengerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            this.unregisterReceiver(wifiStateReceiver);
        } catch (Exception e) {
            Log.d(TAG, "onDestroy: " + e);
        }
        super.onDestroy();
    }

    private void sayHello() {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, MessengerService.MSG_SAY_HELLO, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void addEvent(boolean type) {
        DocumentReference ref = db.collection("users").document(Objects.requireNonNull(auth.getUid()));
        ref.get().addOnSuccessListener(documentSnapshot -> {
            @SuppressLint("MissingPermission") Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnSuccessListener(location -> {
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Event event = new Event(auth.getUid(), myService.getCurrentTime(), geoPoint, type);
                db.collection("events").add(event);
                if (type) {
                    Toast.makeText(this, "You are now checked in", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You are now checked out", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setSiteName() {
        DocumentReference ref = db.collection("users").document(Objects.requireNonNull(auth.getUid()));
        ref.get().addOnSuccessListener(snapshot -> {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                GeoPoint g = snapshot.getGeoPoint("latlng");
                if (g != null) {
                    addresses = geocoder.getFromLocation(g.getLatitude(), g.getLongitude(), 1);
                }
                String name = null;
                if (addresses != null) {
                    name = addresses.get(0).getAddressLine(0);
                }
                siteName.setText(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setCheckInText() {
        final DocumentReference ref = db.collection("users").document(Objects.requireNonNull(auth.getUid()));
        ref.get().addOnSuccessListener(snapshot -> {
            Boolean currentStatus = snapshot.getBoolean("status");
            if (currentStatus != null) {
                if (currentStatus) {
                    btnCheckin.setFabText("Check Out");
                } else {
                    btnCheckin.setFabText("Check In");
                }
            }
        });
    }

    private void addListeners() {
        final DocumentReference ref = db.collection("users").document(Objects.requireNonNull(auth.getUid()));
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
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
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

    @SuppressLint("MissingPermission")
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

        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.position(latLng);
        mMap.addMarker(mMarkerOptions);

        CircleOptions mCircleOptions = new CircleOptions()
                .center(latLng).radius(GEOFENCE_RADIUS_IN_METERS).fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT).strokeWidth(0);
        mMap.addCircle(mCircleOptions);
    }

    private void removeGeofence() {
        if (mMap != null) {
            mMap.clear();
        }
        if (mGeofencingClient != null) {
            mGeofencingClient.removeGeofences(mGeofencePendingIntent);
        }
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
                removeGeofence();
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
