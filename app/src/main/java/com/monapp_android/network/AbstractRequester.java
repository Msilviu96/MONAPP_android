package com.monapp_android.network;

import com.monapp_android.application.MONAPP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;


public abstract class AbstractRequester {
    protected static final String link = MONAPP.ADDRESS + "/rest/";
    protected String path;

    public AbstractRequester(String path) {
        this.path = path;
    }

    public  abstract JSONObject makeRequest() throws IOException, JSONException;
}
