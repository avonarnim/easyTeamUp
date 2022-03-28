package com.example.easyteamup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private String username;
    private GoogleMap mMap;
    private LatLngBounds currentCameraBounds;
    private Button createEventBtn, profileBtn;
    private HashMap<Integer, LatLng> locations;
    private HashMap<Integer, String> eventNames;
    private DBHandler dbHandler;

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
                Log.i("", "MAP IDLE");

                // Get bounds and check if they're truly different from previously
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

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

                ArrayList<Event> events = dbHandler.getEventsInArea(bounds.northeast.latitude, bounds.northeast.longitude,
                        bounds.southwest.latitude, bounds.southwest.longitude, System.currentTimeMillis());
                Log.i("", "MAP -- " + events);

                events.add(new Event(1, "good event", "adam", 40.0, 40.0, Long.valueOf(1748337504), Long.valueOf(1848337504)));
                events.add(new Event(2, "ok event", "adam", 45.0, 40.0, Long.valueOf(1748337504), Long.valueOf(1848337504)));
                events.add(new Event(3, "bad event", "adam", 35.0, 40.0, Long.valueOf(1748337504), Long.valueOf(1848337504)));

                for (Event event : events) {
                    locations.put(event.getId(), new LatLng(event.getLatitude(), event.getLongitude()));
                    eventNames.put(event.getId(), event.getName());
                }

                // Add events as markers to map
                Iterator locIt = locations.entrySet().iterator();
                while (locIt.hasNext()) {
                    Map.Entry mapElement = (Map.Entry) locIt.next();
                    LatLng loc = ((LatLng)mapElement.getValue());
                    String eventName = ((String)eventNames.get(mapElement.getKey()));
                    Log.i("", "MAP -- " + loc + " " + eventName + " " + mapElement.getKey());
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(eventName))
                            .setTag(mapElement.getKey());
                }
            }
        });
    }
}