package com.monapp_android.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.monapp_android.DTOs.ChildDTO;
import com.monapp_android.R;
import com.monapp_android.application.MONAPP;
import com.monapp_android.network.GetToken;
import com.monapp_android.network.NetworkTask;
import com.monapp_android.network.AbstractRequester;

import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class TokenRegister extends AppCompatActivity {
    EditText token;
    Button next;
    ChildDTO childDTO;
    JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_register);

        token = (EditText) findViewById(R.id.token);
        next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenText = token.getText().toString();

                if (!validateToken(tokenText)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Your token length should be 7!\nCheck it and try again.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .addNetworkInterceptor(new StethoInterceptor())
                        .build();
                AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

                AndroidNetworking.get("http://192.168.1.155:8000/rest/token/{token}")
                        .addPathParameter("token", tokenText)
                        .setTag("test")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                childDTO = ChildDTO.fromJson(response);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                MONAPP.setChild(childDTO);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(ANError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Your token doesn't exist in our DB!\nCheck it and try again.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });

            }
        });
    }

    private Boolean validateToken(String tokenText) {
        if (tokenText.length() != 7) {
            return false;
        }
        return true;
    }
}
