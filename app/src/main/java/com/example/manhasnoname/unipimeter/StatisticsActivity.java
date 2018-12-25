package com.example.manhasnoname.unipimeter;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {

    TextView dbView;
    Button Refresh;

    // Load Settings
    SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Settings";
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Refresh = (Button) findViewById(R.id.refresh_button);
        dbView = (TextView) findViewById(R.id.databaseView);
        dbView.setMovementMethod(new ScrollingMovementMethod());

        // Attach Database
        dbHandler = new MyDBHandler(this);

        dbView.setText("");
        dbView.append(".. Statistics ..\n");
        dbView.append(dbHandler.Statistics());

        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbView.setText("");
                dbView.append(".. Database Entries ..\n");
                dbView.append("\n-- Driving Instances --\n");
                dbView.append(dbHandler.loadDrivingInstance());
                dbView.append("\n-- Landmark Instances --\n");
                dbView.append(dbHandler.loadLandmarkInstance());
                dbView.append("\n\n.. Statistics ..\n");
                dbView.append(dbHandler.Statistics());
                dbView.append("\n\n\n\n\n\n");

            }
        });


    }
}
