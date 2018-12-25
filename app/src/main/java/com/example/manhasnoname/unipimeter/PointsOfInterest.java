package com.example.manhasnoname.unipimeter;

import com.google.android.gms.maps.model.LatLng;

class PointsOfInterest {

        String name;
        String description;
        String category;
        LatLng coordinates;

        PointsOfInterest(String Name, String Description, String Category, LatLng Coordinates) {
            this.name = Name;
            this.description = Description;
            this.category = Category;
            this.coordinates = Coordinates;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public String getCategory() {
            return this.category;
        }

        public LatLng getCoordinates() {
            return this.coordinates;
        }

        @Override
        public String toString() {
            return this.getName();
        }
    }

