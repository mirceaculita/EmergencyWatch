package com.example.emergencywatch;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequests implements Callable<String> {

    String lat,lon;
    public HttpRequests(GeoPoint location) {
        lat = location.getLatitude()+"";
        lon = location.getLongitude()+"";
    }

    public String call() throws Exception {
        String url = String.format("https://us1.locationiq.com/v1/reverse?key=pk.4c220907e8b2cc4b57c6d4232d410fa3&lat=%s&lon=%s&format=json",lat,lon);
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // return the response as a string
        return response.toString();
    }
}