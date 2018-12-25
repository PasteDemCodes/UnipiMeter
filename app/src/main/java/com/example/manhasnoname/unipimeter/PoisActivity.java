package com.example.manhasnoname.unipimeter;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PoisActivity extends AppCompatActivity {

    ArrayList<PointsOfInterest> pointsOfInterest;
    MyDBHandler dbHandler;
    ListView lv;
    EditText newEntry;
    Button savePoi;
    ArrayAdapter<PointsOfInterest> adapter;

    // Load Settings
    SharedPreferences prefs;
    public static final String MY_PREFS_NAME = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pois);

        lv = (ListView) findViewById(R.id.lvPoi);
        newEntry = (EditText) findViewById(R.id.newPoi);
        savePoi = (Button) findViewById(R.id.savePoi);

        dbHandler = new MyDBHandler(this);
        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME , 0);

        // Load stored Points of Interest

        pointsOfInterest = dbHandler.loadPointsOfInterest();

        // set Adapter
        adapter = new ArrayAdapter<>(PoisActivity.this, android.R.layout.simple_list_item_1, pointsOfInterest);

        lv.setAdapter(adapter);

        // Delete poi on long press
        lv.setLongClickable(true);

        // Save to database and list.
        savePoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String[] entry = newEntry.getText().toString().split(",");
                    newEntry.setText("");

                    LatLng mlatlng = new LatLng(Double.parseDouble(entry[3]), Double.parseDouble(entry[4]));

                    PointsOfInterest nPoi = new PointsOfInterest(entry[0], entry[1], entry[2], mlatlng);

                    dbHandler.addPointOfInterest(nPoi);
                    pointsOfInterest.add(nPoi);

                    adapter.notifyDataSetChanged();
                }

                catch(Exception e) {
                    newEntry.setText("Error. Nothing Saved!");
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

                String lat = Double.toString(pointsOfInterest.get(position).getCoordinates().latitude);
                String lng = Double.toString(pointsOfInterest.get(position).getCoordinates().longitude);

                dbHandler.DeletePoi(lat, lng);
                pointsOfInterest.remove(position);
                adapter.notifyDataSetChanged();

                return true;
            }
        });


    }
}
