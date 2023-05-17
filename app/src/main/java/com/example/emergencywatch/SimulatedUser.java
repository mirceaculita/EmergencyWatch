package com.example.emergencywatch;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.core.content.res.ResourcesCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;


public class SimulatedUser extends Marker {

    Context localContext;
    MapView mapView;
    GeoPoint currentLocation;
    String streetLoc;
    simulatedEmergencyVehicle userVehicle;

    boolean userInVehicle = false;

    boolean liveLocation = false;
    MyLocationNewOverlay locationOverlay = null;
    ArrayList<ArrayList<Double>> route_to_follow;
    Overlay routeOverlay;
    HomeFragment homeFrg;

    public SimulatedUser(Context context, MapView map, GeoPoint startLoc,HomeFragment homeFragment) {
        super(map);
        localContext = context;
        homeFrg = homeFragment;
        mapView = map;
        //set Icon
        Drawable dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_person_pin_24, null);
        this.setIcon(dr);
        this.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        mapView.getOverlays().add(this);
        currentLocation = startLoc;
        updateStreet();
        this.setCurrentLocation(currentLocation);
    }
    public void getStreet(UserStreetCallback callback) {

        new HttpRequests("location",new ArrayList<>(Arrays.asList(this.getLocation())),null, new HttpRequests.HttpListener() {
            @Override
            public void onHttpResponse(String response) throws JsonProcessingException, JSONException {
                // Handle the API response here

                JSONObject jsonObj = new JSONObject(response);

                System.out.println("AAAAAAAAAAAAAaaaa "+response);
                String road = jsonObj.getJSONObject("address").getString("road");
                System.out.println("AAAAAAAAAAAAAaaaa "+road);
                // Get the street name as a string and call the callback function
                try {
                    String streetName = road;
                    callback.onUserStreetAvailable(streetName);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public interface UserStreetCallback {
        void onUserStreetAvailable(String streetName);
    }
    private void updateStreet() {
        getStreet(new SimulatedUser.UserStreetCallback() {
            @Override
            public void onUserStreetAvailable(String streetName) {
                // Use the street name here
                setStreetLoc(streetName);
                System.out.println("Updated user street name. New street name: " + streetName);
                updateIconColor(Color.parseColor("#1D3B81"));
            }
        });
    }
    public void updateIconColor(int colorInt){
        Drawable icon = getIcon();
        icon.setColorFilter(new PorterDuffColorFilter(colorInt, PorterDuff.Mode.SRC_IN));
        this.setIcon(icon);
    }
    public void setCurrentLocation(GeoPoint location){
        if(!userInVehicle) {
            currentLocation = location;
            this.setPosition(currentLocation);
            updateStreet();
        }
    }

    public void useLiveLocation(MyLocationNewOverlay locationOverlayObj){
        if(!userInVehicle && !liveLocation) {
            liveLocation = true;
            this.setVisible(false);
            locationOverlay = locationOverlayObj;
        }
    }
    public void useSimulatedLocation(){
        if(!userInVehicle && liveLocation) {
            liveLocation = false;
            this.setVisible(true);
        }
    }

    @SuppressLint("ResourceAsColor")
    public simulatedEmergencyVehicle simUserStartDrivingToLocation(ArrayList<double[]> route, Context context, MapView map){
        this.setVisible(false);
        if(locationOverlay != null){
            locationOverlay.setEnabled(false);
        }
        ArrayList<Double> lat = new ArrayList<>();
        ArrayList<Double> lon = new ArrayList<>();
        for (double point :route.get(0)){
            lat.add(point);
        }
        for (double point :route.get(1)){
            lon.add(point);
        }
        route_to_follow = new ArrayList<>(Arrays.asList(lat, lon));
        userInVehicle = true;
        userVehicle = new simulatedEmergencyVehicle("user", route_to_follow, false, "#00FFFF", context, map);
        return userVehicle;
    }

    void stopDriving(GeoPoint lastPoint){

        homeFrg.clearMap();
        if(liveLocation){
            userInVehicle = false;
            if(locationOverlay != null){
                locationOverlay.setEnabled(true);
            }

            userVehicle.remove(mapView);
        }
        if(!liveLocation) {
            userInVehicle = false;
            this.setVisible(true);
            if (locationOverlay != null) {
                locationOverlay.setEnabled(false);
            }
            this.setCurrentLocation(lastPoint);

            userVehicle.remove(mapView);
        }
    }


    public GeoPoint getLocation() {
        if (!userInVehicle) {
            System.out.println("Live location is " + liveLocation);
            if (liveLocation) {
                return locationOverlay.getMyLocation();
            } else {
                if (currentLocation != null)
                    return currentLocation;
                else
                    return new GeoPoint(0f, 0f);
            }
        } else {
            GeoPoint location = userVehicle.getLocation();
            GeoPoint lastPointInRoute = new GeoPoint(route_to_follow.get(0).get(route_to_follow.get(0).size() - 1), route_to_follow.get(1).get(route_to_follow.get(1).size() - 1));
            if (distance(location, lastPointInRoute) < 50) {
                stopDriving(lastPointInRoute);
                return this.getLocation();
            }else{
                return userVehicle.getLocation();
            }
        }
    }
    void setStreetLoc(String streetName){
            streetLoc = streetName;
    }
    public String getStreetLoc(){
        if(!userInVehicle)
            return streetLoc;
        else
            return userVehicle.getStreetLoc();
    }
    private static double distance(GeoPoint point1, GeoPoint point2) {
        try {
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
                dist = (dist * 1.609344) * 1000;

                return (dist);
            }
        } catch (Exception e) {
            return 99999999;
        }
    }

}