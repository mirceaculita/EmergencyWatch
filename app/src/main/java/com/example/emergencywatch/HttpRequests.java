package com.example.emergencywatch;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.net.ssl.HttpsURLConnection;

public class HttpRequests extends AsyncTask<Void, Void, String> {

    private String lat, lon, lat1, lon1, lat2, lon2;
    String url;
    private final HttpListener listener;
    String queryWord;
    public HttpRequests(String api,ArrayList<GeoPoint> locations,String query ,HttpListener listener) {
        if(api.equals("routing")) {
            if (locations.size() == 2) {
                GeoPoint start = locations.get(0);
                GeoPoint end = locations.get(1);
                if (start != null && end != null) {
                    this.lat1 = String.valueOf(start.getLatitude());
                    this.lon1 = String.valueOf(start.getLongitude());
                    this.lat2 = String.valueOf(end.getLatitude());
                    this.lon2 = String.valueOf(end.getLongitude());
                    url = String.format("https://api.tomtom.com/routing/1/calculateRoute/%s,%s:%s,%s/json?key=0WzrtLx6slkR4SzhaKIGvGIqYXdgNdex", lat1, lon1, lat2, lon2);
                }
            }
        }
        if(api.equals("location")) {
            if (locations.size() == 1) {
                GeoPoint location = locations.get(0);
                this.lat = String.valueOf(location.getLatitude());
                this.lon = String.valueOf(location.getLongitude());
                url = String.format("https://us1.locationiq.com/v1/reverse?key=pk.4c220907e8b2cc4b57c6d4232d410fa3&lat=%s&lon=%s&format=json", lat, lon);
            }
        }

        if(api.equals("locationSuggestions")){
            if(query != null)
                queryWord = query.replace(" ", "+");;
            url = String.format("https://nominatim.openstreetmap.org/search?q=%s&countrycodes=ro&format=json", queryWord);
        }


        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            //System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // return the response as a string
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            try {
                listener.onHttpResponse(result);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface HttpListener {
        void onHttpResponse(String response) throws JsonProcessingException;
    }
}
