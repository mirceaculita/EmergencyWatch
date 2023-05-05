package com.example.emergencywatch;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NavigationFragment extends Fragment {
    View view;
    MapView map = null;
    //public MyLocationNewOverlay locationOverlay;
    public Button floatingButton;
    IMapController mapController;
    GeoPoint simulatedUserStartLoc = new GeoPoint(47.14698676894605, 27.609202948507853);

    TableLayout locationSuggestions;
    EditText searchBox;

    Double destLat, destLon;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15.5);
        mapController.setCenter(simulatedUserStartLoc);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);

        searchBox = view.findViewById(R.id.searchBoxNav);
        locationSuggestions = view.findViewById(R.id.suggestionTextLayout);

        floatingButton = view.findViewById(R.id.menuButton);
        floatingButton.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).openDrawer();
        });
        Button stuffButton = view.findViewById(R.id.stuffButton);
        stuffButton.setOnClickListener(view -> {
            getRoute(System.out::println);
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                locationSuggestions.removeAllViews();

                handler.removeCallbacks(runnable); // Remove any previously scheduled runnable
                runnable = new Runnable() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void run() {
                        if (charSequence.length() >= 5) {
                            System.out.println("am scris in textbox lmao ce nice");
                            getLocationSuggestions(locationsData -> {
                                System.out.println(locationsData);
                                int limit = 0;
                                if(locationsData.size() > 6)
                                    limit = 6;
                                else
                                    limit = locationsData.size();

                                for(int j = 0; j<limit;j++){
                                    LayoutInflater inflater = LayoutInflater.from(view.getContext());
                                    View locationInfo = inflater.inflate(R.layout.location_data_list, locationSuggestions, false);
                                    locationInfo.setId(View.generateViewId());
                                    TextView locationName = locationInfo.findViewById(R.id.LocationName);
                                    locationName.setId(View.generateViewId());
                                    TextView locationAddress = locationInfo.findViewById(R.id.LocationAddress);
                                    locationAddress.setId(View.generateViewId());
                                    TextView locationDistance = locationInfo.findViewById(R.id.LocationDistance);
                                    locationDistance.setId(View.generateViewId());


                                    String[] locationDataExpanded = locationsData.get(j).get(2).split(",",2);
                                    locationName.setText(locationDataExpanded[0]);

                                    int lastCommaIndex = locationDataExpanded[1].lastIndexOf(",");
                                    int secondToLastCommaIndex = locationDataExpanded[1].lastIndexOf(",", lastCommaIndex - 1);

                                    String address = locationDataExpanded[1].substring(0, secondToLastCommaIndex);

                                    locationAddress.setText(address);
                                    double lat = Double.parseDouble(locationsData.get(j).get(0));
                                    double lon = Double.parseDouble(locationsData.get(j).get(1));
                                    double distanceToPOI = distance(new GeoPoint(lat,lon), simulatedUserStartLoc);
                                    locationDistance.setText(String.format("%.2f km", distanceToPOI/1000));

                                    final int index = j;
                                    locationInfo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            System.out.println("Location details: " + locationsData.get(index));
                                            destLat = Double.parseDouble(locationsData.get(index).get(0));
                                            destLon = Double.parseDouble(locationsData.get(index).get(1));
                                            getRoute(coordinates -> {
                                                map.getOverlays().add(drawRoute(coordinates, "#FFA500"));
                                                mapController.animateTo(simulatedUserStartLoc);

                                            });
                                            locationSuggestions.removeAllViews();
                                        }
                                    });

                                    locationSuggestions.addView(locationInfo); // Add the view to the ViewGroup after setting the OnClickListener

                                }
                            });

                        }
                    }
                };
                handler.postDelayed(runnable, 3000); // Schedule the new runnable with a 3-second delay
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }
    public Polyline drawRoute(ArrayList<double[]> route, String colorString) {

        double[] lat = route.get(0);
        double[] lon = route.get(1);
        System.out.println(lat);
        System.out.println(lon);
        Polyline myPath = new Polyline();
        myPath.setWidth(10f);
        myPath.setColor(Color.parseColor(colorString));


        for (int i = 0; i < lat.length; i++) {
            myPath.addPoint(new GeoPoint(lat[i],lon[i]));
        }

        return (myPath);
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
    public interface RouteCallback {
        void onRouteAvailable(ArrayList<double[]> route);
    }
    public void getRoute(RouteCallback callback) {
        new HttpRequests("routing",new ArrayList<>(Arrays.asList(new GeoPoint(47.17263411570034, 27.535657969818757), new GeoPoint(destLat, destLon))), null,new HttpRequests.HttpListener() {
            @Override
            public void onHttpResponse(String response) throws JsonProcessingException {
                ArrayList<double[]> coordinates = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray routesArray = jsonObject.getJSONArray("routes");
                    JSONObject routeObject = routesArray.getJSONObject(0);
                    JSONArray legsArray = routeObject.getJSONArray("legs");
                    JSONObject legObject = legsArray.getJSONObject(0);
                    JSONArray pointsArray = legObject.getJSONArray("points");

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    callback.onRouteAvailable(coordinates);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface LocSuggCallback {
        void onSuggAvailable(ArrayList<ArrayList<String>> locationsData);
    }
    //ArrayList<ArrayList<String>> locationsData;
    public void getLocationSuggestions(LocSuggCallback callback) {
        new HttpRequests("locationSuggestions",null, searchBox.getText().toString(),new HttpRequests.HttpListener() {
            @Override
            public void onHttpResponse(String response) throws JsonProcessingException {
                // Handle the API response here
                System.out.println(response);

                ArrayList<ArrayList<String>> locationsData = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String lat = jsonObject.getString("lat");
                        String lon = jsonObject.getString("lon");
                        String displayName = jsonObject.getString("display_name");

                        ArrayList<String> locationInfo = new ArrayList<>(Arrays.asList(lat,lon,displayName));
                        locationsData.add(locationInfo);
                    }
                    callback.onSuggAvailable(locationsData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}