package com.example.emergencywatch;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    MapView map = null;
    public MyLocationNewOverlay locationOverlay;

    public Button floatingButton;
    public DrawerLayout drawerLayout;
    IMapController mapController;
    private NavigationView navigationView;

    private Handler mapOrientationHandler;
    private Handler distanceToEvHandler;

    private Handler locationUpdateHandler;


    ArrayList<Double> lat_route_gara = new ArrayList<Double>(Arrays.asList(47.14615, 47.14613, 47.14659, 47.14679, 47.14688, 47.14701, 47.14711, 47.1473, 47.14823, 47.15079, 47.15085, 47.1509, 47.15087, 47.1509, 47.15092, 47.15099, 47.15104, 47.15114, 47.15126, 47.15132, 47.15144, 47.15154, 47.15187, 47.15204, 47.1525, 47.15304, 47.15527, 47.15552, 47.15836, 47.15853, 47.15864, 47.15877, 47.16031, 47.16048, 47.16075, 47.16164, 47.16174, 47.16179, 47.16198, 47.16271, 47.16344, 47.16416, 47.1644, 47.16445, 47.1655, 47.16604, 47.16625, 47.16732, 47.16785, 47.16791, 47.16807, 47.16867, 47.16875, 47.16901, 47.16932, 47.16941, 47.1696, 47.16974, 47.16979, 47.16985, 47.16989, 47.16991, 47.1699, 47.16986, 47.16978, 47.1697, 47.16964, 47.16953, 47.16946, 47.16936, 47.16884, 47.16875, 47.16872, 47.16875, 47.16876, 47.16874, 47.16871, 47.16867, 47.16845, 47.16839, 47.16833, 47.1683, 47.1682, 47.1674, 47.16735, 47.16728, 47.16722, 47.1667, 47.16605, 47.16582, 47.16566, 47.16542, 47.16272, 47.16234, 47.16219, 47.16193, 47.1617, 47.16113, 47.16079, 47.16046, 47.16038, 47.16023, 47.16001, 47.15985, 47.15967, 47.15934, 47.15871, 47.15848, 47.15836, 47.15819, 47.15769, 47.15724, 47.15692, 47.15689, 47.1565, 47.15639, 47.1556, 47.1553, 47.15473, 47.15443, 47.15409, 47.15384, 47.15345, 47.15298, 47.15258, 47.15215, 47.15174, 47.15055, 47.15046, 47.15013, 47.15009, 47.14933, 47.14922, 47.14901, 47.14897, 47.14765, 47.14754, 47.14728, 47.14727, 47.14696, 47.14646, 47.14629, 47.14622, 47.14615));
    ArrayList<Double> lon_route_gara = new ArrayList<Double>(Arrays.asList(27.58479, 27.58495, 27.58497, 27.58495, 27.58492, 27.58482, 27.58467, 27.58415, 27.58491, 27.58707, 27.58717, 27.58731, 27.58751, 27.58766, 27.58773, 27.58785, 27.5879, 27.58795, 27.58795, 27.58792, 27.5878, 27.58778, 27.58806, 27.58819, 27.5884, 27.58869, 27.59012, 27.59028, 27.5921, 27.59217, 27.59217, 27.59213, 27.59133, 27.5914, 27.59173, 27.59465, 27.59494, 27.59508, 27.5949, 27.59444, 27.5935, 27.59194, 27.59122, 27.59115, 27.59046, 27.58828, 27.58739, 27.58329, 27.58116, 27.58093, 27.58051, 27.57896, 27.57875, 27.57822, 27.57745, 27.57727, 27.57705, 27.57705, 27.57702, 27.57695, 27.57686, 27.57672, 27.57655, 27.57646, 27.57638, 27.57634, 27.57634, 27.57639, 27.57627, 27.57621, 27.57617, 27.57612, 27.57592, 27.57548, 27.57486, 27.5748, 27.57479, 27.57481, 27.57581, 27.57606, 27.57608, 27.57612, 27.57595, 27.5745, 27.57434, 27.57407, 27.57385, 27.57259, 27.57091, 27.57125, 27.57135, 27.57149, 27.57302, 27.57321, 27.57326, 27.57331, 27.57331, 27.57319, 27.57305, 27.573, 27.57299, 27.57305, 27.57306, 27.57313, 27.57326, 27.5737, 27.57465, 27.57489, 27.57498, 27.57504, 27.57497, 27.57475, 27.57457, 27.57447, 27.57424, 27.57425, 27.5738, 27.57369, 27.57357, 27.57355, 27.57358, 27.57362, 27.57375, 27.57399, 27.5743, 27.57475, 27.57537, 27.57702, 27.57705, 27.57753, 27.57765, 27.57873, 27.5788, 27.57914, 27.5793, 27.58141, 27.58148, 27.58194, 27.58205, 27.58257, 27.58369, 27.58418, 27.58444, 27.58479));

    ArrayList<ArrayList<Double>> route_gara = new ArrayList<ArrayList<Double>>(Arrays.asList(lat_route_gara, lon_route_gara));
    ArrayList<Double> lat_route_sud = new ArrayList<Double>(Arrays.asList(47.15075, 47.15085, 47.15104, 47.15109, 47.15114, 47.1512, 47.15118, 47.15111, 47.15082, 47.15073, 47.15049, 47.14938, 47.14922, 47.1491, 47.14895, 47.14881, 47.1475, 47.1469, 47.14675, 47.14644, 47.14617, 47.14504, 47.14495, 47.14491, 47.14393, 47.14491, 47.14495, 47.14504, 47.14168, 47.14384, 47.14465, 47.14536, 47.14571, 47.1458, 47.1459, 47.14596, 47.14597, 47.14602, 47.14626, 47.14745, 47.14908, 47.14957, 47.15064));
    ArrayList<Double> lon_route_sud = new ArrayList<Double>(Arrays.asList(27.58794, 27.58789, 27.5879, 27.58793, 27.58807, 27.58834, 27.58845, 27.58868, 27.58956, 27.58989, 27.5906, 27.59401, 27.59447, 27.59478, 27.5954, 27.59619, 27.60568, 27.60971, 27.60964, 27.60954, 27.60925, 27.60688, 27.60696, 27.60688, 27.60477, 27.60688, 27.60696, 27.60688, 27.59978, 27.59657, 27.59547, 27.59452, 27.59418, 27.59413, 27.59413, 27.59405, 27.59397, 27.59384, 27.59345, 27.59209, 27.59005, 27.58939, 27.58803));

    ArrayList<ArrayList<Double>> route_sud = new ArrayList<ArrayList<Double>>(Arrays.asList(lat_route_sud, lon_route_sud));

    ArrayList<Double> lon_route_tatarasi = new ArrayList<Double>(Arrays.asList(27.60116, 27.60149, 27.60391, 27.60582, 27.60838, 27.60965, 27.61121, 27.61212, 27.61247, 27.61315, 27.61337, 27.61401, 27.61493, 27.6152, 27.61844, 27.61856, 27.61978, 27.61994, 27.62016, 27.62054, 27.62076, 27.6215, 27.62217, 27.62348, 27.62361, 27.62368, 27.62406, 27.62429, 27.62482, 27.6249, 27.62507, 27.62535, 27.62545, 27.62545, 27.62542, 27.62536, 27.62515, 27.62488, 27.6246, 27.62273, 27.62232, 27.6219, 27.62182, 27.62162, 27.6214, 27.60432, 27.60425, 27.60444, 27.6044, 27.60436, 27.60425, 27.60325, 27.60304, 27.60117, 27.6011));
    ArrayList<Double> lat_route_tatarasi = new ArrayList<Double>(Arrays.asList(47.15891, 47.159, 47.15948, 47.15982, 47.16006, 47.16015, 47.16014, 47.16017, 47.16021, 47.1601, 47.16, 47.15991, 47.15977, 47.15979, 47.1593, 47.15922, 47.15903, 47.15898, 47.15887, 47.1586, 47.15853, 47.15798, 47.15753, 47.15658, 47.15649, 47.15632, 47.15603, 47.15599, 47.1556, 47.15554, 47.15541, 47.15508, 47.15482, 47.15465, 47.1545, 47.15434, 47.15406, 47.15382, 47.15367, 47.15323, 47.1531, 47.15282, 47.15275, 47.15233, 47.15158, 47.15441, 47.15442, 47.15507, 47.15549, 47.15563, 47.1558, 47.15684, 47.15701, 47.15885, 47.15888));
    ArrayList<ArrayList<Double>> route_tatarasi = new ArrayList<ArrayList<Double>>(Arrays.asList(lat_route_tatarasi, lon_route_tatarasi));

    ArrayList<ArrayList<Object>> ev_list = new ArrayList<>();
    TextView searchBox;
    GeoPoint userCurrentPos;

    simulatedEmergencyVehicle ambulance_vehicle;
    simulatedEmergencyVehicle firetruck_vehicle;
    ConstraintLayout constraintLayout;

    simulatedEmergencyVehicle closestVehicle;
    Context context;
    TableLayout vehicleDetailsTable;
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    GeoPoint simulatedUserStartLoc = new GeoPoint(47.14698676894605, 27.609202948507853);

    SimulatedUser simulatedUser;
    float currentMapAngle = 0f;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 102);
        checkPermission(Manifest.permission.INTERNET, 103);
        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, 104);
        checkPermission(Manifest.permission.POST_NOTIFICATIONS, 105);
        checkPermission(Manifest.permission.FOREGROUND_SERVICE, 106);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_maps);
        context = this;
        map = findViewById(R.id.map);
        floatingButton = findViewById(R.id.menuButton);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        searchBox = findViewById(R.id.searchBox);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Button stuffButton = findViewById(R.id.stuffButton);
        constraintLayout = findViewById(R.id.constraintLayoutMapView);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        vehicleDetailsTable = findViewById(R.id.slideView_table);
        LinearLayout bottomSheet = findViewById(R.id.slideup_sheet);
        ArrayList[] ev_car_data;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                map.postInvalidate(); // Trigger a map view update
                handler.postDelayed(this, 32); // Update the map view every 2 seconds
            }
        }, 32); // Update the map view 30 times a second

        mapController = map.getController();
        mapController.setZoom(15.5);
        mapController.setCenter(simulatedUserStartLoc);

        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        //locationOverlay = new MyLocationNewOverlay(provider, map);
        //locationOverlay.enableFollowLocation();
        //locationOverlay.runOnFirstFix(() -> Log.d("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix())));

        //map.getOverlayManager().add(locationOverlay);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);

        mapOrientationHandler = new Handler();
        distanceToEvHandler = new Handler();
        locationUpdateHandler = new Handler();
        ArrayList<ArrayList<View>> evList = new ArrayList<>();
        navigationView.setNavigationItemSelectedListener(this);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(navigationView);
            }
        });

        startMapOrientation();
        startdistanceToEVChecker();
        startLocationUpdate();

        final int[] count = {0};
        stuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count[0] == 0)
                    addActiveEmergencyToMap(firetruck_vehicle);
                else if (count[0] == 1)
                    addActiveEmergencyToMap(ambulance_vehicle);
                count[0]++;
            }
        });


        ambulance_vehicle = new simulatedEmergencyVehicle("police", route_gara, true, "#90EE90", this, map);
        firetruck_vehicle = new simulatedEmergencyVehicle("firetruck", route_sud, true, "#90EE90", this, map);
        //userCurrentPos = locationOverlay.getMyLocation();


        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        simulatedUser = new SimulatedUser(context,map, simulatedUserStartLoc);
        map.getOverlays().add(simulatedUser);

        map.setOnTouchListener(new View.OnTouchListener() {
            private boolean markerSelected = false;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeoPoint mapOldLoc = (GeoPoint) map.getMapCenter();
                double mapZoomOld = map.getZoomLevelDouble();
                float lastX;
                float lastY;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Check if marker was selected
                    Point screenPoint = new Point((int) event.getX(), (int) event.getY());
                    GeoPoint markerLocation = simulatedUser.getPosition();
                    Point markerPoint = map.getProjection().toPixels(markerLocation, null);
                    int markerWidth = simulatedUser.getIcon().getIntrinsicWidth();
                    int markerHeight = simulatedUser.getIcon().getIntrinsicHeight();

                    RectF markerRect = new RectF(markerPoint.x - markerWidth/2f,
                            markerPoint.y - markerHeight/2f,
                            markerPoint.x + markerWidth/2f,
                            markerPoint.y + markerHeight/2f);

                    if (markerRect.contains(screenPoint.x, screenPoint.y)) {
                        simulatedUser.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        markerSelected = true;
                        lastX = event.getX();
                        lastY = event.getY();
                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (markerSelected) {
                        // Move marker with finger
                        simulatedUser.updateIconColor(Color.MAGENTA);
                        GeoPoint point = (GeoPoint) map.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                        simulatedUser.setPosition(point);
                        mapController.animateTo(point);
                        // Remember last position
                        lastX = event.getX();
                        lastY = event.getY();

                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (markerSelected) {
                        // Change marker size back to normal
                        simulatedUser.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        simulatedUser.setCurrentLocation(simulatedUser.getPosition());
                        mapController.setZoom(mapZoomOld);
                        mapController.setCenter(mapOldLoc);
                        markerSelected = false;
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
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
    ArrayList<simulatedEmergencyVehicle> notified_Stage1 = new ArrayList<>();
    ArrayList<simulatedEmergencyVehicle> notified_Stage2 = new ArrayList<>();
    ArrayList<simulatedEmergencyVehicle> notified_Stage3 = new ArrayList<>();
    void checkIfNotify(simulatedEmergencyVehicle vehicle){
        String vehicleStreet = vehicle.getStreetLoc();
        double distanceToSimUser = distance(simulatedUser.getLocation(), vehicle.getLocation());
        //System.out.println("Distance to sim user: " + distanceToSimUser);
        if (distanceToSimUser > 1000) {
            notified_Stage1.remove(vehicle);
            notified_Stage2.remove(vehicle);
            notified_Stage3.remove(vehicle);
        }

        // check if vehicle is approaching the user
        double headingDiff = vehicle.getHeadingToPoint(simulatedUser.getLocation()) - vehicle.getHeading();
        if (headingDiff > -20 && headingDiff < 20) {
            //System.out.println(capitalize(vehicle.getType()) + " is approaching the simulated user location. Heading diff: "+headingDiff);
            if(vehicle.getIconColor() != Color.GREEN)
                vehicle.updateIconColor(Color.GREEN);
            if (vehicleStreet != null) {
                if (vehicleStreet.equals(simulatedUser.getStreetLoc())) {
                    vehicle.updateIconColor(Color.BLUE);
                    if (distanceToSimUser < 1000 && distanceToSimUser > 500 && !notified_Stage1.contains(vehicle)) {
                        NormalNotification.showNotification(this, "EmergencyWatch Alerts", capitalize(vehicle.getType()) + " is less than 1000 meters away. Prepare to move over.", vehicle.getBitmapIcon());
                        notified_Stage1.add(vehicle);
                    } else if (distanceToSimUser < 500 && distanceToSimUser > 100 && !notified_Stage2.contains(vehicle)) {
                        NormalNotification.showNotification(this, "EmergencyWatch Alerts", capitalize(vehicle.getType()) + " is less than 500 meters away. Prepare to move over.", vehicle.getBitmapIcon());
                        notified_Stage2.add(vehicle);
                    } else if (distanceToSimUser < 100 && !notified_Stage3.contains(vehicle)) {
                        NormalNotification.showNotification(this, "EmergencyWatch Alerts", capitalize(vehicle.getType()) + " is right behind you. Move over immediately", vehicle.getBitmapIcon());
                        notified_Stage3.add(vehicle);
                    }
                }
            }
        } else{
            //System.out.println(capitalize(vehicle.getType()) + " is moving away from the simulated user location. Heading diff: "+headingDiff);
            if(vehicle.getIconColor() != Color.RED)
                vehicle.updateIconColor(Color.RED);
        }
    }

    @SuppressLint("SetTextI18n")
    void updateVehicleDistanceText(){
        userCurrentPos = simulatedUser.getLocation();
        TextView noActiveEmergencies = findViewById(R.id.noActiveEmergencies);
        if (ev_list.size() != 0) {
            noActiveEmergencies.setText(ev_list.size() +" emergencies in your area.");
            for (int i = 0; i < ev_list.size(); i++) {
                simulatedEmergencyVehicle vehicle = (simulatedEmergencyVehicle)ev_list.get(i).get(0);
                checkIfNotify(vehicle);
                TextView distanceText = (TextView) ev_list.get(i).get(1);
                int dist = (int) distance(vehicle.getLocation(), simulatedUser.getLocation()/*userCurrentPos*/);
                distanceText.setText("" + dist + " m");
            }
        }else{
            noActiveEmergencies.setText("No emergencies in your area.");
        }
    }

    @SuppressLint("SetTextI18n")
    void addActiveEmergencyToMap(simulatedEmergencyVehicle vehicle) {
        vehicle.draw();

        System.out.println("Added "+ vehicle.getType()+ " to slide panel");

        LayoutInflater inflater = LayoutInflater.from(this);
        View vehicleDetails = inflater.inflate(R.layout.emergency_vehicle_details, vehicleDetailsTable);
        TextView vehicleType = vehicleDetails.findViewById(R.id.vehicleDetails_type);
        TextView vehicleDistance = vehicleDetails.findViewById(R.id.vehicleDetails_distance);
        TextView vehicleLocation = vehicleDetails.findViewById(R.id.vehicleDetails_location);
        ImageView vehicleIcon = vehicleDetails.findViewById(R.id.vehicleDetails_icon);
        vehicleIcon.setImageDrawable(vehicle.getIcon());

        Bitmap bitmap = drawableToBitmap(vehicle.getIcon());

        // Load the bitmap into the ImageView using Glide
        Glide.with(context)
                .load(bitmap)
                .into(vehicleIcon);


        vehicleType.setText(capitalize(vehicle.getType()));
        vehicleLocation.setText("Street: Fetching...");
        vehicleLocation.setId(View.generateViewId());
        vehicleDistance.setText((int) (distance(vehicle.getLocation(), userCurrentPos)) + "m");
        vehicleDetails.setId(View.generateViewId());
        vehicleType.setId(View.generateViewId());
        vehicleDistance.setId(View.generateViewId());
        vehicleIcon.setId(View.generateViewId());
        userCurrentPos = simulatedUser.getLocation();
        ArrayList<Object> vehicleData = new ArrayList<>();
        vehicleData.add(vehicle);
        vehicleData.add(vehicleDistance);
        vehicleData.add(vehicleLocation);
        ev_list.add(vehicleData);
        updateVehicleDistanceText();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMapOrientation();
        stopdistanceToEVChecker();
        stopLocationUpdate();
    }

    public static int convertDpToPixel(float dp, Context context) {
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    Runnable mapOrientationChecker = new Runnable() {
        @Override
        public void run() {
            try {
                currentMapAngle = map.getMapOrientation();
                if (currentMapAngle > 10f || currentMapAngle < -10f)
                    mapController.animateTo(userCurrentPos, map.getZoomLevelDouble(), null, 0f);

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                int mapOrientationCheckInterval = 3000;
                mapOrientationHandler.postDelayed(mapOrientationChecker, mapOrientationCheckInterval);
            }
        }
    };

    Runnable distanceToEVChecker = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            if(simulatedUser != null)
                userCurrentPos = simulatedUser.getLocation();
            else{
                userCurrentPos = new GeoPoint(0f,0f);
            }
            try {
                if (ev_list != null && ev_list.size() != 0 && userCurrentPos != null) {
                    updateVehicleDistanceText();
                    closestVehicle = (simulatedEmergencyVehicle) ev_list.get(0).get(0);
                    for (int i = 0; i < ev_list.size(); i++) {
                        simulatedEmergencyVehicle new_vehicle =(simulatedEmergencyVehicle) ev_list.get(i).get(0);
                        if (distance(new_vehicle.getLocation(), userCurrentPos) < distance(closestVehicle.getLocation(), userCurrentPos)) {
                            closestVehicle = (simulatedEmergencyVehicle) ev_list.get(i).get(0);
                        }
                        searchBox.setText("Closest vehicle: " + capitalize(closestVehicle.getType()) + " \n Distance: " + (int) distance(closestVehicle.getLocation(), userCurrentPos) +" m");
                       }
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                int interval = 1000;
                distanceToEvHandler.postDelayed(distanceToEVChecker, interval);
            }
        }
    };

    Runnable locationUpdateRunnable = new Runnable() {
        int i = 0;
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            try{
            if (ev_list.size() != 0) {
                if (i < ev_list.size()) {
                    simulatedEmergencyVehicle vehicle = (simulatedEmergencyVehicle)ev_list.get(i).get(0);
                    System.out.println("Request location for "+ vehicle.getType() + " coord: "+ vehicle.getLocation());
                    vehicle.getStreet(new simulatedEmergencyVehicle.StreetCallback() {
                        @Override
                        public void onStreetAvailable(String streetName) {
                            // Use the street name here
                            System.out.println("updated location for "+  vehicle.getType() + "it's location is: "+ streetName);
                            TextView text = (TextView)ev_list.get(i).get(2);
                            text.setText("Street: " + streetName);
                            vehicle.setStreetLoc(streetName);
                            if(i+1 != ev_list.size()){
                                i++;
                            }else{
                                i = 0;
                            }
                        }
                    });
                }
            }

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                int interval = 5000;
                locationUpdateHandler.postDelayed(locationUpdateRunnable, interval);
            }
        }
    };
    void startLocationUpdate() {
        locationUpdateRunnable.run();
    }

    void stopLocationUpdate() {
        locationUpdateHandler.removeCallbacks(locationUpdateRunnable);
    }

    void startdistanceToEVChecker() {
        distanceToEVChecker.run();
    }

    void stopdistanceToEVChecker() {
        distanceToEvHandler.removeCallbacks(distanceToEVChecker);
    }

    void startMapOrientation() {
        mapOrientationChecker.run();
    }

    void stopMapOrientation() {
        mapOrientationHandler.removeCallbacks(mapOrientationChecker);
    }
    public String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //add
        //locationOverlay.enableMyLocation();
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}

