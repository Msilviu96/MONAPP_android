package com.monapp_android.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;


public abstract class AbstractRequester {
    protected static final String link = "http://192.168.1.155:8000/rest/";
    protected String path;

    public AbstractRequester(String path) {
        this.path = path;
    }

    public  abstract JSONObject makeRequest() throws IOException, JSONException;
}
