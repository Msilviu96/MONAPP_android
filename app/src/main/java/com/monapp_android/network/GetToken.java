package com.monapp_android.network;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetToken extends AbstractRequester {
    private String token;

    public GetToken(String token) {
        super("token/" + token);
        this.token = token;
    }

    @Override
    public JSONObject makeRequest() throws IOException, JSONException {
        URL url = new URL(this.link + this.path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("charset", "utf-8");
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(false);
        int res = urlConnection.getResponseCode();

        if (404 == res){
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
