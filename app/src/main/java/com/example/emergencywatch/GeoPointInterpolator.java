package com.example.emergencywatch;

import org.osmdroid.util.GeoPoint;

public class GeoPointInterpolator {

    public interface Type {
        Type LINEAR = new Linear();
        Type ACCELERATE = new Accelerate();
        Type DECELERATE = new Decelerate();

        GeoPoint interpolate(float t, GeoPoint a, GeoPoint b);
    }

    public static class LinearFixed implements Type {
        @Override
        public GeoPoint interpolate(float t, GeoPoint a, GeoPoint b) {
            double lat = a.getLatitude() + ((b.getLatitude() - a.getLatitude()) * t);
            double lng = a.getLongitude() + ((b.getLongitude() - a.getLongitude()) * t);
            return new GeoPoint(lat, lng);
        }
    }

    private static class Linear implements Type {
        @Override
        public GeoPoint interpolate(float t, GeoPoint a, GeoPoint b) {
            return new LinearFixed().interpolate(t, a, b);
        }
    }

    private static class Accelerate implements Type {
        @Override
        public GeoPoint interpolate(float t, GeoPoint a, GeoPoint b) {
            double lat = a.getLatitude() + ((b.getLatitude() - a.getLatitude()) * t * t);
            double lng = a.getLongitude() + ((b.getLongitude() - a.getLongitude()) * t * t);
            return new GeoPoint(lat, lng);
        }
    }

    private static class Decelerate implements Type {
        @Override
        public GeoPoint interpolate(float t, GeoPoint a, GeoPoint b) {
            t = (1 - (float) Math.pow((1 - t), 2));
            double lat = a.getLatitude() + ((b.getLatitude() - a.getLatitude()) * t);
            double lng = a.getLongitude() + ((b.getLongitude() - a.getLongitude()) * t);
            return new GeoPoint(lat, lng);
        }
    }

}
