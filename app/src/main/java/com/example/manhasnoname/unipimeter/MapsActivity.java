package com.example.manhasnoname.unipimeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.Manifest;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    // Attach Code to Components
    GoogleMap mMap;
    LocationManager locManager;
    TextView speed_text, poiNear;

    // User's current location. Updates when onLocationChanged() is triggered.
    Location loc;
    ArrayList<PointsOfInterest> pointsOfInterest;
    String provider;

    // Load Settings
    SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Settings";
    MyDBHandler dbHandler;

    // speed limit is assumed as km/hr, radius is assumed as kms.
    int speedLimit = 0;
    int radius = 0;

    Date date = new Date();

    //Declare timer, timer resets list that holds visited points of interest
    CountDownTimer cTimer = null;

    boolean write_drive_speed;
    ArrayList<String> visitedPoi = new ArrayList<>();

    // Notifications for exceeding driving speed limit
    NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Attach Database
        dbHandler = new MyDBHandler(this);

        // grab saved Points of Interest from DB
        pointsOfInterest = dbHandler.loadPointsOfInterest();

        // Load GPS data
        locManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locManager.getBestProvider(criteria, true);

        // Grab TextViews
        speed_text = (TextView) findViewById(R.id.speed_text);
        poiNear = (TextView) findViewById(R.id.poiNear);

        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, 0);

        // Get stored values from settings
        speedLimit = prefs.getInt("speed_limit", 70);
        radius = prefs.getInt("radius_poi", 5);

        startTimer();


        // If permission is granted set location manager to receive updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            loc = locManager.getLastKnownLocation(provider);

            // Request location updates every 1s.
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }


    // Building Notifications
    public void sendNotification(String title, String text, int id) {

        // Channel id is ignored in versions < 8.
        mBuilder = new NotificationCompat.Builder(this, NotificationChannelHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(id, mBuilder.build());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            // Move camera to user's location when Map are loaded.

            LatLng myPosition = new LatLng(loc.getLatitude(), loc.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myPosition)         // Sets the center of the map to Geolocation
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        LoadMarkersOnMap();

    }

    // Grabs saved PoI from DB and then displays them on map as Markers.
    public void LoadMarkersOnMap() {

        pointsOfInterest = dbHandler.loadPointsOfInterest();

        for(int i = 0; i < pointsOfInterest.size(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(pointsOfInterest.get(i).getCoordinates())
                    .title(pointsOfInterest.get(i).getName()));
        }
    }

    // Timer helps to avoid writing 24/7 events of driver being near the same PoI.
    public void startTimer() {
        cTimer = new CountDownTimer(60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {

                visitedPoi.clear();
                cTimer.start();
            }
        };

    }


    // <----  Necessary Overrides for attaching LocationListener.   ---->

    // driving speed and distance from PoIs are calculated here and then displayed.
    @Override
    public void onLocationChanged(Location loc) {

        String locationString = "(" + loc.getLatitude() + ", " + loc.getLongitude() + ")";

        if (loc == null) {
            speed_text.setText("00 km/h");
            speed_text.append("\nMy Location\n");
            speed_text.append(locationString);

        } else {
            int speed = (int) ((loc.getSpeed() * 3600) / 1000);
            speed_text.setText(speed + " km/h");
            speed_text.append("\nMy Location\n");
            speed_text.append(locationString);
            poiNear.setText("");

            for (int i = 0; i < pointsOfInterest.size(); i++) {

                Location markerLocation = new Location("Loop Marker Provider");
                markerLocation.setLatitude(pointsOfInterest.get(i).getCoordinates().latitude);
                markerLocation.setLongitude(pointsOfInterest.get(i).getCoordinates().longitude);

                // Result should be in kms
                double distance = loc.distanceTo(markerLocation) / 1000;
                distance = Double.parseDouble(new DecimalFormat("##.#").format(distance));
                String timestamp = new Timestamp(date.getTime()).toString();


                if (distance <= radius) {
                    String text = pointsOfInterest.get(i).getName() + ": " + distance + " km\n";
                    poiNear.append(text);

                    // If point of interest is not visited in last 10 mins, write event to db.
                    if (!visitedPoi.contains(pointsOfInterest.get(i).getName())) {

                        // Add instance to database [Timestamp, PoI, Location]
                        dbHandler.addPoiInstance(timestamp, pointsOfInterest.get(i).getName(), locationString);

                    }

                    // Array gets updated so i wont write to database same point of interest when location is changed.
                    visitedPoi.add(pointsOfInterest.get(i).getName());


                }
                if (speed >= speedLimit) {

                    // Avoid writing to database while speed limit is exceeded.
                    // Reset when speed drops lower that the limit.
                    if(write_drive_speed) {

                        sendNotification("Speed Limit Exceeded!", "You are driving way too fast! Reduce speed.", 1);

                        // Add instance to database [Timestamp, PoI, Location]
                        dbHandler.addDrivingInstance(timestamp, Double.toString(speed), locationString);

                        write_drive_speed = false;
                    }
                }

                else {
                    write_drive_speed = true;
                }
            }

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
