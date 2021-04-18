package com.monapp_android.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.monapp_android.R;

public class BlockedApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_app);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
