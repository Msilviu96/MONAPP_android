package com.monapp_android.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.monapp_android.DTOs.ChildDTO;

public class MONAPP extends Application {
    public static final String CHANNEL_ID = "bsckgroundServiceChannel";
    public static final String ADDRESS = "http://192.168.1.103:8000";

    private static Context applicationContext;
    private static ChildDTO child;


    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Background Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }

    public static Context getAppContext() {
        return applicationContext;
    }

    public static void setChild(ChildDTO childDTO){ child = childDTO; }

    public static ChildDTO getChild() { return child;}
}
