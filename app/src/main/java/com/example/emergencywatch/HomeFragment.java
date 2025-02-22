package com.example.emergencywatch;


import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private final Handler distanceToEvHandler = new Handler();
    private final Handler locationUpdateHandler = new Handler();
    private final Handler userLocationUpdateHandler = new Handler();
    private final Handler updateNotificationHandler = new Handler();
    public MyLocationNewOverlay locationOverlay;
    public DrawerLayout drawerLayout;
    MapView map = null;
    IMapController mapController;
    ArrayList<Double> lat_route_gara = new ArrayList<>(Arrays.asList(47.14615, 47.14613, 47.14659, 47.14679, 47.14688, 47.14701, 47.14711, 47.1473, 47.14823, 47.15079, 47.15085, 47.1509, 47.15087, 47.1509, 47.15092, 47.15099, 47.15104, 47.15114, 47.15126, 47.15132, 47.15144, 47.15154, 47.15187, 47.15204, 47.1525, 47.15304, 47.15527, 47.15552, 47.15836, 47.15853, 47.15864, 47.15877, 47.16031, 47.16048, 47.16075, 47.16164, 47.16174, 47.16179, 47.16198, 47.16271, 47.16344, 47.16416, 47.1644, 47.16445, 47.1655, 47.16604, 47.16625, 47.16732, 47.16785, 47.16791, 47.16807, 47.16867, 47.16875, 47.16901, 47.16932, 47.16941, 47.1696, 47.16974, 47.16979, 47.16985, 47.16989, 47.16991, 47.1699, 47.16986, 47.16978, 47.1697, 47.16964, 47.16953, 47.16946, 47.16936, 47.16884, 47.16875, 47.16872, 47.16875, 47.16876, 47.16874, 47.16871, 47.16867, 47.16845, 47.16839, 47.16833, 47.1683, 47.1682, 47.1674, 47.16735, 47.16728, 47.16722, 47.1667, 47.16605, 47.16582, 47.16566, 47.16542, 47.16272, 47.16234, 47.16219, 47.16193, 47.1617, 47.16113, 47.16079, 47.16046, 47.16038, 47.16023, 47.16001, 47.15985, 47.15967, 47.15934, 47.15871, 47.15848, 47.15836, 47.15819, 47.15769, 47.15724, 47.15692, 47.15689, 47.1565, 47.15639, 47.1556, 47.1553, 47.15473, 47.15443, 47.15409, 47.15384, 47.15345, 47.15298, 47.15258, 47.15215, 47.15174, 47.15055, 47.15046, 47.15013, 47.15009, 47.14933, 47.14922, 47.14901, 47.14897, 47.14765, 47.14754, 47.14728, 47.14727, 47.14696, 47.14646, 47.14629, 47.14622, 47.14615));
    ArrayList<Double> lon_route_gara = new ArrayList<>(Arrays.asList(27.58479, 27.58495, 27.58497, 27.58495, 27.58492, 27.58482, 27.58467, 27.58415, 27.58491, 27.58707, 27.58717, 27.58731, 27.58751, 27.58766, 27.58773, 27.58785, 27.5879, 27.58795, 27.58795, 27.58792, 27.5878, 27.58778, 27.58806, 27.58819, 27.5884, 27.58869, 27.59012, 27.59028, 27.5921, 27.59217, 27.59217, 27.59213, 27.59133, 27.5914, 27.59173, 27.59465, 27.59494, 27.59508, 27.5949, 27.59444, 27.5935, 27.59194, 27.59122, 27.59115, 27.59046, 27.58828, 27.58739, 27.58329, 27.58116, 27.58093, 27.58051, 27.57896, 27.57875, 27.57822, 27.57745, 27.57727, 27.57705, 27.57705, 27.57702, 27.57695, 27.57686, 27.57672, 27.57655, 27.57646, 27.57638, 27.57634, 27.57634, 27.57639, 27.57627, 27.57621, 27.57617, 27.57612, 27.57592, 27.57548, 27.57486, 27.5748, 27.57479, 27.57481, 27.57581, 27.57606, 27.57608, 27.57612, 27.57595, 27.5745, 27.57434, 27.57407, 27.57385, 27.57259, 27.57091, 27.57125, 27.57135, 27.57149, 27.57302, 27.57321, 27.57326, 27.57331, 27.57331, 27.57319, 27.57305, 27.573, 27.57299, 27.57305, 27.57306, 27.57313, 27.57326, 27.5737, 27.57465, 27.57489, 27.57498, 27.57504, 27.57497, 27.57475, 27.57457, 27.57447, 27.57424, 27.57425, 27.5738, 27.57369, 27.57357, 27.57355, 27.57358, 27.57362, 27.57375, 27.57399, 27.5743, 27.57475, 27.57537, 27.57702, 27.57705, 27.57753, 27.57765, 27.57873, 27.5788, 27.57914, 27.5793, 27.58141, 27.58148, 27.58194, 27.58205, 27.58257, 27.58369, 27.58418, 27.58444, 27.58479));
    ArrayList<ArrayList<Double>> route_gara = new ArrayList<>(Arrays.asList(lat_route_gara, lon_route_gara));
    ArrayList<Double> lat_route_sud = new ArrayList<>(Arrays.asList(47.15075, 47.15085, 47.15104, 47.15109, 47.15114, 47.1512, 47.15118, 47.15111, 47.15082, 47.15073, 47.15049, 47.14938, 47.14922, 47.1491, 47.14895, 47.14881, 47.1475, 47.1469, 47.14675, 47.14644, 47.14617, 47.14504, 47.14495, 47.14491, 47.14393, 47.14491, 47.14495, 47.14504, 47.14168, 47.14384, 47.14465, 47.14536, 47.14571, 47.1458, 47.1459, 47.14596, 47.14597, 47.14602, 47.14626, 47.14745, 47.14908, 47.14957, 47.15064));
    ArrayList<Double> lon_route_sud = new ArrayList<>(Arrays.asList(27.58794, 27.58789, 27.5879, 27.58793, 27.58807, 27.58834, 27.58845, 27.58868, 27.58956, 27.58989, 27.5906, 27.59401, 27.59447, 27.59478, 27.5954, 27.59619, 27.60568, 27.60971, 27.60964, 27.60954, 27.60925, 27.60688, 27.60696, 27.60688, 27.60477, 27.60688, 27.60696, 27.60688, 27.59978, 27.59657, 27.59547, 27.59452, 27.59418, 27.59413, 27.59413, 27.59405, 27.59397, 27.59384, 27.59345, 27.59209, 27.59005, 27.58939, 27.58803));
    ArrayList<Double> lat_gps = new ArrayList<>(Arrays.asList(47.14698, 47.14696, 47.14691, 47.14664, 47.14648, 47.14639, 47.14634, 47.14625, 47.14652, 47.14666, 47.14672, 47.14682, 47.14697, 47.14701, 47.14706, 47.14711, 47.14717, 47.14742, 47.14757, 47.1477, 47.14779, 47.14794, 47.14807, 47.14809, 47.14829, 47.14859, 47.14937, 47.14986, 47.15044, 47.15112, 47.15141, 47.15224, 47.15244, 47.15324, 47.15332, 47.15334, 47.15366, 47.15375, 47.1539, 47.15408, 47.15423, 47.15445, 47.15481, 47.15494, 47.1552, 47.15533, 47.15544, 47.1556, 47.15577, 47.15586, 47.15604, 47.15645, 47.157, 47.15761, 47.15778, 47.15828, 47.15879, 47.15879, 47.15882, 47.15889, 47.1588, 47.15877, 47.15875, 47.15874, 47.15873, 47.15872, 47.15874, 47.15875, 47.15877, 47.1588, 47.15885, 47.15887, 47.15913, 47.15914, 47.15922, 47.15935, 47.15956, 47.15982, 47.16001, 47.16026, 47.16038, 47.16046, 47.16053, 47.1606, 47.16073, 47.1609, 47.16098, 47.16124, 47.16134, 47.16146, 47.16176, 47.16187, 47.16194, 47.16224, 47.16237, 47.16264, 47.16268, 47.16308, 47.16333, 47.16341, 47.16352, 47.16416, 47.16423, 47.16437, 47.16438, 47.16442, 47.16445, 47.16452, 47.16466, 47.16505, 47.1654, 47.16554, 47.16557, 47.16595, 47.16607, 47.16613, 47.1665, 47.16666, 47.1669, 47.16742, 47.16777, 47.16827, 47.16869, 47.16884, 47.16904, 47.16921, 47.1694, 47.16961, 47.16962, 47.16982, 47.16998, 47.17004, 47.1707, 47.17087, 47.17125, 47.17158, 47.17177, 47.17191, 47.17197, 47.17208, 47.1722, 47.17234, 47.17243, 47.17259, 47.1727, 47.173, 47.17316, 47.17348, 47.17375, 47.17382, 47.17396, 47.17433, 47.17443, 47.17456, 47.17467, 47.17504, 47.17529, 47.1754, 47.17577, 47.17618, 47.17645, 47.1765, 47.17659, 47.17671, 47.17678, 47.17696, 47.17677, 47.17625, 47.17619));
    ArrayList<Double> lon_gps = new ArrayList<>(Arrays.asList(27.6092, 27.60935, 27.60967, 27.60961, 27.60955, 27.6095, 27.60943, 27.60948, 27.61006, 27.6102, 27.61025, 27.61029, 27.61036, 27.61003, 27.60971, 27.60944, 27.60901, 27.60723, 27.60617, 27.60525, 27.60456, 27.60344, 27.60251, 27.60234, 27.60239, 27.60247, 27.6027, 27.60285, 27.60301, 27.60322, 27.60332, 27.60358, 27.60364, 27.60388, 27.6039, 27.60391, 27.60401, 27.60403, 27.60408, 27.60414, 27.60419, 27.60425, 27.60436, 27.60439, 27.60442, 27.60441, 27.60438, 27.60432, 27.60422, 27.60415, 27.60399, 27.60358, 27.60304, 27.60242, 27.60225, 27.60176, 27.60123, 27.60122, 27.60112, 27.60105, 27.60086, 27.60081, 27.60075, 27.60068, 27.6006, 27.60053, 27.60042, 27.60038, 27.60033, 27.60026, 27.60024, 27.60022, 27.59982, 27.59974, 27.59964, 27.59947, 27.59913, 27.59866, 27.59826, 27.59766, 27.59737, 27.59719, 27.59701, 27.59685, 27.59654, 27.59625, 27.59611, 27.59573, 27.59559, 27.59555, 27.59518, 27.59508, 27.59503, 27.5948, 27.59472, 27.59453, 27.5945, 27.59405, 27.5937, 27.59356, 27.59334, 27.59194, 27.59179, 27.59147, 27.59144, 27.59136, 27.59129, 27.59123, 27.5911, 27.59085, 27.59064, 27.59054, 27.59051, 27.59025, 27.59016, 27.59006, 27.58981, 27.5897, 27.58955, 27.58923, 27.58902, 27.58872, 27.58838, 27.58824, 27.58805, 27.58786, 27.58761, 27.58723, 27.5872, 27.58688, 27.58668, 27.58662, 27.586, 27.58581, 27.58537, 27.58507, 27.58492, 27.58484, 27.58482, 27.5848, 27.58476, 27.58465, 27.58448, 27.58428, 27.58409, 27.58361, 27.58337, 27.5829, 27.58258, 27.5825, 27.58236, 27.58205, 27.58195, 27.58182, 27.58172, 27.58131, 27.58099, 27.58086, 27.58042, 27.57985, 27.57943, 27.57933, 27.57914, 27.57875, 27.57848, 27.57797, 27.57771, 27.57682, 27.57666));
    ArrayList<ArrayList<Double>> route_sud = new ArrayList<>(Arrays.asList(lat_route_sud, lon_route_sud));
    ArrayList<Double> lon_route_tatarasi = new ArrayList<>(Arrays.asList(27.60116, 27.60149, 27.60391, 27.60582, 27.60838, 27.60965, 27.61121, 27.61212, 27.61247, 27.61315, 27.61337, 27.61401, 27.61493, 27.6152, 27.61844, 27.61856, 27.61978, 27.61994, 27.62016, 27.62054, 27.62076, 27.6215, 27.62217, 27.62348, 27.62361, 27.62368, 27.62406, 27.62429, 27.62482, 27.6249, 27.62507, 27.62535, 27.62545, 27.62545, 27.62542, 27.62536, 27.62515, 27.62488, 27.6246, 27.62273, 27.62232, 27.6219, 27.62182, 27.62162, 27.6214, 27.60432, 27.60425, 27.60444, 27.6044, 27.60436, 27.60425, 27.60325, 27.60304, 27.60117, 27.6011));
    ArrayList<Double> lat_route_tatarasi = new ArrayList<>(Arrays.asList(47.15891, 47.159, 47.15948, 47.15982, 47.16006, 47.16015, 47.16014, 47.16017, 47.16021, 47.1601, 47.16, 47.15991, 47.15977, 47.15979, 47.1593, 47.15922, 47.15903, 47.15898, 47.15887, 47.1586, 47.15853, 47.15798, 47.15753, 47.15658, 47.15649, 47.15632, 47.15603, 47.15599, 47.1556, 47.15554, 47.15541, 47.15508, 47.15482, 47.15465, 47.1545, 47.15434, 47.15406, 47.15382, 47.15367, 47.15323, 47.1531, 47.15282, 47.15275, 47.15233, 47.15158, 47.15441, 47.15442, 47.15507, 47.15549, 47.15563, 47.1558, 47.15684, 47.15701, 47.15885, 47.15888));
    ArrayList<ArrayList<Double>> route_tatarasi = new ArrayList<>(Arrays.asList(lat_route_tatarasi, lon_route_tatarasi));
    ArrayList<ArrayList<Object>> ev_list = new ArrayList<>();
    TextView searchBox;
    GeoPoint userCurrentPos;
    SimulatedEmergencyVehicle ambulance_vehicle;
    SimulatedEmergencyVehicle firetruck_vehicle;
    ConstraintLayout constraintLayout;
    SimulatedEmergencyVehicle closestVehicle;
    Context context;
    TableLayout vehicleDetailsTable;
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    LinearLayout bottomSheet;
    GeoPoint simulatedUserStartLoc = new GeoPoint(47.14698676894605, 27.609202948507853);
    SimulatedUser simulatedUser;
    float currentMapAngle = 0f;
    TTSNotificationManager ttsNotificationManager;
    View view;
    Double destLat, destLon;
    Overlay activeRoute;
    TextView emergencyActiveTitle;
    ArrayList<double[]> coordinates;
    TextView emergencyActiveInfo;
    TextView noEmgTitlePanel;
    Button settingsButton;
    Button centerMapButton;
    Marker finishMarker;
    ArrayList<Marker> globalPointsToCheck = new ArrayList<>();
    Button straightenMap;
    int activeRouteTimeToTravel;
    SimulatedEmergencyVehicle chosenVehicle;
    ArrayList<Marker> vehicleLocationEstimations = new ArrayList<>();
    ArrayList<SimulatedEmergencyVehicle> notified_Stage1 = new ArrayList<>();
    ArrayList<SimulatedEmergencyVehicle> notified_Stage2 = new ArrayList<>();
    ArrayList<SimulatedEmergencyVehicle> notified_Stage3 = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private final Runnable updateNotificationRunnable = new Runnable() {
        @Override
        public void run() {
            if (closestVehicle != null) {
                updatePermanentNotification("EmergencyWatch", "Closest vehicle: " + capitalize(closestVehicle.getType()) + ". \nDistance: " + (int) distance(closestVehicle.getLocation(), userCurrentPos) + " m", R.drawable.baseline_running_with_errors_24);
                updateNotificationHandler.postDelayed(this, 3000); // Repeat every 3000 milliseconds
            } else {
                updatePermanentNotification("EmergencyWatch", "No active emergencies in your area.", R.drawable.baseline_running_with_errors_24);
            }
        }
    };
    public HomeFragment() {
        // Required empty public constructor
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        context = ctx;
        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        map = view.findViewById(R.id.map);
        //floatingButton = view.findViewById(R.id.menuButton);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        searchBox = view.findViewById(R.id.searchBoxNav);
        constraintLayout = view.findViewById(R.id.constraintLayoutMapView);
        vehicleDetailsTable = view.findViewById(R.id.slideView_table);
        bottomSheet = view.findViewById(R.id.slideup_sheet);
        emergencyActiveTitle = view.findViewById(R.id.slidePanel_title_activeEMG);
        noEmgTitlePanel = view.findViewById(R.id.slidePanel_title_noEMG);
        emergencyActiveInfo = view.findViewById(R.id.activeEmergencyInfo);
        emergencyActiveTitle.setVisibility(View.INVISIBLE);
        emergencyActiveInfo.setVisibility(View.INVISIBLE);
        TableLayout locationSuggestions;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15.5);
        mapController.setCenter(simulatedUserStartLoc);
        settingsButton = view.findViewById(R.id.settings_floating_button);
        centerMapButton = view.findViewById(R.id.center_floating_button);
        finishMarker = new Marker(map);
        straightenMap = view.findViewById(R.id.straigthen_floating_button);

        final Handler mapUpdateHandler = new Handler();
        mapUpdateHandler.postDelayed(new Runnable() {
            public void run() {
                map.postInvalidate(); // Trigger a map view update
                mapUpdateHandler.postDelayed(this, 32);
            }
        }, 32); // Update the map view 30 times a second


        //live location settings
        GpsMyLocationProvider provider = new GpsMyLocationProvider(context);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(provider, map);
        locationOverlay.runOnFirstFix(() -> Log.d("MyTag", String.format("First location fix: %s", locationOverlay.getLastFix())));

        SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = (prefs, key) -> {

            if (key.equals("simulatedUserPos") && !prefs.getBoolean("simulatedUserPos", true)) {
                map.getOverlayManager().add(locationOverlay);
                simulatedUser.useLiveLocation(locationOverlay);
                locationOverlay.enableFollowLocation();
                locationOverlay.setEnabled(true);
                map.onResume();
                map.invalidate();
            }

            if (key.equals("simulatedUserPos") && prefs.getBoolean("simulatedUserPos", true)) {
                map.getOverlayManager().remove(locationOverlay);
                simulatedUser.useSimulatedLocation();
                locationOverlay.disableFollowLocation();
                locationOverlay.setEnabled(false);
                mapController.animateTo(simulatedUser.getLocation());
                map.invalidate();
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);


        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);
        locationSuggestions = view.findViewById(R.id.suggestionTextLayout);

        startUserLocUpdate();
        startdistanceToEVChecker();
        startLocationUpdate();

        final int[] count = {0};
        Button stuffButton = view.findViewById(R.id.stuffButton);
        stuffButton.setOnClickListener(view -> {
            if (count[0] == 0) {
                firetruck_vehicle = new SimulatedEmergencyVehicle("firetruck", route_sud, sharedPreferences.getBoolean("vehicleRoutes", false), "#9F2B68", Integer.parseInt(sharedPreferences.getString("vehicleSpeed", "120")), context, map);
                addActiveEmergencyToMap(firetruck_vehicle);

            } else if (count[0] == 1) {
                ambulance_vehicle = new SimulatedEmergencyVehicle("police", route_gara, sharedPreferences.getBoolean("vehicleRoutes", false), "#9F2B68", Integer.parseInt(sharedPreferences.getString("vehicleSpeed", "120")), context, map);
                addActiveEmergencyToMap(ambulance_vehicle);
            }
            count[0]++;
        });

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        // create simulatedUser object and add it to map
        simulatedUser = new SimulatedUser(context, map, simulatedUserStartLoc, this);
        map.getOverlays().add(simulatedUser);

        map.setOnTouchListener(new View.OnTouchListener() {
            private boolean markerSelected = false;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeoPoint mapOldLoc = (GeoPoint) map.getMapCenter();
                double mapZoomOld = map.getZoomLevelDouble();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Check if marker was selected
                    Point screenPoint = new Point((int) event.getX(), (int) event.getY());
                    GeoPoint markerLocation = simulatedUser.getPosition();
                    Point markerPoint = map.getProjection().toPixels(markerLocation, null);
                    int markerWidth = simulatedUser.getIcon().getIntrinsicWidth();
                    int markerHeight = simulatedUser.getIcon().getIntrinsicHeight();

                    RectF markerRect = new RectF(markerPoint.x - markerWidth / 2f,
                            markerPoint.y - markerHeight / 2f,
                            markerPoint.x + markerWidth / 2f,
                            markerPoint.y + markerHeight / 2f);

                    if (markerRect.contains(screenPoint.x, screenPoint.y)) {
                        simulatedUser.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        markerSelected = true;
                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (markerSelected) {
                        // Move marker with finger
                        simulatedUser.updateIconColor(Color.MAGENTA);
                        mapController.animateTo(map.getMapCenter(), map.getZoomLevelDouble(), 0L, 0f);
                        GeoPoint point = (GeoPoint) map.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                        simulatedUser.setPosition(point);
                        mapController.animateTo(point);

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

        ttsNotificationManager = new TTSNotificationManager(context);

        settingsButton.setOnClickListener(view -> {
            assert mainActivity != null;
            mainActivity.showSettings();
        });

        centerMapButton.setOnClickListener(view -> {
            mapController.animateTo(simulatedUser.getLocation(), 18.0, 1500L, 0f);
            //mapController.animateTo(simulatedUser.getLocation());
        });


        straightenMap.setOnClickListener(view -> {
            currentMapAngle = map.getMapOrientation();
            mapController.animateTo(map.getMapCenter(), map.getZoomLevelDouble(), 1500L, 0f);
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler();
            private final Handler vehicleIntersectionHandler = new Handler();
            private Runnable runnable;
            private Runnable vehicleIntersectionRunnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                locationSuggestions.removeAllViews();

                if (charSequence.length() >= 3) {
                    LayoutInflater inflater = LayoutInflater.from(view.getContext());
                    View locationInfo = inflater.inflate(R.layout.location_data_list, locationSuggestions, false);
                    locationInfo.setId(View.generateViewId());
                    TextView locationName = locationInfo.findViewById(R.id.LocationName);
                    locationName.setId(View.generateViewId());
                    TextView locationAddress = locationInfo.findViewById(R.id.LocationAddress);
                    locationAddress.setId(View.generateViewId());
                    TextView locationDistance = locationInfo.findViewById(R.id.LocationDistance);
                    locationDistance.setId(View.generateViewId());

                    locationDistance.setText("");
                    locationAddress.setText("");
                    locationName.setText("Searching...");
                    locationSuggestions.addView(locationInfo); // Add the view to the ViewGroup after setting the OnClickListener

                }


                handler.removeCallbacks(runnable); // Remove any previously scheduled runnable
                runnable = new Runnable() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void run() {
                        if (charSequence.length() >= 3) {
                            System.out.println("am scris in textbox lmao ce nice");
                            getLocationSuggestions(locationsData -> {
                                System.out.println(locationsData.size());
                                if (locationsData.size() == 0) {
                                    locationSuggestions.removeAllViews();
                                    LayoutInflater inflater = LayoutInflater.from(view.getContext());
                                    View locationInfo = inflater.inflate(R.layout.location_data_list, locationSuggestions, false);
                                    locationInfo.setId(View.generateViewId());
                                    TextView locationName = locationInfo.findViewById(R.id.LocationName);
                                    locationName.setId(View.generateViewId());
                                    TextView locationAddress = locationInfo.findViewById(R.id.LocationAddress);
                                    locationAddress.setId(View.generateViewId());
                                    TextView locationDistance = locationInfo.findViewById(R.id.LocationDistance);
                                    locationDistance.setId(View.generateViewId());
                                    locationDistance.setText("");
                                    locationAddress.setText("");
                                    locationName.setText("No results found");
                                    locationSuggestions.addView(locationInfo); // Add the view to the ViewGroup after setting the OnClickListener

                                } else {
                                    int limit = Math.min(locationsData.size(), 6);
                                    locationSuggestions.removeAllViews();

                                    for (int j = 0; j < limit; j++) {
                                        LayoutInflater inflater = LayoutInflater.from(view.getContext());
                                        View locationInfo = inflater.inflate(R.layout.location_data_list, locationSuggestions, false);
                                        locationInfo.setId(View.generateViewId());
                                        TextView locationName = locationInfo.findViewById(R.id.LocationName);
                                        locationName.setId(View.generateViewId());
                                        TextView locationAddress = locationInfo.findViewById(R.id.LocationAddress);
                                        locationAddress.setId(View.generateViewId());
                                        TextView locationDistance = locationInfo.findViewById(R.id.LocationDistance);
                                        locationDistance.setId(View.generateViewId());


                                        String[] locationDataExpanded = locationsData.get(j).get(2).split(",", 2);
                                        locationName.setText(locationDataExpanded[0]);

                                        int lastCommaIndex = locationDataExpanded[1].lastIndexOf(",");
                                        int secondToLastCommaIndex = locationDataExpanded[1].lastIndexOf(",", lastCommaIndex - 1);

                                        String address = locationDataExpanded[1].substring(0, secondToLastCommaIndex);

                                        locationAddress.setText(address);
                                        double lat = Double.parseDouble(locationsData.get(j).get(0));
                                        double lon = Double.parseDouble(locationsData.get(j).get(1));
                                        double distanceToPOI = distance(new GeoPoint(lat, lon), simulatedUserStartLoc);
                                        locationDistance.setText(String.format("%.2f km", distanceToPOI / 1000));

                                        final int index = j;
                                        locationInfo.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                System.out.println("Location details: " + locationsData.get(index));
                                                destLat = Double.parseDouble(locationsData.get(index).get(0));
                                                destLon = Double.parseDouble(locationsData.get(index).get(1));

                                                getRouteUser2Point(routeData -> {
                                                    clearMap();
                                                    coordinates = (ArrayList<double[]>) routeData.get(1);

                                                    System.out.println(Arrays.toString(coordinates.get(0)));
                                                    System.out.println(Arrays.toString(coordinates.get(1)));
                                                    activeRoute = drawRouteReal(coordinates, "#00FFFF");
                                                    map.getOverlays().add(activeRoute);

                                                    finishMarker.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_flag_circle_24, null));
                                                    finishMarker.setPosition(new GeoPoint(coordinates.get(0)[coordinates.get(0).length - 1], coordinates.get(1)[coordinates.get(1).length - 1]));
                                                    finishMarker.setAnchor(0.5f, 0.5f);
                                                    map.getOverlays().add(finishMarker);

                                                    GeoPoint start = simulatedUser.getLocation();
                                                    GeoPoint end = new GeoPoint(destLat, destLon);

                                                    BoundingBox b = getBoundingBox(start, end);
                                                    map.zoomToBoundingBox(b, true, 100);
                                                    map.invalidate();
                                                    locationSuggestions.removeAllViews();
                                                    addActiveEmergencyToMap(simulatedUser.simUserStartDrivingToLocation(coordinates, sharedPreferences, context, map));

                                                    vehicleIntersectionRunnable = new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (coordinates != null) {
                                                                managePossibleIntersections(coordinates);
                                                                vehicleIntersectionHandler.postDelayed(this, 5000);
                                                            }
                                                        }
                                                    };
                                                    vehicleIntersectionHandler.postDelayed(vehicleIntersectionRunnable, 5000);

                                                });
                                            }
                                        });

                                        locationSuggestions.addView(locationInfo); // Add the view to the ViewGroup after setting the OnClickListener

                                    }
                                }
                            });

                        }
                    }
                };
                handler.postDelayed(runnable, 2000); // Schedule the new runnable with a 3-second delay
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // Inflate the layout for context fragment
        return view;
    }

    private BoundingBox getBoundingBox(GeoPoint origin, GeoPoint destination) {
        double north = Math.max(origin.getLatitude(), destination.getLatitude());
        double south = Math.min(origin.getLatitude(), destination.getLatitude());
        double east = Math.max(origin.getLongitude(), destination.getLongitude());
        double west = Math.min(origin.getLongitude(), destination.getLongitude());

        return new BoundingBox(north, east, south, west);
    }

    public void clearMap() {
        try {
            coordinates = null;
            map.getOverlayManager().remove(activeRoute);
            map.getOverlayManager().remove(finishMarker);
            for (Marker marker : globalPointsToCheck) {
                map.getOverlayManager().remove(marker);
            }
        } catch (Exception e) {
            //nope
        }
    }
    void managePossibleIntersections(ArrayList<double[]> route_coordinates) {
        //get possible intersection point
        ArrayList<Double> lat = new ArrayList<>();
        ArrayList<Double> lon = new ArrayList<>();
        for (double point : route_coordinates.get(0)) {
            lat.add(point);
        }
        for (double point : route_coordinates.get(1)) {
            lon.add(point);
        }
        ArrayList<ArrayList<Double>> routePoints = new ArrayList<>();
        routePoints.add(lat);
        routePoints.add(lon);
        if (ev_list.size() != 0) {
            for (int i = 0; i < ev_list.size(); i++) {
                SimulatedEmergencyVehicle vehicle = (SimulatedEmergencyVehicle) ev_list.get(i).get(0);
                System.out.println("Checking " + vehicle.getType());
                if (!vehicle.getReachedDest()) {
                    if (vehicle.getDest() == null) {
                        vehicle.setDest(verifyVehicleIntersections(routePoints, vehicle));
                    }
                    if (vehicle.getDest() != null) {
                        System.out.println("Possible intersection for " + vehicle.getType() + " is " + vehicle.getDest());
                        chosenVehicle = vehicle;
                        destLat = vehicle.getDest().getLatitude();
                        destLon = vehicle.getDest().getLongitude();
                        System.out.println(vehicle.getType() + " distance to dest " + distance(vehicle.getPosition(), vehicle.getDest()));
                        if (distance(vehicle.getPosition(), vehicle.getDest()) < 800 && distance(vehicle.getDest(), simulatedUser.getLocation()) > 100) {
                            //get route to that point and plot it
                            getRouteVehicle2Point(routeData -> {
                                if (routeData.get(1) != null) {
                                    // check if time for user and vehicle are close
                                    getRouteUser2Point(routeData2 -> {

                                        int vehicleRouteLength = (int) routeData.get(2);
                                        double vehicleSpeedMs = vehicle.getSpeedMs();
                                        double vehicleTravelTime = vehicleRouteLength / vehicleSpeedMs;
                                        int userRouteLength = (int) routeData2.get(2);
                                        double userVehicleSpeedMs = Integer.parseInt(sharedPreferences.getString("userVehicleSpeed", "120")) * (1000.0 / 3600.0);
                                        double userTravelTime = userRouteLength / userVehicleSpeedMs;


                                        //check time of arrival, if time is bad stop checking this car
                                        System.out.println("MATH: vehicle travel time: " + vehicleTravelTime + " <-> user travel time " + userTravelTime);
                                        if (abs(vehicleTravelTime - userTravelTime) <= 15) {
                                            float minutesToIntersetions = (float) userTravelTime / 60;
                                            @SuppressLint("DefaultLocale") String minutesToIntesectionString = String.format("%.2f", minutesToIntersetions);
                                            vehicle.setTravelTimeOnActiveRoute((Integer) routeData.get(0));
                                            vehicle.setOnActiveRoute(true);
                                            vehicle.updateIconColor(Color.BLUE);
                                            if (notified_Stage3.contains(vehicle)) {
                                                System.out.println(vehicle.getType() + " already notified about possible intersection");
                                            } else {
                                                ttsNotificationManager.createTTSNotification(capitalize("Possible intersection with a " + vehicle.getType() + " vehicle on your route in approximately " + minutesToIntesectionString + "minutes if you maintain " + Integer.parseInt(sharedPreferences.getString("userVehicleSpeed", "120")) + " kilometers per hour average speed"));
                                                notified_Stage3.add(vehicle);
                                            }
                                        } else {
                                            vehicle.setOnActiveRoute(false);
                                        }
                                    });

                                }
                            });
                        }
                    }
                } else {
                    vehicle.updateIconColor(Color.BLACK);
                }
            }
        }


    }
    public Polyline drawRouteReal(ArrayList<double[]> route, String colorString) {

        double[] lat = route.get(0);
        double[] lon = route.get(1);
        System.out.println(Arrays.toString(lat));
        System.out.println(Arrays.toString(lon));
        Polyline myPath = new Polyline();
        myPath.setWidth(10f);
        myPath.setColor(Color.parseColor(colorString));


        for (int i = 0; i < lat.length; i++) {
            myPath.addPoint(new GeoPoint(lat[i], lon[i]));
        }

        return (myPath);
    }

    public GeoPoint verifyVehicleIntersections(ArrayList<ArrayList<Double>> coordinates, SimulatedEmergencyVehicle vehicle) {
        ArrayList<Marker> pointsToCheck = new ArrayList<>();
        for (int j = 0; j < coordinates.get(0).size(); j += 10) {
            double routeLat = coordinates.get(0).get(j);
            double routeLon = coordinates.get(1).get(j);
            double headingDiff = vehicle.getHeadingToPoint(new GeoPoint(routeLat, routeLon)) - vehicle.getHeading();
            if (distance(vehicle.getLocation(), new GeoPoint(routeLat, routeLon)) < 800 && headingDiff < 10 && headingDiff > -10) {
                Marker pointToCheck = new Marker(map);
                pointToCheck.setIcon(ResourcesCompat.getDrawable(context.getResources(), R.drawable.baseline_radio_button_checked_24, null));
                pointToCheck.setPosition(new GeoPoint(routeLat, routeLon));
                pointToCheck.setAnchor(0.5f, 0.5f);
                pointsToCheck.add(pointToCheck);
                globalPointsToCheck.add(pointToCheck);
                map.getOverlays().add(pointToCheck);
            }
        }
        if (pointsToCheck.size() > 1) {
            GeoPoint closestPoint = new GeoPoint(0d, 0d);
            for (Marker pointToCheck : pointsToCheck) {
                if (distance(vehicle.getPosition(), pointToCheck.getPosition()) < distance(closestPoint, vehicle.getPosition())) {
                    closestPoint = pointToCheck.getPosition();
                }

            }
            return closestPoint;
        } else {
            if (pointsToCheck.size() != 0)
                return pointsToCheck.get(0).getPosition();
            else
                return null;
        }
    }

    public void getRouteUser2Point(RouteCallback callback) {
        new HttpRequests("routing", new ArrayList<>(Arrays.asList(simulatedUser.getLocation(), new GeoPoint(destLat, destLon))), null, response -> {
            ArrayList<double[]> coordinates = new ArrayList<>();
            ArrayList<Object> routeData = new ArrayList<>();
            int travelTime;
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject routeObject = routesArray.getJSONObject(0);
                JSONArray legsArray = routeObject.getJSONArray("legs");
                JSONObject legObject = legsArray.getJSONObject(0);
                JSONArray pointsArray = legObject.getJSONArray("points");

                JSONObject summaryObj = routeObject.getJSONObject("summary");
                travelTime = summaryObj.getInt("noTrafficTravelTimeInSeconds");
                int lengthInMeters = summaryObj.getInt("lengthInMeters");
                routeData.add(travelTime);
                int length = pointsArray.length();
                double[] latitudes = new double[length];
                double[] longitudes = new double[length];

                for (int i = 0; i < length; i++) {
                    JSONObject pointObject = pointsArray.getJSONObject(i);
                    double latitude = pointObject.getDouble("latitude");
                    double longitude = pointObject.getDouble("longitude");
                    latitudes[i] = latitude;
                    longitudes[i] = longitude;
                }
                coordinates.add(latitudes);
                coordinates.add(longitudes);
                routeData.add(coordinates);
                routeData.add(lengthInMeters);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                callback.onRouteAvailable(routeData);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getRouteVehicle2Point(RouteCallback callback) {
        new HttpRequests("routingVehicle", new ArrayList<>(Arrays.asList(chosenVehicle.getPosition(), new GeoPoint(destLat, destLon))), null, response -> {
            ArrayList<double[]> coordinates = new ArrayList<>();
            ArrayList<Object> routeData = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject routeObject = routesArray.getJSONObject(0);
                JSONArray legsArray = routeObject.getJSONArray("legs");
                JSONObject legObject = legsArray.getJSONObject(0);
                JSONArray pointsArray = legObject.getJSONArray("points");

                JSONObject summaryObj = routeObject.getJSONObject("summary");
                int travelTime = summaryObj.getInt("noTrafficTravelTimeInSeconds");
                int lengthInMeters = summaryObj.getInt("lengthInMeters");
                routeData.add(travelTime);
                int length = pointsArray.length();
                double[] latitudes = new double[length];
                double[] longitudes = new double[length];

                for (int i = 0; i < length; i++) {
                    JSONObject pointObject = pointsArray.getJSONObject(i);
                    double latitude = pointObject.getDouble("latitude");
                    double longitude = pointObject.getDouble("longitude");
                    latitudes[i] = latitude;
                    longitudes[i] = longitude;
                }
                coordinates.add(latitudes);
                coordinates.add(longitudes);
                routeData.add(coordinates);
                routeData.add(lengthInMeters);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                callback.onRouteAvailable(routeData);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getLocationSuggestions(LocSuggCallback callback) {
        new HttpRequests("locationSuggestions", null, searchBox.getText().toString(), response -> {
            // Handle the API response here
            ArrayList<ArrayList<String>> locationsData = new ArrayList<>();
            try {
                System.out.println(response);
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String lat = jsonObject.getString("lat");
                    String lon = jsonObject.getString("lon");
                    String displayName = jsonObject.getString("display_name");

                    ArrayList<String> locationInfo = new ArrayList<>(Arrays.asList(lat, lon, displayName));
                    locationsData.add(locationInfo);
                }
                callback.onSuggAvailable(locationsData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    void checkIfNotify(SimulatedEmergencyVehicle vehicle) {
        String vehicleStreet = vehicle.getStreetLoc();
        double distanceToSimUser = distance(simulatedUser.getLocation(), vehicle.getLocation());
        if (distanceToSimUser > 3000) {
            notified_Stage1.remove(vehicle);
            notified_Stage2.remove(vehicle);
            notified_Stage3.remove(vehicle);
        }
        int normalNotificationDistance1 = Integer.parseInt(sharedPreferences.getString("normalNotificationDistance1", "1000"));
        int normalNotificationDistance2 = Integer.parseInt(sharedPreferences.getString("normalNotificationDistance2", "500"));
        int normalNotificationDistance3 = Integer.parseInt(sharedPreferences.getString("normalNotificationDistance3", "200"));
        int normalNotificationDistance4 = Integer.parseInt(sharedPreferences.getString("normalNotificationDistance4", "100"));

        // check if vehicle is approaching the user
        double headingDiff = vehicle.getHeadingToPoint(simulatedUser.getLocation()) - vehicle.getHeading();
        if (headingDiff > -20 && headingDiff < 20) {
            //System.out.println(capitalize(vehicle.getType()) + " is approaching the simulated user location. Heading diff: "+headingDiff);
            //if(vehicle.getIconColor() != Color.GREEN)
            //  vehicle.updateIconColor(Color.GREEN);

            if (vehicleStreet != null) {
                if (vehicleStreet.equals(simulatedUser.getStreetLoc())) {

                    // vehicle.updateIconColor(Color.BLUE);
                    if (distanceToSimUser < normalNotificationDistance1 && distanceToSimUser > normalNotificationDistance2 && !notified_Stage1.contains(vehicle)) {
                        //NormalNotification.showNotification(context, "EmergencyWatch Alerts", capitalize(vehicle.getType()) + " is less than " + normalNotificationDistance1 + " meters away. Prepare to move over.", vehicle.getBitmapIcon());
                        NormalNotification.showNotification(context, "EmergencyWatch Alerts", capitalize(vehicle.getTip()) + " este la mai putin de " + normalNotificationDistance1 + " metri distana. Pregateste-te sa te dai la o parte.", vehicle.getBitmapIcon());
                        notified_Stage1.add(vehicle);
                    } else if (distanceToSimUser < normalNotificationDistance2 && distanceToSimUser > normalNotificationDistance3 && !notified_Stage2.contains(vehicle)) {
                        NormalNotification.showNotification(context, "EmergencyWatch Alerts", capitalize(vehicle.getTip()) + " este la mai putin de " + normalNotificationDistance2 + " metri distana. Pregateste-te sa te dai la o parte.", vehicle.getBitmapIcon());
                        notified_Stage2.add(vehicle);
                    } else if (distanceToSimUser < normalNotificationDistance3 && !notified_Stage3.contains(vehicle)) {
                        //ttsNotificationManager.createTTSNotification(capitalize(vehicle.getType()) + " is right behind you. Move over immediately");
                        ttsNotificationManager.createTTSNotification("O " + capitalize(vehicle.getTip()) + " s-a apropiat foarte tare de tine. Pregătește-te să te muți la o parte.");
                        notified_Stage3.add(vehicle);
                    }
                } else {
                    if (distanceToSimUser <= normalNotificationDistance4 && !notified_Stage3.contains(vehicle)) {
                        //ttsNotificationManager.createTTSNotification("A " + capitalize(vehicle.getType()) + " vehicle is around your location. Beware of possible intersection.");

                        ttsNotificationManager.createTTSNotification("O " +capitalize(vehicle.getTip()) +" este în zona ta. Ai grijă la posibile intersecțiii cu aceasta. ");
                        notified_Stage3.add(vehicle);
                    }
                }
            } else {
                if (distanceToSimUser <= normalNotificationDistance4 && !notified_Stage3.contains(vehicle)) {
                    //ttsNotificationManager.createTTSNotification("A " + capitalize(vehicle.getType()) + " vehicle is around your location. Beware of possible intersection.");
                    ttsNotificationManager.createTTSNotification("O " +capitalize(vehicle.getTip()) +" este în zona ta. Ai grijă la posibile intersecțiii cu aceasta. ");
                    notified_Stage3.add(vehicle);
                }
            }
        } else {
            if (distanceToSimUser <= normalNotificationDistance4 && !notified_Stage3.contains(vehicle)) {
                //ttsNotificationManager.createTTSNotification("A " + capitalize(vehicle.getType()) + " vehicle is around your location. Beware of possible intersection.");
                ttsNotificationManager.createTTSNotification("O " +capitalize(vehicle.getTip()) +" este în zona ta. Ai grijă la posibile intersecțiii cu aceasta. ");
                notified_Stage3.add(vehicle);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    void updateVehicleDistanceText() {
        TextView noActiveEmergencies = view.findViewById(R.id.noActiveEmergencies);
        if (ev_list.size() != 0) {
            noActiveEmergencies.setText(ev_list.size() + " emergencies in your area.");
            for (int i = 0; i < ev_list.size(); i++) {
                SimulatedEmergencyVehicle vehicle = (SimulatedEmergencyVehicle) ev_list.get(i).get(0);
                checkIfNotify(vehicle);
                TextView distanceText = (TextView) ev_list.get(i).get(1);
                int dist = (int) distance(vehicle.getLocation(), simulatedUser.getLocation()/*userCurrentPos*/);
                distanceText.setText("" + dist + " m");
            }
        } else {
            noActiveEmergencies.setText("No emergencies in your area.");
        }
    }

    @SuppressLint("SetTextI18n")
    void addActiveEmergencyToMap(SimulatedEmergencyVehicle vehicle) {

        vehicle.draw();
        if (!Objects.equals(vehicle.getType(), "user")) {
            System.out.println("Added " + vehicle.getType() + " to slide panel");

            LayoutInflater inflater = LayoutInflater.from(context);
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
            ArrayList<Object> vehicleData = new ArrayList<>();
            vehicleData.add(vehicle);
            vehicleData.add(vehicleDistance);
            vehicleData.add(vehicleLocation);
            ev_list.add(vehicleData);
            updateVehicleDistanceText();
        }

    }

    void startUserLocUpdate() {
        userLocationUpdateRunnable.run();
    }

    void stopUserLocUpdate() {
        userLocationUpdateHandler.removeCallbacks(userLocationUpdateRunnable);
    }
    Runnable distanceToVehiclesRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            try {
                if (ev_list != null && ev_list.size() != 0 && userCurrentPos != null) {
                    //trigger anim.
                    emergencyActiveInfo.setVisibility(View.VISIBLE);
                    emergencyActiveTitle.setVisibility(View.VISIBLE);
                    noEmgTitlePanel.setVisibility(View.INVISIBLE);
                    updateVehicleDistanceText();
                    closestVehicle = (SimulatedEmergencyVehicle) ev_list.get(0).get(0);
                    for (int i = 0; i < ev_list.size(); i++) {
                        SimulatedEmergencyVehicle new_vehicle = (SimulatedEmergencyVehicle) ev_list.get(i).get(0);
                        if (distance(new_vehicle.getLocation(), userCurrentPos) < distance(closestVehicle.getLocation(), userCurrentPos)) {
                            closestVehicle = (SimulatedEmergencyVehicle) ev_list.get(i).get(0);
                        }
                        //pentru engleza
                        //String text = capitalize(closestVehicle.getType()) + " vehicle at " + (int) distance(closestVehicle.getLocation(), userCurrentPos) + " m";
                        String text = capitalize(closestVehicle.getTip()) + " la " + (int) distance(closestVehicle.getLocation(), userCurrentPos) + " m";
                        emergencyActiveInfo.setText(text);
                    }
                } else {
                    emergencyActiveInfo.setVisibility(View.INVISIBLE);
                    emergencyActiveTitle.setVisibility(View.INVISIBLE);
                    noEmgTitlePanel.setVisibility(View.VISIBLE);
                }
            } finally {
                // 100% guarantee that context always happens, even if
                // your update method throws an exception
                int interval = 1000;
                distanceToEvHandler.postDelayed(distanceToVehiclesRunnable, interval);
            }
        }
    };

    void startLocationUpdate() {
        vehicleLocationUpdateRunnable.run();
    }    Runnable vehicleLocationUpdateRunnable = new Runnable() {
        int i = 0;

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            try {
                if (ev_list.size() != 0) {
                    if (i < ev_list.size()) {
                        SimulatedEmergencyVehicle vehicle = (SimulatedEmergencyVehicle) ev_list.get(i).get(0);
                        System.out.println("Request location for " + vehicle.getType() + " coord: " + vehicle.getLocation());
                        vehicle.getStreet(streetName -> {
                            // Use the street name here
                            System.out.println("updated location for " + vehicle.getType() + "it's location is: " + streetName);
                            TextView text = (TextView) ev_list.get(i).get(2);
                            text.setText("Street: " + streetName);
                            vehicle.setStreetLoc(streetName);
                            if (i + 1 != ev_list.size()) {
                                i++;
                            } else {
                                i = 0;
                            }
                        });
                    }
                }

            } finally {
                // 100% guarantee that context always happens, even if
                // your update method throws an exception
                int interval = 5000;
                locationUpdateHandler.postDelayed(vehicleLocationUpdateRunnable, interval);
            }
        }
    };

    void stopLocationUpdate() {
        locationUpdateHandler.removeCallbacks(vehicleLocationUpdateRunnable);
    }    Runnable userLocationUpdateRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            try {
                userCurrentPos = simulatedUser.getLocation();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                int interval = 1000;
                userLocationUpdateHandler.postDelayed(userLocationUpdateRunnable, interval);
            }
        }
    };

    void startdistanceToEVChecker() {
        distanceToVehiclesRunnable.run();
    }

    void stopdistanceToEVChecker() {
        distanceToEvHandler.removeCallbacks(distanceToVehiclesRunnable);
    }

    public String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void updatePermanentNotification(String title, String content, int smallIconResId) {
        Intent updateNotificationIntent = new Intent(context, ForegroundActivity.class);
        updateNotificationIntent.setAction(ForegroundActivity.ACTION_UPDATE_NOTIFICATION);
        updateNotificationIntent.putExtra(ForegroundActivity.EXTRA_TITLE, title);
        updateNotificationIntent.putExtra(ForegroundActivity.EXTRA_CONTENT, content);
        updateNotificationIntent.putExtra(ForegroundActivity.EXTRA_SMALL_ICON_RES_ID, smallIconResId);
        context.startService(updateNotificationIntent);
    }

    public void onResume() {
        super.onResume();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        updateNotificationHandler.removeCallbacks(updateNotificationRunnable);
    }

    public void onPause() {
        super.onPause();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        updateNotificationHandler.postDelayed(updateNotificationRunnable, 3000);
    }

    public interface RouteCallback {
        void onRouteAvailable(ArrayList<Object> routeData);
    }

    public interface LocSuggCallback {
        void onSuggAvailable(ArrayList<ArrayList<String>> locationsData);
    }








}