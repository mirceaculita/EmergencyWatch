package com.example.emergencywatch;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.core.content.res.ResourcesCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


public class SimulatedUser extends Marker {

    Context localContext;
    MapView mapView;
    GeoPoint currentLocation;
    String streetLoc;

    public SimulatedUser(Context context, MapView map, GeoPoint startLoc) {
        super(map);
        localContext = context;
        mapView = map;
        //set Icon
        Drawable dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_person_pin_24, null);
        this.setIcon(dr);
        this.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        mapView.getOverlays().add(this);
        currentLocation = startLoc;
        updateStreet();
        this.setPosition(currentLocation);
    }
    public void getStreet(UserStreetCallback callback) {

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
                try {
                    String streetName = roadNode.asText();
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
                updateIconColor(Color.GREEN);
            }
        });
    }
    public void updateIconColor(int colorInt){
        Drawable icon = getIcon();
        icon.setColorFilter(new PorterDuffColorFilter(colorInt, PorterDuff.Mode.SRC_IN));
        this.setIcon(icon);
    }
    public void setCurrentLocation(GeoPoint location){
        currentLocation = location;
        updateStreet();
    }
    public GeoPoint getLocation(){
        if(currentLocation != null)
            return currentLocation;
        else
            return new GeoPoint(0f,0f);
    }
    void setStreetLoc(String streetName){
        streetLoc = streetName;}
    public String getStreetLoc(){return streetLoc;}
}