package com.example.manhasnoname.unipimeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MyDBHandler extends SQLiteOpenHelper {

    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UnipiMeterDB.db";

    // TABLE 1 holds every instance for exceeding driving speed limit
    public static final String TABLE_1_NAME = "SPEED";
    public static final String TABLE_1_COLUMN_1 = "Timestamp";
    public static final String TABLE_1_COLUMN_2 = "Driving_Speed";
    public static final String TABLE_1_COLUMN_3 = "Location";

    // TABLE 2 holds every instance for driving near a Point of Interest
    public static final String TABLE_2_NAME = "LANDMARK";
    public static final String TABLE_2_COLUMN_1 = "Timestamp";
    public static final String TABLE_2_COLUMN_2 = "Poi";
    public static final String TABLE_2_COLUMN_3 = "Location";

    // TABLE 3 holds saved Points of Interest
    public static final String TABLE_3_NAME = "POINTOFINTEREST";
    public static final String TABLE_3_COLUMN_1 = "name";
    public static final String TABLE_3_COLUMN_2 = "description";
    public static final String TABLE_3_COLUMN_3 = "category";
    public static final String TABLE_3_COLUMN_4 = "latitude";
    public static final String TABLE_3_COLUMN_5 = "longitude";


    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_1 = String.format("CREATE TABLE %s (%s TEXT, %s TEXT, %s TEXT)",
                                                TABLE_1_NAME, TABLE_1_COLUMN_1, TABLE_1_COLUMN_2, TABLE_1_COLUMN_3);

        String CREATE_TABLE_2 = String.format("CREATE TABLE %s (%s TEXT, %s TEXT, %s TEXT)",
                                                TABLE_2_NAME, TABLE_2_COLUMN_1, TABLE_2_COLUMN_2, TABLE_2_COLUMN_3);

        String CREATE_TABLE_3 = String.format("CREATE TABLE %s (%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                TABLE_3_NAME, TABLE_3_COLUMN_1, TABLE_3_COLUMN_2, TABLE_3_COLUMN_3, TABLE_3_COLUMN_4, TABLE_3_COLUMN_5);


        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public void addDrivingInstance(String timestamp, String drivingSpeed, String location) {
        ContentValues values = new ContentValues();
        values.put(TABLE_1_COLUMN_1, timestamp);
        values.put(TABLE_1_COLUMN_2, drivingSpeed);
        values.put(TABLE_1_COLUMN_3, location);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_1_NAME, null, values);
        db.close();
    }

    public void addPoiInstance(String timestamp, String poi, String location) {
        ContentValues values = new ContentValues();
        values.put(TABLE_2_COLUMN_1, timestamp);
        values.put(TABLE_2_COLUMN_2, poi);
        values.put(TABLE_2_COLUMN_3, location);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_2_NAME, null, values);
        db.close();
    }

    public void addPointOfInterest(PointsOfInterest poi) {
        ContentValues values = new ContentValues();
        values.put(TABLE_3_COLUMN_1, poi.getName());
        values.put(TABLE_3_COLUMN_2, poi.getDescription());
        values.put(TABLE_3_COLUMN_3, poi.getCategory());
        values.put(TABLE_3_COLUMN_4, poi.getCoordinates().latitude);
        values.put(TABLE_3_COLUMN_5, poi.getCoordinates().longitude);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_3_NAME, null, values);
        db.close();
    }

    // Returns every exceed of speed limit
    public String loadDrivingInstance() {
        String query = String.format("SELECT * FROM %s ", TABLE_1_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String result = "";

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                result += "Timestamp: " + cursor.getString(0) + "\n";
                result += "Driving Speed: " + cursor.getString(1) + "\n";
                result += "Location: " + cursor.getString(2) + "\n\n";
            }
        }

        cursor.close();
        db.close();
        return result;
    }

    // Returns every you were near a Point of Interest
    public String loadLandmarkInstance() {
        String query = String.format("SELECT * FROM %s ", TABLE_2_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String result = "";

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                result += "Timestamp: " + cursor.getString(0) + "\n";
                result += "Point of Interest: " + cursor.getString(1) + "\n";
                result += "Location: " + cursor.getString(2) + "\n\n";
            }
        }

        cursor.close();
        db.close();

        return result;
    }

    // Returns saved Points of Interest as an object list
    public ArrayList<PointsOfInterest> loadPointsOfInterest() {
        String query = String.format("SELECT * FROM %s ", TABLE_3_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<PointsOfInterest> pointsOfInterest = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                Double latitude = Double.parseDouble(cursor.getString(3));
                Double longitude = Double.parseDouble(cursor.getString(4));

                LatLng location = new LatLng(latitude, longitude);

                PointsOfInterest poi = new PointsOfInterest(cursor.getString(0),
                                                            cursor.getString(1),
                                                            cursor.getString(2),
                                                            location);

                pointsOfInterest.add(poi);
            }

        }

        cursor.close();
        db.close();

        return pointsOfInterest;
    }

    // Returns string containing how many times drive limit was exceeded,
    // how many points of interest where visited
    // and which one was visited the most.
    public String Statistics() {

        String query = String.format("SELECT COUNT(*) FROM %s ", TABLE_1_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String result = "";

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                result += "\nSpeed Limit exceeded: " + cursor.getString(0) + " time(s).";
            }

        }

        result += "\n\n";

        cursor.close();

        query = String.format("SELECT COUNT(*) FROM %s ", TABLE_2_NAME);
        cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                result += "Points of Interest visited: " + cursor.getString(0) + ".";
            }
        }

        cursor.close();

        query = String.format("SELECT Poi, COUNT(*) AS counter FROM %s GROUP BY Poi ORDER BY counter ASC LIMIT 1", TABLE_2_NAME);
        cursor = db.rawQuery(query, null);

        result += "\n";

        String favLocation = "";
        String favVisited = "0";

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                favLocation = cursor.getString(0);
                favVisited = cursor.getString(1);
            }
        }

        cursor.close();

        if (favVisited.equals("0")) {
            result += "No Points of Interest visited yet.";

        } else {
            result += "Favorite Location: " + favLocation + " visited: " + favVisited + " time(s).";
        }

        cursor.close();
        db.close();

        return result;
    }

    public void DeletePoi(String lat, String lng) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_3_NAME, TABLE_3_COLUMN_4 + "=?" + " AND " + TABLE_3_COLUMN_5 + "=?", new String[] {lat, lng});
        db.close();

    }

}