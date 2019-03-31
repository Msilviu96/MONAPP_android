package com.monapp_android.tasks;

import android.os.SystemClock;

import com.monapp_android.DTOs.ChildDTO;
import com.monapp_android.GPS.GPSTracker;
import com.monapp_android.application.MONAPP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendCoordinatesTask extends AbstractTask {

    private static final String LINK = "http://192.168.0.192:8080/rest/child_coordinates/";
    private ChildDTO childDTO;
    private Long delay;

    public SendCoordinatesTask(ChildDTO childDTO, Long delay) {
        this.childDTO = childDTO;
        this.delay = delay;
    }

    @Override
    public void beforeRun() {
        return;
    }

    @Override
    public void run() {
        beforeRun();

        GPSTracker gps = GPSTracker.getInstance();
        gps.canGetLocation();
        gps.getLocation(MONAPP.getAppContext());


        while (checkTokenState(childDTO.getToken())) {
            SystemClock.sleep(delay);
            String coordinates = "{\"latitude\":" + Double.toString(gps.getLatitude()) + ", " + "\"longitude\":" + Double.toString(gps.getLongitude()) + "}";
            System.out.println(coordinates);
        }
//        GPSTracker gpsTracker = new GPSTracker(MONAPP.getAppContext());
//        JSONObject jsonObject;
//
//        while (checkTokenState(childDTO.getToken())) {
//            SystemClock.sleep(delay);
//            String coordinates = getCoordinates(gpsTracker);
//            System.out.println(coordinates);

//            try {
//                this.makeRequest(coordinates);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    //}

    private boolean checkTokenState(String token) {
        return true;
        //TO DO
    }

    private JSONObject makeRequest(String coordinates) throws IOException, JSONException {
        URL url = new URL(this.LINK + this.childDTO.getToken());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("charset", "utf-8");
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);

        OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
        osw.write(coordinates);
        osw.flush();
        osw.close();

        int res = urlConnection.getResponseCode();

        if (404 == res) {
            return null;
        }
        if (400 == res) {
            return new JSONObject("{error: 'Input criteria not correct'}");
        }
        if (422 == res) {
            return new JSONObject("{error: 'Invalid User'}");
        }
        if (500 == res) {
            return new JSONObject("{error: 'Internal server error'}");
        }
        if (503 == res) {
            return new JSONObject("{error: 'The server is currently unavailable'}");
        }
        if (200 == res) {
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

            return new JSONObject(sb.toString());
        }
        return new JSONObject("{error: 'Unknown error'}");
    }
}

