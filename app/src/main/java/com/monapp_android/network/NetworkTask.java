package com.monapp_android.network;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class NetworkTask extends AsyncTask<AbstractRequester, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(AbstractRequester... params) {
        try {
            return params[0].makeRequest();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
