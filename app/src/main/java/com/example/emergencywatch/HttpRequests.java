package com.example.emergencywatch;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.net.ssl.HttpsURLConnection;

public class HttpRequests extends AsyncTask<Void, Void, String> {

    private final String lat, lon;
    private final HttpListener listener;

    public HttpRequests(GeoPoint location, HttpListener listener) {
        this.lat = String.valueOf(location.getLatitude());
        this.lon = String.valueOf(location.getLongitude());
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String url = String.format("https://us1.locationiq.com/v1/reverse?key=pk.4c220907e8b2cc4b57c6d4232d410fa3&lat=%s&lon=%s&format=json", lat, lon);
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
