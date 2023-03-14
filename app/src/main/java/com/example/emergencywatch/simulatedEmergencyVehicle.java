package com.example.emergencywatch;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.core.content.res.ResourcesCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class simulatedEmergencyVehicle extends Marker {
    ArrayList<Double> lat_route;
    ArrayList<Double> lon_route;

    boolean canMoveMarker = true;


    Marker carMarker;
    Drawable carMarkerIcon;
    Context localContext;
    MapView mapView;
    GeoPoint currentLocation;
    String vehicleType;
    Drawable staticIcon;

    boolean drawRouteToMap;
    ArrayList<ArrayList<Double>> routes;

    String routesColor;
    public simulatedEmergencyVehicle(String type, ArrayList<ArrayList<Double>> route, boolean drawRoute, String routeColor, Context context, MapView map) {
        super(map);
        lat_route = route.get(0);
        lon_route = route.get(1);
        carMarker = this;
        routesColor = routeColor;
        routes = route;
        drawRouteToMap = drawRoute;
        vehicleType = type;
        localContext = context;
        mapView = map;
        //set Icon
        Drawable dr;

        switch (type) {
            case "ambulance":
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_emergency_24, null);
                break;
            case "firetruck":
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_airport_shuttle_24, null);
                break;
            case "police":
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_local_police_24, null);
                break;
            default:
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_launcher_foreground, null);
                break;
        }
        staticIcon = dr;
        this.setIcon(dr);
        this.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        carMarkerIcon = this.getIcon();
    }

    public void draw(){
        if (drawRouteToMap)
            mapView.getOverlays().add(drawRoute(routes, routesColor));
        mapView.getOverlays().add(carMarker);
        if (canMoveMarker)
            updateMarkerPos(0, lat_route.size(), 100);
    }

    public double distance(GeoPoint point1, GeoPoint point2, String unit) {
        double lat1 = point1.getLatitude();
        double lon1 = point1.getLongitude();
        double lat2 = point2.getLatitude();
        double lon2 = point2.getLongitude();

        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            } else if (unit.equals("m")) {
                dist = (dist * 1.609344) * 1000;
            }
            return (dist);
        }
    }
    public GeoPoint interpolate(float t, GeoPoint a, GeoPoint b) {
        return new GeoPointInterpolator.LinearFixed().interpolate(t, a, b);
    }
    private Drawable flipDrawable(Drawable drawable) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        Bitmap flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(localContext.getResources(), flippedBitmap);
    }
    private void updateMarkerPos(int i, int n, int waitTime) {
        canMoveMarker = true;
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (i < n) {

                    GeoPoint futurePoint = new GeoPoint(lat_route.get(i + 1), lon_route.get(i + 1));
                    GeoPoint currentPoint = new GeoPoint(lat_route.get(i), lon_route.get(i));
                    currentLocation = currentPoint;
                    double orientation = lon_route.get(i) - lon_route.get(i + 1);
                    if (orientation > 0) {
                        carMarker.setIcon(flipDrawable(carMarkerIcon));
                    } else {
                        carMarker.setIcon(carMarkerIcon);
                    }

                    double distance = distance(currentPoint, futurePoint, "m");
                    int speedKPH = 90;
                    int waittime_local = (int) (distance/(speedKPH/3.6)*1000);
                    System.out.println("waittime: " + waittime_local);
                    moveMarker(carMarker, currentPoint, futurePoint, waittime_local);
                    if (i + 2 == lat_route.size()) {
                        updateMarkerPos(0, n, waittime_local);
                    } else {
                        updateMarkerPos(i + 1, n, waittime_local);
                    }
                }
            }
        }, waitTime);
    }
    private void moveMarker(Marker marker, GeoPoint currentPos, GeoPoint newPosition, double duration) {
        // Get the current position of the marker

        // Create a new animation to move the marker to the new position
        final Interpolator interpolator = new LinearInterpolator();
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) ((float) elapsed / duration));
                GeoPoint position = interpolate(t, currentPos, newPosition);
                marker.setPosition(position);

                if (t < 1.0) {
                    // Post again 16ms later for the next frame.
                    handler.postDelayed(this, 32);
                }
            }
        });
        canMoveMarker = true;
    }
    public Polyline drawRoute(ArrayList<ArrayList<Double>> route, String colorString) {

        ArrayList<Double> lat = route.get(0);
        ArrayList<Double> lon = route.get(1);
        Polyline myPath = new Polyline();
        myPath.setWidth(10f);
        myPath.setColor(Color.parseColor(colorString));


        for (int i = 0; i < lat.size(); i++) {
            myPath.addPoint(new GeoPoint(lat.get(i), lon.get(i)));
        }

        return (myPath);
    }

    public GeoPoint getLocation(){
        if(currentLocation != null)
            return currentLocation;
        else
            return new GeoPoint(0f,0f);
    }

    public interface StreetCallback {
        void onStreetAvailable(String streetName);
    }

    public void getStreet(StreetCallback callback) {
        new HttpRequests(this.getLocation(), new HttpRequests.HttpListener() {
            @Override
            public void onHttpResponse(String response) throws JsonProcessingException {
                // Handle the API response here
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response);
                // get the "address" field
                JsonNode addressNode = rootNode.get("address");
                JsonNode roadNode = addressNode.get("road");
                // Get the street name as a string and call the callback function
                String streetName = roadNode.asText();
                callback.onStreetAvailable(streetName);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    public String getType(){
        return vehicleType;
    }

    public Drawable getIcon(){
        return staticIcon;
    }

}