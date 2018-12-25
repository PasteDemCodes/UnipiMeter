package com.example.manhasnoname.unipimeter;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {


    // Load Settings
    SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Settings";

    // speed limit is assumed as km/hr, radius is assumed as kms.
    int speedLimit = 0;
    int radius_poi = 0;

    EditText drive_speed;
    EditText radius_distance;

    Button save_settings_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        drive_speed = (EditText) findViewById(R.id.top_drive_speed_text);
        radius_distance = (EditText) findViewById(R.id.radius_text);
        save_settings_button = (Button) findViewById(R.id.save_settings_button);


        try {
            prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME , 0);
            speedLimit = prefs.getInt("speed_limit", 70);
            radius_poi = prefs.getInt("radius_poi", 5);
        }

        catch (Exception e) {

        }

        drive_speed.setText(Integer.toString(speedLimit), TextView.BufferType.EDITABLE);
        radius_distance.setText(Integer.toString(radius_poi), TextView.BufferType.EDITABLE);

        save_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int new_drive_limit = Integer.parseInt(drive_speed.getText().toString());
                int new_radius_distance = Integer.parseInt(radius_distance.getText().toString());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("speed_limit", new_drive_limit);
                editor.putInt("radius_poi", new_radius_distance);
                editor.commit();
            }
        });

    }
}
