package com.example.easyteamup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private String username;
    private GoogleMap mMap;
    private LatLngBounds currentCameraBounds;
    private Button createEventBtn, profileBtn;
    private HashMap<Integer, LatLng> locations;
    private HashMap<Integer, String> eventNames;
    private DBHandler dbHandler;
    LocationManager locationManager;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        locations = new HashMap<Integer, LatLng>();
        eventNames = new HashMap<Integer, String>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        int hasLocPermission = ContextCompat.checkSelfPermission(this, new String(Manifest.permission.ACCESS_FINE_LOCATION));
        if (hasLocPermission == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        createEventBtn = findViewById(R.id.idBtnMapToCreateEvent);
        profileBtn = findViewById(R.id.idBtnMapToProfile);

        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, CreateEventActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        dbHandler = new DBHandler(MapsActivity.this);
    }

    public void idleAction() {


        // Get bounds and check if they're truly different from previously
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        Log.i("", "MAP -- projection " + bounds.northeast + " " + bounds.southwest);

        if (currentCameraBounds != null
                && currentCameraBounds.northeast.latitude == bounds.northeast.latitude
                && currentCameraBounds.northeast.longitude == bounds.northeast.longitude
                && currentCameraBounds.southwest.latitude == bounds.southwest.latitude
                && currentCameraBounds.southwest.longitude == bounds.southwest.longitude) {
            return;
        }

        currentCameraBounds = bounds;

        // Get events in bounds
        // Several dummy events added to prove ability to show on map (without manually creating events while blackbox testing)

        ArrayList<Event> events = dbHandler.getEventsInArea(username, bounds.northeast.latitude, bounds.northeast.longitude,
                bounds.southwest.latitude, bounds.southwest.longitude, System.currentTimeMillis());

        for (Event event : events) {
            locations.put(event.getId(), new LatLng(event.getLatitude(), event.getLongitude()));
            eventNames.put(event.getId(), event.getName());
        }

        // Add events as markers to map
        Iterator locIt = locations.entrySet().iterator();
        while (locIt.hasNext()) {
            Map.Entry mapElement = (Map.Entry) locIt.next();
            LatLng loc = ((LatLng) mapElement.getValue());
            String eventName = ((String) eventNames.get(mapElement.getKey()));
            Log.i("", "MAP -- " + loc + " " + eventName + " " + mapElement.getKey());
            mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(eventName))
                    .setTag(mapElement.getKey());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get initial bounds
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        currentCameraBounds = bounds;
        Log.i("", "MAP -- projection " + bounds.northeast + " " + bounds.southwest);

        // Direct to event page if event is clicked
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String eventName = marker.getTitle();
                Integer eventId = (Integer) marker.getTag();
                Log.i("", "MAP CLICKED -- " + " " + eventName + " " + eventId + " " + username);

                Intent intent = new Intent(MapsActivity.this, EventPageActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventName", eventName);
                startActivity(intent);
                return false;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                idleAction();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            // Location permission is granted. Continue the action or workflow
            // in your app.
        }
        return;

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        idleAction();

        if (locationManager != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(this);
            }
        }

    }
}