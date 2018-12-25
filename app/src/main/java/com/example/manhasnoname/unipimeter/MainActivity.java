package com.example.manhasnoname.unipimeter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    Button launch_button;
    Button settings_button;
    Button statistics_button;
    Button editpoi_button;
    MyDBHandler dbHandler;

    // Load Settings
    SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new MyDBHandler(this);


        // Ask for user permission on start up

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME , 0);

        // Set defaults if launching for first time
        // else get settings
        if (prefs.getInt("key_weight", 0) == 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("speed_limit", 70);
            editor.putInt("radius_poi", 5);
            editor.putInt("key_weight", 75);
            editor.commit();

            // Adding a few default Points of Interest
            dbHandler.addPointOfInterest(new PointsOfInterest("Village Cinemas Renti", "Movies", "Entertainment", new LatLng(37.975605, 23.668184)));
            dbHandler.addPointOfInterest(new PointsOfInterest("University of Piraeus", "University", "Education", new LatLng(37.941739,  23.652708)));
            dbHandler.addPointOfInterest(new PointsOfInterest("Kotzia Square", "Park", "Entertainment", new LatLng(37.979865, 23.729446)));
        }

        launch_button = (Button) findViewById(R.id.launch_button);
        settings_button = (Button) findViewById(R.id.settings_button);
        statistics_button = (Button) findViewById(R.id.statistics_button);
        editpoi_button = (Button) findViewById(R.id.editpoi_button);

        // When launch button is clicked. Start Google Maps Activity.
        launch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            }
        });

        // When editPOI is clicked, open Editing Activity
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        statistics_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
            }
        });

        editpoi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PoisActivity.class);
                startActivity(intent);
            }
        });

        // Check for enabled GPS
        isLocationEnabled();
    }


    // Checks whether GPS service is enabled or not.
    // If not, take user to settings menu in order to activate it.
    private void isLocationEnabled() {

        LocationManager locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }


}
