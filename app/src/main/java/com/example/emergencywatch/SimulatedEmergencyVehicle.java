
/*
 SimulatedEmergencyVehicle is a class that represents a simulated emergency vehicle on a map.
 It extends the Marker class from the osmdroid library.
 */
package com.example.emergencywatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class SimulatedEmergencyVehicle extends Marker {
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

    String streetLoc;
    double heading;
    String routesColor;

    int iconColor;
    double distanceToUser = 0;
    int speedKPH;

    Polyline activeRoute;
    ArrayList<double[]> activeRoutePoints;
    boolean onActiveRoute = false;

    boolean reachedDest = false;


    GeoPoint destination = null;
    int travelTimeOnActiveRoute = 9999;
    boolean annoucedReachedDest = false;
    Bitmap bitmap;
    /**
     * Constructor for the SimulatedEmergencyVehicle class.
     *
     * @param type       The type of the emergency vehicle.
     * @param route      The route of the vehicle as a list of latitude and longitude coordinates.
     * @param drawRoute  Whether to draw the route on the map.
     * @param routeColor The color of the route on the map.
     * @param speedKph   The speed of the vehicle in kilometers per hour.
     * @param context    The context of the application.
     * @param map        The MapView on which the vehicle will be displayed.
     */
    public SimulatedEmergencyVehicle(String type, ArrayList<ArrayList<Double>> route, boolean drawRoute, String routeColor, int speedKph, Context context, MapView map) {
        super(map);
        lat_route = route.get(0);
        lon_route = route.get(1);
        carMarker = this;
        routesColor = routeColor;
        routes = route;
        speedKPH = speedKph;
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
            case "ambulanță":
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_emergency_24, null);
                break;
            case "mașină de pompieri":
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_airport_shuttle_24, null);
                break;
            case "poliție":
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_local_police_24, null);
                break;
            case "user":
                dr = ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_my_location_24_green, null);
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
    /**
     * Calculate the heading (bearing) between two GeoPoints.
     *
     * @param currentPoint The current GeoPoint.
     * @param futurePoint  The future GeoPoint.
     * @return The heading in degrees.
     */
    public static double calculateHeading(GeoPoint currentPoint, GeoPoint futurePoint) {
        if (currentPoint != null && futurePoint != null) {
            double lat1 = currentPoint.getLatitude() * Math.PI / 180;
            double lon1 = currentPoint.getLongitude() * Math.PI / 180;
            double lat2 = futurePoint.getLatitude() * Math.PI / 180;
            double lon2 = futurePoint.getLongitude() * Math.PI / 180;
            double dLon = lon2 - lon1;
            double y = Math.sin(dLon) * Math.cos(lat2);
            double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
            double heading = Math.atan2(y, x);
            heading = Math.toDegrees(heading);
            if (heading < 0) {
                heading += 360;
            }
            return heading;
        } else {
            return 0;
        }
    }
    /**
     * Draw the route on the map.
     *
     * @param route The route coordinates as a list of latitude and longitude pairs.
     * @return The Polyline representing the route.
     */
    private Polyline drawRouteReal(ArrayList<double[]> route) {


        double[] lat = route.get(0);
        double[] lon = route.get(1);
        System.out.println(Arrays.toString(lat));
        System.out.println(Arrays.toString(lon));
        Polyline myPath = new Polyline();
        myPath.setWidth(10f);
        myPath.setColor(Color.parseColor("#0000FF"));


        for (int i = 0; i < lat.length; i++) {
            myPath.addPoint(new GeoPoint(lat[i], lon[i]));
        }

        return (myPath);
    }
    /**
     * Set the speed of the vehicle in kilometers per hour.
     *
     * @param speed The speed in kilometers per hour.
     */
    public void setSpeedKPH(int speed) {
        speedKPH = speed;
    }

    /**
     * Check if the vehicle has reached its destination.
     *
     * @return True if the vehicle has reached its destination, false otherwise.
     */
    public boolean getReachedDest() {
        return reachedDest;
    }
    /**
     * Set the reached destination status of the vehicle.
     *
     * @param bool True if the vehicle has reached its destination, false otherwise.
     */
    public void setReachedDest(boolean bool) {
        if (!annoucedReachedDest) {
            System.out.println(getType() + " reached it's destination");
            annoucedReachedDest = true;
        }
        reachedDest = bool;
    }
    /**
     * Get the destination GeoPoint of the vehicle.
     *
     * @return The destination GeoPoint.
     */
    public GeoPoint getDest() {
        return destination;
    }

    /**
     * Set the destination GeoPoint of the vehicle.
     *
     * @param dest The destination GeoPoint.
     */
    public void setDest(GeoPoint dest) {
        destination = dest;
        System.out.println("Set destination for " + getType() + " at " + destination);
    }
    /**
     * Get the travel time to the destination.
     *
     * @return The travel time in seconds.
     */
    public int getTravelTimeOnActiveRoute() {
        return travelTimeOnActiveRoute;
    }

    /**
     * Set the travel time to the destination.
     *
     * @param time The travel time in seconds
     */
    public void setTravelTimeOnActiveRoute(int time) {
        travelTimeOnActiveRoute = time;
    }

    /**
     * Set boolean  for vehicle to signal it it on an active route.
     *
     * @param onActiveRoute Boolean that represents whether the vehicle is on an active route
     */
    public void setOnActiveRoute(boolean onActiveRoute) {
        this.onActiveRoute = onActiveRoute;
    }

    /**
     * Draw the simulated vehicle on the map and animate it along the route.\
     *
     */
    public void draw() {
        if (drawRouteToMap)
            mapView.getOverlays().add(drawRoute(routes, routesColor));
        mapView.getOverlays().add(carMarker);
        if (canMoveMarker)
            updateMarkerPos(0, lat_route.size(), 100);
    }
    /**

     * Calculates the distance between two GeoPoints using the Haversine formula.
     * @param point1 The first GeoPoint.
     * @param point2 The second GeoPoint.
     * @param unit The unit of measurement for the distance (e.g., "K" for kilometers, "N" for nautical miles, "m" for meters).

     * @return The distance between the two GeoPoints in the specified unit.
     */
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
    /**

     * Interpolates between two GeoPoints using linear interpolation.
     *
     * @param t The interpolation parameter, ranging from 0 to 1, where 0 represents point a and 1 represents point b.
     * @param a The starting GeoPoint.
     * @param b The ending GeoPoint.
     *
     * @return The interpolated GeoPoint between point a and point b.
     */
    public GeoPoint interpolate(float t, GeoPoint a, GeoPoint b) {
        return new GeoPointInterpolator.LinearFixed().interpolate(t, a, b);
    }
    /**
     * Flips a Drawable horizontally.
     *
     * @param drawable The Drawable to be flipped.
     *
     * @return The flipped Drawable with horizontal orientation.
     */
    private Drawable flipDrawable(Drawable drawable) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        Bitmap flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(localContext.getResources(), flippedBitmap);
    }

    /**
     * Updates the color of the icon associated with this object.
     *
     * @param colorInt The integer representation of the desired color.
     */
    public void updateIconColor(int colorInt) {
        Drawable icon = getIcon();
        icon.setColorFilter(new PorterDuffColorFilter(colorInt, PorterDuff.Mode.SRC_IN));
        iconColor = colorInt;
        this.setIcon(icon);
    }

    public int getIconColor() {
        return iconColor;
    }

    public Bitmap getBitmapIcon() {
        return bitmap;
    }
    /**
     * Updates the position of the marker based on the specified parameters.
     *
     * @param i The current index of the marker's position in the route.
     * @param n The total number of positions in the route.
     *
     * @param waitTime The time delay before updating the marker's position.
     */
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
                    heading = calculateHeading(currentPoint, futurePoint);
                    double orientation = lon_route.get(i) - lon_route.get(i + 1);
                    if (orientation > 0) {
                        carMarker.setIcon(flipDrawable(carMarkerIcon));
                    } else {
                        carMarker.setIcon(carMarkerIcon);
                    }

                    double distance = distance(currentPoint, futurePoint, "m");
                    int waittime_local = (int) (distance / (speedKPH / 3.6) * 1000);
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

    /**
     *
     * Checks if the vehicle has reached its destination.
     * If the destination is set and the distance between the current position and the destination is less than 50 meters,
     * the "reachedDest" flag is set to true.
     *
     */
    void checkIfVehicleReachedDest() {
        if (getDest() != null) {
            double dist = distance(getPosition(), getDest(), "m");
            if (dist < 50) {
                setReachedDest(true);
            }
        }
    }

    /**
     * Moves the specified marker from the current position to a new position with a smooth animation.
     *
     * @param marker The marker to be moved.
     * @param currentPos The current position of the marker.
     * @param newPosition The new position to move the marker to.
     * @param duration The duration of the animation in milliseconds.
     */
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
                setCurrentLocation(position);
                marker.setPosition(position);
                checkIfVehicleReachedDest();
                if (t < 1.0) {
                    // Post again 16ms later for the next frame.
                    handler.postDelayed(this, 32);
                }
            }
        });
        canMoveMarker = true;
    }

    /**

     Calculates the distance between the current location and a specified point.
     @param point The GeoPoint representing the point.
     @return The distance between the current location and the specified point in meters.
     */
    public double getDistanceToPoint(GeoPoint point) {
        return distance(getLocation(), point, "m");
    }

    /**

     Draws a route on the map using the provided coordinates and color.

     @param route The route coordinates as an ArrayList of ArrayLists containing latitude and longitude values.

     @param colorString The color of the route represented as a string.

     @return The Polyline representing the drawn route.
     */
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
    /**

     Returns the current location as a GeoPoint.
     If the current location is null, a default GeoPoint with latitude 0 and longitude 0 is returned.
     @return The current location as a GeoPoint.
     */
    public GeoPoint getLocation() {
        if (currentLocation != null)
            return currentLocation;
        else
            return new GeoPoint(0f, 0f);
    }
    /**

     Retrieves the street name for the current location and invokes the provided callback function.
     @param callback The callback function to be called when the street name is available.
     */
    public void getStreet(StreetCallback callback) {
        new HttpRequests("location", new ArrayList<>(Arrays.asList(this.getLocation())), null, new HttpRequests.HttpListener() {
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
                    callback.onStreetAvailable(streetName);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public String getType() {
        return vehicleType;
    }
    public String getTip() {
        String vehicleTypeRO = null;
        switch (getType()){
            case "firetruck":
                vehicleTypeRO =  "mașină de pompieri";
                break;
            case "police":
                vehicleTypeRO =  "poliție";
                break;
            case "ambulance":
                vehicleTypeRO =  "ambulanță";
                break;
        }

        return vehicleTypeRO;
    }

    public Drawable getIcon() {
        return staticIcon;
    }

    public double getHeading() {
        return heading;
    }

    public void setCurrentLocation(GeoPoint location) {
        currentLocation = location;
    }

    public String getStreetLoc() {
        return streetLoc;
    }

    void setStreetLoc(String streetName) {
        streetLoc = streetName;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(double value) {
        distanceToUser = value;
    }

    public double getSpeedMs() {
        return speedKPH * (1000.0 / 3600.0);
    }

    public double getHeadingToPoint(GeoPoint point) {
        return calculateHeading(currentLocation, point);
    }

    public interface StreetCallback {
        void onStreetAvailable(String streetName);
    }
}